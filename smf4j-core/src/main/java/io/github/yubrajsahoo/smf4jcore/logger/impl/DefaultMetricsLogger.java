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

package io.github.yubrajsahoo.smf4jcore.logger.impl;

import io.github.yubrajsahoo.smf4jcore.constants.MetricsConstant;
import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.domain.Metrics;
import io.github.yubrajsahoo.smf4jcore.logger.MetricsLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link MetricsLogger} for formatting and logging metric recording events at info level.
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
public class DefaultMetricsLogger implements MetricsLogger {

    /**
     * Default constructor for DefaultMetricsLogger.
     */
    public DefaultMetricsLogger() {
    }

    private static final Logger log = LoggerFactory.getLogger(DefaultMetricsLogger.class);

    /**
     * Prepares a formatted log message string containing common metric attributes (name, tags, description).
     *
     * @param message the prefix message
     * @param metrics the metric whose attributes are being formatted
     * @return the formatted log message string
     */
    protected String prepareLog(String message, Metrics metrics) {
        StringBuilder logBuilder = new StringBuilder(message);
        logBuilder.append("name=")
                .append(metrics.getName());

        metrics.getTags().forEach(tag -> logBuilder.append("->")
                .append(tag.getKey())
                .append("=")
                .append(tag.getValue()));

        logBuilder
                .append("->")
                .append("description=")
                .append(metrics.getDescription());

        return logBuilder.toString();
    }

    /**
     * Logs the details of a recorded {@link Metrics}.
     * <p>
     * Formats the metric data using {@link #prepareLog(String, Metrics)}.
     * If the metric is an instance of {@link CounterMetrics}, it appends the increment value to the log message.
     * The final message is logged at the info level.
     * </p>
     *
     * @param metrics the metric data to log
     */
    @Override
    public void log(Metrics metrics) {
        String message = metrics.isEnabled()
                ? MetricsConstant.DEFAULT_LOG_MESSAGE
                : MetricsConstant.DEFAULT_DISABLED_LOG_MESSAGE;

        String logMessage = prepareLog(message, metrics);

        if (metrics instanceof CounterMetrics counterMetrics) {
            logMessage += "->" + "increment=" + counterMetrics.getIncrement();
        }

        log.info(logMessage);
    }
}
