package io.github.yubrajsahoo.smf4j.api.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetricsTypeTest {

    @Test
    @DisplayName("Test Enum Values")
    void testEnumValues() {
        MetricsType[] values = MetricsType.values();
        assertThat(values).containsExactly(
                MetricsType.COUNTER,
                MetricsType.TIMER,
                MetricsType.GAUGE,
                MetricsType.LONG_TASK_TIMER
        );
    }

    @Test
    @DisplayName("Test Enum Value Of")
    void testEnumValueOf() {
        assertThat(MetricsType.valueOf("COUNTER")).isEqualTo(MetricsType.COUNTER);
        assertThat(MetricsType.valueOf("TIMER")).isEqualTo(MetricsType.TIMER);
        assertThat(MetricsType.valueOf("GAUGE")).isEqualTo(MetricsType.GAUGE);
        assertThat(MetricsType.valueOf("LONG_TASK_TIMER")).isEqualTo(MetricsType.LONG_TASK_TIMER);
    }
}

