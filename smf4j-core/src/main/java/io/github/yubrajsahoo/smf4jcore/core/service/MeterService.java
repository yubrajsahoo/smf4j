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

package io.github.yubrajsahoo.smf4jcore.core.service;

import io.github.yubrajsahoo.smf4jcore.core.domain.Metrics;
import io.github.yubrajsahoo.smf4jcore.core.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.core.factory.MeterFactory;
import io.github.yubrajsahoo.smf4jcore.counter.service.CounterMeterService;
import io.micrometer.core.instrument.Timer;

/**
 * Service interface for recording metric data into underlying meter registries (e.g. Micrometer).
 * <p>
 * Implementations are specialized to handle a specific {@link MetricsType} (such as {@link MetricsType#COUNTER}).
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @see CounterMeterService
 * @see MeterFactory
 * @since 0.0.1
 */
public interface MeterService {

    /**
     * Gets the {@link MetricsType} supported by this service implementation.
     *
     * @return the supported {@link MetricsType}
     */
    MetricsType getType();

    /**
     * Starts a new {@link Timer.Sample} to measure execution time.
     * <p>
     * This method is intended to be overridden by services that deal with timing metrics
     * (e.g., {@link MetricsType#TIMER}). By default, it throws an {@link IllegalArgumentException}
     * if the underlying implementation does not support timer sampling.
     * </p>
     *
     * @return a new {@link Timer.Sample} instance
     * @throws IllegalArgumentException if the operation is not supported by the implementation
     */
    default Timer.Sample start() {
        throw new IllegalArgumentException("Implementation not Present");
    }

    /**
     * Records the given metric data into the underlying metric registry.
     *
     * @param metrics the metric object containing name, tags, description, and values to record
     * @throws IllegalArgumentException if the provided metric object is null or incompatible with this meter service
     */
    void recordMetrics(Metrics metrics);
}