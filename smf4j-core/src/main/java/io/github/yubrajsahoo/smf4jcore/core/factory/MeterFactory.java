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

package io.github.yubrajsahoo.smf4jcore.core.factory;


import io.github.yubrajsahoo.smf4jcore.core.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.core.service.MeterService;
import io.github.yubrajsahoo.smf4jcore.core.service.MetricsService;

import java.util.*;

/**
 * Factory for resolving specific {@link MeterService} implementations based on {@link MetricsType}.
 * <p>
 * During initialization, all available {@link MeterService} beans are indexed by their respective
 * {@link MeterService#getType()}. This enables metric dispatchers like {@link MetricsService}
 * to dynamically obtain the appropriate service for a given metric type.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @see MeterService
 * @see MetricsType
 * @since 0.0.1
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
