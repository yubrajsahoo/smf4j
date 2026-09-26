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

package io.github.yubrajsahoo.smf4j.engine.processor;

import io.github.yubrajsahoo.smf4j.api.annotation.Gauge;
import io.github.yubrajsahoo.smf4j.engine.service.MetricsService;
import io.github.yubrajsahoo.smf4j.engine.service.impl.GaugeMetricsService;
import io.github.yubrajsahoo.smf4j.engine.spel.SpelContextBuilder;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.function.ToDoubleFunction;

/**
 * A Spring {@link BeanPostProcessor} that processes the {@link Gauge} annotation on fields and methods.
 * <p>
 * This processor scans beans for fields and no-arg methods annotated with {@code @Gauge}.
 * It constructs a function to extract the value and registers the gauge via {@link MetricsService}.
 * If the annotation specifies a SpEL {@code expression}, it is evaluated against the target value.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
public class GaugeAnnotationProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(GaugeAnnotationProcessor.class);

    private final GaugeMetricsService metricsService;
    private final BeanResolver beanResolver;
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * Constructs a new {@link GaugeAnnotationProcessor}.
     *
     * @param metricsService the service for registering metrics
     * @param beanResolver   the bean resolver for SpEL evaluation
     */
    public GaugeAnnotationProcessor(GaugeMetricsService metricsService, BeanResolver beanResolver) {
        this.metricsService = metricsService;
        this.beanResolver = beanResolver;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, @Nonnull String beanName) throws BeansException {
        Object target = getTargetObject(bean);
        Class<?> targetClass = target.getClass();

        processFields(targetClass, target, beanName);
        processMethods(targetClass, target, beanName);

        return bean;
    }

    private Object getTargetObject(Object bean) {
        if (org.springframework.aop.support.AopUtils.isAopProxy(bean)) {
            Object singletonTarget = org.springframework.aop.framework.AopProxyUtils.getSingletonTarget(bean);
            if (singletonTarget != null) {
                return singletonTarget;
            }
        }
        return bean;
    }

    private void processFields(Class<?> targetClass, Object target, String beanName) {
        ReflectionUtils.doWithFields(targetClass, field -> {
            Gauge gauge = field.getAnnotation(Gauge.class);
            if (gauge != null) {
                ReflectionUtils.makeAccessible(field);
                Expression parsedExpression = parseExpression(gauge.expression());

                ToDoubleFunction<Object> function = obj -> {
                    try {
                        Object value = field.get(obj);
                        return extractDoubleValue(value, parsedExpression);
                    } catch (Exception e) {
                        log.error("Error reading gauge field '{}' in bean '{}'", field.getName(), beanName, e);
                        return 0.0;
                    }
                };
                registerGauge(gauge, target, function);
            }
        });
    }

    private void processMethods(Class<?> targetClass, Object target, String beanName) {
        ReflectionUtils.doWithMethods(targetClass, method -> {
            Gauge gauge = method.getAnnotation(Gauge.class);
            if (gauge != null) {
                if (method.getParameterCount() > 0) {
                    log.warn("Method '{}' in bean '{}' annotated with @Gauge should not have any parameters", method.getName(), beanName);
                    return;
                }
                ReflectionUtils.makeAccessible(method);
                Expression parsedExpression = parseExpression(gauge.expression());

                ToDoubleFunction<Object> function = obj -> {
                    try {
                        Object value = method.invoke(obj);
                        return extractDoubleValue(value, parsedExpression);
                    } catch (Exception e) {
                        log.error("Error invoking gauge method '{}' in bean '{}'", method.getName(), beanName, e);
                        return 0.0;
                    }
                };
                registerGauge(gauge, target, function);
            }
        });
    }

    private Expression parseExpression(String expressionStr) {
        if (StringUtils.hasText(expressionStr)) {
            return parser.parseExpression(expressionStr);
        }
        return null;
    }

    private double extractDoubleValue(Object targetValue, Expression expression) {
        if (targetValue == null) {
            return 0.0;
        }
        if (expression != null) {
            Number num = expression.getValue(targetValue, Number.class);
            return num != null ? num.doubleValue() : 0.0;
        }
        return targetValue instanceof Number num ? num.doubleValue() : 0.0;
    }

    /**
     * Registers the gauge via the {@link MetricsService}.
     *
     * @param gauge    the gauge annotation
     * @param bean     the object instance to monitor
     * @param function the function to extract the value
     */
    private void registerGauge(Gauge gauge, Object bean, ToDoubleFunction<Object> function) {
        try {
            StandardEvaluationContext context = SpelContextBuilder.buildContext(null, bean, null, beanResolver);
            metricsService.recordGauge(gauge, bean, function, context);
        } catch (Exception e) {
            log.error("Error registering gauge '{}'", gauge.name(), e);
        }
    }
}
