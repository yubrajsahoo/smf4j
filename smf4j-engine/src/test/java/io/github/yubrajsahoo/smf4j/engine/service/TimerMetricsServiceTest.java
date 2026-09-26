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
import io.github.yubrajsahoo.smf4j.core.service.impl.TimerMeterService;
import io.github.yubrajsahoo.smf4j.engine.Smf4jEngineTestAutoConfiguration;
import io.github.yubrajsahoo.smf4j.engine.service.impl.TimerMetricsService;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TimerMeterService Unit Test")
@SpringBootTest(classes = Smf4jEngineTestAutoConfiguration.class)
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TimerMetricsServiceTest {
    @Autowired
    private TimerMeterService timerMeterService;

    @Autowired
    private TimerMetricsService timerMetricsService;

    @io.github.yubrajsahoo.smf4j.api.annotation.Timer(name = "test.timer", enable = false)
    @SuppressWarnings("java:S1186")
    private void dummyDisabledTimer() {
    }

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
    @DisplayName("getType should return MetricsType.TIMER")
    void getType() {
        assertEquals(MetricsType.TIMER, timerMeterService.getType());
    }

    @Test
    @DisplayName("start should return a valid Timer.Sample")
    void start() {
        Timer.Sample sample = timerMeterService.start();
        assertNotNull(sample);
    }

    @Test
    @DisplayName("recordMetrics should register and record timer metrics correctly")
    void recordMetrics() {
        TimerMetrics metrics = JsonConverter.fromJsonFile(
                "/json/timer-metrics.json", TimerMetrics.class
        );

        // create a sample and measure time
        Timer.Sample sample = timerMeterService.start();
        metrics.setSample(sample);

        timerMeterService.recordMetrics(metrics);

        Timer timer = meterRegistry.find("http.requests.total").timer();

        assertNotNull(timer);
        assertEquals(1L, timer.count());
        assertTrue(timer.totalTime(TimeUnit.MILLISECONDS) >= 0);

        Meter.Id id = timer.getId();
        assertEquals("http.requests.total", id.getName());
        assertEquals("Total incoming HTTP requests", id.getDescription());
        assertEquals("GET", id.getTag("method"));
        assertEquals("SUCCESS", id.getTag("outcome"));
    }

    @Test
    @DisplayName("recordMetrics should throw IllegalArgumentException for invalid metrics type")
    void recordMetrics_invalidType() {
        CounterMetrics counterMetrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class
        );

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                timerMeterService.recordMetrics(counterMetrics)
        );

        assertEquals("Invalid metrics type for TimerMeterService", exception.getMessage());
    }

    @Test
    @DisplayName("recordMetrics should throw IllegalArgumentException for sample null")
    void recordMetrics_sampleNull() {
        TimerMetrics timerMetrics = JsonConverter.fromJsonFile(
                "/json/timer-metrics.json", TimerMetrics.class
        );

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                timerMeterService.recordMetrics(timerMetrics)
        );

        assertEquals("Invalid metrics type for TimerMeterService", exception.getMessage());
    }

    @Test
    @DisplayName("start should return null when timer is null")
    void start_withNullTimer() {
        Timer.Sample sample = timerMetricsService.start(null);
        assertNull(sample);
    }

    @Test
    @DisplayName("start should return null when timer is disabled")
    void start_withDisabledTimer() throws NoSuchMethodException {
        io.github.yubrajsahoo.smf4j.api.annotation.Timer timer = this.getClass().getDeclaredMethod("dummyDisabledTimer").getAnnotation(io.github.yubrajsahoo.smf4j.api.annotation.Timer.class);
        Timer.Sample sample = timerMetricsService.start(timer);
        assertNull(sample);
    }

    @Test
    @DisplayName("recordTimer should return early when timer is null")
    void recordTimer_withNullTimer() {
        Timer.Sample sample = timerMeterService.start();
        timerMetricsService.recordTimer(sample, null, new org.springframework.expression.spel.support.StandardEvaluationContext());
        Assertions.assertTrue(meterRegistry.getMeters().isEmpty());
    }

    @io.github.yubrajsahoo.smf4j.api.annotation.Timer(name = "test.happy.timer", enable = true)
    @SuppressWarnings("java:S1186")
    private void dummyHappyTimer() {}

    @Test
    @DisplayName("recordTimer should record metrics correctly when valid arguments are provided")
    void recordTimer_happyPath() throws NoSuchMethodException {
        io.github.yubrajsahoo.smf4j.api.annotation.Timer timer = this.getClass().getDeclaredMethod("dummyHappyTimer").getAnnotation(io.github.yubrajsahoo.smf4j.api.annotation.Timer.class);
        Timer.Sample sample = timerMeterService.start();
        timerMetricsService.recordTimer(sample, timer, new org.springframework.expression.spel.support.StandardEvaluationContext());
        
        Timer recordedTimer = meterRegistry.find("test.happy.timer").timer();
        assertNotNull(recordedTimer);
        assertEquals(1L, recordedTimer.count());

        String formattedMessage = listAppender.list.get(0).getFormattedMessage();
        String expectedMessage = "Metrics Logs For With->name=test.happy.timer->description=none";
        assertEquals(expectedMessage, formattedMessage);
    }
}
