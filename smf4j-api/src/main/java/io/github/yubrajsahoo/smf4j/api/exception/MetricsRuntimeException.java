/*
 * Copyright 2024 Yubraj Sahoo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.yubrajsahoo.smf4j.api.exception;

import io.github.yubrajsahoo.smf4j.api.constant.MetricsConstant;

/**
 * Base unchecked exception for the SMF4J API.
 * <p>
 * This exception allows associating a specific metrics string with the runtime ERROR_METRICS,
 * providing additional context about the metric that caused or is related to the issue.
 * </p>
 */
public class MetricsRuntimeException extends RuntimeException {

    /**
     * The metric string associated with this exception, if any.
     */
    private final String metrics;

    /**
     * Constructs a new {@code MetricsRuntimeException} with the specified detail message.
     *
     * @param message the detail message
     */
    public MetricsRuntimeException(String message) {
        super(message);
        this.metrics = MetricsConstant.ERROR_METRICS;
    }

    /**
     * Constructs a new {@code MetricsRuntimeException} with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public MetricsRuntimeException(String message, Throwable cause) {
        super(message, cause);
        this.metrics = MetricsConstant.ERROR_METRICS;
    }

    /**
     * Constructs a new {@code MetricsRuntimeException} with the specified detail message and associated metrics string.
     *
     * @param message the detail message
     * @param metrics the metrics string related to this exception
     */
    public MetricsRuntimeException(String message, String metrics) {
        super(message);
        this.metrics = metrics;
    }

    /**
     * Constructs a new {@code MetricsRuntimeException} with the specified detail message, cause, and associated metrics string.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     * @param metrics the metrics string related to this exception
     */
    public MetricsRuntimeException(String message, Throwable cause, String metrics) {
        super(message, cause);
        this.metrics = metrics;
    }

    /**
     * Retrieves the metrics string associated with this exception.
     *
     * @return the associated metrics string, or {@code null} if none was provided
     */
    public String getMetrics() {
        return metrics;
    }
}
