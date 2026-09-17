package io.github.yubrajsahoo.smf4j.api.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.ToDoubleFunction;

import static org.assertj.core.api.Assertions.assertThat;

class GaugeMetricsTest {

    @Test
    @DisplayName("Test Getters And Setters")
    void testGettersAndSetters() {
        GaugeMetrics<String> metrics = new GaugeMetrics<>();
        ToDoubleFunction<String> func = String::length;

        metrics.setInstance("test");
        metrics.setFunction(func);
        metrics.setName("testName");
        metrics.setDescription("testDesc");
        metrics.setEnable(true);
        metrics.setTags(List.of(new Tag()));

        assertThat(metrics.getInstance()).isEqualTo("test");
        assertThat(metrics.getFunction()).isEqualTo(func);
        assertThat(metrics.getName()).isEqualTo("testName");
        assertThat(metrics.getDescription()).isEqualTo("testDesc");
        assertThat(metrics.isEnable()).isTrue();
        assertThat(metrics.getTags()).hasSize(1);
    }

    @Test
    @DisplayName("Test Builder")
    void testBuilder() {
        ToDoubleFunction<String> func = String::length;
        GaugeMetrics<String> metrics = GaugeMetrics.builder("bName", "test", func)
                .description("bDesc")
                .enable(false)
                .tags(List.of())
                .build();

        assertThat(metrics.getInstance()).isEqualTo("test");
        assertThat(metrics.getFunction()).isEqualTo(func);
        assertThat(metrics.getName()).isEqualTo("bName");
        assertThat(metrics.getDescription()).isEqualTo("bDesc");
        assertThat(metrics.isEnable()).isFalse();
        assertThat(metrics.getTags()).isEmpty();
    }
}

