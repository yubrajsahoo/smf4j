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

import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.domain.Metrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.meter.service.MeterService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Implementation of {@link MeterService} dedicated to registering and incrementing
 * Micrometer {@link Counter} instances in a {@link MeterRegistry}.
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @see MeterService
 * @see CounterMetrics
 * @see MeterRegistry
 * @since 0.0.1
 */
public class CounterMeterService implements MeterService {

    private static final MetricsType METRICS_TYPE = MetricsType.COUNTER;

    private final MeterRegistry meterRegistry;

    /**
     * Constructs a new {@link CounterMeterService} with the specified {@link MeterRegistry}.
     *
     * @param meterRegistry the Micrometer meter registry to register counters with
     */
    public CounterMeterService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link MetricsType#COUNTER}
     */
    @Override
    public MetricsType getType() {
        return METRICS_TYPE;
    }

    /**
     * Records a counter metric by registering it with the {@link MeterRegistry} and incrementing its value.
     *
     * @param metrics the metric object, expected to be an instance of {@link CounterMetrics}
     * @throws IllegalArgumentException if {@code metrics} is {@code null} or not an instance of {@link CounterMetrics}
     */
    @Override
    public void record(Metrics metrics) {
        if (metrics instanceof CounterMetrics counterMetrics) {
            Counter.builder(counterMetrics.getName())
                    .description(counterMetrics.getDescription())
                    .tags(counterMetrics.getTags())
                    .register(meterRegistry)
                    .increment(counterMetrics.getIncrement());
        } else {
            throw new IllegalArgumentException("Invalid metrics type for CounterMeterService");
        }
    }
}
