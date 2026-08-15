package io.github.yubrajsahoo.smf4jcore.meter.service;

import io.github.yubrajsahoo.smf4jcore.Smf4jCoreTestApplication;
import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.domain.Metrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.helper.DataBuilder;
import io.github.yubrajsahoo.smf4jcore.meter.service.impl.CounterMeterService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit/Integration test for {@link CounterMeterService} configured via {@link Smf4jCoreTestApplication}.
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 */
class CounterMeterServiceTest extends Smf4jCoreTestApplication {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    @Qualifier("originalCounterMeterService")
    private CounterMeterService originalCounterMeterService;

    @BeforeEach
    void setUp() {
        meterRegistry.clear();
    }

    @Test
    @DisplayName("Should store and increment metrics in the meter registry")
    void should_store_metrics() {
        // Given
        CounterMetrics counterMetrics = CounterMetrics.builder()
                .name("orders.created")
                .description("Counts the number of orders created")
                .tags(Tags.of("region", "US", "env", "prod"))
                .increment(5)
                .enabled(true)
                .build();

        // When
        originalCounterMeterService.record(counterMetrics);

        // Then
        Counter counter = meterRegistry.find("orders.created").counter();
        assertNotNull(counter, "Counter should be registered in the meter registry");
        assertEquals(5.0, counter.count());
        assertEquals("Counts the number of orders created", counter.getId().getDescription());
        assertEquals("US", counter.getId().getTag("region"));
        assertEquals("prod", counter.getId().getTag("env"));
    }

    @Test
    @DisplayName("Should store and increment metrics loaded from JSON fixture")
    void should_store_metrics_from_json() {
        // Given
        CounterMetrics counterMetrics = DataBuilder.fromFile(
                "src/test/resources/json/counter-metrics.json",
                CounterMetrics.class
        );

        // When
        originalCounterMeterService.record(counterMetrics);

        // Then
        Counter counter = meterRegistry.find("orders.created").counter();
        assertNotNull(counter, "Counter should be registered in the meter registry");
        assertEquals(1.0, counter.count());
        assertEquals("Counts the number of orders created", counter.getId().getDescription());
    }

    @Test
    @DisplayName("Should return COUNTER metric type")
    void should_return_counter_metric_type() {
        assertEquals(MetricsType.COUNTER, originalCounterMeterService.getType());
    }

    @Test
    @DisplayName("Should throw illegal argument exception for null parameter")
    void should_throw_illegal_argument_exception_for_null_parameter() {
        assertThrows(IllegalArgumentException.class, () ->
                originalCounterMeterService.record(null)
        );
    }

    @Test
    @DisplayName("Should throw illegal argument exception for invalid metrics")
    void should_throw_illegal_argument_exception_for_invalid_metrics() {
        assertThrows(IllegalArgumentException.class, () ->
                originalCounterMeterService.record(new Metrics() {
                })
        );
    }
}