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

import io.micrometer.core.instrument.Timer;

/**
 * Model representing a timer metric.
 * <p>
 * This class extends {@link Metrics} to include properties specific to timer metrics,
 * such as the {@link Timer.Sample} used for measuring execution time.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
public class TimerMetrics extends Metrics {

    private Timer.Sample sample;

    /**
     * Gets the timer sample associated with this metric.
     *
     * @return the timer sample
     */
    public Timer.Sample getSample() {
        return sample;
    }

    /**
     * Sets the timer sample for this metric.
     *
     * @param sample the timer sample to set
     */
    public void setSample(Timer.Sample sample) {
        this.sample = sample;
    }

    /**
     * Builder class for constructing {@link TimerMetrics} instances.
     */
    public static class Builder {
        private final TimerMetrics metrics;

        /**
         * Creates a new Builder instance.
         */
        public Builder() {
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
         * @param tags the metric tags
         * @return this builder
         */
        public Builder tags(io.micrometer.core.instrument.Tags tags) {
            this.metrics.setTags(tags);
            return this;
        }

        /**
         * Sets whether the metric is enabled.
         *
         * @param enabled the enabled flag
         * @return this builder
         */
        public Builder enabled(boolean enabled) {
            this.metrics.setEnabled(enabled);
            return this;
        }

        /**
         * Sets the timer sample for the metric.
         *
         * @param sample the timer sample
         * @return this builder
         */
        public Builder sample(Timer.Sample sample) {
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
