package io.github.yubrajsahoo.smf4jcore.aspect;

import io.github.yubrajsahoo.smf4jcore.counter.annotation.Counter;
import io.github.yubrajsahoo.smf4jcore.core.annotation.Tags;
import io.github.yubrajsahoo.smf4jcore.timer.annotation.Timer;
import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {Smf4jAutoConfiguration.class, MetricsAspectIntegrationTest.TestConfig.class})
class MetricsAspectIntegrationTest {

    @Autowired
    private DummyService dummyService;

    @Autowired
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry.clear();
    }

    @Test
    @DisplayName("Should Trigger Counter Aspect When Method Annotated With @Counter Is Invoked")
    void testCounterAnnotationTriggersAspect() {
        String result = dummyService.performAction("test-input");
        assertEquals("Success: test-input", result);

        io.micrometer.core.instrument.Counter counter = meterRegistry.find("dummy.counter").counter();
        assertNotNull(counter, "Counter metric should be recorded");
        assertEquals(1.0, counter.count());
        assertEquals("Success: test-input", counter.getId().getTag("methodResult"));
        assertEquals("SUCCESS", counter.getId().getTag("status"));
    }

    @Test
    @DisplayName("Should Trigger Counter Aspect When Method Annotated With @Counter Throws Exception")
    void testCounterAnnotationTriggersAspectOnException() {
        assertThrows(RuntimeException.class, () -> dummyService.performActionWithError());

        io.micrometer.core.instrument.Counter counter = meterRegistry.find("dummy.counter.error").counter();
        assertNotNull(counter, "Counter metric should be recorded even on exception");
        assertEquals(1.0, counter.count());
        assertEquals("FAILURE", counter.getId().getTag("status"));
        assertEquals("Intended error", counter.getId().getTag("errorMessage"));
    }

    @Test
    @DisplayName("Should Trigger Timer Aspect When Method Annotated With @Timer Is Invoked")
    void testTimerAnnotationTriggersAspect() {
        String result = dummyService.performTimeAction();
        assertEquals("Done", result);

        io.micrometer.core.instrument.Timer timer = meterRegistry.find("dummy.timer").timer();
        assertNotNull(timer, "Timer metric should be recorded");
        assertEquals(1L, timer.count());
        assertTrue(timer.totalTime(TimeUnit.MILLISECONDS) >= 0);
        assertEquals("Done", timer.getId().getTag("methodResult"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public DummyService dummyService() {
            return new DummyService();
        }
    }

    @Service
    public static class DummyService {

        @Counter(
                name = "dummy.counter",
                description = "A dummy counter metric",
                tags = {
                        @Tags(key = "methodResult", value = "#result"),
                        @Tags(key = "status", value = "#error == null ? 'SUCCESS' : 'FAILURE'")
                }
        )
        public String performAction(String input) {
            return "Success: " + input;
        }

        @Counter(
                name = "dummy.counter.error",
                description = "A dummy counter metric for error",
                tags = {
                        @Tags(key = "status", value = "#error == null ? 'SUCCESS' : 'FAILURE'"),
                        @Tags(key = "errorMessage", value = "#error.message")
                }
        )
        public void performActionWithError() {
            throw new RuntimeException("Intended error");
        }

        @Timer(
                name = "dummy.timer",
                description = "A dummy timer metric",
                tags = {
                        @Tags(key = "methodResult", value = "#result")
                }
        )
        public String performTimeAction() {
            return "Done";
        }
    }
}
