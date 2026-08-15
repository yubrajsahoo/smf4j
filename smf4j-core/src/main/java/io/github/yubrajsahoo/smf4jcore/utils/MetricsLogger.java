package io.github.yubrajsahoo.smf4jcore.utils;

import io.github.yubrajsahoo.smf4jcore.constants.MetricsConstant;
import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.domain.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for formatting and logging metric recording events at debug level.
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
public final class MetricsLogger {

    private static final Logger log = LoggerFactory.getLogger(MetricsLogger.class);

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private MetricsLogger() {
    }

    /**
     * Prepares a formatted log message string containing common metric attributes (name, tags, description).
     *
     * @param message the prefix message
     * @param metrics the metric whose attributes are being formatted
     * @return the formatted log message string
     */
    public static String prepareLog(String message, Metrics metrics) {
        StringBuilder logBuilder = new StringBuilder(message);
        logBuilder.append("name=")
                .append(metrics.getName());

        metrics.getTags().forEach(tag -> logBuilder.append("->")
                .append(tag.getKey())
                .append("=")
                .append(tag.getValue()));

        logBuilder
                .append("->")
                .append("description=")
                .append(metrics.getDescription());

        return logBuilder.toString();
    }

    /**
     * Logs the details of a recorded {@link CounterMetrics} at debug level if debug logging is enabled.
     *
     * @param metrics the counter metric data to log
     */
    public static void log(CounterMetrics metrics) {
        String message = metrics.isEnabled()
                ? MetricsConstant.DEFAULT_LOG_MESSAGE
                : MetricsConstant.DEFAULT_DISABLED_LOG_MESSAGE;

        String logMessage = prepareLog(message, metrics) +
                "->" +
                "increment=" +
                metrics.getIncrement();

        log.info(logMessage);
    }
}
