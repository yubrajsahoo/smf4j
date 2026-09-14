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

package io.github.yubrajsahoo.smf4jcore.factory;

import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.meter.service.MeterService;
import io.github.yubrajsahoo.smf4jcore.meter.service.impl.CounterMeterService;
import io.github.yubrajsahoo.smf4jcore.meter.service.impl.TimerMeterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Smf4jAutoConfiguration.class)
class MeterFactoryTest {
    @Autowired
    MeterFactory meterFactory;

    @Test
    @DisplayName("Get Meter Service For Counter")
    void testGetMeterService_Counter() {
        MetricsType metricsType = MetricsType.COUNTER;

        Optional<MeterService> optionalMeterService = meterFactory.getMeterService(metricsType);
        assertTrue(optionalMeterService.isPresent());

        MeterService meterService = optionalMeterService.get();

        assertEquals(metricsType, meterService.getType());
        assertEquals(CounterMeterService.class.getSimpleName(), meterService.getClass().getSimpleName());
    }

    @Test
    @DisplayName("Get Meter Service For Timer")
    void testGetMeterService_Timer() {
        MetricsType metricsType = MetricsType.TIMER;

        Optional<MeterService> optionalMeterService = meterFactory.getMeterService(metricsType);
        assertTrue(optionalMeterService.isPresent());

        MeterService meterService = optionalMeterService.get();

        assertEquals(metricsType, meterService.getType());
        assertEquals(TimerMeterService.class.getSimpleName(), meterService.getClass().getSimpleName());
    }
}