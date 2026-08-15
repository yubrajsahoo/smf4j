package io.github.yubrajsahoo.smf4jcore.meter.service.impl;

import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.domain.Metrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.meter.service.MeterService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Implementation of {@link MeterService} dedicated to registering and incrementing
 * Micrometer {@link Counter} instances in a {@link MeterRegistry}.
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see MeterService
 * @see CounterMetrics
 * @see MeterRegistry
 */
public class CounterMeterService implements MeterService {

    private static final MetricsType METRICS_TYPE = MetricsType.COUNTER;

    private final MeterRegistry meterRegistry;

    /**
     * Constructs a new {@link CounterMeterService} with the specified {@link MeterRegistry}.
     *
     * @param meterRegistry the Micrometer meter registry to register counters with
     */
    public CounterMeterService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link MetricsType#COUNTER}
     */
    @Override
    public MetricsType getType() {
        return METRICS_TYPE;
    }

    /**
     * Records a counter metric by registering it with the {@link MeterRegistry} and incrementing its value.
     *
     * @param metrics the metric object, expected to be an instance of {@link CounterMetrics}
     * @throws IllegalArgumentException if {@code metrics} is {@code null} or not an instance of {@link CounterMetrics}
     */
    @Override
    public void record(Metrics metrics) {
        if (metrics instanceof CounterMetrics counterMetrics) {
            Counter.builder(counterMetrics.getName())
                    .description(counterMetrics.getDescription())
                    .tags(counterMetrics.getTags())
                    .register(meterRegistry)
                    .increment(counterMetrics.getIncrement());
        } else {
            throw new IllegalArgumentException("Invalid metrics type for CounterMeterService");
        }
    }
}
