package io.github.yubrajsahoo.smf4j.engine.utils;

import io.github.yubrajsahoo.smf4j.api.constant.MetricsConstant;
import io.github.yubrajsahoo.smf4j.api.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4j.api.domain.Metrics;
import io.github.yubrajsahoo.smf4j.api.domain.Tag;
import io.github.yubrajsahoo.smf4j.api.enums.LogLevel;
import io.github.yubrajsahoo.smf4j.engine.service.impl.CounterMetricsService;

import java.util.List;

/**
 * Utility class for logging metrics across different log levels.
 * <p>
 * This class provides static methods to simplify the recording of counter metrics.
 * It requires initialization with a {@link CounterMetricsService} before its
 * static methods can be effectively used to record metrics.
 * </p>
 *
 * @author Yubraj Sahoo
 */
public class LogMetrics {
    private static CounterMetricsService metricsService;

    /**
     * Initializes the {@code MetricsLogger} with a specific {@link CounterMetricsService}.
     *
     * @param counterMetricsService the service used to record metrics
     */
    @SuppressWarnings("all")
    public LogMetrics(CounterMetricsService counterMetricsService) {
        metricsService = counterMetricsService;
    }

    /**
     * Records a counter metric at the specified log level.
     *
     * @param name        the name of the metric
     * @param description a brief description of the metric
     * @param enabled     whether the metric collection is enabled
     * @param level       the {@link LogLevel} at which to record the metric
     * @param tags        an array of {@link Tag}s associated with the metric
     */
    public static void log(String name, String description, boolean enabled, LogLevel level, Tag... tags) {
        Metrics metrics = buildCounter(level, name, description, enabled, tags);
        metricsService.recordCounter(metrics, level);
    }

    /**
     * Records a counter metric at the DEBUG log level.
     *
     * @param name        the name of the metric
     * @param description a brief description of the metric
     * @param enabled     whether the metric collection is enabled
     * @param tags        an array of {@link Tag}s associated with the metric
     */
    public static void debug(String name, String description, boolean enabled, Tag... tags) {
        LogLevel level = LogLevel.DEBUG;

        Metrics metrics = buildCounter(level, name, description, enabled, tags);
        metricsService.recordCounter(metrics, level);
    }

    /**
     * Records a counter metric at the INFO log level.
     *
     * @param name        the name of the metric
     * @param description a brief description of the metric
     * @param enabled     whether the metric collection is enabled
     * @param tags        an array of {@link Tag}s associated with the metric
     */
    public static void info(String name, String description, boolean enabled, Tag... tags) {
        LogLevel level = LogLevel.INFO;

        Metrics metrics = buildCounter(level, name, description, enabled, tags);
        metricsService.recordCounter(metrics, level);
    }

    /**
     * Records a counter metric at the WARN log level.
     *
     * @param name        the name of the metric
     * @param description a brief description of the metric
     * @param enabled     whether the metric collection is enabled
     * @param tags        an array of {@link Tag}s associated with the metric
     */
    public static void warn(String name, String description, boolean enabled, Tag... tags) {
        LogLevel level = LogLevel.WARN;

        Metrics metrics = buildCounter(level, name, description, enabled, tags);
        metricsService.recordCounter(metrics, level);
    }

    /**
     * Records a counter metric at the ERROR log level.
     *
     * @param name        the name of the metric
     * @param description a brief description of the metric
     * @param enabled     whether the metric collection is enabled
     * @param tags        an array of {@link Tag}s associated with the metric
     */
    public static void error(String name, String description, boolean enabled, Tag... tags) {
        LogLevel level = LogLevel.ERROR;

        Metrics metrics = buildCounter(level, name, description, enabled, tags);
        metricsService.recordCounter(metrics, level);
    }

    /**
     * Builds a {@link Metrics} instance for a counter.
     *
     * @param level
     * @param name        the name of the metric
     * @param description a brief description of the metric
     * @param enabled     whether the metric collection is enabled
     * @param tags        an array of {@link Tag}s associated with the metric
     * @return the constructed {@link Metrics} instance
     */
    private static Metrics buildCounter(LogLevel level, String name, String description, boolean enabled, Tag... tags) {
        List<Tag> tagList = new java.util.ArrayList<>(List.of(tags));
        tagList.add(new Tag(MetricsConstant.LEVEL, level.name()));

        return CounterMetrics.builder()
                .name(name)
                .description(description)
                .enable(enabled)
                .tags(tagList)
                .build();
    }
}