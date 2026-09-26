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

package io.github.yubrajsahoo.smf4j.api.enums;

/**
 * Represents the different types of metrics supported by SMF4J.
 * <p>
 * These types align with common telemetry primitives for measuring system behavior.
 * </p>
 */
public enum MetricsType {
    /**
     * A metric that represents a single numerical value that only ever goes up.
     * Often used to count requests, errors, or events.
     */
    COUNTER,

    /**
     * A metric that measures both the rate that a particular event is called and the
     * distribution of its duration.
     */
    TIMER,

    /**
     * A metric that represents a single numerical value that can arbitrarily go up and down.
     * Used for values like current memory usage or queue sizes.
     */
    GAUGE,

    /**
     * A specialized timer designed specifically to measure the duration of tasks that
     * are still actively running.
     */
    LONG_TASK_TIMER
}
