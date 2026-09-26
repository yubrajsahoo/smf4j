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

import io.github.yubrajsahoo.smf4j.api.annotation.Timer;
import io.github.yubrajsahoo.smf4j.api.domain.Tag;
import io.github.yubrajsahoo.smf4j.api.domain.TimerMetrics;
import io.github.yubrajsahoo.smf4j.api.enums.MetricsType;
import io.github.yubrajsahoo.smf4j.core.factory.MeterFactory;
import io.github.yubrajsahoo.smf4j.core.logger.MetricsLogger;
import io.github.yubrajsahoo.smf4j.core.service.MeterService;
import io.github.yubrajsahoo.smf4j.engine.service.MetricsService;
import io.github.yubrajsahoo.smf4j.engine.spel.SpelEvaluator;
import io.micrometer.core.instrument.Timer.Sample;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;

/**
 * Service for starting and recording timer metrics.
 */
public class TimerMetricsService extends MetricsService {

    /**
     * Constructs a new {@link TimerMetricsService}.
     *
     * @param spelEvaluator the SpEL evaluator
     * @param meterFactory  the meter factory
     * @param metricsLogger the metrics logger
     */
    public TimerMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        super(spelEvaluator, meterFactory, metricsLogger);
    }

    /**
     * Starts a timer sample.
     *
     * @param timer the timer annotation metadata
     * @return the started timer sample, or {@code null} if the timer is not enabled or meter service is unavailable
     */
    public Sample start(Timer timer) {
        if (timer == null || !timer.enable()) {
            return null;
        }
        return meterFactory.getMeterService(MetricsType.TIMER)
                .map(MeterService::start)
                .orElse(null);
    }

    /**
     * Records the timer metric using the provided sample and evaluation context.
     *
     * @param sample  the timer sample to stop and record
     * @param timer   the timer annotation metadata
     * @param context the SpEL evaluation context
     */
    public void recordTimer(Sample sample, Timer timer, StandardEvaluationContext context) {
        if (timer == null) {
            log.warn("Cannot record metrics for null Timer annotation or null sample");
            return;
        }

        try {
            List<Tag> tags = evaluateTags(timer.tags(), context);

            TimerMetrics metrics = TimerMetrics.builder()
                    .name(timer.name())
                    .description(timer.description())
                    .tags(tags)
                    .enable(timer.enable())
                    .sample(sample)
                    .build();

            recordMetrics(MetricsType.TIMER, metrics);
        } catch (Exception exception) {
            log.error("Error while recording timer metric '{}': {}", timer.name(), exception.getMessage(), exception);
        }
    }
}