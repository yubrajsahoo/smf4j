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

/**
 * Represents a key-value pair used to provide additional metadata or dimensions for metrics.
 * <p>
 * Tags allow for categorizing or filtering telemetry data based on specific attributes.
 * </p>
 */
public class Tag {

    /**
     * The key or name of the tag.
     */
    private String key;

    /**
     * The value associated with the tag key.
     */
    private String value;

    /**
     * Default constructor for creating an empty tag.
     */
    public Tag() {
    }

    /**
     * Constructs a tag using the provided builder.
     *
     * @param builder the builder containing tag data
     */
    private Tag(Builder builder) {
        this.key = builder.key;
        this.value = builder.value;
    }

    /**
     * Retrieves the key of this tag.
     *
     * @return the tag key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key of this tag.
     *
     * @param key the tag key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Retrieves the value of this tag.
     *
     * @return the tag value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of this tag.
     *
     * @param value the tag value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Creates a new builder instance for constructing a {@link Tag}.
     *
     * @return a new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder class for creating {@link Tag} instances in a fluent manner.
     */
    public static class Builder {
        private String key;
        private String value;

        private Builder() {
        }

        /**
         * Sets the key for the tag.
         *
         * @param key the tag key
         * @return this builder instance
         */
        public Builder key(String key) {
            this.key = key;
            return this;
        }

        /**
         * Sets the value for the tag.
         *
         * @param value the tag value
         * @return this builder instance
         */
        public Builder value(String value) {
            this.value = value;
            return this;
        }

        /**
         * Builds and returns a new {@link Tag} instance using the configured properties.
         *
         * @return a new {@link Tag}
         */
        public Tag build() {
            return new Tag(this);
        }
    }
}