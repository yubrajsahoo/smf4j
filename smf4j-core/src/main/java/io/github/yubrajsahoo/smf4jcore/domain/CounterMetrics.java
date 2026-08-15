package io.github.yubrajsahoo.smf4jcore.domain;

import io.micrometer.core.instrument.Tags;

/**
 * Domain model representing the configuration and payload for a counter metric.
 * <p>
 * Extends {@link Metrics} to include counter-specific attributes, primarily {@link #getIncrement()}.
 * Instances can be constructed directly using the default constructor or fluently using {@link #builder()}.
 * </p>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * CounterMetrics metrics = CounterMetrics.builder()
 *     .name("http.requests")
 *     .description("Total incoming HTTP requests")
 *     .tags(Tags.of("uri", "/api/v1/orders"))
 *     .increment(1)
 *     .build();
 * }</pre>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see Metrics
 * @see Builder
 */
public class CounterMetrics extends Metrics {

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
     *
     * @author Yubraj Sahoo
     * @version 0.0.1
     * @since 0.0.1
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
         * Sets the Micrometer {@link Tags} for the counter.
         *
         * @param tags the metric tags
         * @return this builder instance for method chaining
         */
        public Builder tags(Tags tags) {
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
         * @param enabled {@code true} to enable, {@code false} to disable
         * @return this builder instance for method chaining
         */
        public Builder enabled(boolean enabled) {
            metrics.setEnabled(enabled);
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