package io.github.yubrajsahoo.smf4j.engine.utils;

import io.github.yubrajsahoo.smf4j.api.config.LogMetricsProperties;
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
 * It requires initialization with a {@link CounterMetricsService} and optionally
 * {@link LogMetricsProperties} before its static methods can be effectively used.
 * </p>
 * <p>
 * If {@link LogMetricsProperties} are provided, you can omit the metric {@code name} 
 * and {@code description} in the log method calls, and it will fall back to the defaults 
 * specified in the properties.
 * </p>
 * <p>
 * Tags are passed as varargs of {@link String}s in sequential key-value pairs. 
 * For example: {@code LogMetrics.info(true, "key1", "value1", "key2", "value2")}. 
 * An odd number of tag arguments will result in an {@link IllegalArgumentException}.
 * </p>
 *
 * @author Yubraj Sahoo
 */
public class LogMetrics {
    private static CounterMetricsService metricsService;
    private static LogMetricsProperties properties;

    /**
     * Initializes the {@code MetricsLogger} with a specific {@link CounterMetricsService}.
     *
     * @param counterMetricsService the service used to record metrics
     * @param logMetricsProperties  properties for default name and description
     */
    @SuppressWarnings("all")
    public LogMetrics(CounterMetricsService counterMetricsService, LogMetricsProperties logMetricsProperties) {
        metricsService = counterMetricsService;
        properties = logMetricsProperties;
    }

    /**
     * Records a counter metric at the DEBUG log level.
     *
     * @param name        the name of the metric
     * @param description a brief description of the metric
     * @param enabled     whether the metric collection is enabled
     * @param tags        key-value pairs of tags as strings
     */
    public static void debug(String name, String description, boolean enabled, String... tags) {
        log(name, description, enabled, LogLevel.DEBUG, tags);
    }

    /**
     * Records a counter metric at the DEBUG log level using default name and description.
     *
     * @param enabled whether the metric collection is enabled
     * @param tags    key-value pairs of tags as strings
     */
    public static void debug(boolean enabled, String... tags) {
        log(enabled, LogLevel.DEBUG, tags);
    }

    /**
     * Records a counter metric at the INFO log level.
     *
     * @param name        the name of the metric
     * @param description a brief description of the metric
     * @param enabled     whether the metric collection is enabled
     * @param tags        key-value pairs of tags as strings
     */
    public static void info(String name, String description, boolean enabled, String... tags) {
        log(name, description, enabled, LogLevel.INFO, tags);
    }

    /**
     * Records a counter metric at the INFO log level using default name and description.
     *
     * @param enabled whether the metric collection is enabled
     * @param tags    key-value pairs of tags as strings
     */
    public static void info(boolean enabled, String... tags) {
        log(enabled, LogLevel.INFO, tags);
    }

    /**
     * Records a counter metric at the WARN log level.
     *
     * @param name        the name of the metric
     * @param description a brief description of the metric
     * @param enabled     whether the metric collection is enabled
     * @param tags        key-value pairs of tags as strings
     */
    public static void warn(String name, String description, boolean enabled, String... tags) {
        log(name, description, enabled, LogLevel.WARN, tags);
    }

    /**
     * Records a counter metric at the WARN log level using default name and description.
     *
     * @param enabled whether the metric collection is enabled
     * @param tags    key-value pairs of tags as strings
     */
    public static void warn(boolean enabled, String... tags) {
        log(enabled, LogLevel.WARN, tags);
    }

    /**
     * Records a counter metric at the ERROR log level.
     *
     * @param name        the name of the metric
     * @param description a brief description of the metric
     * @param enabled     whether the metric collection is enabled
     * @param tags        key-value pairs of tags as strings
     */
    public static void error(String name, String description, boolean enabled, String... tags) {
        log(name, description, enabled, LogLevel.ERROR, tags);
    }

    /**
     * Records a counter metric at the ERROR log level using default name and description.
     *
     * @param enabled whether the metric collection is enabled
     * @param tags    key-value pairs of tags as strings
     */
    public static void error(boolean enabled, String... tags) {
        log(enabled, LogLevel.ERROR, tags);
    }

    /**
     * Records a counter metric at the specified log level.
     *
     * @param name        the name of the metric
     * @param description a brief description of the metric
     * @param enabled     whether the metric collection is enabled
     * @param level       the {@link LogLevel} at which to record the metric
     * @param tags        key-value pairs of tags as strings
     */
    public static void log(String name, String description, boolean enabled, LogLevel level, String... tags) {
        Metrics metrics = buildCounter(level, name, description, enabled, tags);
        metricsService.recordCounter(metrics, level);
    }

    /**
     * Records a counter metric at the specified log level using default name and description.
     *
     * @param enabled whether the metric collection is enabled
     * @param level   the {@link LogLevel} at which to record the metric
     * @param tags    key-value pairs of tags as strings
     */
    public static void log(boolean enabled, LogLevel level, String... tags) {
        log(properties != null ? properties.getName() : null, properties != null ? properties.getDescription() : null, enabled, level, tags);
    }

    /**
     * Builds a {@link Metrics} instance for a counter.
     *
     * @param level       the {@link LogLevel} to associate with this metric
     * @param name        the name of the metric
     * @param description a brief description of the metric
     * @param enabled     whether the metric collection is enabled
     * @param tags        key-value pairs of tags as strings
     * @return the constructed {@link Metrics} instance
     * @throws IllegalArgumentException if an odd number of tags is provided
     */
    private static Metrics buildCounter(LogLevel level, String name, String description, boolean enabled, String... tags) {
        List<Tag> tagList = buildTags(tags);
        tagList.add(new Tag(MetricsConstant.LEVEL, level.name()));

        return CounterMetrics.builder()
                .name(name)
                .description(description)
                .enable(enabled)
                .tags(tagList)
                .build();
    }

    /**
     * Converts an array of strings representing key-value pairs into a list of {@link Tag}s.
     *
     * @param tags an array of strings where elements at even indices are keys and 
     *             elements at odd indices are values.
     * @return a list of constructed {@link Tag} instances
     * @throws IllegalArgumentException if an odd number of tags is provided
     */
    private static List<Tag> buildTags(String... tags) {
        List<Tag> tagList = new java.util.ArrayList<>();
        if (tags != null) {
            if (tags.length % 2 != 0) {
                throw new IllegalArgumentException("Tags must be provided in key-value pairs");
            }
            for (int i = 0; i < tags.length; i += 2) {
                tagList.add(new Tag(tags[i], tags[i + 1]));
            }
        }
        return tagList;
    }
}