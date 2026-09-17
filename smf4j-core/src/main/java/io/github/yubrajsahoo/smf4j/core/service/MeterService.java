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

package io.github.yubrajsahoo.smf4j.core.service;

import io.github.yubrajsahoo.smf4j.api.domain.Metrics;
import io.github.yubrajsahoo.smf4j.api.enums.MetricsType;

/**
 * Defines the contract for services that record and process specific types of metrics.
 * <p>
 * Implementations of this interface are responsible for handling a specific {@link MetricsType}
 * (e.g., Counter, Timer, Gauge) and registering the corresponding telemetry data
 * into the underlying metric registry (such as Micrometer).
 * </p>
 */
public interface MeterService {

    /**
     * Retrieves the type of metric this service is responsible for handling.
     *
     * @return the {@link MetricsType} supported by this service
     */
    MetricsType getType();

    /**
     * Records or registers the given metric data.
     *
     * @param metrics the {@link Metrics} object containing the configuration and payload to be recorded
     */
    void recordMetrics(Metrics metrics);

    /**
     * Starts a new {@link io.micrometer.core.instrument.Timer.Sample} to measure execution time.
     * <p>
     * This method is intended to be overridden by services that deal with timing metrics.
     * By default, it throws an {@link IllegalArgumentException} if the underlying implementation
     * does not support timer sampling.
     * </p>
     *
     * @return a new {@link io.micrometer.core.instrument.Timer.Sample} instance
     * @throws IllegalArgumentException if the operation is not supported by the implementation
     */
    default io.micrometer.core.instrument.Timer.Sample start() {
        throw new IllegalArgumentException("Not supported");
    }
}
