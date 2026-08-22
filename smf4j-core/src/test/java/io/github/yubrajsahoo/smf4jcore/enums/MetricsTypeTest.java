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

package io.github.yubrajsahoo.smf4jcore.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for the {@link MetricsType} enumeration.
 * <p>
 * Validates all declared metric type constants and the {@code valueOf} behaviour
 * for both valid and invalid names.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see MetricsType
 */
class MetricsTypeTest {

    /**
     * Verifies that the enum declares exactly three metric types.
     */
    @Test
    void shouldHaveThreeValues() {
        assertThat(MetricsType.values()).hasSize(3);
    }

    /**
     * Verifies that {@link MetricsType#COUNTER} can be resolved via {@code valueOf}.
     */
    @Test
    void shouldContainCounter() {
        assertThat(MetricsType.valueOf("COUNTER")).isEqualTo(MetricsType.COUNTER);
    }

    /**
     * Verifies that {@link MetricsType#TIMER} can be resolved via {@code valueOf}.
     */
    @Test
    void shouldContainTimer() {
        assertThat(MetricsType.valueOf("TIMER")).isEqualTo(MetricsType.TIMER);
    }

    /**
     * Verifies that {@link MetricsType#GAUGE} can be resolved via {@code valueOf}.
     */
    @Test
    void shouldContainGauge() {
        assertThat(MetricsType.valueOf("GAUGE")).isEqualTo(MetricsType.GAUGE);
    }

    /**
     * Verifies that {@code valueOf} throws {@link IllegalArgumentException}
     * when given an unsupported metric type name.
     */
    @Test
    void valueOf_withInvalidName_shouldThrowException() {
        assertThatThrownBy(() -> MetricsType.valueOf("HISTOGRAM"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
