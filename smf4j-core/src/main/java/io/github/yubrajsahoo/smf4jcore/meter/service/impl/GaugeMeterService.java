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

import io.github.yubrajsahoo.smf4jcore.domain.GaugeMetrics;
import io.github.yubrajsahoo.smf4jcore.domain.Metrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.meter.service.MeterService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Service for registering gauge metrics with a {@link MeterRegistry}.
 */
public class GaugeMeterService implements MeterService {
    private static final MetricsType METRICS_TYPE = MetricsType.GAUGE;

    private final MeterRegistry meterRegistry;

    /**
     * Constructs a new {@link GaugeMeterService}.
     *
     * @param meterRegistry the meter registry
     */
    public GaugeMeterService(MeterRegistry meterRegistry) {
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
     * Records the given metric data into the underlying metric registry.
     *
     * @param metrics the metric object containing name, tags, description, and values to record
     * @throws IllegalArgumentException if the provided metric object is null or incompatible with this meter service
     */
    @Override
    public void recordMetrics(Metrics metrics) {
        if (metrics instanceof GaugeMetrics<?> gaugeMetrics) {
            registerGauge(gaugeMetrics);
        } else {
            throw new IllegalArgumentException("Invalid metrics type for GaugeMeterService");
        }
    }

    private <T> void registerGauge(GaugeMetrics<T> gaugeMetrics) {
        Gauge.builder(gaugeMetrics.getName(), gaugeMetrics.getInstance(), gaugeMetrics.getFunction())
                .description(gaugeMetrics.getDescription())
                .tags(gaugeMetrics.getTags())
                .register(meterRegistry)
                .measure();
    }
}
