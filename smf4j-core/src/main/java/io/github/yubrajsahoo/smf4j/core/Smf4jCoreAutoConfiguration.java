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

package io.github.yubrajsahoo.smf4j.core;

import io.github.yubrajsahoo.smf4j.core.factory.MeterFactory;
import io.github.yubrajsahoo.smf4j.core.service.MeterService;
import io.github.yubrajsahoo.smf4j.core.service.impl.CounterMeterService;
import io.github.yubrajsahoo.smf4j.core.service.impl.GaugeMeterService;
import io.github.yubrajsahoo.smf4j.core.service.impl.TimerMeterService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Auto-configuration class for the SMF4J core module.
 * <p>
 * This class is responsible for automatically configuring the necessary beans,
 * aspects, and metric registries required for the Simple Metrics Facade for Java (SMF4J)
 * when used in a Spring Boot environment.
 * </p>
 */
@AutoConfiguration
public class Smf4jCoreAutoConfiguration {

    /**
     * Registers a fallback {@link SimpleMeterRegistry} if no {@link MeterRegistry} bean is currently present in the application context.
     *
     * @return a default {@link SimpleMeterRegistry} instance
     */
    @Bean
    @ConditionalOnMissingBean(MeterRegistry.class)
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    /**
     * Creates and registers a {@link MeterFactory} bean.
     * <p>
     * The factory manages the provided list of {@link MeterService} instances,
     * allowing for retrieval of the appropriate service based on metrics type.
     * </p>
     *
     * @param meterServices a list of available {@link MeterService} instances injected by Spring
     * @return a new {@link MeterFactory} instance
     */
    @Bean
    public MeterFactory meterFactory(List<MeterService> meterServices) {
        return new MeterFactory(meterServices);
    }

    /**
     * Creates and registers a {@link CounterMeterService} bean if one is not already present.
     * <p>
     * This service handles metrics of type COUNTER and registers them with the provided {@link MeterRegistry}.
     * </p>
     *
     * @param meterRegistry the micrometer registry used for metrics collection
     * @return a new {@link CounterMeterService} instance
     */
    @Bean
    @ConditionalOnMissingBean(CounterMeterService.class)
    public CounterMeterService counterMeterService(MeterRegistry meterRegistry) {
        return new CounterMeterService(meterRegistry);
    }

    /**
     * Creates and registers a {@link GaugeMeterService} bean if one is not already present.
     * <p>
     * This service handles metrics of type GAUGE and registers them with the provided {@link MeterRegistry}.
     * </p>
     *
     * @param meterRegistry the micrometer registry used for metrics collection
     * @return a new {@link GaugeMeterService} instance
     */
    @Bean
    @ConditionalOnMissingBean(GaugeMeterService.class)
    public GaugeMeterService gaugeMeterService(MeterRegistry meterRegistry) {
        return new GaugeMeterService(meterRegistry);
    }

    /**
     * Creates and registers a {@link TimerMeterService} bean if one is not already present.
     * <p>
     * This service handles metrics of type TIMER and registers them with the provided {@link MeterRegistry}.
     * </p>
     *
     * @param meterRegistry the micrometer registry used for metrics collection
     * @return a new {@link TimerMeterService} instance
     */
    @Bean
    @ConditionalOnMissingBean(TimerMeterService.class)
    public TimerMeterService timerMeterService(MeterRegistry meterRegistry) {
        return new TimerMeterService(meterRegistry);
    }
}
