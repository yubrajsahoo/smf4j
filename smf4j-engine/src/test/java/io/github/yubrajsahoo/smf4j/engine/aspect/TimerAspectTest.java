package io.github.yubrajsahoo.smf4j.engine.aspect;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.yubrajsahoo.smf4j.api.annotation.Tags;
import io.github.yubrajsahoo.smf4j.api.annotation.Timer;
import io.github.yubrajsahoo.smf4j.core.logger.impl.DefaultMetricsLogger;
import io.github.yubrajsahoo.smf4j.engine.Smf4jEngineTestAutoConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
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

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = Smf4jEngineTestAutoConfiguration.class)
class TimerAspectTest {

    @Autowired
    private TimerAspect timerAspect;

    @Autowired
    private MeterRegistry meterRegistry;

    @Mock
    private ProceedingJoinPoint joinPoint;

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

    @Timer(
            name = "http.requests.total",
            description = "Total incoming HTTP requests",
            enable = true,
            tags = {
                    @Tags(key = "method", value = "#result"),
                    @Tags(key = "outcome", value = "#error == null ? 'SUCCESS' : 'FAILURE'")
            }
    )
    @SuppressWarnings("java:S1186")
    private void dummyTimerMethod() {}

    @Test
    @DisplayName("Should Execute Around Timer Successfully and Record Metrics")
    void testAroundTimer_Success() throws Throwable {
        Timer timer = this.getClass().getDeclaredMethod("dummyTimerMethod").getAnnotation(Timer.class);

        when(joinPoint.proceed()).thenReturn("GET");

        Object result = timerAspect.aroundTimer(joinPoint, timer);
        assertEquals("GET", result);

        //should store metrics
        io.micrometer.core.instrument.Timer savedTimer = meterRegistry.find("http.requests.total")
                .timer();

        assertNotNull(savedTimer);
        assertEquals(1L, savedTimer.count());
        assertTrue(savedTimer.totalTime(TimeUnit.MILLISECONDS) >= 0);

        io.micrometer.core.instrument.Meter.Id id = savedTimer.getId();
        assertEquals("http.requests.total", id.getName());
        assertEquals("Total incoming HTTP requests", id.getDescription());
        assertEquals("GET", id.getTag("method"));
        assertEquals("SUCCESS", id.getTag("outcome"));
    }

    @Test
    @DisplayName("Should Execute Around Timer When Target Throws Exception and Record Metrics")
    void testAroundTimer_Exception() throws Throwable {
        Timer timer = this.getClass().getDeclaredMethod("dummyTimerMethod").getAnnotation(Timer.class);

        Throwable exception = new RuntimeException("Test Exception");
        when(joinPoint.proceed()).thenThrow(exception);

        Throwable thrown = assertThrows(RuntimeException.class, () -> timerAspect.aroundTimer(joinPoint, timer));
        assertEquals("Test Exception", thrown.getMessage());

        //should store metrics
        io.micrometer.core.instrument.Timer savedTimer = meterRegistry.find("http.requests.total")
                .timer();

        assertNotNull(savedTimer);
        assertEquals(1L, savedTimer.count());
        assertTrue(savedTimer.totalTime(TimeUnit.MILLISECONDS) >= 0);

        io.micrometer.core.instrument.Meter.Id id = savedTimer.getId();
        assertEquals("http.requests.total", id.getName());
        assertEquals("Total incoming HTTP requests", id.getDescription());
        assertEquals("none", id.getTag("method"));
        assertEquals("FAILURE", id.getTag("outcome"));
    }
}
