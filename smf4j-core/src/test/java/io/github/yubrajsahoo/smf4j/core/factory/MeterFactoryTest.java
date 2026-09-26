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

package io.github.yubrajsahoo.smf4j.core.factory;

import io.github.yubrajsahoo.smf4j.api.domain.Metrics;
import io.github.yubrajsahoo.smf4j.api.enums.MetricsType;
import io.github.yubrajsahoo.smf4j.core.service.MeterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class MeterFactoryTest {

    @Test
    @DisplayName("Should successfully retrieve registered meter service")
    void shouldReturnRegisteredMeterService() {
        // Given
        MeterService counterService = new StubMeterService(MetricsType.COUNTER);
        MeterService gaugeService = new StubMeterService(MetricsType.GAUGE);
        MeterFactory factory = new MeterFactory(Arrays.asList(counterService, gaugeService));

        // When
        Optional<MeterService> resultCounter = factory.getMeterService(MetricsType.COUNTER);
        Optional<MeterService> resultGauge = factory.getMeterService(MetricsType.GAUGE);
        Optional<MeterService> resultTimer = factory.getMeterService(MetricsType.TIMER);

        // Then
        assertThat(resultCounter).isPresent().contains(counterService);
        assertThat(resultGauge).isPresent().contains(gaugeService);
        assertThat(resultTimer).isEmpty();
    }

    @Test
    @DisplayName("Should return empty optional when null metrics type is passed")
    void shouldReturnEmptyForNullType() {
        // Given
        MeterService counterService = new StubMeterService(MetricsType.COUNTER);
        MeterFactory factory = new MeterFactory(Collections.singletonList(counterService));

        // When
        Optional<MeterService> result = factory.getMeterService(null);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle null list of meter services gracefully")
    void shouldHandleNullListGracefully() {
        // Given
        MeterFactory factory = new MeterFactory(null);

        // When
        Optional<MeterService> result = factory.getMeterService(MetricsType.COUNTER);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty list of meter services")
    void shouldHandleEmptyList() {
        // Given
        MeterFactory factory = new MeterFactory(Collections.emptyList());

        // When
        Optional<MeterService> result = factory.getMeterService(MetricsType.GAUGE);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should ignore meter services returning null type")
    void shouldIgnoreServiceWithNullType() {
        // Given
        MeterService nullTypeService = new StubMeterService(null);
        MeterFactory factory = new MeterFactory(Collections.singletonList(nullTypeService));

        // When
        Optional<MeterService> result = factory.getMeterService(MetricsType.COUNTER);

        // Then
        assertThat(result).isEmpty();
    }

    private static class StubMeterService implements MeterService {
        private final MetricsType type;

        public StubMeterService(MetricsType type) {
            this.type = type;
        }

        @Override
        public MetricsType getType() {
            return type;
        }

        @Override
        public void recordMetrics(Metrics metrics) {
            // Not needed for factory testing
        }
    }
}
