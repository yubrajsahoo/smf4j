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

package io.github.yubrajsahoo.smf4jcore.domain;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.lang.Nullable;
import java.util.function.ToDoubleFunction;

/**
 * Domain model representing the configuration and payload for a gauge metric.
 * <p>
 * Extends {@link Metrics} to include gauge-specific attributes, primarily {@link #getInstance()} and {@link #getFunction()}.
 * Instances can be constructed directly using the default constructor or fluently using {@link #builder(String, Object, ToDoubleFunction)}.
 * </p>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * GaugeMetrics<Queue<?>> metrics = GaugeMetrics.builder("queue.size", queue, Queue::size)
 *     .description("Current size of the queue")
 *     .tags(Tags.of("type", "task"))
 *     .build();
 * }</pre>
 *
 * @param <T> the type of the object monitored by this gauge
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @see Metrics
 * @see Builder
 * @since 0.0.1
 */
public class GaugeMetrics<T> extends Metrics {

    private T instance;

    private ToDoubleFunction<T> function;

    /**
     * Constructs a new default {@link GaugeMetrics} instance.
     */
    public GaugeMetrics() {
        super();
    }

    /**
     * Gets the object instance to be monitored by the gauge.
     *
     * @return the monitored instance
     */
    @Nullable
    public T getInstance() {
        return instance;
    }

    /**
     * Sets the object instance to be monitored by the gauge.
     *
     * @param instance the monitored instance
     */
    public void setInstance(@Nullable T instance) {
        this.instance = instance;
    }

    /**
     * Gets the function that produces the gauge value from the monitored instance.
     *
     * @return the gauge function
     */
    public ToDoubleFunction<T> getFunction() {
        return function;
    }

    /**
     * Sets the function that produces the gauge value from the monitored instance.
     *
     * @param function the gauge function
     */
    public void setFunction(ToDoubleFunction<T> function) {
        this.function = function;
    }

    /**
     * Creates a new fluent {@link Builder} instance for constructing {@link GaugeMetrics}.
     *
     * @param name the gauge metric name
     * @param obj the object to monitor
     * @param f the value-producing function
     * @param <T> the type of the object monitored by this gauge
     * @return a new {@link Builder} instance
     */
    public static <T> Builder<T> builder(String name, @Nullable T obj, ToDoubleFunction<T> f) {
        return new Builder<>(name, obj, f);
    }

    /**
     * A fluent builder for creating and configuring {@link GaugeMetrics} instances.
     *
     * @param <T> the type of the object monitored by this gauge
     * @author Yubraj Sahoo
     * @version 0.0.1
     * @since 0.0.1
     */
    public static class Builder<T> {

        private final GaugeMetrics<T> metrics;

        /**
         * Constructs a new {@link Builder} initialized with a fresh {@link GaugeMetrics} instance.
         *
         * @param name the gauge metric name
         * @param obj the object to monitor
         * @param f the value-producing function
         */
        private Builder(String name, @Nullable T obj, ToDoubleFunction<T> f) {
            this.metrics = new GaugeMetrics<>();
            this.metrics.setName(name);
            this.metrics.setInstance(obj);
            this.metrics.setFunction(f);
        }

        /**
         * Sets the description of the gauge metric.
         *
         * @param description the gauge description
         * @return this builder instance for method chaining
         */
        public Builder<T> description(String description) {
            metrics.setDescription(description);
            return this;
        }

        /**
         * Sets the Micrometer {@link Tags} for the gauge.
         *
         * @param tags the metric tags
         * @return this builder instance for method chaining
         */
        public Builder<T> tags(Tags tags) {
            metrics.setTags(tags);
            return this;
        }

        /**
         * Sets whether the gauge metric is enabled.
         *
         * @param enabled {@code true} to enable, {@code false} to disable
         * @return this builder instance for method chaining
         */
        public Builder<T> enabled(boolean enabled) {
            metrics.setEnabled(enabled);
            return this;
        }

        /**
         * Builds and returns the configured {@link GaugeMetrics} instance.
         *
         * @return the constructed {@link GaugeMetrics}
         */
        public GaugeMetrics<T> build() {
            return metrics;
        }
    }
}
