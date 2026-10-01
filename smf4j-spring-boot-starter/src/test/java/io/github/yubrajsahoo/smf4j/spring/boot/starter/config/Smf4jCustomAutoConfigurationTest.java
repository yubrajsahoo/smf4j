package io.github.yubrajsahoo.smf4j.spring.boot.starter.config;

import io.github.yubrajsahoo.smf4j.api.domain.Metrics;
import io.github.yubrajsahoo.smf4j.api.enums.LogLevel;
import io.github.yubrajsahoo.smf4j.core.logger.MetricsLogger;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {Smf4jCustomAutoConfigurationTest.CustomConfiguration.class, Smf4jAutoConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class Smf4jCustomAutoConfigurationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void shouldBackOffWhenCustomBeansAreProvided() {
        assertThat(context.getBean(MetricsLogger.class)).isInstanceOf(CustomMetricsLogger.class);
        assertThat(context.getBean(MeterRegistry.class)).isInstanceOf(CustomMeterRegistry.class);
    }

    @SuppressWarnings("all")
    static class CustomMetricsLogger extends MetricsLogger {
        @Override
        public void log(Metrics metrics) {}
        @Override
        public void log(Metrics metrics, LogLevel level) {}
    }

    static class CustomMeterRegistry extends SimpleMeterRegistry {
    }

    @Configuration
    static class CustomConfiguration {

        @Bean
        public MetricsLogger customMetricsLogger() {
            return new CustomMetricsLogger();
        }

        @Bean
        public MeterRegistry customMeterRegistry() {
            return new CustomMeterRegistry();
        }
    }
}
