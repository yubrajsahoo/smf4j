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
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = Smf4jCoreAutoConfiguration.class)
class CounterMeterServiceTest {

    @Autowired
    private MeterRegistry meterRegistry;
    @Autowired
    private CounterMeterService service;

    @BeforeEach
    void setUp() {
        meterRegistry.clear();
    }

    @Test
    @DisplayName("getType should return COUNTER metric type")
    void testGetType() {
        assertThat(service.getType()).isEqualTo(MetricsType.COUNTER);
    }

    @Test
    @DisplayName("Should successfully record CounterMetrics parsed from JSON file")
    void testRecordMetrics_WithCounterMetricsJson() {
        CounterMetrics counterMetrics = JsonConverter.fromJsonFile(
                "/json/counter-metrics.json", CounterMetrics.class);

        service.recordMetrics(counterMetrics);

        Counter counter = meterRegistry.find("test.counter").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(5.0);
        assertThat(counter.getId().getDescription()).isEqualTo("A test counter");
        assertThat(counter.getId().getTag("env")).isEqualTo("test");
        assertThat(counter.getId().getTag("region")).isEqualTo("us-east");
    }

    @Test
    @DisplayName("Should throw exception when GaugeMetrics parsed from JSON file is provided")
    void testRecordMetrics_WithGaugeMetricsJson() {
        GaugeMetrics gaugeMetrics = JsonConverter.fromJsonFile(
                "/json/gauge-metrics.json", GaugeMetrics.class);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.recordMetrics(gaugeMetrics);
        });
        assertThat(ex.getMessage()).isEqualTo("Invalid metrics type for CounterMeterService");
    }

    @Test
    @DisplayName("Should throw exception when TimerMetrics parsed from JSON file is provided")
    void testRecordMetrics_WithTimerMetricsJson() {
        TimerMetrics timerMetrics = JsonConverter.fromJsonFile(
                "/json/timer-metrics.json", TimerMetrics.class);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.recordMetrics(timerMetrics);
        });
        assertThat(ex.getMessage()).isEqualTo("Invalid metrics type for CounterMeterService");
    }
}
