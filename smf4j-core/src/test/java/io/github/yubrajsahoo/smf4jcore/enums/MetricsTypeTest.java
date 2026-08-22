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
