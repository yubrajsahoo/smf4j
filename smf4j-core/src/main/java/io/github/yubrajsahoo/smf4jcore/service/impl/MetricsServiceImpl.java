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
import io.github.yubrajsahoo.smf4jcore.annotation.Timer;
import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.domain.TimerMetrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.factory.MeterFactory;
import io.github.yubrajsahoo.smf4jcore.service.MetricsService;
import io.github.yubrajsahoo.smf4jcore.spel.SpelEvaluator;
import io.github.yubrajsahoo.smf4jcore.logger.MetricsLogger;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Default implementation of {@link MetricsService}.
 * <p>
 * Evaluates dynamic metric tag expressions via {@link SpelEvaluator}, constructs concrete domain models
 * like {@link CounterMetrics}, logs the recording at debug level, and delegates execution to the
 * appropriate {@link io.github.yubrajsahoo.smf4jcore.meter.service.MeterService} provided by {@link MeterFactory}.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @see MetricsService
 * @see SpelEvaluator
 * @see MeterFactory
 * @since 0.0.1
 */
public class MetricsServiceImpl implements MetricsService {

    private static final Logger log = LoggerFactory.getLogger(MetricsServiceImpl.class);

    private final SpelEvaluator spelEvaluator;
    private final MeterFactory meterFactory;
    private final MetricsLogger metricsLogger;

    /**
     * Constructs a new {@link MetricsServiceImpl} with the required dependencies.
     *
     * @param spelEvaluator the SpEL evaluator for resolving dynamic metric tags; must not be {@code null}
     * @param meterFactory  the meter factory for dispatching to specific meter services; must not be {@code null}
     * @param metricsLogger the metrics logger for logging metric details; must not be {@code null}
     * @throws NullPointerException if any dependency is {@code null}
     */
    public MetricsServiceImpl(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        this.spelEvaluator = Objects.requireNonNull(spelEvaluator, "spelEvaluator must not be null");
        this.meterFactory = Objects.requireNonNull(meterFactory, "meterFactory must not be null");
        this.metricsLogger = Objects.requireNonNull(metricsLogger, "metricsLogger must not be null");
    }

    /**
     * Starts a new {@link io.micrometer.core.instrument.Timer.Sample} to measure execution time.
     * <p>
     * This method is a convenience wrapper for initiating a timing sample which
     * can later be stopped and recorded against a specific timer metric.
     * </p>
     *
     * @return a new {@link io.micrometer.core.instrument.Timer.Sample} instance
     */
    @Override
    public io.micrometer.core.instrument.Timer.Sample start() {
        return meterFactory.getMeterService(MetricsType.TIMER)
                .orElseThrow(() -> new IllegalStateException(
                        "No MeterService found for metrics type: " + MetricsType.TIMER)
                ).start();
    }

    /**
     * Records a counter metric by evaluating dynamic tag expressions with SpEL,
     * building {@link CounterMetrics}, logging the metric details, and delegating
     * to the corresponding {@link io.github.yubrajsahoo.smf4jcore.meter.service.MeterService}.
     *
     * @param counter the {@link Counter} annotation metadata
     * @param context the SpEL evaluation context containing invocation variables
     */
    @Override
    public void recordMetrics(Counter counter, StandardEvaluationContext context) {
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

            metricsLogger.log(metrics);

            if (!metrics.isEnabled()) {
                return;
            }
            meterFactory.getMeterService(MetricsType.COUNTER)
                    .ifPresentOrElse(
                            meterService -> meterService.recordMetrics(metrics),
                            () -> log.warn("No MeterService found for metrics type: {}", MetricsType.COUNTER)
                    );
        } catch (Exception exception) {
            log.error("Error while recording counter metric '{}': {}", counter.name(), exception.getMessage(), exception);
        }
    }

    /**
     * Processes and records a timer metric based on the metadata in {@link Timer}
     * and the given SpEL {@link StandardEvaluationContext}.
     *
     * @param sample  the {@link io.micrometer.core.instrument.Timer.Sample} to capture letency
     * @param timer   the {@link Timer} annotation containing metric definition and metadata
     * @param context the SpEL evaluation context providing variables for dynamic tag resolution
     */
    @Override
    public void recordMetrics(io.micrometer.core.instrument.Timer.Sample sample, Timer timer, StandardEvaluationContext context) {
        if (timer == null) {
            log.warn("Cannot record metrics for null timer annotation");
            return;
        }

        try {
            Tags tags = evaluateTags(timer.tags(), context);

            TimerMetrics metrics = TimerMetrics.builder()
                    .name(timer.name())
                    .description(timer.description())
                    .tags(tags)
                    .enabled(timer.enable())
                    .sample(sample)
                    .build();

            metricsLogger.log(metrics);

            if (metrics.isEnabled()) {
                meterFactory.getMeterService(MetricsType.TIMER)
                        .ifPresentOrElse(
                                meterService -> meterService.recordMetrics(metrics),
                                () -> log.warn("No MeterService found for metrics type: {}", MetricsType.TIMER)
                        );
            }
        } catch (Exception exception) {
            log.error("Error while recording timer metric '{}': {}", timer.name(), exception.getMessage(), exception);
        }
    }

    /**
     * Evaluates tag expressions defined in annotations against the given SpEL evaluation context.
     *
     * @param tags    the array of {@link io.github.yubrajsahoo.smf4jcore.annotation.Tags} to evaluate
     * @param context the SpEL evaluation context
     * @return the evaluated Micrometer {@link Tags}
     */
    private Tags evaluateTags(io.github.yubrajsahoo.smf4jcore.annotation.Tags[] tags,
                              StandardEvaluationContext context) {
        if (tags == null || tags.length == 0) {
            return Tags.empty();
        }
        List<Tag> tagList = new ArrayList<>(tags.length);
        for (io.github.yubrajsahoo.smf4jcore.annotation.Tags tag : tags) {
            if (tag != null) {
                String value = spelEvaluator.evaluate(tag.value(), context);
                tagList.add(Tag.of(tag.key(), value));
            }
        }
        return Tags.of(tagList);
    }
}
