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
 * Annotation to enable counter metric tracking on a method.
 * <p>
 * When a method is annotated with {@code @Counter}, an aspect intercepts the method execution
 * and increments a metric with the specified name, description, dynamic/static tags, and increment amount.
 * </p>
 *
 * <h2>Example Usage</h2>
 * <pre>{@code
 * @Counter(
 *     name = "orders.created",
 *     description = "Counts the total number of orders placed",
 *     tags = {
 *         @Tags(key = "currency", value = "#order.currency"),
 *         @Tags(key = "status", value = "#result.status")
 *     },
 *     increment = 1
 * )
 * public Order createOrder(Order order) {
 *     // business logic
 *     return order;
 * }
 * }</pre>
 *
 * @see Tags
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Counter {

    /**
     * The unique name identifying the counter metric.
     *
     * @return the metric name
     */
    String name();

    /**
     * A human-readable description of what this counter measures.
     *
     * @return the description of the counter metric, defaults to {@value MetricsConstant#NONE}
     */
    String description() default MetricsConstant.NONE;

    /**
     * An array of {@link Tags} representing key-value dimensions for the counter.
     * <p>
     * Tag values can be literal strings or Spring Expression Language (SpEL) expressions
     * evaluated against method arguments, return value ({@code #result}), or thrown exception ({@code #error}).
     * </p>
     *
     * @return an array of {@link Tags}, defaults to an empty array
     */
    Tags[] tags() default {};

    /**
     * The fixed amount by which the counter is incremented on each invocation.
     *
     * @return the increment step value, defaults to {@code 1}
     */
    long increment() default 1;

    /**
     * Parameter to enable or disable the metrics collection.
     *
     * @return true or false to enable or disable the metrics collection, defaults to true
     */
    boolean enable() default true;
}
