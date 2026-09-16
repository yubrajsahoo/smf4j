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

package io.github.yubrajsahoo.smf4jcore.timer;

import helper.JsonConverter;
import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import io.github.yubrajsahoo.smf4jcore.counter.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.core.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.timer.domain.TimerMetrics;
import io.github.yubrajsahoo.smf4jcore.timer.service.TimerMeterService;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TimerMeterService Unit Test")
@SpringBootTest(classes = Smf4jAutoConfiguration.class)
class TimerMeterServiceTest {
    @Autowired
    private TimerMeterService timerMeterService;

    @Autowired
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry.clear();
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
        TimerMetrics metrics = JsonConverter.read(
                "src/test/resources/json/timer-metrics.json", TimerMetrics.class
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
        CounterMetrics counterMetrics = JsonConverter.read(
                "src/test/resources/json/counter-metrics.json", CounterMetrics.class
        );

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                timerMeterService.recordMetrics(counterMetrics)
        );

        assertEquals("Invalid metrics type for TimerMeterService", exception.getMessage());
    }

    @Test
    @DisplayName("recordMetrics should throw IllegalArgumentException for sample null")
    void recordMetrics_sampleNull() {
        TimerMetrics timerMetrics = JsonConverter.read(
                "src/test/resources/json/timer-metrics.json", TimerMetrics.class
        );

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                timerMeterService.recordMetrics(timerMetrics)
        );

        assertEquals("Invalid metrics type for TimerMeterService", exception.getMessage());
    }
}
