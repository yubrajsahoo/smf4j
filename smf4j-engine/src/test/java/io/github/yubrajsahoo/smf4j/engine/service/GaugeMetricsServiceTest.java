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
import io.github.yubrajsahoo.smf4j.api.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4j.api.domain.GaugeMetrics;
import io.github.yubrajsahoo.smf4j.api.domain.Tag;
import io.github.yubrajsahoo.smf4j.api.enums.MetricsType;
import io.github.yubrajsahoo.smf4j.core.service.impl.GaugeMeterService;
import io.github.yubrajsahoo.smf4j.engine.Smf4jEngineTestAutoConfiguration;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("GaugeMeterService Unit Test")
@SpringBootTest(classes = Smf4jEngineTestAutoConfiguration.class)
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GaugeMetricsServiceTest {

    @Autowired
    private GaugeMeterService gaugeMeterService;

    @Autowired
    private io.github.yubrajsahoo.smf4j.engine.service.impl.GaugeMetricsService gaugeMetricsService;

    @SuppressWarnings("java:S1186")
    @io.github.yubrajsahoo.smf4j.api.annotation.Gauge(name = "test.gauge", enable = false)
    private void dummyDisabledGauge() {}

    @SuppressWarnings("java:S1186")
    @io.github.yubrajsahoo.smf4j.api.annotation.Gauge(name = "test.happy.gauge", enable = true)
    private void dummyHappyGauge() {}

    @Autowired
    private MeterRegistry meterRegistry;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        meterRegistry.clear();
        Logger logger = (Logger) LoggerFactory.getLogger(io.github.yubrajsahoo.smf4j.core.logger.MetricsLogger.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(io.github.yubrajsahoo.smf4j.core.logger.MetricsLogger.class);
        logger.detachAppender(listAppender);
        listAppender.clearAllFilters();
    }

    @Test
    @DisplayName("getType should return MetricsType.GAUGE")
    void getType() {
        assertEquals(MetricsType.GAUGE, gaugeMeterService.getType());
    }

    @Test
    @DisplayName("recordMetrics should register gauge metrics correctly")
    void recordMetrics() {
        AtomicInteger testObj = new AtomicInteger(42);

        GaugeMetrics<AtomicInteger> metrics = GaugeMetrics.<AtomicInteger>builder("test.gauge", testObj, AtomicInteger::doubleValue)
                .description("Test Gauge")
                .tags(List.of(Tag.of("env", "test")))
                .build();

        gaugeMeterService.recordMetrics(metrics);

        Gauge gauge = meterRegistry.find("test.gauge").gauge();
        assertNotNull(gauge);
        assertEquals(42.0, gauge.value());

        Meter.Id id = gauge.getId();
        assertEquals("test.gauge", id.getName());
        assertEquals("Test Gauge", id.getDescription());
        assertEquals("test", id.getTag("env"));
    }

    @Test
    @DisplayName("recordMetrics should throw IllegalArgumentException for invalid metrics type")
    void recordMetrics_invalidType() {
        CounterMetrics counterMetrics = new CounterMetrics();

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                gaugeMeterService.recordMetrics(counterMetrics)
        );

        assertEquals("Invalid metrics type for GaugeMeterService", exception.getMessage());
    }

    @Test
    @DisplayName("Should return early when Gauge is null")
    void recordGauge_withNullGauge() {
        gaugeMetricsService.recordGauge(null, new Object(), obj -> 1.0, new org.springframework.expression.spel.support.StandardEvaluationContext());
        Assertions.assertTrue(meterRegistry.getMeters().isEmpty());
    }

    @Test
    @DisplayName("Should return early when bean is null")
    void recordGauge_withNullBean() throws NoSuchMethodException {
        io.github.yubrajsahoo.smf4j.api.annotation.Gauge gauge = this.getClass().getDeclaredMethod("dummyDisabledGauge").getAnnotation(io.github.yubrajsahoo.smf4j.api.annotation.Gauge.class);
        gaugeMetricsService.recordGauge(gauge, null, obj -> 1.0, new org.springframework.expression.spel.support.StandardEvaluationContext());
        Assertions.assertTrue(meterRegistry.getMeters().isEmpty());
    }

    @Test
    @DisplayName("Should return early when function is null")
    void recordGauge_withNullFunction() throws NoSuchMethodException {
        io.github.yubrajsahoo.smf4j.api.annotation.Gauge gauge = this.getClass().getDeclaredMethod("dummyDisabledGauge").getAnnotation(io.github.yubrajsahoo.smf4j.api.annotation.Gauge.class);
        gaugeMetricsService.recordGauge(gauge, new Object(), null, new org.springframework.expression.spel.support.StandardEvaluationContext());
        Assertions.assertTrue(meterRegistry.getMeters().isEmpty());
    }

    @Test
    @DisplayName("Should not record when enable is false")
    void recordGauge_whenDisabled() throws NoSuchMethodException {
        io.github.yubrajsahoo.smf4j.api.annotation.Gauge gauge = this.getClass().getDeclaredMethod("dummyDisabledGauge").getAnnotation(io.github.yubrajsahoo.smf4j.api.annotation.Gauge.class);
        gaugeMetricsService.recordGauge(gauge, new Object(), obj -> 1.0, new org.springframework.expression.spel.support.StandardEvaluationContext());
        Assertions.assertTrue(meterRegistry.getMeters().isEmpty());
    }

    @Test
    @DisplayName("Should record gauge when valid arguments are provided")
    void recordGauge_happyPath() throws NoSuchMethodException {
        io.github.yubrajsahoo.smf4j.api.annotation.Gauge gauge = this.getClass().getDeclaredMethod("dummyHappyGauge").getAnnotation(io.github.yubrajsahoo.smf4j.api.annotation.Gauge.class);
        gaugeMetricsService.recordGauge(gauge, new Object(), obj -> 42.0, new org.springframework.expression.spel.support.StandardEvaluationContext());
        
        Gauge recordedGauge = meterRegistry.find("test.happy.gauge").gauge();
        assertNotNull(recordedGauge);
        assertEquals(42.0, recordedGauge.value());

        String formattedMessage = listAppender.list.get(0).getFormattedMessage();
        String expectedMessage = "Metrics Logs For With->name=test.happy.gauge->description=none";
        assertEquals(expectedMessage, formattedMessage);
    }
}
