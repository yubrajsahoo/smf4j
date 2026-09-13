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

package io.github.yubrajsahoo.smf4jcore.meter.service.impl;

import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.domain.TimerMetrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import helper.JsonConverter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("CounterMeterService Unit Test")
@SpringBootTest(classes = Smf4jAutoConfiguration.class)
class CounterMeterServiceTest {
    @Autowired
    private CounterMeterService counterMeterService;

    @Autowired
    private MeterRegistry meterRegistry;


    @BeforeEach
    void setUp() {
        meterRegistry.clear();
    }

    @Test
    @DisplayName("getType should return MetricsType.COUNTER")
    void getType() {
        assertEquals(MetricsType.COUNTER, counterMeterService.getType());
    }

    @Test
    @DisplayName("recordMetrics should register and increment counter metrics correctly")
    void recordMetrics() {
        CounterMetrics metrics = JsonConverter.read(
                "src/test/resources/json/counter-metrics.json", CounterMetrics.class
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
        TimerMetrics timerMetrics = JsonConverter.read(
                "src/test/resources/json/timer-metrics.json", TimerMetrics.class
        );

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                counterMeterService.recordMetrics(timerMetrics)
        );

        assertEquals("Invalid metrics type for CounterMeterService", exception.getMessage());
    }
}