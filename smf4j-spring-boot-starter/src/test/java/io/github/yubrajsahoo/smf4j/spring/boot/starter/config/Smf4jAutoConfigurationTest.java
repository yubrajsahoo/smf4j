package io.github.yubrajsahoo.smf4j.spring.boot.starter.config;

import io.github.yubrajsahoo.smf4j.api.config.Smf4jMetricsProperties;
import io.github.yubrajsahoo.smf4j.core.factory.MeterFactory;
import io.github.yubrajsahoo.smf4j.core.logger.MetricsLogger;
import io.github.yubrajsahoo.smf4j.core.logger.impl.DefaultMetricsLogger;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Smf4jAutoConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class Smf4jAutoConfigurationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void shouldRegisterDefaultBeans() {
        assertThat(context.getBean(Smf4jMetricsProperties.class)).isNotNull();
        assertThat(context.getBean(MetricsLogger.class)).isInstanceOf(DefaultMetricsLogger.class);
        assertThat(context.getBean(MeterRegistry.class)).isInstanceOf(SimpleMeterRegistry.class);
        assertThat(context.getBean(MeterFactory.class)).isNotNull();
        assertThat(context.getBean(CounterMeterService.class)).isNotNull();
        assertThat(context.getBean(GaugeMeterService.class)).isNotNull();
        assertThat(context.getBean(TimerMeterService.class)).isNotNull();
        assertThat(context.getBean(BeanResolver.class)).isNotNull();
        assertThat(context.getBean(ExpressionParser.class)).isNotNull();
        assertThat(context.getBean(SpelEvaluator.class)).isNotNull();
        assertThat(context.getBean(CounterMetricsService.class)).isNotNull();
        assertThat(context.getBean(TimerMetricsService.class)).isNotNull();
        assertThat(context.getBean(GaugeMetricsService.class)).isNotNull();
        assertThat(context.getBean(CounterAspect.class)).isNotNull();
        assertThat(context.getBean(TimerAspect.class)).isNotNull();
        assertThat(context.getBean(GaugeAnnotationProcessor.class)).isNotNull();
        assertThat(context.getBean(LogMetrics.class)).isNotNull();
    }
}
