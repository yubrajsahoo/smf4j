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

import io.github.yubrajsahoo.smf4jcore.annotation.Timer;
import io.github.yubrajsahoo.smf4jcore.service.MetricsService;
import io.github.yubrajsahoo.smf4jcore.service.impl.TimerMetricsService;
import io.github.yubrajsahoo.smf4jcore.spel.SpelContextBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Aspect that intercepts methods annotated with {@link Timer} to record execution time metrics.
 * <p>
 * Uses {@link MetricsService} to start a timing sample before the method execution and stops it
 * afterward, recording the elapsed duration along with any dynamically resolved tags via SpEL expressions.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @see Timer
 * @see MetricsService
 * @since 0.0.1
 */
@Aspect
public class TimerAspect {
    private static final Logger log = LoggerFactory.getLogger(TimerAspect.class);

    private final TimerMetricsService metricsService;
    private final BeanResolver beanResolver;

    /**
     * Constructs a new {@link TimerAspect} with the specified metrics service and bean resolver.
     *
     * @param metricsService the service responsible for processing and recording metrics
     * @param beanResolver   the resolver used for evaluating Spring beans in SpEL expressions
     */
    public TimerAspect(TimerMetricsService metricsService, BeanResolver beanResolver) {
        this.metricsService = metricsService;
        this.beanResolver = beanResolver;
    }

    /**
     * Around advice that intercepts methods annotated with {@link Timer}.
     * <p>
     * Wraps the method execution to ensure metrics are recorded even if exceptions occur.
     * If an error happens specifically during the metric recording process, it is logged and the method result is unaffected.
     * </p>
     *
     * @param joinPoint the join point representing the intercepted method execution
     * @param timer     the {@link Timer} annotation metadata from the intercepted method
     * @return the result of the intercepted method execution
     * @throws Throwable if the underlying method throws an exception
     */
    @Around("@annotation(timer)")
    public Object aroundTimer(ProceedingJoinPoint joinPoint, Timer timer) throws Throwable {
        return recordMetrics(joinPoint, timer);
    }

    /**
     * Executes the target method and records the execution duration.
     * <p>
     * A timing sample is started before execution. Once the method completes (normally or exceptionally),
     * a SpEL context is built and the duration is recorded via the {@link TimerMetricsService}.
     * </p>
     *
     * @param joinPoint the join point representing the method execution
     * @param timer     the {@link Timer} annotation metadata
     * @return the result of the method execution
     * @throws Throwable if the underlying method throws an exception
     */
    private Object recordMetrics(ProceedingJoinPoint joinPoint, Timer timer) throws Throwable {
        Object result = null;
        Throwable error = null;
        io.micrometer.core.instrument.Timer.Sample sample = null;
        
        try {
            sample = metricsService.start(timer);
        } catch (Exception e) {
            log.error("Error starting Timer metric sample: {}", e.getMessage(), e);
        }

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            error = e;
            throw e;
        } finally {
            if (sample != null) {
                try {
                    StandardEvaluationContext context = SpelContextBuilder.buildContext(
                            joinPoint, result, error, beanResolver);
                    metricsService.recordTimer(sample, timer, context);
                } catch (Exception e) {
                    log.error("Error while recording Timer Metrics: {}", e.getMessage(), e);
                }
            }
        }
    }
}
