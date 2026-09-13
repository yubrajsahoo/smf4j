package io.github.yubrajsahoo.smf4jcore.logger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import helper.JsonConverter;
import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.logger.impl.DefaultMetricsLogger;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Smf4jAutoConfiguration.class)
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
        CounterMetrics metrics = JsonConverter.read(
                "src/test/resources/json/counter-metrics.json", CounterMetrics.class
        );

        metricsLogger.log(metrics);

        String formattedMessage = listAppender.list.get(0).getFormattedMessage();

        String expectedMessage = "Metrics Logs For With->name=http.requests.total->" +
                "method=GET->outcome=SUCCESS->description=Total incoming HTTP requests->increment=3";
        assertEquals(expectedMessage, formattedMessage);
    }

    @Test
    @DisplayName("Test case for log with disable log message")
    void testLog_disable() {
        CounterMetrics metrics = JsonConverter.read(
                "src/test/resources/json/counter-metrics.json", CounterMetrics.class
        );
        metrics.setEnabled(false);

        metricsLogger.log(metrics);

        String formattedMessage = listAppender.list.get(0).getFormattedMessage();

        String expectedMessage = "Metrics Disabled For->name=http.requests.total->" +
                "method=GET->outcome=SUCCESS->description=Total incoming HTTP requests->increment=3";
        assertEquals(expectedMessage, formattedMessage);
    }
}