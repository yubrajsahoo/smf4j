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

import io.github.yubrajsahoo.smf4jcore.annotation.Counter;
import io.github.yubrajsahoo.smf4jcore.annotation.Timer;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Service interface for processing metric annotations and dispatching recordings to meter services.
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @see io.github.yubrajsahoo.smf4jcore.service.impl.MetricsServiceImpl
 * @since 0.0.1
 */
public interface MetricsService {

    /**
     * Starts a new {@link io.micrometer.core.instrument.Timer.Sample} to measure execution time.
     * <p>
     * This method is a convenience wrapper for initiating a timing sample which
     * can later be stopped and recorded against a specific timer metric.
     * </p>
     *
     * @return a new {@link io.micrometer.core.instrument.Timer.Sample} instance
     */
    io.micrometer.core.instrument.Timer.Sample start();

    /**
     * Processes and records a counter metric based on the metadata in {@link Counter}
     * and the given SpEL {@link StandardEvaluationContext}.
     *
     * @param counter the {@link Counter} annotation containing metric definition and metadata
     * @param context the SpEL evaluation context providing variables for dynamic tag resolution
     */
    void recordMetrics(Counter counter, StandardEvaluationContext context);

    /**
     * Processes and records a timer metric based on the metadata in {@link Timer}
     * and the given SpEL {@link StandardEvaluationContext}.
     *
     * @param sample the {@link io.micrometer.core.instrument.Timer.Sample} to capture letency
     * @param timer   the {@link Timer} annotation containing metric definition and metadata
     * @param context the SpEL evaluation context providing variables for dynamic tag resolution
     */
    void recordMetrics(io.micrometer.core.instrument.Timer.Sample sample, Timer timer, StandardEvaluationContext context);
}