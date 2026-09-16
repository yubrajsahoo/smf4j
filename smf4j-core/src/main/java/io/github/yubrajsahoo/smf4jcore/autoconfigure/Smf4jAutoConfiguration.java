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

package io.github.yubrajsahoo.smf4jcore.autoconfigure;

import io.github.yubrajsahoo.smf4jcore.aspect.CounterAspect;
import io.github.yubrajsahoo.smf4jcore.aspect.TimerAspect;
import io.github.yubrajsahoo.smf4jcore.factory.MeterFactory;
import io.github.yubrajsahoo.smf4jcore.meter.service.MeterService;
import io.github.yubrajsahoo.smf4jcore.meter.service.impl.TimerMeterService;
import io.github.yubrajsahoo.smf4jcore.meter.service.impl.CounterMeterService;
import io.github.yubrajsahoo.smf4jcore.service.MetricsService;
import io.github.yubrajsahoo.smf4jcore.service.impl.MetricsServiceImpl;
import io.github.yubrajsahoo.smf4jcore.logger.MetricsLogger;
import io.github.yubrajsahoo.smf4jcore.logger.impl.DefaultMetricsLogger;
import io.github.yubrajsahoo.smf4jcore.spel.SpelContextBuilder;
import io.github.yubrajsahoo.smf4jcore.spel.SpelEvaluator;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.List;

/**
 * Spring Boot auto-configuration class for SMF4J (Simple Metrics Facade for Java).
 * <p>
 * Configures and registers all foundational beans required for metric interception,
 * SpEL expression evaluation, and metric registry integration:
 * </p>
 * <ul>
 *   <li>{@link MeterRegistry} (falls back to {@link SimpleMeterRegistry})</li>
 *   <li>{@link ExpressionParser} (defaults to {@link SpelExpressionParser})</li>
 *   <li>{@link SpelEvaluator}</li>
 *   <li>{@link CounterMeterService}</li>
 *   <li>{@link TimerMeterService}</li>
 *   <li>{@link MeterFactory}</li>
 *   <li>{@link MetricsLogger}</li>
 *   <li>{@link MetricsService} (backed by {@link MetricsServiceImpl})</li>
 *   <li>{@link SpelContextBuilder}</li>
 *   <li>{@link CounterAspect}</li>
 * </ul>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
@AutoConfiguration
@EnableAspectJAutoProxy
public class Smf4jAutoConfiguration {

    /**
     * Registers a fallback {@link SimpleMeterRegistry} if no {@link MeterRegistry} bean is currently present in the application context.
     *
     * @return a default {@link SimpleMeterRegistry} instance
     */
    @Bean
    @ConditionalOnMissingBean(MeterRegistry.class)
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
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
     * Creates a {@link CounterMeterService} bean if none is defined.
     *
     * @param meterRegistry the Micrometer meter registry
     * @return a new {@link CounterMeterService} instance
     */
    @Bean
    @ConditionalOnMissingBean(CounterMeterService.class)
    public CounterMeterService counterMeterService(MeterRegistry meterRegistry) {
        return new CounterMeterService(meterRegistry);
    }

    /**
     * Creates a {@link TimerMeterService} bean if none is defined.
     *
     * @param meterRegistry the Micrometer meter registry
     * @return a new {@link TimerMeterService} instance
     */
    @Bean
    @ConditionalOnMissingBean(TimerMeterService.class)
    public TimerMeterService timerMeterService(MeterRegistry meterRegistry) {
        return new TimerMeterService(meterRegistry);
    }

    /**
     * Creates a {@link io.github.yubrajsahoo.smf4jcore.meter.service.impl.GaugeMeterService} bean if none is defined.
     *
     * @param meterRegistry the Micrometer meter registry
     * @return a new {@link io.github.yubrajsahoo.smf4jcore.meter.service.impl.GaugeMeterService} instance
     */
    @Bean
    @ConditionalOnMissingBean(io.github.yubrajsahoo.smf4jcore.meter.service.impl.GaugeMeterService.class)
    public io.github.yubrajsahoo.smf4jcore.meter.service.impl.GaugeMeterService gaugeMeterService(MeterRegistry meterRegistry) {
        return new io.github.yubrajsahoo.smf4jcore.meter.service.impl.GaugeMeterService(meterRegistry);
    }

    /**
     * Creates a {@link MeterFactory} bean by aggregating all available {@link MeterService} implementations.
     *
     * @param meterServices the list of registered {@link MeterService} beans
     * @return a new {@link MeterFactory} instance
     */
    @Bean
    @ConditionalOnMissingBean(MeterFactory.class)
    public MeterFactory meterFactory(List<MeterService> meterServices) {
        return new MeterFactory(meterServices);
    }

    /**
     * Creates a {@link MetricsLogger} bean if none is defined.
     *
     * @return a new {@link DefaultMetricsLogger} instance
     */
    @Bean
    @ConditionalOnMissingBean(MetricsLogger.class)
    public MetricsLogger metricsLogger() {
        return new DefaultMetricsLogger();
    }

    /**
     * Creates a {@link MetricsService} bean for evaluating metric tags and delegating recordings.
     *
     * @param spelEvaluator the SpEL evaluator for resolving dynamic metric tag expressions
     * @param meterFactory  the meter factory for retrieving specific meter services
     * @return a new {@link MetricsServiceImpl} instance
     */
    @Bean
    @ConditionalOnMissingBean(MetricsService.class)
    public MetricsService metricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        return new MetricsServiceImpl(spelEvaluator, meterFactory, metricsLogger);
    }

    /**
     * Creates a {@link CounterAspect} bean to intercept methods annotated with {@link io.github.yubrajsahoo.smf4jcore.annotation.Counter}.
     *
     * @param metricsService the metrics service used to process intercepted metric events
     * @return a new {@link CounterAspect} instance
     */
    @Bean
    @ConditionalOnMissingBean(CounterAspect.class)
    public CounterAspect counterAspect(MetricsService metricsService, BeanResolver beanResolver) {
        return new CounterAspect(metricsService, beanResolver);
    }

    /**
     * Creates a {@link TimerAspect} bean to intercept methods annotated with {@link io.github.yubrajsahoo.smf4jcore.annotation.Timer}.
     *
     * @param metricsService the metrics service used to process intercepted metric events
     * @param beanResolver   the bean resolver for resolving Spring beans in SpEL expressions
     * @return a new {@link TimerAspect} instance
     */
    @Bean
    @ConditionalOnMissingBean(TimerAspect.class)
    public TimerAspect timerAspect(MetricsService metricsService, BeanResolver beanResolver) {
        return new TimerAspect(metricsService, beanResolver);
    }

    /**
     * Creates a {@link io.github.yubrajsahoo.smf4jcore.processor.GaugeAnnotationProcessor} bean.
     *
     * @param metricsService the metrics service used to process gauge metrics
     * @param beanResolver   the bean resolver for resolving Spring beans in SpEL expressions
     * @return a new {@link io.github.yubrajsahoo.smf4jcore.processor.GaugeAnnotationProcessor} instance
     */
    @Bean
    @ConditionalOnMissingBean(io.github.yubrajsahoo.smf4jcore.processor.GaugeAnnotationProcessor.class)
    public io.github.yubrajsahoo.smf4jcore.processor.GaugeAnnotationProcessor gaugeAnnotationProcessor(MetricsService metricsService, BeanResolver beanResolver) {
        return new io.github.yubrajsahoo.smf4jcore.processor.GaugeAnnotationProcessor(metricsService, beanResolver);
    }

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
}