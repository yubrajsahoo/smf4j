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

package io.github.yubrajsahoo.smf4j.api.annotation;

import io.github.yubrajsahoo.smf4j.api.constant.MetricsConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable gauge metric tracking on a method or field.
 * <p>
 * When a method or field is annotated with {@code @Gauge}, an aspect intercepts or registers it
 * to supply a gauge metric with the specified name, description, and dynamic/static tags.
 * </p>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * @Gauge(
 *     name = "queue.size",
 *     description = "Current size of the processing queue",
 *     tags = {
 *         @Tags(key = "type", value = "task")
 *     }
 * )
 * public int getQueueSize() {
 *     return queue.size();
 * }
 * }</pre>
 *
 * @see Tags
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Gauge {

    /**
     * The unique name identifying the gauge metric.
     *
     * @return the metric name
     */
    String name();

    /**
     * A human-readable description of what this gauge measures.
     *
     * @return the description of the gauge metric, defaults to {@value MetricsConstant#NONE}
     */
    String description() default MetricsConstant.NONE;

    /**
     * An array of {@link Tags} representing key-value dimensions for the gauge.
     * <p>
     * Tag values can be literal strings or Spring Expression Language (SpEL) expressions
     * evaluated against method arguments, return value ({@code #result}), or thrown exception ({@code #error}).
     * </p>
     *
     * @return an array of {@link Tags}, defaults to an empty array
     */
    Tags[] tags() default {};

    /**
     * A SpEL expression evaluated against the annotated element's value to produce the gauge value.
     * For example, if annotating a List field, expression could be "size()".
     *
     * @return the SpEL expression, defaults to empty
     */
    String expression() default "";

    /**
     * Parameter to enable or disable the metrics collection.
     *
     * @return true or false to enable or disable the metrics collection, defaults to true
     */
    boolean enable() default true;
}
