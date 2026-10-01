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

package io.github.yubrajsahoo.smf4j.engine.service.impl;

import io.github.yubrajsahoo.smf4j.api.annotation.Counter;
import io.github.yubrajsahoo.smf4j.api.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4j.api.domain.Metrics;
import io.github.yubrajsahoo.smf4j.api.domain.Tag;
import io.github.yubrajsahoo.smf4j.api.enums.LogLevel;
import io.github.yubrajsahoo.smf4j.api.enums.MetricsType;
import io.github.yubrajsahoo.smf4j.core.factory.MeterFactory;
import io.github.yubrajsahoo.smf4j.core.logger.MetricsLogger;
import io.github.yubrajsahoo.smf4j.core.service.MeterService;
import io.github.yubrajsahoo.smf4j.engine.service.MetricsService;
import io.github.yubrajsahoo.smf4j.engine.spel.SpelEvaluator;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

/**
 * Service for recording counter metrics.
 */
public class CounterMetricsService extends MetricsService {
    /**
     * Constructs a new {@link CounterMetricsService}.
     *
     * @param spelEvaluator the SpEL evaluator
     * @param meterFactory  the meter factory
     * @param metricsLogger the metrics logger
     */
    public CounterMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        super(spelEvaluator, meterFactory, metricsLogger);
    }

    /**
     * Records a counter metric by evaluating dynamic tag expressions with SpEL,
     * building {@link CounterMetrics}, logging the metric details, and delegating
     * to the corresponding {@link MeterService}.
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
            List<Tag> tags = evaluateTags(counter.tags(), context);

            CounterMetrics metrics = CounterMetrics.builder()
                    .name(counter.name())
                    .description(counter.description())
                    .tags(tags)
                    .enable(counter.enable())
                    .increment(counter.increment())
                    .build();

            recordMetrics(MetricsType.COUNTER, metrics);
        } catch (Exception exception) {
            log.error("Error while recording counter metric '{}': {}", counter.name(), exception.getMessage(), exception);
        }
    }

    /**
     * Records a counter metric using the provided metrics data and log level.
     * <p>
     * This method logs the metrics using the configured {@link MetricsLogger} at the specified {@link LogLevel}.
     * If the metric is enabled, it delegates the actual recording of the metric to the appropriate
     * {@link io.github.yubrajsahoo.smf4j.core.service.MeterService} obtained from the {@link MeterFactory}
     * for the {@link MetricsType#COUNTER}.
     * </p>
     *
     * @param metrics the metrics data to be recorded
     * @param level   the log level at which the metrics should be logged
     */
    public void recordCounter(Metrics metrics, LogLevel level) {
        metricsLogger.log(metrics, level);

        if (!metrics.isEnable()) {
            return;
        }

        meterFactory.getMeterService(MetricsType.COUNTER)
                .ifPresentOrElse(
                        meterService -> meterService.recordMetrics(metrics),
                        () -> log.warn("No MeterService found for metrics type: {}", MetricsType.COUNTER)
                );
    }
}