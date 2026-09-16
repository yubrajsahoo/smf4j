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

package io.github.yubrajsahoo.smf4jcore.core;

import io.github.yubrajsahoo.smf4jcore.core.domain.Metrics;
import io.github.yubrajsahoo.smf4jcore.core.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.core.service.MeterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MeterServiceTest {
    private MeterService meterService;

    @BeforeEach
    void setUp() {
        meterService = new MeterService() {
            @Override
            public MetricsType getType() {
                return null;
            }

            @SuppressWarnings("java:S1186")
            @Override
            public void recordMetrics(Metrics metrics) {
            }
        };
    }

    @Test
    @DisplayName("Should Throw IllegalArgumentException")
    void testStart_Default() {
        assertThrows(IllegalArgumentException.class, () -> meterService.start());
    }
}