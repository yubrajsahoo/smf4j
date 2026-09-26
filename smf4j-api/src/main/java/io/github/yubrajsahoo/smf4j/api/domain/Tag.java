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
     * Constructor for tags.
     *
     * @param key   the tags key
     * @param value tags values
     */
    public Tag(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static Tag of(String key, String value) {
        return new Tag(key, value);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}