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

package io.github.yubrajsahoo.smf4jcore.gauge;

import io.micrometer.core.instrument.Tags;

import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import io.github.yubrajsahoo.smf4jcore.counter.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.core.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.gauge.domain.GaugeMetrics;
import io.github.yubrajsahoo.smf4jcore.gauge.service.GaugeMeterService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("GaugeMeterService Unit Test")
@SpringBootTest(classes = Smf4jAutoConfiguration.class)
class GaugeMeterServiceTest {

    @Autowired
    private GaugeMeterService gaugeMeterService;

    @Autowired
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry.clear();
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
                .tags(Tags.of("env", "test"))
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
}
