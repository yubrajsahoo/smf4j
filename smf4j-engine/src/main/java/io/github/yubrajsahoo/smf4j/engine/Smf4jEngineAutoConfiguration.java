/*
 * Copyright 2024 Yubraj Sahoo
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.yubrajsahoo.smf4j.engine;

import io.github.yubrajsahoo.smf4j.api.annotation.Counter;
import io.github.yubrajsahoo.smf4j.api.annotation.Timer;
import io.github.yubrajsahoo.smf4j.core.Smf4jCoreAutoConfiguration;
import io.github.yubrajsahoo.smf4j.core.factory.MeterFactory;
import io.github.yubrajsahoo.smf4j.core.logger.MetricsLogger;
import io.github.yubrajsahoo.smf4j.engine.aspect.CounterAspect;
import io.github.yubrajsahoo.smf4j.engine.aspect.TimerAspect;
import io.github.yubrajsahoo.smf4j.engine.processor.GaugeAnnotationProcessor;
import io.github.yubrajsahoo.smf4j.engine.service.impl.CounterMetricsService;
import io.github.yubrajsahoo.smf4j.engine.service.impl.GaugeMetricsService;
import io.github.yubrajsahoo.smf4j.engine.service.impl.TimerMetricsService;
import io.github.yubrajsahoo.smf4j.engine.spel.SpelEvaluator;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * Auto-configuration class for the SMF4J core module.
 * <p>
 * This class is responsible for automatically configuring the necessary beans,
 * aspects, and metric registries required for the Simple Metrics Facade for Java (SMF4J)
 * when used in a Spring Boot environment.
 * </p>
 */
@AutoConfiguration
@AutoConfigureAfter(Smf4jCoreAutoConfiguration.class) // <-- Use this instead
public class Smf4jEngineAutoConfiguration {
    /**
     * Create a {@link BeanResolver} bean to resolve bean for metrics.
     *
     * @param applicationContext the application context
     * @return the bean resolver
     */
    @Bean
    @ConditionalOnMissingBean(BeanResolver.class)
    public BeanResolver smf4jBeanResolver(ApplicationContext applicationContext) {
        return new BeanFactoryResolver(applicationContext);
    }

    /**
     * Registers a default Spring Expression Language {@link ExpressionParser} if none is found in the context.
     *
     * @return a {@link SpelExpressionParser} instance
     */
    @Bean
    @ConditionalOnMissingBean(ExpressionParser.class)
    public ExpressionParser expressionParser() {
        return new SpelExpressionParser();
    }

    /**
     * Creates a {@link SpelEvaluator} bean using the configured {@link ExpressionParser}.
     *
     * @param expressionParser the Spring expression parser used for SpEL compilation
     * @return a configured {@link SpelEvaluator} instance
     */
    @Bean
    @ConditionalOnMissingBean(SpelEvaluator.class)
    public SpelEvaluator spelEvaluator(ExpressionParser expressionParser) {
        return new SpelEvaluator(expressionParser);
    }

    /**
     * Creates a {@link CounterMetricsService} bean for evaluating metric tags and delegating recordings.
     *
     * @param spelEvaluator the SpEL evaluator for resolving dynamic metric tag expressions
     * @param meterFactory  the meter factory for retrieving specific meter services
     * @param metricsLogger the logger
     * @return a new {@link CounterMetricsService} instance
     */
    @Bean
    @ConditionalOnMissingBean(CounterMetricsService.class)
    public CounterMetricsService counterMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        return new CounterMetricsService(spelEvaluator, meterFactory, metricsLogger);
    }

    /**
     * Creates a {@link TimerMetricsService} bean for evaluating metric tags and delegating recordings.
     *
     * @param spelEvaluator the SpEL evaluator for resolving dynamic metric tag expressions
     * @param meterFactory  the meter factory for retrieving specific meter services
     * @param metricsLogger the logger
     * @return a new {@link TimerMetricsService} instance
     */
    @Bean
    @ConditionalOnMissingBean(TimerMetricsService.class)
    public TimerMetricsService timerMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        return new TimerMetricsService(spelEvaluator, meterFactory, metricsLogger);
    }

    /**
     * Creates a {@link GaugeMetricsService} bean for evaluating metric tags and delegating recordings.
     *
     * @param spelEvaluator the SpEL evaluator for resolving dynamic metric tag expressions
     * @param meterFactory  the meter factory for retrieving specific meter services
     * @param metricsLogger the logger
     * @return a new {@link GaugeMetricsService} instance
     */
    @Bean
    @ConditionalOnMissingBean(GaugeMetricsService.class)
    public GaugeMetricsService gaugeMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        return new GaugeMetricsService(spelEvaluator, meterFactory, metricsLogger);
    }

    /**
     * Creates a {@link CounterAspect} bean to intercept methods annotated with {@link Counter}.
     *
     * @param counterMetricsService the metrics service used to process intercepted metric events
     * @param beanResolver          the bean resolver for resolving Spring beans in SpEL expressions
     * @return a new {@link CounterAspect} instance
     */
    @Bean
    @ConditionalOnMissingBean(CounterAspect.class)
    public CounterAspect counterAspect(CounterMetricsService counterMetricsService, BeanResolver beanResolver) {
        return new CounterAspect(counterMetricsService, beanResolver);
    }

    /**
     * Creates a {@link TimerAspect} bean to intercept methods annotated with {@link Timer}.
     *
     * @param timerMetricsService the metrics service used to process intercepted metric events
     * @param beanResolver        the bean resolver for resolving Spring beans in SpEL expressions
     * @return a new {@link TimerAspect} instance
     */
    @Bean
    @ConditionalOnMissingBean(TimerAspect.class)
    public TimerAspect timerAspect(TimerMetricsService timerMetricsService, BeanResolver beanResolver) {
        return new TimerAspect(timerMetricsService, beanResolver);
    }

    /**
     * Creates a {@link GaugeAnnotationProcessor} bean.
     *
     * @param gaugeMetricsService the metrics service used to process gauge metrics
     * @param beanResolver        the bean resolver for resolving Spring beans in SpEL expressions
     * @return a new {@link GaugeAnnotationProcessor} instance
     */
    @Bean
    @ConditionalOnMissingBean(GaugeAnnotationProcessor.class)
    public GaugeAnnotationProcessor gaugeAnnotationProcessor(GaugeMetricsService gaugeMetricsService, BeanResolver beanResolver) {
        return new GaugeAnnotationProcessor(gaugeMetricsService, beanResolver);
    }
}
