/*
 * Copyright 2024 Yubraj Sahoo
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.yubrajsahoo.smf4j.api.domain;

import java.util.List;

/**
 * Base abstract class representing a core metric within the telemetry system.
 * <p>
 * This class provides common properties shared across different types of metrics,
 * such as the metric's name, description, associated tags, and enablement status.
 * </p>
 */
public abstract class Metrics {

    /**
     * The unique name or identifier for the metric.
     */
    private String name;

    /**
     * A human-readable description explaining the purpose of the metric.
     */
    private String description;

    /**
     * A list of tags providing metadata or dimensions for filtering and categorizing the metric.
     */
    private List<Tag> tags;

    /**
     * A flag indicating whether the metric is currently enabled for collection or reporting.
     */
    private boolean enable;

    /**
     * Protected default constructor for use by subclasses.
     */
    protected Metrics() {
    }

    /**
     * Retrieves the name of the metric.
     *
     * @return the metric name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the metric.
     *
     * @param name the metric name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the description of the metric.
     *
     * @return the metric description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the metric.
     *
     * @param description the metric description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the list of tags associated with this metric.
     *
     * @return a list of {@link Tag} objects
     */
    public List<Tag> getTags() {
        return tags;
    }

    /**
     * Sets the tags associated with this metric.
     *
     * @param tags a list of {@link Tag} objects
     */
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /**
     * Checks whether this metric is enabled.
     *
     * @return {@code true} if the metric is enabled, {@code false} otherwise
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Sets the enablement status of this metric.
     *
     * @param enable {@code true} to enable the metric, {@code false} to disable it
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
