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

package io.github.yubrajsahoo.smf4jcore.service;

import io.github.yubrajsahoo.smf4jcore.domain.Metrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.factory.MeterFactory;
import io.github.yubrajsahoo.smf4jcore.logger.MetricsLogger;
import io.github.yubrajsahoo.smf4jcore.spel.SpelEvaluator;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Service interface for processing metric annotations and dispatching recordings to meter services.
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
public abstract class MetricsService {
    /** Logger instance for this class. */
    protected static final Logger log = LoggerFactory.getLogger(MetricsService.class);

    /** The SpEL evaluator used to evaluate tag values. */
    protected final SpelEvaluator spelEvaluator;
    
    /** The factory used to obtain meter services. */
    protected final MeterFactory meterFactory;
    
    /** The logger used to log metrics events. */
    protected final MetricsLogger metricsLogger;

    /**
     * Constructs a new {@link MetricsService}.
     *
     * @param spelEvaluator the SpEL evaluator
     * @param meterFactory the meter factory
     * @param metricsLogger the metrics logger
     */
    protected MetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        this.spelEvaluator = spelEvaluator;
        this.meterFactory = meterFactory;
        this.metricsLogger = metricsLogger;
    }

    /**
     * Records the given metrics using the appropriate meter service.
     *
     * @param metricsType the type of the metrics
     * @param metrics the metrics to record
     */
    protected void recordMetrics(MetricsType metricsType, Metrics metrics) {
        metricsLogger.log(metrics);

        if (!metrics.isEnabled()) {
            return;
        }

        meterFactory.getMeterService(metricsType)
                .ifPresentOrElse(
                        meterService -> meterService.recordMetrics(metrics),
                        () -> log.warn("No MeterService found for metrics type: {}", metricsType)
                );
    }

    /**
     * Evaluates tag expressions defined in annotations against the given SpEL evaluation context.
     *
     * @param tags    the array of {@link io.github.yubrajsahoo.smf4jcore.annotation.Tags} to evaluate
     * @param context the SpEL evaluation context
     * @return the evaluated Micrometer {@link Tags}
     */
    protected Tags evaluateTags(io.github.yubrajsahoo.smf4jcore.annotation.Tags[] tags,
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