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

import io.github.yubrajsahoo.smf4jcore.domain.Metrics;
import io.github.yubrajsahoo.smf4jcore.domain.TimerMetrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.meter.service.MeterService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

/**
 * Implementation of {@link MeterService} specifically designed to handle {@link MetricsType#TIMER} metrics.
 * <p>
 * This service uses a {@link MeterRegistry} to start timing samples and record execution durations.
 * It provides the core functionality to interact with the underlying Micrometer registry for timer metrics.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @see MeterService
 * @since 0.0.1
 */
public class TimerMeterService implements MeterService {
    private static final MetricsType METRICS_TYPE = MetricsType.TIMER;

    private final MeterRegistry meterRegistry;

    /**
     * Constructs a new {@code TimerMeterService} with the specified {@link MeterRegistry}.
     *
     * @param meterRegistry the micrometer registry used for recording timer metrics
     */
    public TimerMeterService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Gets the {@link MetricsType} supported by this service implementation.
     *
     * @return the supported {@link MetricsType}
     */
    @Override
    public MetricsType getType() {
        return METRICS_TYPE;
    }

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
    @Override
    public Timer.Sample start() {
        return Timer.start(meterRegistry);
    }

    /**
     * Records the given metric data into the underlying metric registry.
     *
     * @param metrics the metric object containing name, tags, description, and values to record
     * @throws IllegalArgumentException if the provided metric object is null or incompatible with this meter service
     */
    @Override
    public void recordMetrics(Metrics metrics) {
        if (metrics instanceof TimerMetrics timerMetrics && timerMetrics.getSample() != null) {
            Timer timer = Timer.builder(timerMetrics.getName())
                    .description(timerMetrics.getDescription())
                    .tags(timerMetrics.getTags())
                    .register(meterRegistry);

            timerMetrics.getSample().stop(timer);
        } else {
            throw new IllegalArgumentException("Invalid metrics type for TimerMeterService");
        }
    }
}
