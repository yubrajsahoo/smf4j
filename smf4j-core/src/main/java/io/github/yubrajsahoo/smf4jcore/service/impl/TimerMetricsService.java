package io.github.yubrajsahoo.smf4jcore.service.impl;

import io.github.yubrajsahoo.smf4jcore.annotation.Timer;
import io.github.yubrajsahoo.smf4jcore.domain.TimerMetrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.factory.MeterFactory;
import io.github.yubrajsahoo.smf4jcore.logger.MetricsLogger;
import io.github.yubrajsahoo.smf4jcore.meter.service.MeterService;
import io.github.yubrajsahoo.smf4jcore.service.MetricsService;
import io.github.yubrajsahoo.smf4jcore.spel.SpelEvaluator;
import io.micrometer.core.instrument.Tags;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Service for starting and recording timer metrics.
 */
public class TimerMetricsService extends MetricsService {

    /**
     * Constructs a new {@link TimerMetricsService}.
     *
     * @param spelEvaluator the SpEL evaluator
     * @param meterFactory the meter factory
     * @param metricsLogger the metrics logger
     */
    public TimerMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        super(spelEvaluator, meterFactory, metricsLogger);
    }

    /**
     * Starts a timer sample.
     *
     * @param timer the timer annotation metadata
     * @return the started timer sample, or {@code null} if the timer is not enabled or meter service is unavailable
     */
    public io.micrometer.core.instrument.Timer.Sample start(Timer timer) {
        if (timer == null || !timer.enable()) {
            return null;
        }
        return meterFactory.getMeterService(MetricsType.TIMER)
                .map(MeterService::start)
                .orElse(null);
    }

    /**
     * Records the timer metric using the provided sample and evaluation context.
     *
     * @param sample the timer sample to stop and record
     * @param timer the timer annotation metadata
     * @param context the SpEL evaluation context
     */
    public void recordTimer(io.micrometer.core.instrument.Timer.Sample sample, Timer timer, StandardEvaluationContext context) {
        if (timer == null) {
            log.warn("Cannot record metrics for null Timer annotation or null sample");
            return;
        }

        try {
            Tags tags = evaluateTags(timer.tags(), context);

            TimerMetrics metrics = TimerMetrics.builder()
                    .name(timer.name())
                    .description(timer.description())
                    .tags(tags)
                    .enabled(timer.enable())
                    .sample(sample)
                    .build();

            recordMetrics(MetricsType.TIMER, metrics);
        } catch (Exception exception) {
            log.error("Error while recording timer metric '{}': {}", timer.name(), exception.getMessage(), exception);
        }
    }
}
