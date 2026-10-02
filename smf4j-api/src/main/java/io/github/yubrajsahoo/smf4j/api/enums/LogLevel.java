package io.github.yubrajsahoo.smf4j.api.enums;

import org.slf4j.Logger;

import java.util.Arrays;

/**
 * Represents the severity levels used for logging metrics and other diagnostic information.
 */
public enum LogLevel {
    /**
     * Indicates that logging is completely disabled.
     */
    DISABLED,

    /**
     * Used for fine-grained informational events that are most useful to debug an application.
     */
    DEBUG,

    /**
     * Used for informational messages that highlight the progress of the application at coarse-grained level.
     */
    INFO,

    /**
     * Used for potentially harmful situations which still allow the application to continue running.
     */
    WARN,

    /**
     * Used for error events that might still allow the application to continue running.
     */
    ERROR;

    /**
     * Retrieves the {@code LogLevel} corresponding to the provided string.
     * <p>
     * The matching is case-insensitive. If no matching {@code LogLevel} is found,
     * it defaults to {@link #INFO}.
     * </p>
     *
     * @param level the string representation of the log level to retrieve
     * @return the corresponding {@code LogLevel}, or {@link #INFO} if not found
     */
    public static LogLevel getLogLevel(String level) {
        return Arrays.stream(LogLevel.values())
                .filter(logLevel -> logLevel.name().equalsIgnoreCase(level))
                .findFirst()
                .orElse(LogLevel.INFO);
    }

    /**
     * Logs the provided message at the specified log level.
     * <p>
     * Supported log levels are "DEBUG", "INFO", "WARN", and "ERROR". If an unsupported
     * or null log level is provided, it defaults to the INFO level.
     * </p>
     *
     * @param logger  the logger to use
     * @param level   the target log level (e.g., "DEBUG", "INFO", "WARN", "ERROR")
     * @param message the formatted metric message to be logged
     */
    public static void log(Logger logger, LogLevel level, String message) {
        switch (level) {
            case DISABLED:
                break;
            case DEBUG:
                if (logger.isDebugEnabled()) {
                    logger.debug(message);
                }
                break;
            case WARN:
                if (logger.isWarnEnabled()) {
                    logger.warn(message);
                }
                break;
            case ERROR:
                if (logger.isErrorEnabled()) {
                    logger.error(message);
                }
                break;
            case INFO:
            default:
                if (logger.isInfoEnabled()) {
                    logger.info(message);
                }
                break;
        }
    }
}
