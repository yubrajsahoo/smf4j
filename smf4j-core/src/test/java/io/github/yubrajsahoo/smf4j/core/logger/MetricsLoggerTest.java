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
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Smf4jCoreAutoConfiguration.class)
@DisplayName("MetricsLogger Unit Test")
class MetricsLoggerTest {
    @Autowired
    private MetricsLogger metricsLogger;
    private ListAppender<ILoggingEvent> listAppender;


    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(DefaultMetricsLogger.class);

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
    @DisplayName("Test case for log with default log message")
    void testLog_default() {
        CounterMetrics metrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );

        metricsLogger.log(metrics);

        String formattedMessage = listAppender.list.get(0).getFormattedMessage();

        String expectedMessage = "Metrics Logs For With->name=test.counter->" +
                "env=test->region=us-east->description=A test counter->increment=5";
        assertEquals(expectedMessage, formattedMessage);
    }

    @Test
    @DisplayName("Test case for log with disable log message")
    void testLog_disable() {
        CounterMetrics metrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );
        metrics.setEnable(false);

        metricsLogger.log(metrics);

        String formattedMessage = listAppender.list.get(0).getFormattedMessage();

        String expectedMessage = "Metrics Disabled For->name=test.counter->" +
                "env=test->region=us-east->description=A test counter->increment=5";
        assertEquals(expectedMessage, formattedMessage);
    }
}