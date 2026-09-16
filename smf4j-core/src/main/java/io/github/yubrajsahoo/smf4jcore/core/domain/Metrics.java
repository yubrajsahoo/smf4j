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

package io.github.yubrajsahoo.smf4jcore.core.domain;

import io.github.yubrajsahoo.smf4jcore.counter.domain.CounterMetrics;

import io.micrometer.core.instrument.Tags;

/**
 * Model representing the common configuration properties for a metric.
 * <p>
 * This class serves as the base type for concrete metric definitions such as
 * {@link CounterMetrics}. It encapsulates shared properties including metric name,
 * description, associated Micrometer {@link Tags}, and an enabled/disabled flag.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @see CounterMetrics
 * @since 0.0.1
 */
public abstract class Metrics {

    private String name;
    private String description;
    private Tags tags = Tags.empty();
    private boolean enabled = true;

    /**
     * Default constructor for subclasses of {@link Metrics}.
     */
    protected Metrics() {
    }

    /**
     * Gets the unique name of the metric.
     *
     * @return the metric name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the unique name of the metric.
     *
     * @param name the metric name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the human-readable description of the metric.
     *
     * @return the metric description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the human-readable description of the metric.
     *
     * @param description the metric description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the Micrometer {@link Tags} associated with the metric.
     *
     * @return the tags associated with the metric, never {@code null}
     */
    public Tags getTags() {
        return tags;
    }

    /**
     * Sets the Micrometer {@link Tags} associated with the metric.
     *
     * @param tags the tags to associate with the metric
     */
    public void setTags(Tags tags) {
        this.tags = tags;
    }

    /**
     * Checks whether this metric is enabled for recording.
     *
     * @return {@code true} if the metric is enabled, {@code false} otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether this metric is enabled for recording.
     *
     * @param enabled {@code true} to enable recording, {@code false} to disable
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}