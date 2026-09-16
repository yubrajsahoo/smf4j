package io.github.yubrajsahoo.smf4jcore.service.impl;

import io.github.yubrajsahoo.smf4jcore.annotation.Gauge;
import io.github.yubrajsahoo.smf4jcore.domain.GaugeMetrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.factory.MeterFactory;
import io.github.yubrajsahoo.smf4jcore.logger.MetricsLogger;
import io.github.yubrajsahoo.smf4jcore.service.MetricsService;
import io.github.yubrajsahoo.smf4jcore.spel.SpelEvaluator;
import io.micrometer.core.instrument.Tags;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.function.ToDoubleFunction;

/**
 * Service for evaluating tags and recording gauge metrics.
 */
public class GaugeMetricsService extends MetricsService {

    /**
     * Constructs a new {@link GaugeMetricsService}.
     *
     * @param spelEvaluator the SpEL evaluator
     * @param meterFactory the meter factory
     * @param metricsLogger the metrics logger
     */
    public GaugeMetricsService(SpelEvaluator spelEvaluator, MeterFactory meterFactory, MetricsLogger metricsLogger) {
        super(spelEvaluator, meterFactory, metricsLogger);
    }

    /**
     * Evaluates tag expressions and records the gauge metric.
     *
     * @param gauge the {@link Gauge} annotation metadata
     * @param bean the bean object to measure
     * @param function the function to extract the metric value
     * @param context the SpEL evaluation context
     */
    public void recordGauge(Gauge gauge, Object bean, ToDoubleFunction<Object> function, StandardEvaluationContext context) {
        if (gauge == null || bean == null || function == null) {
            log.warn("Cannot record metrics for null Gauge annotation, bean, or function");
            return;
        }

        try {
            Tags tags = evaluateTags(gauge.tags(), context);

            GaugeMetrics<Object> metrics = GaugeMetrics.builder(gauge.name(), bean, function)
                    .description(gauge.description())
                    .tags(tags)
                    .enabled(gauge.enable())
                    .build();

            recordMetrics(MetricsType.GAUGE, metrics);
        } catch (Exception exception) {
            log.error("Error while recording gauge metric '{}': {}", gauge.name(), exception.getMessage(), exception);
        }
    }
}
