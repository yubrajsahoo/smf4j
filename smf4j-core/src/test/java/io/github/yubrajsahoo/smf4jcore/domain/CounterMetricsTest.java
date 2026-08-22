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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link CounterMetrics} and its {@link CounterMetrics.Builder}.
 * <p>
 * Validates default constructor initialisation, setter/getter contracts,
 * fluent builder configuration, and builder method chaining.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see CounterMetrics
 * @see Metrics
 */
class CounterMetricsTest {

    /**
     * Verifies that a newly constructed {@link CounterMetrics} has the expected default values:
     * {@code null} name, {@code null} description, empty tags, enabled, and increment of 1.
     */
    @Test
    void defaultConstructor_shouldSetDefaultValues() {
        CounterMetrics metrics = new CounterMetrics();

        assertThat(metrics.getName()).isNull();
        assertThat(metrics.getDescription()).isNull();
        assertThat(metrics.getTags()).isEqualTo(Tags.empty());
        assertThat(metrics.isEnabled()).isTrue();
        assertThat(metrics.getIncrement()).isEqualTo(1L);
    }

    /**
     * Verifies that all setters correctly store values and that the corresponding
     * getters return the same values.
     */
    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        CounterMetrics metrics = new CounterMetrics();
        Tags tags = Tags.of("env", "prod");

        metrics.setName("test.counter");
        metrics.setDescription("A test counter");
        metrics.setTags(tags);
        metrics.setEnabled(false);
        metrics.setIncrement(5);

        assertThat(metrics.getName()).isEqualTo("test.counter");
        assertThat(metrics.getDescription()).isEqualTo("A test counter");
        assertThat(metrics.getTags()).isEqualTo(tags);
        assertThat(metrics.isEnabled()).isFalse();
        assertThat(metrics.getIncrement()).isEqualTo(5L);
    }

    /**
     * Verifies that the {@link CounterMetrics.Builder} correctly configures all fields
     * when every builder method is invoked.
     */
    @Test
    void builder_shouldBuildWithAllFields() {
        Tags tags = Tags.of("region", "us-east-1");

        CounterMetrics metrics = CounterMetrics.builder()
                .name("http.requests")
                .description("Total HTTP requests")
                .tags(tags)
                .increment(3)
                .enabled(false)
                .build();

        assertThat(metrics.getName()).isEqualTo("http.requests");
        assertThat(metrics.getDescription()).isEqualTo("Total HTTP requests");
        assertThat(metrics.getTags()).isEqualTo(tags);
        assertThat(metrics.getIncrement()).isEqualTo(3L);
        assertThat(metrics.isEnabled()).isFalse();
    }

    /**
     * Verifies that the builder uses default values for unset fields
     * (only {@code name} is explicitly set).
     */
    @Test
    void builder_withDefaults_shouldUseDefaultValues() {
        CounterMetrics metrics = CounterMetrics.builder()
                .name("simple.counter")
                .build();

        assertThat(metrics.getName()).isEqualTo("simple.counter");
        assertThat(metrics.getDescription()).isNull();
        assertThat(metrics.getTags()).isEqualTo(Tags.empty());
        assertThat(metrics.getIncrement()).isEqualTo(1L);
        assertThat(metrics.isEnabled()).isTrue();
    }

    /**
     * Verifies that each builder method returns the same {@link CounterMetrics.Builder} instance,
     * enabling fluent method chaining.
     */
    @Test
    void builder_shouldSupportMethodChaining() {
        CounterMetrics.Builder builder = CounterMetrics.builder();

        CounterMetrics.Builder returned = builder.name("test");
        assertThat(returned).isSameAs(builder);

        returned = builder.description("desc");
        assertThat(returned).isSameAs(builder);

        returned = builder.tags(Tags.empty());
        assertThat(returned).isSameAs(builder);

        returned = builder.increment(2);
        assertThat(returned).isSameAs(builder);

        returned = builder.enabled(true);
        assertThat(returned).isSameAs(builder);
    }
}
