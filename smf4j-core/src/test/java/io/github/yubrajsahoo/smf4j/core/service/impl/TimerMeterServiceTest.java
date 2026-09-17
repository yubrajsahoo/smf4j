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
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = Smf4jCoreAutoConfiguration.class)
class TimerMeterServiceTest {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private TimerMeterService service;

    @BeforeEach
    void setUp() {
        meterRegistry.clear();
    }

    @Test
    @DisplayName("getType should return TIMER metric type")
    void testGetType() {
        assertThat(service.getType()).isEqualTo(MetricsType.TIMER);
    }

    @Test
    @SuppressWarnings("java:S2925")
    @DisplayName("Should successfully record TimerMetrics parsed from JSON file")
    void testRecordMetrics_WithTimerMetricsJson() throws InterruptedException {
        TimerMetrics timerMetrics = JsonConverter.fromJsonFile("/json/timer-metrics.json", TimerMetrics.class);

        // Start the timer using the service's start method
        Timer.Sample sample = service.start();
        timerMetrics.setSample(sample);

        // Simulate some processing delay
        TimeUnit.MILLISECONDS.sleep(10);

        service.recordMetrics(timerMetrics);

        Timer timer = meterRegistry.find("test.timer").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1L);
        assertThat(timer.getId().getDescription()).isEqualTo("A test timer");

        // Assert that the recorded time is at least 10ms (converted to nanoseconds or seconds depending on precision)
        assertThat(timer.totalTime(TimeUnit.MILLISECONDS)).isGreaterThanOrEqualTo(10.0);
    }

    @Test
    @DisplayName("Should throw exception when CounterMetrics parsed from JSON file is provided")
    void testRecordMetrics_WithCounterMetricsJson() {
        CounterMetrics counterMetrics = JsonConverter.fromJsonFile("/json/counter-metrics.json", CounterMetrics.class);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.recordMetrics(counterMetrics);
        });
        assertThat(ex.getMessage()).isEqualTo("Invalid metrics type for TimerMeterService");
    }

    @Test
    @DisplayName("Should throw exception when GaugeMetrics parsed from JSON file is provided")
    void testRecordMetrics_WithGaugeMetricsJson() {
        GaugeMetrics gaugeMetrics = JsonConverter.fromJsonFile("/json/gauge-metrics.json", GaugeMetrics.class);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.recordMetrics(gaugeMetrics);
        });
        assertThat(ex.getMessage()).isEqualTo("Invalid metrics type for TimerMeterService");
    }
}
