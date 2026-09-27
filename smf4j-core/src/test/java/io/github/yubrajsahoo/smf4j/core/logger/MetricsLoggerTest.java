package io.github.yubrajsahoo.smf4j.core.logger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import helper.JsonConverter;
import io.github.yubrajsahoo.smf4j.api.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4j.api.domain.Metrics;
import io.github.yubrajsahoo.smf4j.api.config.Smf4jMetricsProperties;
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
    @DisplayName("Test case for log with default log message")
    void testLog_default() {
        CounterMetrics metrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );

        metricsLogger.log(metrics);

        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(ch.qos.logback.classic.Level.INFO, event.getLevel());
        String expectedMessage = "Metrics Logs For With->name=test.counter->" +
                "env=test->region=us-east->description=A test counter->increment=5";
        assertEquals(expectedMessage, event.getFormattedMessage());
    }

    @Test
    @DisplayName("Test case for log with disable log message")
    void testLog_disable() {
        CounterMetrics metrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );
        metrics.setEnable(false);

        metricsLogger.log(metrics);

        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(ch.qos.logback.classic.Level.INFO, event.getLevel());
        String expectedMessage = "Metrics Disabled For->name=test.counter->" +
                "env=test->region=us-east->description=A test counter->increment=5";
        assertEquals(expectedMessage, event.getFormattedMessage());
    }

    @Test
    @DisplayName("Test case for log with DEBUG level")
    void testLog_debug() {
        CounterMetrics metrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );

        Smf4jMetricsProperties properties = new Smf4jMetricsProperties();
        properties.setLogLevel("DEBUG");
        properties.setLogMessage("");
        MetricsLogger customLogger = new DefaultMetricsLogger(properties);
        customLogger.log(metrics);

        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(ch.qos.logback.classic.Level.DEBUG, event.getLevel());
    }

    @Test
    @DisplayName("Test case for log with WARN level")
    void testLog_warn() {
        CounterMetrics metrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );

        Smf4jMetricsProperties properties = new Smf4jMetricsProperties();
        properties.setLogLevel("WARN");
        MetricsLogger customLogger = new DefaultMetricsLogger(properties);
        customLogger.log(metrics);

        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(ch.qos.logback.classic.Level.WARN, event.getLevel());
    }

    @Test
    @DisplayName("Test case for log with ERROR level")
    void testLog_error() {
        CounterMetrics metrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );

        Smf4jMetricsProperties properties = new Smf4jMetricsProperties();
        properties.setLogLevel("ERROR");
        MetricsLogger customLogger = new DefaultMetricsLogger(properties);
        customLogger.log(metrics);

        ILoggingEvent event = listAppender.list.get(0);
        assertEquals(ch.qos.logback.classic.Level.ERROR, event.getLevel());
    }

    @Test
    @DisplayName("Test case for log with DISABLED level")
    void testLog_disabled() {
        CounterMetrics metrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );

        Smf4jMetricsProperties properties = new Smf4jMetricsProperties();
        properties.setLogLevel("DISABLED");
        MetricsLogger customLogger = new DefaultMetricsLogger(properties);
        
        int initialSize = listAppender.list.size();
        customLogger.log(metrics);

        assertEquals(initialSize, listAppender.list.size(), "No logs should be appended when DISABLED");
    }

    @Test
    @DisplayName("Test case for prepareLog")
    void testPrepareLog() {
        CounterMetrics metrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );

        MetricsLogger abstractLogger = new MetricsLogger() {
            @Override
            public void log(Metrics m) {
                // Not used
            }

            @Override
            public void log(Metrics metrics, io.github.yubrajsahoo.smf4j.api.enums.LogLevel level) {
                // Not used
            }
        };

        String logOutput = abstractLogger.prepareLog("Prefix->", metrics);

        String expectedLogOutput = "Prefix->name=test.counter->env=test->region=us-east->description=A test counter";
        assertEquals(expectedLogOutput, logOutput);
    }

    @Test
    @DisplayName("Test case for prepareTagsLog")
    void testPrepareTagsLog() {
        CounterMetrics metrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );

        MetricsLogger abstractLogger = new MetricsLogger() {
            @Override
            public void log(Metrics m) {
                // Not used
            }

            @Override
            public void log(Metrics metrics, io.github.yubrajsahoo.smf4j.api.enums.LogLevel level) {
                // Not used
            }
        };

        String tagsOutput = abstractLogger.prepareTagsLog(metrics);

        String expectedTagsOutput = "->env=test->region=us-east";
        assertEquals(expectedTagsOutput, tagsOutput);
    }
}