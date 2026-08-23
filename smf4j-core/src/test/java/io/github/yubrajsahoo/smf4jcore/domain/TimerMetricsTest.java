package io.github.yubrajsahoo.smf4jcore.domain;

import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link TimerMetrics} and its {@link TimerMetrics.Builder}.
 * <p>
 * Validates default constructor initialization, setter/getter contracts,
 * fluent builder configuration, and builder method chaining.
 * </p>
 */
class TimerMetricsTest {

    @Test
    void defaultConstructor_shouldSetDefaultValues() {
        TimerMetrics metrics = new TimerMetrics();

        assertThat(metrics.getName()).isNull();
        assertThat(metrics.getDescription()).isNull();
        assertThat(metrics.getTags()).isEqualTo(Tags.empty());
        assertThat(metrics.isEnabled()).isTrue();
        assertThat(metrics.getSample()).isNull();
    }

    @Test
    void settersAndGetters_shouldWorkCorrectly() {
        TimerMetrics metrics = new TimerMetrics();
        Tags tags = Tags.of("env", "prod");
        Timer.Sample sample = mock(Timer.Sample.class);

        metrics.setName("test.timer");
        metrics.setDescription("A test timer");
        metrics.setTags(tags);
        metrics.setEnabled(false);
        metrics.setSample(sample);

        assertThat(metrics.getName()).isEqualTo("test.timer");
        assertThat(metrics.getDescription()).isEqualTo("A test timer");
        assertThat(metrics.getTags()).isEqualTo(tags);
        assertThat(metrics.isEnabled()).isFalse();
        assertThat(metrics.getSample()).isEqualTo(sample);
    }

    @Test
    void builder_shouldBuildWithAllFields() {
        Tags tags = Tags.of("region", "us-east-1");
        Timer.Sample sample = mock(Timer.Sample.class);

        TimerMetrics metrics = new TimerMetrics.Builder()
                .name("http.requests.latency")
                .description("Latency for HTTP requests")
                .tags(tags)
                .sample(sample)
                .enabled(false)
                .build();

        assertThat(metrics.getName()).isEqualTo("http.requests.latency");
        assertThat(metrics.getDescription()).isEqualTo("Latency for HTTP requests");
        assertThat(metrics.getTags()).isEqualTo(tags);
        assertThat(metrics.getSample()).isEqualTo(sample);
        assertThat(metrics.isEnabled()).isFalse();
    }

    @Test
    void builder_withDefaults_shouldUseDefaultValues() {
        TimerMetrics metrics = new TimerMetrics.Builder()
                .name("simple.timer")
                .build();

        assertThat(metrics.getName()).isEqualTo("simple.timer");
        assertThat(metrics.getDescription()).isNull();
        assertThat(metrics.getTags()).isEqualTo(Tags.empty());
        assertThat(metrics.getSample()).isNull();
        assertThat(metrics.isEnabled()).isTrue();
    }

    @Test
    void builder_shouldSupportMethodChaining() {
        TimerMetrics.Builder builder = new TimerMetrics.Builder();

        TimerMetrics.Builder returned = builder.name("test");
        assertThat(returned).isSameAs(builder);

        returned = builder.description("desc");
        assertThat(returned).isSameAs(builder);

        returned = builder.tags(Tags.empty());
        assertThat(returned).isSameAs(builder);

        returned = builder.sample(mock(Timer.Sample.class));
        assertThat(returned).isSameAs(builder);

        returned = builder.enabled(true);
        assertThat(returned).isSameAs(builder);
    }
}
