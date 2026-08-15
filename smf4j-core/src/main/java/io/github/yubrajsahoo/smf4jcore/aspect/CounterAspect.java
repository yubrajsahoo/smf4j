package io.github.yubrajsahoo.smf4jcore.aspect;

import io.github.yubrajsahoo.smf4jcore.annotation.Counter;
import io.github.yubrajsahoo.smf4jcore.service.MetricsService;
import io.github.yubrajsahoo.smf4jcore.spel.SpelContextBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Aspect that intercepts methods annotated with {@link Counter} to capture and record counter metrics.
 * <p>
 * This aspect intercepts both successful invocations via {@link #captureReturn(JoinPoint, Counter, Object)}
 * and exceptional terminations via {@link #captureException(JoinPoint, Counter, Throwable)}.
 * It constructs a SpEL context containing method arguments, return values, or exceptions,
 * and delegates metric recording to the configured {@link MetricsService}.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see Counter
 * @see MetricsService
 * @see SpelContextBuilder
 */
@Aspect
public class CounterAspect {

    private final MetricsService metricsService;

    /**
     * Constructs a new {@link CounterAspect} with the specified {@link MetricsService}.
     *
     * @param metricsService the service responsible for processing and recording metrics
     */
    public CounterAspect(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    /**
     * Advice that executes upon successful return of a method annotated with {@link Counter}.
     * <p>
     * Creates a SpEL evaluation context populated with method parameters and the return value (as {@code #result}),
     * then invokes {@link MetricsService#record(Counter, StandardEvaluationContext)}.
     * </p>
     *
     * @param joinPoint the AOP join point representing the method invocation
     * @param counter   the {@link Counter} annotation on the intercepted method
     * @param result    the return value of the intercepted method invocation
     */
    @AfterReturning(
            pointcut = "@annotation(counter)",
            returning = "result"
    )
    public void captureReturn(JoinPoint joinPoint, Counter counter, Object result) {
        StandardEvaluationContext standardEvaluationContext =
                SpelContextBuilder.buildContext(joinPoint, result, null);

        metricsService.record(counter, standardEvaluationContext);
    }

    /**
     * Advice that executes when a method annotated with {@link Counter} throws an exception.
     * <p>
     * Creates a SpEL evaluation context populated with method parameters and the thrown exception (as {@code #error}),
     * then invokes {@link MetricsService#record(Counter, StandardEvaluationContext)}.
     * </p>
     *
     * @param joinPoint the AOP join point representing the method invocation
     * @param counter   the {@link Counter} annotation on the intercepted method
     * @param exception the exception thrown during method execution
     */
    @AfterThrowing(
            pointcut = "@annotation(counter)",
            throwing = "exception"
    )
    public void captureException(JoinPoint joinPoint, Counter counter, Throwable exception) {
        StandardEvaluationContext standardEvaluationContext =
                SpelContextBuilder.buildContext(joinPoint, null, exception);

        metricsService.record(counter, standardEvaluationContext);
    }
}