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

package io.github.yubrajsahoo.smf4jcore.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import helper.JsonConverter;
import io.github.yubrajsahoo.smf4jcore.annotation.Counter;
import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import io.github.yubrajsahoo.smf4jcore.logger.impl.DefaultMetricsLogger;
import io.github.yubrajsahoo.smf4jcore.spel.SpelContextBuilder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
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
import org.springframework.expression.BeanResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = Smf4jAutoConfiguration.class)
class MetricsServiceTest {
    @Autowired
    MetricsService metricsService;

    @Autowired
    MeterRegistry meterRegistry;

    @Mock
    private BeanResolver beanResolver;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    private ListAppender<ILoggingEvent> listAppender;


    @BeforeEach
    void setUp() {
        meterRegistry.clear();
        Mockito.reset(joinPoint, methodSignature, beanResolver);
        when(joinPoint.getSignature()).thenReturn(methodSignature);

        //varify log for DefaultMetricsLogger
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
    @DisplayName("Should Start Timer")
    void testStart() {
        Timer.Sample start = metricsService.start();
        assertNotNull(start);
    }

    @Test
    @DisplayName("Should Not Store Any Counter Metrics Due to Counter Null")
    void testRecordMetrics_Counter_Null() {
        metricsService.recordMetrics((Counter) null, buildContext(null, null));

        //should not store metrics
        io.micrometer.core.instrument.Counter savedCounter = meterRegistry.find("http.requests.total")
                .counter();

        assertNull(savedCounter);

    }

    @Test
    @DisplayName("Should Not Store Any Counter Metrics Due to disabled")
    void testRecordMetrics_Counter_Disabled() {
        Counter counter = JsonConverter.read(
                "src/test/resources/json/counter-disabled.json", Counter.class
        );

        metricsService.recordMetrics(counter, buildContext(null, null));

        //should log disabled log
        String formattedMessage = listAppender.list.get(0).getFormattedMessage();
        String expectedMessage = "Metrics Disabled For->name=http.requests.total->" +
                "method=GET->outcome=SUCCESS->description=Total incoming HTTP requests->increment=3";

        assertEquals(expectedMessage, formattedMessage);

        //should not store metrics
        io.micrometer.core.instrument.Counter savedCounter = meterRegistry.find("http.requests.total")
                .counter();

        assertNull(savedCounter);
    }

    @Test
    @DisplayName("Should Store Counter Metrics")
    void testRecordMetrics_Counter_Enabled() {
        Counter counter = JsonConverter.read(
                "src/test/resources/json/counter-enabled.json", Counter.class
        );

        metricsService.recordMetrics(counter, buildContext("GET", null));

        //should log enabled log
        String formattedMessage = listAppender.list.get(0).getFormattedMessage();
        String expectedMessage = "Metrics Logs For With->name=http.requests.total->" +
                "method=GET->outcome=SUCCESS->description=Total incoming HTTP requests->increment=3";

        assertEquals(expectedMessage, formattedMessage);

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
    @DisplayName("Should Store Counter Metrics With Error")
    void testRecordMetrics_Counter_Enabled_WithError() {
        Counter counter = JsonConverter.read(
                "src/test/resources/json/counter-enabled.json", Counter.class
        );

        metricsService.recordMetrics(counter, buildContext(null, new RuntimeException("Test Exception")));

        //should log enabled log
        String formattedMessage = listAppender.list.get(0).getFormattedMessage();
        String expectedMessage = "Metrics Logs For With->name=http.requests.total->" +
                "method=none->outcome=FAILURE->description=Total incoming HTTP requests->increment=3";

        assertEquals(expectedMessage, formattedMessage);

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
    @DisplayName("Should Not Store Any Timer Metrics Due to Sample Null")
    void testRecordMetrics_Sample_Null() {
        io.github.yubrajsahoo.smf4jcore.annotation.Timer timer = JsonConverter.read(
                "src/test/resources/json/timer-enabled.json",
                io.github.yubrajsahoo.smf4jcore.annotation.Timer.class
        );

        metricsService.recordMetrics(
                (Timer.Sample) null,
                timer,
                buildContext(null, null)
        );

        //should not store metrics
        io.micrometer.core.instrument.Timer savedTimer = meterRegistry.find("http.requests.total")
                .timer();

        assertNull(savedTimer);
    }

    @Test
    @DisplayName("Should Not Store Any Timer Metrics Due to Timer Null")
    void testRecordMetrics_Timer_Null() {
        metricsService.recordMetrics(
                mock(Timer.Sample.class),
                null,
                buildContext(null, null)
        );

        //should not store metrics
        io.micrometer.core.instrument.Timer savedTimer = meterRegistry.find("http.requests.total")
                .timer();

        assertNull(savedTimer);
    }

    @Test
    @DisplayName("Should Not Store Any Timer Metrics Due to disabled")
    void testRecordMetrics_Timer_Disabled() {
        Timer.Sample sample = metricsService.start();

        io.github.yubrajsahoo.smf4jcore.annotation.Timer timer = JsonConverter.read(
                "src/test/resources/json/timer-disabled.json",
                io.github.yubrajsahoo.smf4jcore.annotation.Timer.class
        );

        metricsService.recordMetrics(sample, timer, buildContext(null, null));

        //should log disabled log
        String formattedMessage = listAppender.list.get(0).getFormattedMessage();
        String expectedMessage = "Metrics Disabled For->name=http.requests.total->" +
                "method=GET->outcome=SUCCESS->description=Total incoming HTTP requests";

        assertEquals(expectedMessage, formattedMessage);

        //should not store metrics
        io.micrometer.core.instrument.Counter savedCounter = meterRegistry.find("http.requests.total")
                .counter();

        assertNull(savedCounter);
    }

    @Test
    @DisplayName("Should Handle Exception While Recording Counter Metrics")
    void testRecordMetrics_Counter_Exception() {
        Counter counter = mock(Counter.class);
        when(counter.name()).thenReturn("test.counter");
        when(counter.tags()).thenThrow(new RuntimeException("Simulated exception"));

        assertDoesNotThrow(() -> metricsService.recordMetrics(counter, buildContext(null, null)));
    }

    @Test
    @DisplayName("Should Store Timer Metrics")
    void testRecordMetrics_Timer_Enabled() {
        Timer.Sample sample = metricsService.start();

        io.github.yubrajsahoo.smf4jcore.annotation.Timer timer = JsonConverter.read(
                "src/test/resources/json/timer-enabled.json",
                io.github.yubrajsahoo.smf4jcore.annotation.Timer.class
        );

        metricsService.recordMetrics(sample, timer, buildContext("GET", null));

        //should log enabled log
        String formattedMessage = listAppender.list.get(0).getFormattedMessage();
        String expectedMessage = "Metrics Logs For With->name=http.requests.total->" +
                "method=GET->outcome=SUCCESS->description=Total incoming HTTP requests";

        assertEquals(expectedMessage, formattedMessage);

        //should store metrics
        io.micrometer.core.instrument.Timer savedTimer = meterRegistry.find("http.requests.total")
                .timer();

        assertNotNull(savedTimer);
        assertEquals(1L, savedTimer.count());
        assertTrue(savedTimer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS) >= 0);

        io.micrometer.core.instrument.Meter.Id id = savedTimer.getId();
        assertEquals("http.requests.total", id.getName());
        assertEquals("Total incoming HTTP requests", id.getDescription());
        assertEquals("GET", id.getTag("method"));
        assertEquals("SUCCESS", id.getTag("outcome"));
    }

    @Test
    @DisplayName("Should Store Timer Metrics With Error")
    void testRecordMetrics_Timer_Enabled_WithError() {
        Timer.Sample sample = metricsService.start();

        io.github.yubrajsahoo.smf4jcore.annotation.Timer timer = JsonConverter.read(
                "src/test/resources/json/timer-enabled.json",
                io.github.yubrajsahoo.smf4jcore.annotation.Timer.class
        );

        metricsService.recordMetrics(sample, timer, buildContext(null, new RuntimeException("Test Exception")));

        //should log enabled log
        String formattedMessage = listAppender.list.get(0).getFormattedMessage();
        String expectedMessage = "Metrics Logs For With->name=http.requests.total->" +
                "method=none->outcome=FAILURE->description=Total incoming HTTP requests";

        assertEquals(expectedMessage, formattedMessage);

        //should store metrics
        io.micrometer.core.instrument.Timer savedTimer = meterRegistry.find("http.requests.total")
                .timer();

        assertNotNull(savedTimer);
        assertEquals(1L, savedTimer.count());
        assertTrue(savedTimer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS) >= 0);

        io.micrometer.core.instrument.Meter.Id id = savedTimer.getId();
        assertEquals("http.requests.total", id.getName());
        assertEquals("Total incoming HTTP requests", id.getDescription());
        assertEquals("none", id.getTag("method"));
        assertEquals("FAILURE", id.getTag("outcome"));
    }

    @Test
    @DisplayName("Should Handle Exception While Recording Timer Metrics")
    void testRecordMetrics_Timer_Exception() {
        Timer.Sample sample = metricsService.start();
        io.github.yubrajsahoo.smf4jcore.annotation.Timer timer = mock(io.github.yubrajsahoo.smf4jcore.annotation.Timer.class);
        when(timer.name()).thenReturn("test.timer");
        when(timer.tags()).thenThrow(new RuntimeException("Simulated exception"));

        assertDoesNotThrow(() -> metricsService.recordMetrics(sample, timer, buildContext(null, null)));
    }

    @Test
    @DisplayName("Should Not Store Any Gauge Metrics Due to Gauge Null")
    void testRecordMetrics_Gauge_Null() {
        metricsService.recordMetrics(null, new Object(), obj -> 1.0, buildContext(null, null));

        io.micrometer.core.instrument.Gauge savedGauge = meterRegistry.find("test.gauge").gauge();
        assertNull(savedGauge);
    }

    @Test
    @DisplayName("Should Not Store Any Gauge Metrics Due to disabled")
    void testRecordMetrics_Gauge_Disabled() {
        io.github.yubrajsahoo.smf4jcore.annotation.Gauge gauge = mock(io.github.yubrajsahoo.smf4jcore.annotation.Gauge.class);
        when(gauge.name()).thenReturn("test.gauge");
        when(gauge.description()).thenReturn("Test Gauge");
        when(gauge.enable()).thenReturn(false);
        when(gauge.tags()).thenReturn(new io.github.yubrajsahoo.smf4jcore.annotation.Tags[0]);

        metricsService.recordMetrics(gauge, new Object(), obj -> 1.0, buildContext(null, null));

        //should log disabled log
        String formattedMessage = listAppender.list.get(0).getFormattedMessage();
        assertTrue(formattedMessage.contains("Metrics Disabled For"));

        io.micrometer.core.instrument.Gauge savedGauge = meterRegistry.find("test.gauge").gauge();
        assertNull(savedGauge);
    }

    @Test
    @DisplayName("Should Store Gauge Metrics")
    void testRecordMetrics_Gauge_Enabled() {
        io.github.yubrajsahoo.smf4jcore.annotation.Gauge gauge = mock(io.github.yubrajsahoo.smf4jcore.annotation.Gauge.class);
        when(gauge.name()).thenReturn("test.gauge");
        when(gauge.description()).thenReturn("Test Gauge");
        when(gauge.enable()).thenReturn(true);
        when(gauge.tags()).thenReturn(new io.github.yubrajsahoo.smf4jcore.annotation.Tags[0]);

        Object testObj = new Object();
        metricsService.recordMetrics(gauge, testObj, obj -> 42.0, buildContext(null, null));

        //should log enabled log
        String formattedMessage = listAppender.list.get(0).getFormattedMessage();
        assertTrue(formattedMessage.contains("Metrics Logs For With"));

        io.micrometer.core.instrument.Gauge savedGauge = meterRegistry.find("test.gauge").gauge();
        assertNotNull(savedGauge);
        assertEquals(42.0, savedGauge.value());

        io.micrometer.core.instrument.Meter.Id id = savedGauge.getId();
        assertEquals("test.gauge", id.getName());
        assertEquals("Test Gauge", id.getDescription());
    }

    @Test
    @DisplayName("Should Handle Exception While Recording Gauge Metrics")
    void testRecordMetrics_Gauge_Exception() {
        io.github.yubrajsahoo.smf4jcore.annotation.Gauge gauge = mock(io.github.yubrajsahoo.smf4jcore.annotation.Gauge.class);
        when(gauge.name()).thenReturn("test.gauge");
        when(gauge.tags()).thenThrow(new RuntimeException("Simulated exception"));

        assertDoesNotThrow(() -> metricsService.recordMetrics(gauge, new Object(), obj -> 1.0, buildContext(null, null)));
    }

    private StandardEvaluationContext buildContext(Object result, Throwable error) {
        return SpelContextBuilder.buildContext(joinPoint, result, error, beanResolver);
    }
}