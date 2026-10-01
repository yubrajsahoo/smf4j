package io.github.yubrajsahoo.smf4j.api.config;

/**
 * Configuration properties for SMF4J metrics.
 * <p>
 * This class encapsulates the configuration for metric logging. It allows you to customize
 * the log levels and the messages used when metrics are recorded or when metric logging is disabled.
 * In a Spring Boot environment, these properties are typically bound to the {@code smf4j.metrics} prefix
 * in your {@code application.yml} or {@code application.properties} file.
 * </p>
 *
 * <p><b>Example configuration (application.yml):</b></p>
 * <pre>
 * smf4j:
 *   metrics:
 *     log-level: INFO
 *     log-message: "Metrics Logs For With->"
 *     disable-log-level: DEBUG
 *     disable-log-message: "Metrics Disabled For->"
 * </pre>
 */
public class Smf4jMetricsProperties {

    /**
     * Default log level used when logging metrics.
     */
    public static final String DEFAULT_LOG_LEVEL = "INFO";

    /**
     * Default message prefix used when logging metrics.
     */
    public static final String DEFAULT_LOG_MESSAGE = "Metrics Logs For With->";

    /**
     * Default message prefix used when logging metrics that are disabled.
     */
    public static final String DEFAULT_DISABLED_LOG_MESSAGE = "Metrics Disabled For->";

    /**
     * The log level for active metric logs.
     * <p>
     * Specifies the severity level at which enabled metrics should be logged
     * (e.g., {@code DEBUG}, {@code INFO}, {@code WARN}, {@code ERROR}).
     * </p>
     */
    private String logLevel;

    /**
     * The log message format/prefix for active metric logs.
     * <p>
     * This prefix is prepended to the generated metric details when the metric is enabled.
     * </p>
     */
    private String logMessage;

    /**
     * The log level used when metric logging is disabled.
     * <p>
     * Specifies the severity level at which disabled metrics should be logged
     * (e.g., {@code TRACE}, {@code DEBUG}). Typically set to a lower severity to avoid spamming the logs.
     * </p>
     */
    private String disableLogLevel;

    /**
     * The message format/prefix used when metric logging is disabled.
     * <p>
     * This prefix is prepended to the generated metric details when the metric is disabled.
     * </p>
     */
    private String disableLogMessage;

    /**
     * Gets the configured log level for active metrics.
     * If no value is provided, it returns {@link #DEFAULT_LOG_LEVEL}.
     *
     * @return the log level as a string (e.g., {@code INFO})
     */
    public String getLogLevel() {
        return (logLevel == null || logLevel.trim().isEmpty())
                ? DEFAULT_LOG_LEVEL
                : logLevel;
    }

    /**
     * Sets the log level for active metrics.
     *
     * @param logLevel the desired log level (e.g., {@code INFO}, {@code DEBUG})
     */
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * Gets the log message prefix for active metrics.
     * If no value is provided, it returns {@link #DEFAULT_LOG_MESSAGE}.
     *
     * @return the log message prefix
     */
    public String getLogMessage() {
        return (logMessage == null || logMessage.trim().isEmpty())
                ? DEFAULT_LOG_MESSAGE
                : logMessage;
    }

    /**
     * Sets the log message prefix for active metrics.
     *
     * @param logMessage the message prefix to be prepended to the metric log
     */
    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    /**
     * Gets the configured log level used when metrics are disabled.
     * If no value is provided, it falls back to {@link #DEFAULT_LOG_LEVEL}.
     *
     * @return the log level for disabled metrics
     */
    public String getDisableLogLevel() {
        return (disableLogLevel == null || disableLogLevel.trim().isEmpty())
                ? DEFAULT_LOG_LEVEL
                : disableLogLevel;
    }

    /**
     * Sets the log level used when metrics are disabled.
     *
     * @param disableLogLevel the desired log level (e.g., {@code DEBUG})
     */
    public void setDisableLogLevel(String disableLogLevel) {
        this.disableLogLevel = disableLogLevel;
    }

    /**
     * Gets the log message prefix used when metrics are disabled.
     * If no value is provided, it returns {@link #DEFAULT_DISABLED_LOG_MESSAGE}.
     *
     * @return the disable log message prefix
     */
    public String getDisableLogMessage() {
        return (disableLogMessage == null || disableLogMessage.trim().isEmpty())
                ? DEFAULT_DISABLED_LOG_MESSAGE
                : disableLogMessage;
    }

    /**
     * Sets the log message prefix used when metrics are disabled.
     *
     * @param disableLogMessage the message prefix to be prepended when metrics are disabled
     */
    public void setDisableLogMessage(String disableLogMessage) {
        this.disableLogMessage = disableLogMessage;
    }
}
