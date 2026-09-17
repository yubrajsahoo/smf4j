package io.github.yubrajsahoo.smf4j.api.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TimerMetricsTest {
    @Test
    @DisplayName("Test Getters And Setters")
    void testGettersAndSetters() {
        TimerMetrics metrics = new TimerMetrics();
        Object sample = new Object();
        metrics.setSample(sample);
        metrics.setName("testName");
        metrics.setDescription("testDesc");
        metrics.setEnable(true);
        metrics.setTags(List.of(new Tag()));

        assertThat(metrics.getSample()).isSameAs(sample);
        assertThat(metrics.getName()).isEqualTo("testName");
        assertThat(metrics.getDescription()).isEqualTo("testDesc");
        assertThat(metrics.isEnable()).isTrue();
        assertThat(metrics.getTags()).hasSize(1);
    }

    @Test
    @DisplayName("Test Builder")
    void testBuilder() {
        Object sample = new Object();
        TimerMetrics metrics = TimerMetrics.builder()
                .sample(sample)
                .name("bName")
                .description("bDesc")
                .enable(false)
                .tags(List.of())
                .build();

        assertThat(metrics.getSample()).isSameAs(sample);
        assertThat(metrics.getName()).isEqualTo("bName");
        assertThat(metrics.getDescription()).isEqualTo("bDesc");
        assertThat(metrics.isEnable()).isFalse();
        assertThat(metrics.getTags()).isEmpty();
    }
}

