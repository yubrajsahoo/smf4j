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
 * Model representing a timer metric.
 * <p>
 * This class extends {@link Metrics} to include properties specific to timer metrics,
 * such as a generic sample object used for measuring execution time.
 * </p>
 */
public class TimerMetrics extends Metrics {

    /**
     * An object representing a timing sample (e.g., used to record duration).
     */
    private Object sample;

    /**
     * Constructs a new default {@link TimerMetrics} instance.
     */
    public TimerMetrics() {
        super();
    }

    /**
     * Gets the timer sample associated with this metric.
     *
     * @return the timer sample
     */
    public Object getSample() {
        return sample;
    }

    /**
     * Sets the timer sample for this metric.
     *
     * @param sample the timer sample to set
     */
    public void setSample(Object sample) {
        this.sample = sample;
    }

    /**
     * Creates a new fluent {@link Builder} instance for constructing {@link TimerMetrics}.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }


    /**
     * Builder class for constructing {@link TimerMetrics} instances.
     */
    public static class Builder {
        private final TimerMetrics metrics;

        /**
         * Creates a new Builder instance.
         */
        private Builder() {
            this.metrics = new TimerMetrics();
        }

        /**
         * Sets the name of the timer metric.
         *
         * @param name the metric name
         * @return this builder
         */
        public Builder name(String name) {
            this.metrics.setName(name);
            return this;
        }

        /**
         * Sets the description of the timer metric.
         *
         * @param description the metric description
         * @return this builder
         */
        public Builder description(String description) {
            this.metrics.setDescription(description);
            return this;
        }

        /**
         * Sets the tags of the timer metric.
         *
         * @param tags a list of {@link Tag} objects
         * @return this builder
         */
        public Builder tags(List<Tag> tags) {
            this.metrics.setTags(tags);
            return this;
        }

        /**
         * Sets whether the metric is enabled.
         *
         * @param enable {@code true} to enable, {@code false} to disable
         * @return this builder
         */
        public Builder enable(boolean enable) {
            this.metrics.setEnable(enable);
            return this;
        }

        /**
         * Sets the timer sample for the metric.
         *
         * @param sample the timer sample
         * @return this builder
         */
        public Builder sample(Object sample) {
            this.metrics.setSample(sample);
            return this;
        }

        /**
         * Builds and returns the configured {@link TimerMetrics} instance.
         *
         * @return the constructed {@link TimerMetrics}
         */
        public TimerMetrics build() {
            return this.metrics;
        }
    }
}
