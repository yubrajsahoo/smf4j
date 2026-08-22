package io.github.yubrajsahoo.smf4jcore.meter.service.impl;

import io.github.yubrajsahoo.smf4jcore.domain.Metrics;
import io.github.yubrajsahoo.smf4jcore.domain.TimerMetrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link TimerMeterService}.
 * <p>
 * Validates timer registration and sample recording in a {@link SimpleMeterRegistry},
 * accumulation across multiple invocations, tag handling, exception handling,
 * and rejection of non-{@link TimerMetrics} instances.
 * </p>
 */
class TimerMeterServiceTest {

    private MeterRegistry meterRegistry;
    private TimerMeterService timerMeterService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        timerMeterService = new TimerMeterService(meterRegistry);
    }

    @Test
    void getType_shouldReturnTimer() {
        assertThat(timerMeterService.getType()).isEqualTo(MetricsType.TIMER);
    }

    @Test
    void start_shouldReturnNewSample() {
        Timer.Sample sample = timerMeterService.start();
        assertThat(sample).isNotNull();
    }

    @Test
    void record_withValidTimerMetrics_shouldRegisterAndStopSample() throws InterruptedException {
        Timer.Sample sample = timerMeterService.start();
        
        Thread.sleep(10); // artificially sleep to record a non-zero time

        TimerMetrics metrics = new TimerMetrics.Builder()
                .name("test.timer")
                .description("Test timer")
                .sample(sample)
                .build();

        timerMeterService.record(metrics);

        Timer timer = meterRegistry.find("test.timer").timer();

        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1L);
        assertThat(timer.totalTime(TimeUnit.MILLISECONDS)).isGreaterThan(0);
        assertThat(timer.getId().getDescription()).isEqualTo("Test timer");
    }

    @Test
    void record_withNullSample_shouldRegisterWithoutException() {
        TimerMetrics metrics = new TimerMetrics.Builder()
                .name("null.sample.timer")
                .sample(null)
                .build();

        // Should not throw NPE because of the safety check we added
        timerMeterService.record(metrics);

        Timer timer = meterRegistry.find("null.sample.timer").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(0L); // Nothing recorded
    }

    @Test
    void record_multipleTimes_shouldAccumulateCountAndDuration() throws InterruptedException {
        // Record 1
        Timer.Sample sample1 = timerMeterService.start();
        Thread.sleep(10);
        TimerMetrics metrics1 = new TimerMetrics.Builder()
                .name("accumulate.timer")
                .sample(sample1)
                .build();
        timerMeterService.record(metrics1);

        // Record 2
        Timer.Sample sample2 = timerMeterService.start();
        Thread.sleep(10);
        TimerMetrics metrics2 = new TimerMetrics.Builder()
                .name("accumulate.timer")
                .sample(sample2)
                .build();
        timerMeterService.record(metrics2);

        Timer timer = meterRegistry.find("accumulate.timer").timer();

        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(2L);
        assertThat(timer.totalTime(TimeUnit.MILLISECONDS)).isGreaterThan(10);
    }

    @Test
    void record_withNonTimerMetrics_shouldThrowIllegalArgumentException() {
        Metrics nonTimerMetrics = new Metrics() {
            // anonymous subclass of Metrics that is NOT TimerMetrics
        };
        nonTimerMetrics.setName("invalid");

        assertThatThrownBy(() -> timerMeterService.record(nonTimerMetrics))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid metrics type for TimerMeterService");
    }
}
