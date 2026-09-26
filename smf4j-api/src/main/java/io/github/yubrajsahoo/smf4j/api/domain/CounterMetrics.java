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

package io.github.yubrajsahoo.smf4j.api.domain;

import java.util.List;

/**
 * Domain model representing the configuration and payload for a counter metric.
 * <p>
 * Extends {@link Metrics} to include counter-specific attributes, primarily the increment value.
 * Instances can be constructed directly using the default constructor or fluently using the builder.
 * </p>
 */
public class CounterMetrics extends Metrics {

    /**
     * The value by which the counter should be incremented.
     */
    private long increment = 1;

    /**
     * Constructs a new default {@link CounterMetrics} instance.
     */
    public CounterMetrics() {
        super();
    }

    /**
     * Gets the value by which the counter should be incremented.
     *
     * @return the increment value
     */
    public long getIncrement() {
        return increment;
    }

    /**
     * Sets the value by which the counter should be incremented.
     *
     * @param increment the increment value to set
     */
    public void setIncrement(long increment) {
        this.increment = increment;
    }

    /**
     * Creates a new fluent {@link Builder} instance for constructing {@link CounterMetrics}.
     *
     * @return a new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A fluent builder for creating and configuring {@link CounterMetrics} instances.
     */
    public static class Builder {

        private final CounterMetrics metrics;

        /**
         * Constructs a new {@link Builder} initialized with a fresh {@link CounterMetrics} instance.
         */
        private Builder() {
            this.metrics = new CounterMetrics();
        }

        /**
         * Sets the name of the counter metric.
         *
         * @param name the counter metric name
         * @return this builder instance for method chaining
         */
        public Builder name(String name) {
            metrics.setName(name);
            return this;
        }

        /**
         * Sets the description of the counter metric.
         *
         * @param description the counter description
         * @return this builder instance for method chaining
         */
        public Builder description(String description) {
            metrics.setDescription(description);
            return this;
        }

        /**
         * Sets the tags for the counter metric.
         *
         * @param tags a list of {@link Tag} objects
         * @return this builder instance for method chaining
         */
        public Builder tags(List<Tag> tags) {
            metrics.setTags(tags);
            return this;
        }

        /**
         * Sets the increment step value for the counter.
         *
         * @param increment the amount to increment
         * @return this builder instance for method chaining
         */
        public Builder increment(long increment) {
            metrics.setIncrement(increment);
            return this;
        }

        /**
         * Sets whether the counter metric is enabled.
         *
         * @param enable {@code true} to enable, {@code false} to disable
         * @return this builder instance for method chaining
         */
        public Builder enable(boolean enable) {
            metrics.setEnable(enable);
            return this;
        }

        /**
         * Builds and returns the configured {@link CounterMetrics} instance.
         *
         * @return the constructed {@link CounterMetrics}
         */
        public CounterMetrics build() {
            return metrics;
        }
    }
}
