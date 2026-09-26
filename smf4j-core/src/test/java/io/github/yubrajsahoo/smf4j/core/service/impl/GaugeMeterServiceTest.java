/*
 * Copyright 2024 Yubraj Sahoo
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.yubrajsahoo.smf4j.core.service.impl;

import helper.JsonConverter;
import io.github.yubrajsahoo.smf4j.api.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4j.api.domain.GaugeMetrics;
import io.github.yubrajsahoo.smf4j.api.domain.TimerMetrics;
import io.github.yubrajsahoo.smf4j.api.enums.MetricsType;
import io.github.yubrajsahoo.smf4j.core.Smf4jCoreAutoConfiguration;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = Smf4jCoreAutoConfiguration.class)
class GaugeMeterServiceTest {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private GaugeMeterService service;

    @BeforeEach
    void setUp() {
        meterRegistry.clear();
    }

    @Test
    @DisplayName("getType should return GAUGE metric type")
    void testGetType() {
        assertThat(service.getType()).isEqualTo(MetricsType.GAUGE);
    }

    @Test
    @DisplayName("Should successfully record GaugeMetrics parsed from JSON file")
    void testRecordMetrics_WithGaugeMetricsJson() {
        // We must supply the type safely when deserializing
        @SuppressWarnings("unchecked")
        GaugeMetrics<AtomicInteger> gaugeMetrics = JsonConverter.fromJsonFile("/json/gauge-metrics.json", GaugeMetrics.class);

        // JSON cannot serialize functional interfaces, so we set them manually for the test
        AtomicInteger testState = new AtomicInteger(42);
        gaugeMetrics.setInstance(testState);
        gaugeMetrics.setFunction(AtomicInteger::doubleValue);

        service.recordMetrics(gaugeMetrics);

        Gauge gauge = meterRegistry.find("test.gauge").gauge();
        assertThat(gauge).isNotNull();
        assertThat(gauge.value()).isEqualTo(42.0);
        assertThat(gauge.getId().getDescription()).isEqualTo("A test gauge");

        // Change the underlying state and verify the gauge reflects it
        testState.set(100);
        assertThat(gauge.value()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Should throw exception when CounterMetrics parsed from JSON file is provided")
    void testRecordMetrics_WithCounterMetricsJson() {
        CounterMetrics counterMetrics = JsonConverter.fromJsonFile("/json/counter-metrics.json", CounterMetrics.class);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.recordMetrics(counterMetrics);
        });
        assertThat(ex.getMessage()).isEqualTo("Invalid metrics type for GaugeMeterService");
    }

    @Test
    @DisplayName("Should throw exception when TimerMetrics parsed from JSON file is provided")
    void testRecordMetrics_WithTimerMetricsJson() {
        TimerMetrics timerMetrics = JsonConverter.fromJsonFile("/json/timer-metrics.json", TimerMetrics.class);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.recordMetrics(timerMetrics);
        });
        assertThat(ex.getMessage()).isEqualTo("Invalid metrics type for GaugeMeterService");
    }
}
