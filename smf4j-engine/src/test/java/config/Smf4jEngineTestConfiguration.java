package config;

import io.github.yubrajsahoo.smf4j.core.factory.MeterFactory;
import io.github.yubrajsahoo.smf4j.core.logger.MetricsLogger;
import io.github.yubrajsahoo.smf4j.engine.aspect.CounterAspect;
import io.github.yubrajsahoo.smf4j.engine.aspect.TimerAspect;
import io.github.yubrajsahoo.smf4j.engine.processor.GaugeAnnotationProcessor;
import io.github.yubrajsahoo.smf4j.engine.service.impl.CounterMetricsService;
import io.github.yubrajsahoo.smf4j.engine.service.impl.GaugeMetricsService;
import io.github.yubrajsahoo.smf4j.engine.service.impl.TimerMetricsService;
import io.github.yubrajsahoo.smf4j.engine.spel.SpelEvaluator;
import io.github.yubrajsahoo.smf4j.engine.utils.LogMetrics;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Configuration
@Import(Smf4jCoreTestConfiguration.class)
public class Smf4jEngineTestConfiguration {
    @Bean
    @ConditionalOnMissingBean(BeanResolver.class)
    public BeanResolver smf4jBeanResolver(ApplicationContext applicationContext) {
        return new BeanFactoryResolver(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean(ExpressionParser.class)
    public ExpressionParser expressionParser() {
        return new SpelExpressionParser();
    }

    @Bean
    @ConditionalOnMissingBean(SpelEvaluator.class)
    public SpelEvaluator spelEvaluator(ExpressionParser expressionParser) {
        return new SpelEvaluator(expressionParser);
    }

    @Bean
    @ConditionalOnMissingBean(CounterMetricsService.class)
    public CounterMetricsService counterMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        return new CounterMetricsService(spelEvaluator, meterFactory, metricsLogger);
    }

    @Bean
    @ConditionalOnMissingBean(TimerMetricsService.class)
    public TimerMetricsService timerMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        return new TimerMetricsService(spelEvaluator, meterFactory, metricsLogger);
    }

    @Bean
    @ConditionalOnMissingBean(GaugeMetricsService.class)
    public GaugeMetricsService gaugeMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        return new GaugeMetricsService(spelEvaluator, meterFactory, metricsLogger);
    }

    @Bean
    @ConditionalOnMissingBean(CounterAspect.class)
    public CounterAspect counterAspect(CounterMetricsService counterMetricsService, BeanResolver beanResolver) {
        return new CounterAspect(counterMetricsService, beanResolver);
    }

    @Bean
    @ConditionalOnMissingBean(TimerAspect.class)
    public TimerAspect timerAspect(TimerMetricsService timerMetricsService, BeanResolver beanResolver) {
        return new TimerAspect(timerMetricsService, beanResolver);
    }

    @Bean
    @ConditionalOnMissingBean(GaugeAnnotationProcessor.class)
    public GaugeAnnotationProcessor gaugeAnnotationProcessor(GaugeMetricsService gaugeMetricsService, BeanResolver beanResolver) {
        return new GaugeAnnotationProcessor(gaugeMetricsService, beanResolver);
    }

    @Bean
    @SuppressWarnings("all")
    public LogMetrics logMetrics(CounterMetricsService counterMetricsService){
        return new LogMetrics(counterMetricsService);
    }
}
