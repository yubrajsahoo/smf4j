package io.github.yubrajsahoo.smf4j.api.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CounterMetricsTest {
    @Test
    @DisplayName("Test Getters And Setters")
    void testGettersAndSetters() {
        CounterMetrics metrics = new CounterMetrics();
        metrics.setIncrement(5L);
        metrics.setName("testName");
        metrics.setDescription("testDesc");
        metrics.setEnable(true);
        metrics.setTags(List.of(new Tag()));

        assertThat(metrics.getIncrement()).isEqualTo(5L);
        assertThat(metrics.getName()).isEqualTo("testName");
        assertThat(metrics.getDescription()).isEqualTo("testDesc");
        assertThat(metrics.isEnable()).isTrue();
        assertThat(metrics.getTags()).hasSize(1);
    }

    @Test
    @DisplayName("Test Builder")
    void testBuilder() {
        CounterMetrics metrics = CounterMetrics.builder()
                .increment(10L)
                .name("bName")
                .description("bDesc")
                .enable(false)
                .tags(List.of())
                .build();

        assertThat(metrics.getIncrement()).isEqualTo(10L);
        assertThat(metrics.getName()).isEqualTo("bName");
        assertThat(metrics.getDescription()).isEqualTo("bDesc");
        assertThat(metrics.isEnable()).isFalse();
        assertThat(metrics.getTags()).isEmpty();
    }
}

