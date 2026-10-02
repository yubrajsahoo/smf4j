package io.github.yubrajsahoo.smf4j.api.config;

/**
 * Root configuration properties for SMF4J.
 * <p>
 * This class typically binds to the {@code smf4j} prefix in configuration files
 * (e.g., {@code application.yml} or {@code application.properties}) when used within
 * a Spring Boot environment. It provides access to nested configuration
 * properties for log metrics and logger behavior.
 * </p>
 */
public class Smf4JProperties {

    /**
     * Default constructor for {@link Smf4JProperties}.
     */
    public Smf4JProperties() {
        // Default constructor
    }

    /**
     * Properties related to log metric definitions (name and description).
     */
    private LogMetricsProperties logMetrics = new LogMetricsProperties();

    /**
     * Properties related to the logger's behavior (log levels and messages).
     */
    private LoggerConfigProperties loggerConfig = new LoggerConfigProperties();

    /**
     * Retrieves the log metrics properties.
     *
     * @return the {@link LogMetricsProperties} configuration
     */
    public LogMetricsProperties getLogMetrics() {
        return logMetrics;
    }

    /**
     * Sets the log metrics properties.
     *
     * @param logMetrics the {@link LogMetricsProperties} configuration to set
     */
    public void setLogMetrics(LogMetricsProperties logMetrics) {
        this.logMetrics = logMetrics;
    }

    /**
     * Retrieves the logger configuration properties.
     *
     * @return the {@link LoggerConfigProperties} configuration
     */
    public LoggerConfigProperties getLoggerConfig() {
        return loggerConfig;
    }

    /**
     * Sets the logger configuration properties.
     *
     * @param loggerConfig the {@link LoggerConfigProperties} configuration to set
     */
    public void setLoggerConfig(LoggerConfigProperties loggerConfig) {
        this.loggerConfig = loggerConfig;
    }
}

