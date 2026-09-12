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
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see TimerAspect
 * @see io.github.yubrajsahoo.smf4jcore.spel.SpelContextBuilder
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

    /**
     * Initialises the {@link TimerAspect} and configures lenient stubs
     * for the mock {@link ProceedingJoinPoint} and {@link MethodSignature}.
     */
    @BeforeEach
    void setUp() {
        timerAspect = new TimerAspect(metricsService, beanResolver);
        lenient().when(joinPoint.getSignature()).thenReturn(methodSignature);
        lenient().when(methodSignature.getParameterNames()).thenReturn(new String[]{});
        lenient().when(joinPoint.getArgs()).thenReturn(new Object[]{});
    }

    /**
     * Verifies that {@link TimerAspect#aroundTimer(ProceedingJoinPoint, Timer)} starts a timing sample,
     * proceeds with execution, and delegates metric recording to the {@link MetricsService}.
     *
     * @throws Throwable if proceeding the join point fails
     */
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

    /**
     * Verifies that the successful return value is correctly bound to {@code #result}
     * in the SpEL evaluation context.
     *
     * @throws Throwable if proceeding the join point fails
     */
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

    /**
     * Verifies that if an exception is thrown during method execution, it is correctly bound
     * to {@code #error} in the SpEL evaluation context, and the exception is rethrown.
     *
     * @throws Throwable if proceeding the join point fails
     */
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

    /**
     * Verifies that method arguments from the join point are correctly populated
     * into the SpEL evaluation context.
     *
     * @throws Throwable if proceeding the join point fails
     */
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

    /**
     * Verifies that if starting the timer sample fails, the execution proceeds normally
     * and no recording is attempted.
     *
     * @throws Throwable if proceeding the join point fails
     */
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
