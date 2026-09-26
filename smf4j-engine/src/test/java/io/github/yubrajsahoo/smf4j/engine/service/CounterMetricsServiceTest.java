/*
 *
 *  * Copyright 2024 Yubraj Sahoo
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.github.yubrajsahoo.smf4j.engine.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import helper.JsonConverter;
import io.github.yubrajsahoo.smf4j.api.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4j.api.domain.TimerMetrics;
import io.github.yubrajsahoo.smf4j.api.enums.MetricsType;
import io.github.yubrajsahoo.smf4j.core.logger.impl.DefaultMetricsLogger;
import io.github.yubrajsahoo.smf4j.core.service.impl.CounterMeterService;
import io.github.yubrajsahoo.smf4j.engine.Smf4jEngineTestAutoConfiguration;
import io.github.yubrajsahoo.smf4j.engine.service.impl.CounterMetricsService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("CounterMeterService Unit Test")
@SpringBootTest(classes = Smf4jEngineTestAutoConfiguration.class)
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CounterMetricsServiceTest {
    @Autowired
    private CounterMeterService counterMeterService;

    @Autowired
    private CounterMetricsService counterMetricsService;

    @Autowired
    private MeterRegistry meterRegistry;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        meterRegistry.clear();
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
    @DisplayName("getType should return MetricsType.COUNTER")
    void getType() {
        assertEquals(MetricsType.COUNTER, counterMeterService.getType());
    }

    @Test
    @DisplayName("recordMetrics should register and increment counter metrics correctly")
    void recordMetrics() {
        CounterMetrics metrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );

        counterMeterService.recordMetrics(metrics);

        Counter counter = meterRegistry.find("http.requests.total")
                .counter();

        assertNotNull(counter);
        assertEquals(3.0, counter.count());

        Meter.Id id = counter.getId();
        assertEquals("http.requests.total", id.getName());
        assertEquals("Total incoming HTTP requests", id.getDescription());
        assertEquals("GET", id.getTag("method"));
        assertEquals("SUCCESS", id.getTag("outcome"));
    }

    @Test
    @DisplayName("recordMetrics should throw IllegalArgumentException for invalid metrics type")
    void recordMetrics_invalidType() {
        TimerMetrics timerMetrics = JsonConverter.fromJsonFile(
                "/json/timer-metrics.json", TimerMetrics.class
        );

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                counterMeterService.recordMetrics(timerMetrics)
        );

        assertEquals("Invalid metrics type for CounterMeterService", exception.getMessage());
    }

    @Test
    @DisplayName("recordCounter should return early when Counter is null")
    void recordCounter_withNullCounter() {
        org.springframework.expression.spel.support.StandardEvaluationContext context = new org.springframework.expression.spel.support.StandardEvaluationContext();
        counterMetricsService.recordCounter(null, context);
        Assertions.assertTrue(meterRegistry.getMeters().isEmpty());
    }

    @io.github.yubrajsahoo.smf4j.api.annotation.Counter(name = "test.happy.counter", enable = true, increment = 1)
    @SuppressWarnings("java:S1186")
    private void dummyHappyCounter() {}

    @Test
    @DisplayName("recordCounter should record metrics correctly when valid arguments are provided")
    void recordCounter_happyPath() throws NoSuchMethodException {
        io.github.yubrajsahoo.smf4j.api.annotation.Counter counter = this.getClass().getDeclaredMethod("dummyHappyCounter").getAnnotation(io.github.yubrajsahoo.smf4j.api.annotation.Counter.class);
        counterMetricsService.recordCounter(counter, new org.springframework.expression.spel.support.StandardEvaluationContext());
        
        Counter recordedCounter = meterRegistry.find("test.happy.counter").counter();
        assertNotNull(recordedCounter);
        assertEquals(1.0, recordedCounter.count());

        String formattedMessage = listAppender.list.get(0).getFormattedMessage();
        String expectedMessage = "Metrics Logs For With->name=test.happy.counter->description=none->increment=1";
        assertEquals(expectedMessage, formattedMessage);
    }
}