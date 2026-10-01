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
import io.github.yubrajsahoo.smf4j.api.enums.LogLevel;

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

    /**
     * Logs the details of a recorded {@link Metrics}.
     *
     * @param metrics the metric data to log
     */
    public abstract void log(Metrics metrics);

    /**
     * Logs the details of a recorded {@link Metrics}.
     *
     * @param metrics the metric data to log
     * @param level the log level
     */
    public abstract void log(Metrics metrics,LogLevel level);

    /**
     * Prepares a formatted log message string containing common metric attributes (name, tags, description).
     *
     * @param message the prefix message
     * @param metrics the metric whose attributes are being formatted
     * @return the formatted log message string
     */
    protected String prepareLog(String message, Metrics metrics) {
        return message + "name=" +
                metrics.getName() +
                prepareTagsLog(metrics) +
                "->" +
                "description=" +
                metrics.getDescription();
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
}
