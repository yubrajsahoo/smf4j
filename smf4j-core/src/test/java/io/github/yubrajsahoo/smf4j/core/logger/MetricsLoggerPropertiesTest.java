package io.github.yubrajsahoo.smf4j.core.logger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import helper.JsonConverter;
import io.github.yubrajsahoo.smf4j.api.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4j.core.Smf4jCoreAutoConfiguration;
import io.github.yubrajsahoo.smf4j.core.logger.impl.DefaultMetricsLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        classes = Smf4jCoreAutoConfiguration.class,
        properties = {
                "smf4j.metrics.logLevel=WARN",
                "smf4j.metrics.logMessage=CustomPropLog->",
                "smf4j.metrics.disableLogLevel=DEBUG",
                "smf4j.metrics.disableLogMessage=CustomPropDisabledLog->"
        }
)
@EnableConfigurationProperties
@DisplayName("MetricsLogger With Properties Unit Test")
class MetricsLoggerPropertiesTest {

    @Autowired
    private MetricsLogger metricsLogger;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(DefaultMetricsLogger.class);
        logger.setLevel(ch.qos.logback.classic.Level.ALL);

        listAppender = new ListAppender<>();
        listAppender.start();

        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(DefaultMetricsLogger.class);
        logger.detachAppender(listAppender);
        listAppender.clearAllFilters();
    }

    @Test
    @DisplayName("Should use log level and message from properties file when metrics are enabled")
    void testLogWithProperties() {
        CounterMetrics metrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );
        metrics.setEnable(true);

        metricsLogger.log(metrics);

        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(ch.qos.logback.classic.Level.WARN, event.getLevel());
        
        String expectedMessage = "CustomPropLog->name=test.counter->" +
                "env=test->region=us-east->description=A test counter->increment=5";
        assertEquals(expectedMessage, event.getFormattedMessage());
    }

    @Test
    @DisplayName("Should use disable log level and message from properties file when metrics are disabled")
    void testDisableLogWithProperties() {
        CounterMetrics metrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );
        metrics.setEnable(false);

        metricsLogger.log(metrics);

        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(ch.qos.logback.classic.Level.DEBUG, event.getLevel());
        
        String expectedMessage = "CustomPropDisabledLog->name=test.counter->" +
                "env=test->region=us-east->description=A test counter->increment=5";
        assertEquals(expectedMessage, event.getFormattedMessage());
    }
}
