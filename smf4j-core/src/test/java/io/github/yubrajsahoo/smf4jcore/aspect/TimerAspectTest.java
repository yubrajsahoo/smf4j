package io.github.yubrajsahoo.smf4jcore.aspect;

import io.github.yubrajsahoo.smf4jcore.annotation.Timer;
import io.github.yubrajsahoo.smf4jcore.service.MetricsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link TimerAspect}.
 * <p>
 * Validates that the aspect correctly constructs a SpEL evaluation context,
 * manages timer samples, and delegates to {@link MetricsService} for both
 * successful executions and exceptions.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class TimerAspectTest {

    @Mock
    private MetricsService metricsService;

    @Mock
    private BeanResolver beanResolver;

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @Mock
    private io.micrometer.core.instrument.Timer.Sample mockSample;

    @Mock
    private Timer timer;

    private TimerAspect timerAspect;

    @BeforeEach
    void setUp() {
        timerAspect = new TimerAspect(metricsService, beanResolver);
        lenient().when(joinPoint.getSignature()).thenReturn(methodSignature);
        lenient().when(methodSignature.getParameterNames()).thenReturn(new String[]{});
        lenient().when(joinPoint.getArgs()).thenReturn(new Object[]{});
    }

    @Test
    void aroundTimer_shouldStartSampleAndDelegateToMetricsService() throws Throwable {
        Object expectedResult = "returnValue";
        when(metricsService.start()).thenReturn(mockSample);
        when(joinPoint.proceed()).thenReturn(expectedResult);

        Object actualResult = timerAspect.aroundTimer(joinPoint, timer);

        assertThat(actualResult).isEqualTo(expectedResult);
        verify(metricsService).start();
        verify(metricsService).record(eq(mockSample), eq(timer), any(StandardEvaluationContext.class));
    }

    @Test
    void aroundTimer_shouldPassResultInContext() throws Throwable {
        Object expectedResult = "theResult";
        when(metricsService.start()).thenReturn(mockSample);
        when(joinPoint.proceed()).thenReturn(expectedResult);

        timerAspect.aroundTimer(joinPoint, timer);

        ArgumentCaptor<StandardEvaluationContext> contextCaptor =
                ArgumentCaptor.forClass(StandardEvaluationContext.class);
        verify(metricsService).record(eq(mockSample), eq(timer), contextCaptor.capture());

        StandardEvaluationContext capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.lookupVariable("result")).isEqualTo(expectedResult);
        assertThat(capturedContext.lookupVariable("error")).isNull();
    }

    @Test
    void aroundTimer_whenExceptionThrown_shouldPassExceptionInContextAndRethrow() throws Throwable {
        RuntimeException expectedException = new RuntimeException("test error");
        when(metricsService.start()).thenReturn(mockSample);
        when(joinPoint.proceed()).thenThrow(expectedException);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            timerAspect.aroundTimer(joinPoint, timer);
        });

        assertThat(thrown).isEqualTo(expectedException);

        ArgumentCaptor<StandardEvaluationContext> contextCaptor =
                ArgumentCaptor.forClass(StandardEvaluationContext.class);
        verify(metricsService).record(eq(mockSample), eq(timer), contextCaptor.capture());

        StandardEvaluationContext capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.lookupVariable("error")).isEqualTo(expectedException);
        assertThat(capturedContext.lookupVariable("result")).isNull();
    }

    @Test
    void aroundTimer_withMethodArguments_shouldSetArgsInContext() throws Throwable {
        when(metricsService.start()).thenReturn(mockSample);
        when(joinPoint.proceed()).thenReturn("done");
        
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"orderId"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{"ORD-42"});

        timerAspect.aroundTimer(joinPoint, timer);

        ArgumentCaptor<StandardEvaluationContext> contextCaptor =
                ArgumentCaptor.forClass(StandardEvaluationContext.class);
        verify(metricsService).record(eq(mockSample), eq(timer), contextCaptor.capture());

        assertThat(contextCaptor.getValue().lookupVariable("orderId")).isEqualTo("ORD-42");
    }

    @Test
    void aroundTimer_whenMetricsServiceStartFails_shouldNotBlockExecution() throws Throwable {
        Object expectedResult = "success";
        when(metricsService.start()).thenThrow(new RuntimeException("Metric system down"));
        when(joinPoint.proceed()).thenReturn(expectedResult);

        Object actualResult = timerAspect.aroundTimer(joinPoint, timer);

        assertThat(actualResult).isEqualTo(expectedResult);
        verify(joinPoint).proceed();
        verify(metricsService, never()).record(any(), any(), any());
    }
}
