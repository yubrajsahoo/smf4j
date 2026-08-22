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

import io.github.yubrajsahoo.smf4jcore.domain.Metrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.meter.service.MeterService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link MeterFactory}.
 * <p>
 * Validates service registration, lookup by {@link MetricsType}, null-safety,
 * and duplicate-type overwrite semantics.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see MeterFactory
 * @see MeterService
 */
class MeterFactoryTest {

    /**
     * Verifies that a registered {@link MeterService} can be retrieved by its {@link MetricsType}.
     */
    @Test
    void getMeterService_withRegisteredType_shouldReturnService() {
        MeterService counterService = new StubMeterService(MetricsType.COUNTER);
        MeterFactory factory = new MeterFactory(List.of(counterService));

        Optional<MeterService> result = factory.getMeterService(MetricsType.COUNTER);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(counterService);
    }

    /**
     * Verifies that looking up an unregistered {@link MetricsType} returns {@link Optional#empty()}.
     */
    @Test
    void getMeterService_withUnregisteredType_shouldReturnEmpty() {
        MeterService counterService = new StubMeterService(MetricsType.COUNTER);
        MeterFactory factory = new MeterFactory(List.of(counterService));

        Optional<MeterService> result = factory.getMeterService(MetricsType.TIMER);

        assertThat(result).isEmpty();
    }

    /**
     * Verifies that passing {@code null} as the {@link MetricsType} returns {@link Optional#empty()}.
     */
    @Test
    void getMeterService_withNullType_shouldReturnEmpty() {
        MeterFactory factory = new MeterFactory(List.of(new StubMeterService(MetricsType.COUNTER)));

        Optional<MeterService> result = factory.getMeterService(null);

        assertThat(result).isEmpty();
    }

    /**
     * Verifies that constructing a factory with a {@code null} service list
     * results in an empty factory with no registered services.
     */
    @Test
    void constructor_withNullList_shouldCreateEmptyFactory() {
        MeterFactory factory = new MeterFactory(null);

        assertThat(factory.getMeterService(MetricsType.COUNTER)).isEmpty();
        assertThat(factory.getMeterService(MetricsType.TIMER)).isEmpty();
        assertThat(factory.getMeterService(MetricsType.GAUGE)).isEmpty();
    }

    /**
     * Verifies that constructing a factory with an empty service list
     * results in an empty factory.
     */
    @Test
    void constructor_withEmptyList_shouldCreateEmptyFactory() {
        MeterFactory factory = new MeterFactory(Collections.emptyList());

        assertThat(factory.getMeterService(MetricsType.COUNTER)).isEmpty();
    }

    /**
     * Verifies that all provided {@link MeterService} instances are correctly
     * indexed by their respective {@link MetricsType}.
     */
    @Test
    void constructor_withMultipleServices_shouldRegisterAll() {
        MeterService counterService = new StubMeterService(MetricsType.COUNTER);
        MeterService timerService = new StubMeterService(MetricsType.TIMER);
        MeterService gaugeService = new StubMeterService(MetricsType.GAUGE);

        MeterFactory factory = new MeterFactory(List.of(counterService, timerService, gaugeService));

        assertThat(factory.getMeterService(MetricsType.COUNTER)).contains(counterService);
        assertThat(factory.getMeterService(MetricsType.TIMER)).contains(timerService);
        assertThat(factory.getMeterService(MetricsType.GAUGE)).contains(gaugeService);
    }

    /**
     * Verifies that {@code null} entries in the service list are gracefully skipped
     * without causing errors.
     */
    @Test
    void constructor_withNullEntryInList_shouldSkipNull() {
        List<MeterService> services = new ArrayList<>();
        services.add(new StubMeterService(MetricsType.COUNTER));
        services.add(null);

        MeterFactory factory = new MeterFactory(services);

        assertThat(factory.getMeterService(MetricsType.COUNTER)).isPresent();
    }

    /**
     * Verifies that a {@link MeterService} whose {@code getType()} returns {@code null}
     * is skipped during registration.
     */
    @Test
    void constructor_withNullTypeService_shouldSkipIt() {
        MeterService nullTypeService = new StubMeterService(null);
        MeterFactory factory = new MeterFactory(List.of(nullTypeService));

        assertThat(factory.getMeterService(MetricsType.COUNTER)).isEmpty();
    }

    /**
     * Verifies that when multiple services share the same {@link MetricsType},
     * the last one in the list overwrites the earlier registration.
     */
    @Test
    void constructor_withDuplicateTypes_lastOneWins() {
        MeterService first = new StubMeterService(MetricsType.COUNTER);
        MeterService second = new StubMeterService(MetricsType.COUNTER);

        MeterFactory factory = new MeterFactory(List.of(first, second));

        assertThat(factory.getMeterService(MetricsType.COUNTER)).isPresent();
        assertThat(factory.getMeterService(MetricsType.COUNTER).get()).isSameAs(second);
    }

    /**
     * Simple stub implementation of {@link MeterService} for testing purposes.
     * Records are no-ops and the type is configurable via the constructor.
     */
    private static class StubMeterService implements MeterService {
        private final MetricsType type;

        /**
         * Constructs a stub with the given metrics type.
         *
         * @param type the {@link MetricsType} this stub reports
         */
        StubMeterService(MetricsType type) {
            this.type = type;
        }

        @Override
        public MetricsType getType() {
            return type;
        }

        @Override
        public void record(Metrics metrics) {
            // no-op for testing
        }
    }
}
