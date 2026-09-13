package io.github.yubrajsahoo.smf4jcore.aspect;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import helper.JsonConverter;
import io.github.yubrajsahoo.smf4jcore.annotation.Counter;
import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import io.github.yubrajsahoo.smf4jcore.logger.impl.DefaultMetricsLogger;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;

@SpringBootTest(classes = Smf4jAutoConfiguration.class)
class CounterAspectTest {

    @Autowired
    private CounterAspect counterAspect;

    @Autowired
    private MeterRegistry meterRegistry;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        meterRegistry.clear();
        Mockito.reset(joinPoint, methodSignature);
        lenient().when(joinPoint.getSignature()).thenReturn(methodSignature);

        //verify log for DefaultMetricsLogger
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
    @DisplayName("Should Capture Return and Record Metrics")
    void testCaptureReturn() {
        Counter counter = JsonConverter.read(
                "src/test/resources/json/counter-enabled.json", Counter.class
        );

        Object result = "GET";
        counterAspect.captureReturn(joinPoint, counter, result);

        //should store metrics
        io.micrometer.core.instrument.Counter savedCounter = meterRegistry.find("http.requests.total")
                .counter();

        assertNotNull(savedCounter);
        assertEquals(3.0, savedCounter.count());

        io.micrometer.core.instrument.Meter.Id id = savedCounter.getId();
        assertEquals("http.requests.total", id.getName());
        assertEquals("Total incoming HTTP requests", id.getDescription());
        assertEquals("GET", id.getTag("method"));
        assertEquals("SUCCESS", id.getTag("outcome"));
    }

    @Test
    @DisplayName("Should Capture Exception and Record Metrics")
    void testCaptureException() {
        Counter counter = JsonConverter.read(
                "src/test/resources/json/counter-enabled.json", Counter.class
        );

        Throwable exception = new RuntimeException("Test Exception");
        counterAspect.captureException(joinPoint, counter, exception);

        //should store metrics
        io.micrometer.core.instrument.Counter savedCounter = meterRegistry.find("http.requests.total")
                .counter();

        assertNotNull(savedCounter);
        assertEquals(3.0, savedCounter.count());

        io.micrometer.core.instrument.Meter.Id id = savedCounter.getId();
        assertEquals("http.requests.total", id.getName());
        assertEquals("Total incoming HTTP requests", id.getDescription());
        assertEquals("none", id.getTag("method"));
        assertEquals("FAILURE", id.getTag("outcome"));
    }
}
