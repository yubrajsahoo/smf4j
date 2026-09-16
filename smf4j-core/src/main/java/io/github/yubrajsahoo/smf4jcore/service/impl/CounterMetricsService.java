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

package io.github.yubrajsahoo.smf4jcore.service.impl;

import io.github.yubrajsahoo.smf4jcore.annotation.Counter;
import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.factory.MeterFactory;
import io.github.yubrajsahoo.smf4jcore.logger.MetricsLogger;
import io.github.yubrajsahoo.smf4jcore.service.MetricsService;
import io.github.yubrajsahoo.smf4jcore.spel.SpelEvaluator;
import io.micrometer.core.instrument.Tags;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Service for recording counter metrics.
 */
public class CounterMetricsService extends MetricsService {
    /**
     * Constructs a new {@link CounterMetricsService}.
     *
     * @param spelEvaluator the SpEL evaluator
     * @param meterFactory the meter factory
     * @param metricsLogger the metrics logger
     */
    public CounterMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        super(spelEvaluator, meterFactory, metricsLogger);
    }

    /**
     * Records a counter metric by evaluating dynamic tag expressions with SpEL,
     * building {@link CounterMetrics}, logging the metric details, and delegating
     * to the corresponding {@link io.github.yubrajsahoo.smf4jcore.meter.service.MeterService}.
     *
     * @param counter the {@link Counter} annotation metadata
     * @param context the SpEL evaluation context containing invocation variables
     */
    public void recordCounter(Counter counter, StandardEvaluationContext context) {
        if (counter == null) {
            log.warn("Cannot record metrics for null Counter annotation");
            return;
        }

        try {
            Tags tags = evaluateTags(counter.tags(), context);

            CounterMetrics metrics = CounterMetrics.builder()
                    .name(counter.name())
                    .description(counter.description())
                    .tags(tags)
                    .enabled(counter.enable())
                    .increment(counter.increment())
                    .build();

            recordMetrics(MetricsType.COUNTER, metrics);
        } catch (Exception exception) {
            log.error("Error while recording counter metric '{}': {}", counter.name(), exception.getMessage(), exception);
        }
    }
}
