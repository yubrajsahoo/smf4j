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

package io.github.yubrajsahoo.smf4j.core.logger.impl;

import io.github.yubrajsahoo.smf4j.api.config.Smf4jMetricsProperties;
import io.github.yubrajsahoo.smf4j.api.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4j.api.domain.Metrics;
import io.github.yubrajsahoo.smf4j.core.logger.MetricsLogger;

/**
 * Default implementation of {@link MetricsLogger} for formatting and logging metric recording events at info level.
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
public class DefaultMetricsLogger extends MetricsLogger {
    private final Smf4jMetricsProperties metricsProperties;

    public DefaultMetricsLogger(Smf4jMetricsProperties smf4jMetricsProperties) {
        this.metricsProperties = smf4jMetricsProperties;
    }

    /**
     * Logs the details of a recorded {@link Metrics}.
     * <p>
     * Formats the metric data using {@link #prepareLog(String, Metrics)}.
     * If the metric is an instance of {@link CounterMetrics}, it appends the increment value to the log message.
     * The final message is logged at the configured level.
     * </p>
     *
     * @param metrics the metric data to log
     */
    @Override
    public void log(Metrics metrics) {
        String message = metrics.isEnable()
                ? metricsProperties.getLogMessage()
                : metricsProperties.getDisableLogMessage();

        String level = metrics.isEnable()
                ? metricsProperties.getLogLevel()
                : metricsProperties.getDisableLogLevel();

        String preparedLog = prepareLog(message, metrics);

        if (metrics instanceof CounterMetrics counterMetrics) {
            preparedLog += "->" + "increment=" + counterMetrics.getIncrement();
        }

        logMessage(level, preparedLog);
    }
}
