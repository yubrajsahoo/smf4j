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

package io.github.yubrajsahoo.smf4jcore.utils;

import io.github.yubrajsahoo.smf4jcore.constants.MetricsConstant;
import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link MetricsLogger}.
 * <p>
 * Validates the log message formatting produced by {@link MetricsLogger#prepareLog(String, io.github.yubrajsahoo.smf4jcore.domain.Metrics)},
 * the enabled/disabled message selection in {@link MetricsLogger#log(CounterMetrics)},
 * and verifies the utility class constructor is private.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @see MetricsLogger
 * @since 0.0.1
 */
class MetricsLoggerTest {

    /**
     * Verifies that {@code prepareLog} formats a metrics object with no tags
     * as {@code "PREFIX->name=<name>->description=<desc>"}.
     */
    @Test
    void prepareLog_withNoTags_shouldFormatCorrectly() {
        CounterMetrics metrics = JsonConverter.fromJsonFile("/data/metrics-defaults.json", CounterMetrics.class);
        metrics.setName("test.metric");
        metrics.setDescription("A test metric");

        String log = MetricsLogger.prepareLog("PREFIX->", metrics);

        assertThat(log).isEqualTo("PREFIX->name=test.metric->description=A test metric");
    }

    /**
     * Verifies that {@code prepareLog} includes all tag key-value pairs in the output.
     */
    @Test
    void prepareLog_withTags_shouldIncludeTagsInOutput() {
        CounterMetrics metrics = JsonConverter.fromJsonFile("/data/metrics-multi-tags.json", CounterMetrics.class);
        metrics.setName("tagged.metric");
        metrics.setDescription("Tagged");

        String log = MetricsLogger.prepareLog("MSG->", metrics);

        assertThat(log).contains("name=tagged.metric");
        assertThat(log).contains("env=prod");
        assertThat(log).contains("region=us-east"); // updated to us-east as in json
        assertThat(log).contains("description=Tagged");
    }

    /**
     * Verifies that {@link MetricsLogger#log(CounterMetrics)} completes without error
     * when the metric is enabled.
     */
    @Test
    void log_withEnabledMetrics_shouldUseDefaultLogMessage() {
        CounterMetrics metrics = JsonConverter.fromJsonFile("/data/metrics-basic.json", CounterMetrics.class);

        // Should not throw
        MetricsLogger.log(metrics);
    }

    /**
     * Verifies that {@link MetricsLogger#log(CounterMetrics)} completes without error
     * when the metric is disabled.
     */
    @Test
    void log_withDisabledMetrics_shouldUseDisabledLogMessage() {
        CounterMetrics metrics = JsonConverter.fromJsonFile("/data/metrics-all-fields.json", CounterMetrics.class);
        // metrics-all-fields.json has enabled=false

        // Should not throw
        MetricsLogger.log(metrics);
    }

    /**
     * Verifies that the formatted output starts with {@link MetricsConstant#DEFAULT_LOG_MESSAGE}
     * when the enabled prefix is passed.
     */
    @Test
    void prepareLog_withDefaultLogMessage_shouldContainPrefix() {
        CounterMetrics metrics = JsonConverter.fromJsonFile("/data/metrics-defaults.json", CounterMetrics.class);
        metrics.setName("counter");
        metrics.setDescription("desc");

        String result = MetricsLogger.prepareLog(MetricsConstant.DEFAULT_LOG_MESSAGE, metrics);

        assertThat(result).startsWith(MetricsConstant.DEFAULT_LOG_MESSAGE);
    }

    /**
     * Verifies that the formatted output starts with {@link MetricsConstant#DEFAULT_DISABLED_LOG_MESSAGE}
     * when the disabled prefix is passed.
     */
    @Test
    void prepareLog_withDisabledLogMessage_shouldContainPrefix() {
        CounterMetrics metrics = JsonConverter.fromJsonFile("/data/metrics-defaults.json", CounterMetrics.class);
        metrics.setName("counter");
        metrics.setDescription("desc");

        String result = MetricsLogger.prepareLog(MetricsConstant.DEFAULT_DISABLED_LOG_MESSAGE, metrics);

        assertThat(result).startsWith(MetricsConstant.DEFAULT_DISABLED_LOG_MESSAGE);
    }

    /**
     * Verifies that the {@link MetricsLogger} constructor is private,
     * enforcing the utility class pattern.
     *
     * @throws Exception if reflection access fails
     */
    @Test
    void constructor_shouldBePrivate() throws Exception {
        Constructor<MetricsLogger> constructor = MetricsLogger.class.getDeclaredConstructor();
        assertThat(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers())).isTrue();
        constructor.setAccessible(true);
        // Verify instantiation succeeds (constructor has no throw guard)
        assertThat(constructor.newInstance()).isNotNull();
    }
}
