package io.github.yubrajsahoo.smf4jcore.logger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Smf4jAutoConfiguration.class)
@DisplayName("MetricsLogger Unit Test")
class MetricsLoggerTest {
    @Autowired
    private MetricsLogger metricsLogger;
    private ListAppender<ILoggingEvent> listAppender;


    @BeforeEach
    void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(MetricsLogger.class);

        listAppender = new ListAppender<>();
        listAppender.start();

        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(MetricsLogger.class);
        logger.detachAppender(listAppender);
    }

    @Test
    void testLog(){

    }
}