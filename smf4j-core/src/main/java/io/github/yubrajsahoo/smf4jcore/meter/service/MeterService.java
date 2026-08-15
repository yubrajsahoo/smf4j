package io.github.yubrajsahoo.smf4jcore.meter.service;

import io.github.yubrajsahoo.smf4jcore.domain.Metrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;

/**
 * Service interface for recording metric data into underlying meter registries (e.g. Micrometer).
 * <p>
 * Implementations are specialized to handle a specific {@link MetricsType} (such as {@link MetricsType#COUNTER}).
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see io.github.yubrajsahoo.smf4jcore.meter.service.impl.CounterMeterService
 * @see io.github.yubrajsahoo.smf4jcore.factory.MeterFactory
 */
public interface MeterService {

    /**
     * Gets the {@link MetricsType} supported by this service implementation.
     *
     * @return the supported {@link MetricsType}
     */
    MetricsType getType();

    /**
     * Records the given metric data into the underlying metric registry.
     *
     * @param metrics the metric object containing name, tags, description, and values to record
     * @throws IllegalArgumentException if the provided metric object is null or incompatible with this meter service
     */
    void record(Metrics metrics);
}