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

package io.github.yubrajsahoo.smf4jcore.constants;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link MetricsConstant}.
 * <p>
 * Validates that all framework constants hold their expected values, the SpEL designators
 * list is immutable, and the utility class constructor is private.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see MetricsConstant
 */
class MetricsConstantTest {

    /**
     * Verifies that {@link MetricsConstant#NONE} holds the expected fallback value {@code "none"}.
     */
    @Test
    void noneConstant_shouldHaveExpectedValue() {
        assertThat(MetricsConstant.NONE).isEqualTo("none");
    }

    /**
     * Verifies that {@link MetricsConstant#DEFAULT_LOG_MESSAGE} holds the expected log prefix.
     */
    @Test
    void defaultLogMessage_shouldHaveExpectedValue() {
        assertThat(MetricsConstant.DEFAULT_LOG_MESSAGE).isEqualTo("Metrics Logs For With->");
    }

    /**
     * Verifies that {@link MetricsConstant#DEFAULT_DISABLED_LOG_MESSAGE} holds the expected disabled log prefix.
     */
    @Test
    void defaultDisabledLogMessage_shouldHaveExpectedValue() {
        assertThat(MetricsConstant.DEFAULT_DISABLED_LOG_MESSAGE).isEqualTo("Metrics Disabled For->");
    }

    /**
     * Verifies that {@link MetricsConstant#ALLOWED_SPEL_DESIGNS} contains exactly the expected
     * SpEL prefixes: {@code @}, {@code #}, and {@code T}.
     */
    @Test
    void allowedSpelDesigns_shouldContainExpectedPrefixes() {
        List<String> allowed = MetricsConstant.ALLOWED_SPEL_DESIGNS;
        assertThat(allowed).containsExactly("@", "#", "T");
    }

    /**
     * Verifies that {@link MetricsConstant#ALLOWED_SPEL_DESIGNS} is an unmodifiable list
     * and throws {@link UnsupportedOperationException} on mutation attempts.
     */
    @Test
    void allowedSpelDesigns_shouldBeImmutable() {
        assertThatThrownBy(() -> MetricsConstant.ALLOWED_SPEL_DESIGNS.add("X"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    /**
     * Verifies that the {@link MetricsConstant} constructor is private,
     * enforcing the utility class pattern.
     *
     * @throws Exception if reflection access fails
     */
    @Test
    void constructor_shouldBePrivate() throws Exception {
        Constructor<MetricsConstant> constructor = MetricsConstant.class.getDeclaredConstructor();
        assertThat(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers())).isTrue();
        constructor.setAccessible(true);
        // Verify instantiation succeeds (constructor has no throw guard)
        assertThat(constructor.newInstance()).isNotNull();
    }
}
