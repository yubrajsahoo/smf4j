package io.github.yubrajsahoo.smf4jcore.counter;

import io.github.yubrajsahoo.smf4jcore.core.logger.impl.DefaultMetricsLogger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import helper.JsonConverter;
import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import io.github.yubrajsahoo.smf4jcore.counter.annotation.Counter;
import io.github.yubrajsahoo.smf4jcore.counter.aspect.CounterAspect;
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
import static org.mockito.Mockito.when;

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

        //verify log for DefaultMetricsLogger and CounterAspect
        Logger logger = (Logger) LoggerFactory.getLogger(DefaultMetricsLogger.class);
        Logger aspectLogger = (Logger) LoggerFactory.getLogger(CounterAspect.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        aspectLogger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(DefaultMetricsLogger.class);
        Logger aspectLogger = (Logger) LoggerFactory.getLogger(CounterAspect.class);
        logger.detachAppender(listAppender);
        aspectLogger.detachAppender(listAppender);
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

    @Test
    @DisplayName("Should Log Error on Exception in captureReturn")
    void testCaptureReturnException() {
        Counter counter = JsonConverter.read(
                "src/test/resources/json/counter-enabled.json", Counter.class
        );

        when(joinPoint.getSignature()).thenThrow(new RuntimeException("Test Exception"));

        counterAspect.captureReturn(joinPoint, counter, "GET");

        assertEquals(1, listAppender.list.size());
        assertEquals("Error while capturing Counter Metrics from Return: Test Exception", listAppender.list.get(0).getFormattedMessage());
        assertEquals("Test Exception", listAppender.list.get(0).getThrowableProxy().getMessage());
    }

    @Test
    @DisplayName("Should Log Error on Exception in captureException")
    void testCaptureExceptionException() {
        Counter counter = JsonConverter.read(
                "src/test/resources/json/counter-enabled.json", Counter.class
        );

        when(joinPoint.getSignature()).thenThrow(new RuntimeException("Test Exception"));

        Throwable exception = new RuntimeException("Original Exception");
        counterAspect.captureException(joinPoint, counter, exception);

        assertEquals(1, listAppender.list.size());
        assertEquals("Error while capturing Counter Metrics from Exception: Test Exception", listAppender.list.get(0).getFormattedMessage());
        assertEquals("Test Exception", listAppender.list.get(0).getThrowableProxy().getMessage());
    }
}
