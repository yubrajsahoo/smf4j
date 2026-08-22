package io.github.yubrajsahoo.smf4jcore.utils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.helper.DataBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

public class MetricsLoggerTest {

    @Test
    @DisplayName("Test Case for Prepare Metrics Log")
    void testPrepareLog() {

        CounterMetrics metrics = DataBuilder.fromFile(
                "src/test/resources/json/counter-metrics.json",
                CounterMetrics.class
        );

        String logMessage = MetricsLogger.prepareLog(
                "Test Metrics Log is : ",
                metrics
        );

        Assertions.assertEquals(
                "Test Metrics Log is : name=orders.created->description=Counts the number of orders created",
                logMessage
        );
    }

    @Test
    @DisplayName("Test Case for Log Counter")
    void testLog_Counter() {

        CounterMetrics metrics = DataBuilder.fromFile(
                "src/test/resources/json/counter-metrics.json",
                CounterMetrics.class
        );

        Logger logger =
                (Logger) LoggerFactory.getLogger(MetricsLogger.class);

        ListAppender<ILoggingEvent> appender =
                new ListAppender<>();

        appender.start();
        logger.addAppender(appender);

        MetricsLogger.log(metrics);

        Assertions.assertEquals(1, appender.list.size());

        String logMessage =
                appender.list.get(0).getFormattedMessage();

        Assertions.assertTrue(
                logMessage.contains("name=orders.created")
        );

        Assertions.assertTrue(
                logMessage.contains(
                        "description=Counts the number of orders created"
                )
        );

        Assertions.assertTrue(
                logMessage.contains(
                        "increment=" + metrics.getIncrement()
                )
        );

        logger.detachAppender(appender);
    }
}