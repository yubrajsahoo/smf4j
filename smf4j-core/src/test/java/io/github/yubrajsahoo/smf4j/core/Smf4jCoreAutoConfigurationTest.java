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

import io.github.yubrajsahoo.smf4j.core.service.impl.CounterMeterService;
import io.github.yubrajsahoo.smf4j.core.service.impl.GaugeMeterService;
import io.github.yubrajsahoo.smf4j.core.service.impl.TimerMeterService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import static org.assertj.core.api.Assertions.assertThat;

class Smf4jCoreAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Smf4jCoreAutoConfiguration.class));

    @Test
    @DisplayName("Should load default beans when no custom beans are provided")
    void shouldLoadDefaultBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(MeterRegistry.class);
            assertThat(context).hasSingleBean(CounterMeterService.class);
            assertThat(context).hasSingleBean(GaugeMeterService.class);
            assertThat(context).hasSingleBean(TimerMeterService.class);
            assertThat(context).hasSingleBean(io.github.yubrajsahoo.smf4j.core.factory.MeterFactory.class);

            // Verify meter registry is the SimpleMeterRegistry fallback
            assertThat(context.getBean(MeterRegistry.class)).isInstanceOf(SimpleMeterRegistry.class);
        });
    }

    @Test
    @DisplayName("Should back off when custom MeterRegistry bean is provided")
    void shouldBackOffWhenCustomMeterRegistryProvided() {
        contextRunner
                .withUserConfiguration(CustomMeterRegistryConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(MeterRegistry.class);
                    assertThat(context.getBean(MeterRegistry.class)).isExactlyInstanceOf(CustomMeterRegistry.class);
                });
    }

    // -- Dummy classes for testing @ConditionalOnMissingBean --

    static class CustomMeterRegistry extends SimpleMeterRegistry {
    }

    static class CustomCounterMeterService extends CounterMeterService {
        public CustomCounterMeterService(MeterRegistry registry) {
            super(registry);
        }
    }


    static class CustomExpressionParser extends SpelExpressionParser {
    }

    @Configuration
    static class CustomMeterRegistryConfig {
        @Bean
        public MeterRegistry meterRegistry() {
            return new CustomMeterRegistry();
        }
    }
}
