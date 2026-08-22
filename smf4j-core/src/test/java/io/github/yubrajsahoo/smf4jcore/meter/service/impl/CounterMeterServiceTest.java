package io.github.yubrajsahoo.smf4jcore.meter.service.impl;

import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.domain.Metrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.utils.JsonConverter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link CounterMeterService}.
 * <p>
 * Validates counter registration and incrementing in a {@link SimpleMeterRegistry},
 * accumulation across multiple invocations, tag handling, and rejection of
 * non-{@link CounterMetrics} instances.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see CounterMeterService
 * @see CounterMetrics
 */
class CounterMeterServiceTest {

    private MeterRegistry meterRegistry;
    private CounterMeterService counterMeterService;

    /**
     * Creates a fresh {@link SimpleMeterRegistry} and {@link CounterMeterService}
     * before each test to ensure test isolation.
     */
    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        counterMeterService = new CounterMeterService(meterRegistry);
    }

    /**
     * Verifies that {@link CounterMeterService#getType()} returns {@link MetricsType#COUNTER}.
     */
    @Test
    void getType_shouldReturnCounter() {
        assertThat(counterMeterService.getType()).isEqualTo(MetricsType.COUNTER);
    }

    /**
     * Verifies that recording a valid {@link CounterMetrics} registers the counter
     * in the registry and increments it by the specified amount.
     */
    @Test
    void record_withValidCounterMetrics_shouldRegisterAndIncrement() {
        CounterMetrics metrics = JsonConverter.fromJsonFile("/data/metrics-basic.json", CounterMetrics.class);

        counterMeterService.record(metrics);

        Counter counter = meterRegistry.find("test.counter")
                .tag("env", "test")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
        assertThat(counter.getId().getDescription()).isEqualTo("Test counter");
    }

    /**
     * Verifies that calling {@code record} multiple times on the same counter
     * accumulates the increment values.
     */
    @Test
    void record_multipleTimes_shouldAccumulateCount() {
        CounterMetrics metrics = JsonConverter.fromJsonFile("/data/metrics-accumulate.json", CounterMetrics.class);

        counterMeterService.record(metrics);
        counterMeterService.record(metrics);

        Counter counter = meterRegistry.find("accumulate.counter").counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(6.0);
    }

    /**
     * Verifies that recording a counter with empty tags succeeds without errors.
     */
    @Test
    void record_withEmptyTags_shouldWork() {
        CounterMetrics metrics = JsonConverter.fromJsonFile("/data/metrics-empty-tags.json", CounterMetrics.class);

        counterMeterService.record(metrics);

        Counter counter = meterRegistry.find("no.tags.counter").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);
    }

    /**
     * Verifies that recording a counter with multiple tags registers the counter
     * with all specified tag key-value pairs.
     */
    @Test
    void record_withMultipleTags_shouldRegisterWithAllTags() {
        CounterMetrics metrics = JsonConverter.fromJsonFile("/data/metrics-multi-tags.json", CounterMetrics.class);

        counterMeterService.record(metrics);

        Counter counter = meterRegistry.find("multi.tag.counter")
                .tag("region", "us-east")
                .tag("env", "prod")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(2.0);
    }

    /**
     * Verifies that passing a non-{@link CounterMetrics} instance to {@code record}
     * throws an {@link IllegalArgumentException}.
     */
    @Test
    void record_withNonCounterMetrics_shouldThrowIllegalArgumentException() {
        Metrics nonCounterMetrics = new Metrics() {
            // anonymous subclass of Metrics that is NOT CounterMetrics
        };
        nonCounterMetrics.setName("invalid");

        assertThatThrownBy(() -> counterMeterService.record(nonCounterMetrics))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid metrics type for CounterMeterService");
    }

    /**
     * Verifies that a large increment value is correctly applied to the counter.
     */
    @Test
    void record_withLargeIncrement_shouldIncrementCorrectly() {
        CounterMetrics metrics = JsonConverter.fromJsonFile("/data/metrics-large-increment.json", CounterMetrics.class);

        counterMeterService.record(metrics);

        Counter counter = meterRegistry.find("large.counter").counter();
        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1000000.0);
    }
}
