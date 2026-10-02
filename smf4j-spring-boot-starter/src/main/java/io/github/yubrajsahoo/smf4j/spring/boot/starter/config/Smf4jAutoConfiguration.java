package io.github.yubrajsahoo.smf4j.spring.boot.starter.config;

import io.github.yubrajsahoo.smf4j.api.config.Smf4JProperties;
import io.github.yubrajsahoo.smf4j.core.factory.MeterFactory;
import io.github.yubrajsahoo.smf4j.core.logger.MetricsLogger;
import io.github.yubrajsahoo.smf4j.core.logger.impl.DefaultMetricsLogger;
import io.github.yubrajsahoo.smf4j.core.service.MeterService;
import io.github.yubrajsahoo.smf4j.core.service.impl.CounterMeterService;
import io.github.yubrajsahoo.smf4j.core.service.impl.GaugeMeterService;
import io.github.yubrajsahoo.smf4j.core.service.impl.TimerMeterService;
import io.github.yubrajsahoo.smf4j.engine.aspect.CounterAspect;
import io.github.yubrajsahoo.smf4j.engine.aspect.TimerAspect;
import io.github.yubrajsahoo.smf4j.engine.processor.GaugeAnnotationProcessor;
import io.github.yubrajsahoo.smf4j.engine.service.impl.CounterMetricsService;
import io.github.yubrajsahoo.smf4j.engine.service.impl.GaugeMetricsService;
import io.github.yubrajsahoo.smf4j.engine.service.impl.TimerMetricsService;
import io.github.yubrajsahoo.smf4j.engine.spel.SpelEvaluator;
import io.github.yubrajsahoo.smf4j.engine.utils.LogMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@AutoConfiguration
@EnableConfigurationProperties
@ConditionalOnProperty(prefix = "smf4j", name = "enabled", havingValue = "true", matchIfMissing = true)
/**
 * Auto-configuration for SMF4J.
 */
public class Smf4jAutoConfiguration {

    /**
     * Default constructor.
     */
    public Smf4jAutoConfiguration() {}

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConfigurationProperties(prefix = "smf4j")
    public Smf4JProperties smf4JProperties() {
        return new Smf4JProperties();
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(MetricsLogger.class)
    public MetricsLogger metricsLogger(Smf4JProperties smf4JProperties) {
       return new DefaultMetricsLogger(smf4JProperties.getLoggerConfig());
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(MeterRegistry.class)
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(MeterFactory.class)
    public MeterFactory meterFactory(List<MeterService> meterServices) {
        return new MeterFactory(meterServices);
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(CounterMeterService.class)
    public CounterMeterService counterMeterService(MeterRegistry meterRegistry) {
        return new CounterMeterService(meterRegistry);
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(GaugeMeterService.class)
    public GaugeMeterService gaugeMeterService(MeterRegistry meterRegistry) {
        return new GaugeMeterService(meterRegistry);
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(TimerMeterService.class)
    public TimerMeterService timerMeterService(MeterRegistry meterRegistry) {
        return new TimerMeterService(meterRegistry);
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(BeanResolver.class)
    public BeanResolver smf4jBeanResolver(ApplicationContext applicationContext) {
        return new BeanFactoryResolver(applicationContext);
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(ExpressionParser.class)
    public ExpressionParser expressionParser() {
        return new SpelExpressionParser();
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(SpelEvaluator.class)
    public SpelEvaluator spelEvaluator(ExpressionParser expressionParser) {
        return new SpelEvaluator(expressionParser);
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(CounterMetricsService.class)
    public CounterMetricsService counterMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        return new CounterMetricsService(spelEvaluator, meterFactory, metricsLogger);
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(TimerMetricsService.class)
    public TimerMetricsService timerMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        return new TimerMetricsService(spelEvaluator, meterFactory, metricsLogger);
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(GaugeMetricsService.class)
    public GaugeMetricsService gaugeMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        return new GaugeMetricsService(spelEvaluator, meterFactory, metricsLogger);
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(CounterAspect.class)
    public CounterAspect counterAspect(CounterMetricsService counterMetricsService, BeanResolver beanResolver) {
        return new CounterAspect(counterMetricsService, beanResolver);
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(TimerAspect.class)
    public TimerAspect timerAspect(TimerMetricsService timerMetricsService, BeanResolver beanResolver) {
        return new TimerAspect(timerMetricsService, beanResolver);
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @ConditionalOnMissingBean(GaugeAnnotationProcessor.class)
    public GaugeAnnotationProcessor gaugeAnnotationProcessor(GaugeMetricsService gaugeMetricsService, BeanResolver beanResolver) {
        return new GaugeAnnotationProcessor(gaugeMetricsService, beanResolver);
    }

    /**
     * Configures bean.
     * @return bean instance
     */
    @Bean
    @SuppressWarnings("all")
    public LogMetrics logMetrics(CounterMetricsService counterMetricsService, Smf4JProperties smf4JProperties){
        return new LogMetrics(counterMetricsService, smf4JProperties.getLogMetrics());
    }
}
