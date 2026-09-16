
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

package io.github.yubrajsahoo.smf4jcore.timer.annotation;

import io.github.yubrajsahoo.smf4jcore.core.annotation.Tags;
import io.github.yubrajsahoo.smf4jcore.core.constant.MetricsConstant;

import java.lang.annotation.*;

/**
 * Annotation to enable execution time tracking on a method.
 * <p>
 * When a method is annotated with {@code @Timer}, an aspect intercepts the method execution
 * and records its duration into a Micrometer {@link io.micrometer.core.instrument.Timer} metric
 * with the specified name, description, and dynamic/static tags.
 * </p>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * @Timer(
 *     name = "process.data.time",
 *     description = "Measures the time taken to process data",
 *     tags = {
 *         @Tags(key = "type", value = "#data.type"),
 *         @Tags(key = "status", value = "#result.status")
 *     }
 * )
 * public ProcessResult processData(Data data) {
 *     // business logic
 *     return new ProcessResult("SUCCESS");
 * }
 * }</pre>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @see Tags
 * @since 0.0.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Timer {

    /**
     * The unique name identifying the timer metric.
     *
     * @return the metric name
     */
    String name();

    /**
     * A human-readable description of what this timer measures.
     *
     * @return the description of the timer metric, defaults to {@value MetricsConstant#NONE}
     */
    String description() default MetricsConstant.NONE;

    /**
     * An array of {@link Tags} representing key-value dimensions for the timer.
     * <p>
     * Tag values can be literal strings or Spring Expression Language (SpEL) expressions
     * evaluated against method arguments, return value ({@code #result}), or thrown exception ({@code #error}).
     * </p>
     *
     * @return an array of {@link Tags}, defaults to an empty array
     */
    Tags[] tags() default {};

    /**
     * Parameter to enable or disable the metrics collection.
     *
     * @return true or false to enable or disable the metrics collection, defaults to true
     */
    boolean enable() default true;
}
