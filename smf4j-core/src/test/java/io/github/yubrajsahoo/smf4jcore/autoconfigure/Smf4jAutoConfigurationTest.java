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

package io.github.yubrajsahoo.smf4jcore.autoconfigure;

import io.github.yubrajsahoo.smf4jcore.annotation.Counter;
import io.github.yubrajsahoo.smf4jcore.aspect.CounterAspect;
import io.github.yubrajsahoo.smf4jcore.factory.MeterFactory;
import io.github.yubrajsahoo.smf4jcore.meter.service.impl.CounterMeterService;
import io.github.yubrajsahoo.smf4jcore.meter.service.impl.TimerMeterService;
import io.github.yubrajsahoo.smf4jcore.service.MetricsService;
import io.github.yubrajsahoo.smf4jcore.service.impl.MetricsServiceImpl;
import io.github.yubrajsahoo.smf4jcore.spel.SpelEvaluator;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link Smf4jAutoConfiguration}.
 * <p>
 * Uses Spring Boot's {@link ApplicationContextRunner} to verify that all SMF4J beans
 * are registered by auto-configuration and that {@code @ConditionalOnMissingBean}
 * correctly backs off when user-defined beans are present.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @see Smf4jAutoConfiguration
 * @since 0.0.1
 */
class Smf4jAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Smf4jAutoConfiguration.class));

    /**
     * Verifies that auto-configuration registers exactly one bean of each
     * required SMF4J type.
     */
    @Test
    void shouldRegisterAllBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(MeterRegistry.class);
            assertThat(context).hasSingleBean(ExpressionParser.class);
            assertThat(context).hasSingleBean(SpelEvaluator.class);
            assertThat(context).hasSingleBean(CounterMeterService.class);
            assertThat(context).hasSingleBean(TimerMeterService.class);
            assertThat(context).hasSingleBean(MeterFactory.class);
            assertThat(context).hasSingleBean(MetricsService.class);
            assertThat(context).hasSingleBean(CounterAspect.class);
            assertThat(context).hasSingleBean(BeanResolver.class);
        });
    }

    /**
     * Verifies that the fallback {@link MeterRegistry} is a {@link SimpleMeterRegistry}
     * when no user-defined registry bean exists.
     */
    @Test
    void shouldUseFallbackSimpleMeterRegistry() {
        contextRunner.run(context ->
                assertThat(context.getBean(MeterRegistry.class)).isInstanceOf(SimpleMeterRegistry.class)
        );
    }

    /**
     * Verifies that the fallback {@link ExpressionParser} is a {@link SpelExpressionParser}
     * when no user-defined parser bean exists.
     */
    @Test
    void shouldUseFallbackSpelExpressionParser() {
        contextRunner.run(context ->
                assertThat(context.getBean(ExpressionParser.class)).isInstanceOf(SpelExpressionParser.class)
        );
    }

    /**
     * Verifies that the auto-configured {@link MetricsService} bean is an instance
     * of {@link MetricsServiceImpl}.
     */
    @Test
    void shouldCreateMetricsServiceImpl() {
        contextRunner.run(context ->
                assertThat(context.getBean(MetricsService.class)).isInstanceOf(MetricsServiceImpl.class)
        );
    }

    /**
     * Verifies that auto-configuration backs off when a user-defined
     * {@link MeterRegistry} bean already exists in the context.
     */
    @Test
    void shouldBackOffWhenMeterRegistryExists() {
        contextRunner
                .withBean(MeterRegistry.class, SimpleMeterRegistry::new)
                .run(context ->
                        assertThat(context).hasSingleBean(MeterRegistry.class)
                );
    }

    /**
     * Verifies that auto-configuration backs off when a user-defined
     * {@link ExpressionParser} bean already exists in the context.
     */
    @Test
    void shouldBackOffWhenExpressionParserExists() {
        contextRunner
                .withBean(ExpressionParser.class, SpelExpressionParser::new)
                .run(context ->
                        assertThat(context).hasSingleBean(ExpressionParser.class)
                );
    }

    /**
     * Verifies that auto-configuration backs off when a user-defined
     * {@link MetricsService} bean already exists, and the registered bean
     * is not an instance of {@link MetricsServiceImpl}.
     */
    @Test
    void shouldBackOffWhenMetricsServiceExists() {
        contextRunner
                .withBean(MetricsService.class, () -> new MetricsService() {
                    /**
                     * Starts a new {@link Timer.Sample} to measure execution time.
                     * <p>
                     * This method is a convenience wrapper for initiating a timing sample which
                     * can later be stopped and recorded against a specific timer metric.
                     * </p>
                     *
                     * @return a new {@link Timer.Sample} instance
                     */
                    @Override
                    public Timer.Sample start() {
                        return null;
                    }

                    /**
                     * Processes and records a counter metric based on the metadata in {@link Counter}
                     * and the given SpEL {@link StandardEvaluationContext}.
                     *
                     * @param counter the {@link Counter} annotation containing metric definition and metadata
                     * @param context the SpEL evaluation context providing variables for dynamic tag resolution
                     */
                    @Override
                    public void record(Counter counter, StandardEvaluationContext context) {

                    }

                    /**
                     * Processes and records a timer metric based on the metadata in {@link io.github.yubrajsahoo.smf4jcore.annotation.Timer}
                     * and the given SpEL {@link StandardEvaluationContext}.
                     *
                     * @param timer   the {@link io.github.yubrajsahoo.smf4jcore.annotation.Timer} annotation containing metric definition and metadata
                     * @param context the SpEL evaluation context providing variables for dynamic tag resolution
                     */
                    @Override
                    public void record(io.github.yubrajsahoo.smf4jcore.annotation.Timer timer, StandardEvaluationContext context) {

                    }
                })
                .run(context -> {
                    assertThat(context).hasSingleBean(MetricsService.class);
                    assertThat(context.getBean(MetricsService.class)).isNotInstanceOf(MetricsServiceImpl.class);
                });
    }
}
