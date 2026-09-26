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

import io.github.yubrajsahoo.smf4j.api.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4j.api.domain.Metrics;
import io.github.yubrajsahoo.smf4j.api.enums.MetricsType;
import io.github.yubrajsahoo.smf4j.core.mapper.TagsMapper;
import io.github.yubrajsahoo.smf4j.core.service.MeterService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service implementation responsible for handling and recording counter metrics.
 * <p>
 * This class implements the {@link MeterService} interface to process metrics
 * of type {@link MetricsType#COUNTER}. It interacts with the provided Micrometer
 * {@link MeterRegistry} to increment counter values based on the configuration
 * defined in the {@link Metrics} payload.
 * </p>
 */
public class CounterMeterService implements MeterService {
    private static final Logger log = LoggerFactory.getLogger(CounterMeterService.class);
    private static final MetricsType METRICS_TYPE = MetricsType.COUNTER;

    private final MeterRegistry meterRegistry;

    /**
     * Constructs a new {@code CounterMetricsService} with the specified meter registry.
     *
     * @param meterRegistry the Micrometer {@link MeterRegistry} used to register and record metrics
     */
    public CounterMeterService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * Retrieves the type of metric this service is responsible for handling.
     *
     * @return the {@link MetricsType} supported by this service
     */
    @Override
    public MetricsType getType() {
        return METRICS_TYPE;
    }

    /**
     * Records or registers the given metric data.
     *
     * @param metrics the {@link Metrics} object containing the configuration and payload to be recorded
     */
    @Override
    public void recordMetrics(Metrics metrics) {
        if (metrics instanceof CounterMetrics counterMetrics) {
            Counter.builder(counterMetrics.getName())
                    .description(counterMetrics.getDescription())
                    .tags(TagsMapper.map(counterMetrics.getTags()))
                    .register(meterRegistry)
                    .increment(counterMetrics.getIncrement());
        } else {
            log.debug("Not able to store metrics with type: {}", metrics);
            throw new IllegalArgumentException("Invalid metrics type for CounterMeterService");
        }
    }
}
