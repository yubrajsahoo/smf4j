/*
 *
 *  * Copyright 2024 Yubraj Sahoo
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.github.yubrajsahoo.smf4jcore.aspect;

import io.github.yubrajsahoo.smf4jcore.annotation.Counter;
import io.github.yubrajsahoo.smf4jcore.service.MetricsService;
import io.github.yubrajsahoo.smf4jcore.utils.JsonConverter;
import org.aspectj.lang.JoinPoint;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CounterAspect}.
 * <p>
 * Validates that the aspect correctly constructs a SpEL evaluation context and
 * delegates to {@link MetricsService} for both successful returns and exceptions.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see CounterAspect
 * @see io.github.yubrajsahoo.smf4jcore.spel.SpelContextBuilder
 */
@ExtendWith(MockitoExtension.class)
class CounterAspectTest {

    @Mock
    private MetricsService metricsService;

    @Mock
    private BeanResolver beanResolver;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    private CounterAspect counterAspect;

    /**
     * Initialises the {@link CounterAspect} and configures lenient stubs
     * for the mock {@link JoinPoint} and {@link MethodSignature}.
     */
    @BeforeEach
    void setUp() {
        counterAspect = new CounterAspect(metricsService, beanResolver);
        lenient().when(joinPoint.getSignature()).thenReturn(methodSignature);
        lenient().when(methodSignature.getParameterNames()).thenReturn(new String[]{});
        lenient().when(joinPoint.getArgs()).thenReturn(new Object[]{});
    }

    /**
     * Verifies that {@link CounterAspect#captureReturn(JoinPoint, Counter, Object)}
     * delegates to {@link MetricsService#record(Counter, StandardEvaluationContext)}.
     */
    @Test
    void captureReturn_shouldDelegateToMetricsService() {
        Counter counter = JsonConverter.fromJsonFile("/data/counter-annot-basic.json", Counter.class);
        Object result = "returnValue";

        counterAspect.captureReturn(joinPoint, counter, result);

        verify(metricsService).record(eq(counter), any(StandardEvaluationContext.class));
    }

    /**
     * Verifies that the return value is bound as {@code #result} in the SpEL context
     * and {@code #error} is {@code null}.
     */
    @Test
    void captureReturn_shouldPassResultInContext() {
        Counter counter = JsonConverter.fromJsonFile("/data/counter-annot-basic.json", Counter.class);
        Object result = "theResult";

        counterAspect.captureReturn(joinPoint, counter, result);

        ArgumentCaptor<StandardEvaluationContext> contextCaptor =
                ArgumentCaptor.forClass(StandardEvaluationContext.class);
        verify(metricsService).record(eq(counter), contextCaptor.capture());

        StandardEvaluationContext capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.lookupVariable("result")).isEqualTo("theResult");
        assertThat(capturedContext.lookupVariable("error")).isNull();
    }

    /**
     * Verifies that {@link CounterAspect#captureException(JoinPoint, Counter, Throwable)}
     * delegates to {@link MetricsService#record(Counter, StandardEvaluationContext)}.
     */
    @Test
    void captureException_shouldDelegateToMetricsService() {
        Counter counter = JsonConverter.fromJsonFile("/data/counter-annot-basic.json", Counter.class);
        RuntimeException exception = new RuntimeException("test error");

        counterAspect.captureException(joinPoint, counter, exception);

        verify(metricsService).record(eq(counter), any(StandardEvaluationContext.class));
    }

    /**
     * Verifies that the exception is bound as {@code #error} in the SpEL context
     * and {@code #result} is {@code null}.
     */
    @Test
    void captureException_shouldPassExceptionInContext() {
        Counter counter = JsonConverter.fromJsonFile("/data/counter-annot-basic.json", Counter.class);
        RuntimeException exception = new RuntimeException("error msg");

        counterAspect.captureException(joinPoint, counter, exception);

        ArgumentCaptor<StandardEvaluationContext> contextCaptor =
                ArgumentCaptor.forClass(StandardEvaluationContext.class);
        verify(metricsService).record(eq(counter), contextCaptor.capture());

        StandardEvaluationContext capturedContext = contextCaptor.getValue();
        assertThat(capturedContext.lookupVariable("error")).isEqualTo(exception);
        assertThat(capturedContext.lookupVariable("result")).isNull();
    }

    /**
     * Verifies that method arguments from the {@link JoinPoint} are registered
     * as named variables in the SpEL context.
     */
    @Test
    void captureReturn_withMethodArguments_shouldSetArgsInContext() {
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"orderId"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{"ORD-42"});

        Counter counter = JsonConverter.fromJsonFile("/data/counter-annot-basic.json", Counter.class);

        counterAspect.captureReturn(joinPoint, counter, null);

        ArgumentCaptor<StandardEvaluationContext> contextCaptor =
                ArgumentCaptor.forClass(StandardEvaluationContext.class);
        verify(metricsService).record(eq(counter), contextCaptor.capture());

        assertThat(contextCaptor.getValue().lookupVariable("orderId")).isEqualTo("ORD-42");
    }

    /**
     * Verifies that a {@code null} return value is correctly bound as {@code #result = null}
     * in the SpEL context.
     */
    @Test
    void captureReturn_withNullResult_shouldSetNullResultInContext() {
        Counter counter = JsonConverter.fromJsonFile("/data/counter-annot-basic.json", Counter.class);

        counterAspect.captureReturn(joinPoint, counter, null);

        ArgumentCaptor<StandardEvaluationContext> contextCaptor =
                ArgumentCaptor.forClass(StandardEvaluationContext.class);
        verify(metricsService).record(eq(counter), contextCaptor.capture());

        assertThat(contextCaptor.getValue().lookupVariable("result")).isNull();
    }

}
