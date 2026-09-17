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

package io.github.yubrajsahoo.smf4j.core.service.impl;

import io.github.yubrajsahoo.smf4j.api.domain.GaugeMetrics;
import io.github.yubrajsahoo.smf4j.api.domain.Metrics;
import io.github.yubrajsahoo.smf4j.api.enums.MetricsType;
import io.github.yubrajsahoo.smf4j.core.mapper.TagsMapper;
import io.github.yubrajsahoo.smf4j.core.service.MeterService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for registering gauge metrics with a {@link MeterRegistry}.
 * <p>
 * This class implements the {@link MeterService} interface to process metrics
 * of type {@link MetricsType#GAUGE}.
 * </p>
 */
public class GaugeMeterService implements MeterService {
    private static final Logger log = LoggerFactory.getLogger(GaugeMeterService.class);
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
            log.debug("Not able to store metrics with type: {}", metrics);
            throw new IllegalArgumentException("Invalid metrics type for GaugeMeterService");
        }
    }

    private <T> void registerGauge(GaugeMetrics<T> gaugeMetrics) {
        Gauge.builder(gaugeMetrics.getName(), gaugeMetrics.getInstance(), gaugeMetrics.getFunction())
                .description(gaugeMetrics.getDescription())
                .tags(TagsMapper.map(gaugeMetrics.getTags()))
                .register(meterRegistry);
    }
}
