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

package io.github.yubrajsahoo.smf4j.core.logger;


import io.github.yubrajsahoo.smf4j.api.domain.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface for logging metrics.
 * <p>
 * Implementations of this interface can be provided to customize the metric logging style.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
public abstract class MetricsLogger {
    private static final Logger log = LoggerFactory.getLogger(MetricsLogger.class);


    /**
     * Logs the details of a recorded {@link Metrics}.
     *
     * @param metrics the metric data to log
     */
    public abstract void log(Metrics metrics);

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

        logBuilder.append(prepareTagsLog(metrics));

        logBuilder
                .append("->")
                .append("description=")
                .append(metrics.getDescription());

        return logBuilder.toString();
    }

    /**
     * Prepares a formatted string containing only the metric tags.
     *
     * @param metrics the metric whose tags are being formatted
     * @return the formatted tags log string
     */
    protected String prepareTagsLog(Metrics metrics) {
        StringBuilder tagsBuilder = new StringBuilder();
        metrics.getTags().forEach(tag -> tagsBuilder.append("->")
                .append(tag.getKey())
                .append("=")
                .append(tag.getValue()));
        return tagsBuilder.toString();
    }

    /**
     * Logs the provided message at the specified log level.
     * <p>
     * Supported log levels are "DEBUG", "INFO", "WARN", and "ERROR". If an unsupported
     * or null log level is provided, it defaults to the INFO level.
     * </p>
     *
     * @param logLevel   the target log level (e.g., "DEBUG", "INFO", "WARN", "ERROR")
     * @param logMessage the formatted metric message to be logged
     */
    protected void logMessage(String logLevel, String logMessage) {
        switch (logLevel) {
            case "DISABLED":
                break;
            case "DEBUG":
                if (log.isDebugEnabled()) {
                    log.debug(logMessage);
                }
                break;
            case "WARN":
                if (log.isWarnEnabled()) {
                    log.warn(logMessage);
                }
                break;
            case "ERROR":
                if (log.isErrorEnabled()) {
                    log.error(logMessage);
                }
                break;
            case "INFO":
            default:
                if (log.isInfoEnabled()) {
                    log.info(logMessage);
                }
                break;
        }
    }
}
