package io.github.yubrajsahoo.smf4jcore.factory;

import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.meter.service.MeterService;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Factory for resolving specific {@link MeterService} implementations based on {@link MetricsType}.
 * <p>
 * During initialization, all available {@link MeterService} beans are indexed by their respective
 * {@link MeterService#getType()}. This enables metric dispatchers like {@link io.github.yubrajsahoo.smf4jcore.service.MetricsService}
 * to dynamically obtain the appropriate service for a given metric type.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see MeterService
 * @see MetricsType
 */
public class MeterFactory {

    private final Map<MetricsType, MeterService> meterServiceMap;

    /**
     * Constructs a new {@link MeterFactory} by registering all provided {@link MeterService} instances.
     *
     * @param meterServices the list of available {@link MeterService} instances; may be {@code null} or empty
     */
    public MeterFactory(List<MeterService> meterServices) {
        Map<MetricsType, MeterService> map = new EnumMap<>(MetricsType.class);
        if (meterServices != null) {
            for (MeterService meterService : meterServices) {
                if (meterService != null && meterService.getType() != null) {
                    map.put(meterService.getType(), meterService);
                }
            }
        }
        this.meterServiceMap = Collections.unmodifiableMap(map);
    }

    /**
     * Retrieves the {@link MeterService} corresponding to the specified {@link MetricsType}.
     *
     * @param metricsType the metric type to look up; if {@code null}, returns {@link Optional#empty()}
     * @return an {@link Optional} containing the corresponding {@link MeterService} if registered, or empty otherwise
     */
    public Optional<MeterService> getMeterService(MetricsType metricsType) {
        if (metricsType == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(meterServiceMap.get(metricsType));
    }
}
