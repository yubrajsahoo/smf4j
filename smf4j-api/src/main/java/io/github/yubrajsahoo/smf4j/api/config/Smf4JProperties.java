package io.github.yubrajsahoo.smf4j.api.config;

public class Smf4JProperties {
    private LogMetricsProperties logMetrics = new LogMetricsProperties();
    private LoggerConfigProperties loggerConfig = new LoggerConfigProperties();

    public LogMetricsProperties getLogMetrics() {
        return logMetrics;
    }

    public void setLogMetrics(LogMetricsProperties logMetrics) {
        this.logMetrics = logMetrics;
    }

    public LoggerConfigProperties getLoggerConfig() {
        return loggerConfig;
    }

    public void setLoggerConfig(LoggerConfigProperties loggerConfig) {
        this.loggerConfig = loggerConfig;
    }
}

