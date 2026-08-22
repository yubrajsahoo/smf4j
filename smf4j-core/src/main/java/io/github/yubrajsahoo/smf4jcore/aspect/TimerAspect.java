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
import io.github.yubrajsahoo.smf4jcore.spel.SpelContextBuilder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@Aspect
public class TimerAspect {
    private static final Logger log = LoggerFactory.getLogger(TimerAspect.class);

    private final BeanResolver beanResolver;
    private final MetricsService metricsService;

    public TimerAspect(BeanResolver beanResolver, MetricsService metricsService) {
        this.beanResolver = beanResolver;
        this.metricsService = metricsService;
    }

    @Around("@annotation(timer)")
    public Object aroundTimer(ProceedingJoinPoint joinPoint, Timer timer) throws Throwable {
        try {
            return record(joinPoint, timer);
        } catch (Throwable e) {
            log.error("Error while Capturing Timer Metrics: {}", e.getMessage(), e);
        }
        return null;
    }

    private Object record(ProceedingJoinPoint joinPoint, Timer timer) throws Throwable {
        Object result = null;
        Throwable error = null;
        io.micrometer.core.instrument.Timer.Sample sample = metricsService.start();
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw e;
        } finally {
            StandardEvaluationContext context = SpelContextBuilder.buildContext(
                    joinPoint, result, error, beanResolver);
            metricsService.record(timer, context);
        }
    }
}
