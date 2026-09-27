package io.github.yubrajsahoo.smf4j.api.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class LogLevelTest {

    @ParameterizedTest
    @CsvSource({
            "DEBUG, DEBUG",
            "debug, DEBUG",
            "INFO, INFO",
            "info, INFO",
            "WARN, WARN",
            "warn, WARN",
            "ERROR, ERROR",
            "error, ERROR",
            "DISABLED, DISABLED",
            "disabled, DISABLED"
    })
    void testGetLogLevel_ValidInput_ReturnsExpectedLevel(String input, LogLevel expected) {
        assertEquals(expected, LogLevel.getLogLevel(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "", "123", "FATAL"})
    void testGetLogLevel_InvalidInput_ReturnsInfo(String input) {
        assertEquals(LogLevel.INFO, LogLevel.getLogLevel(input));
    }

    @Test
    void testGetLogLevel_NullInput_ReturnsInfo() {
        assertEquals(LogLevel.INFO, LogLevel.getLogLevel(null));
    }

    @Test
    void testLog_Disabled_DoesNothing() {
        Logger logger = mock(Logger.class);
        LogLevel.log(logger, LogLevel.DISABLED, "Test message");
        verifyNoInteractions(logger);
    }

    @Test
    void testLog_Debug_WhenEnabled_LogsMessage() {
        Logger logger = mock(Logger.class);
        when(logger.isDebugEnabled()).thenReturn(true);

        LogLevel.log(logger, LogLevel.DEBUG, "Debug message");

        verify(logger).isDebugEnabled();
        verify(logger).debug("Debug message");
        verifyNoMoreInteractions(logger);
    }

    @Test
    void testLog_Debug_WhenDisabled_DoesNotLogMessage() {
        Logger logger = mock(Logger.class);
        when(logger.isDebugEnabled()).thenReturn(false);

        LogLevel.log(logger, LogLevel.DEBUG, "Debug message");

        verify(logger).isDebugEnabled();
        verifyNoMoreInteractions(logger);
    }

    @Test
    void testLog_Info_WhenEnabled_LogsMessage() {
        Logger logger = mock(Logger.class);
        when(logger.isInfoEnabled()).thenReturn(true);

        LogLevel.log(logger, LogLevel.INFO, "Info message");

        verify(logger).isInfoEnabled();
        verify(logger).info("Info message");
        verifyNoMoreInteractions(logger);
    }

    @Test
    void testLog_Info_WhenDisabled_DoesNotLogMessage() {
        Logger logger = mock(Logger.class);
        when(logger.isInfoEnabled()).thenReturn(false);

        LogLevel.log(logger, LogLevel.INFO, "Info message");

        verify(logger).isInfoEnabled();
        verifyNoMoreInteractions(logger);
    }

    @Test
    void testLog_Warn_WhenEnabled_LogsMessage() {
        Logger logger = mock(Logger.class);
        when(logger.isWarnEnabled()).thenReturn(true);

        LogLevel.log(logger, LogLevel.WARN, "Warn message");

        verify(logger).isWarnEnabled();
        verify(logger).warn("Warn message");
        verifyNoMoreInteractions(logger);
    }

    @Test
    void testLog_Warn_WhenDisabled_DoesNotLogMessage() {
        Logger logger = mock(Logger.class);
        when(logger.isWarnEnabled()).thenReturn(false);

        LogLevel.log(logger, LogLevel.WARN, "Warn message");

        verify(logger).isWarnEnabled();
        verifyNoMoreInteractions(logger);
    }

    @Test
    void testLog_Error_WhenEnabled_LogsMessage() {
        Logger logger = mock(Logger.class);
        when(logger.isErrorEnabled()).thenReturn(true);

        LogLevel.log(logger, LogLevel.ERROR, "Error message");

        verify(logger).isErrorEnabled();
        verify(logger).error("Error message");
        verifyNoMoreInteractions(logger);
    }

    @Test
    void testLog_Error_WhenDisabled_DoesNotLogMessage() {
        Logger logger = mock(Logger.class);
        when(logger.isErrorEnabled()).thenReturn(false);

        LogLevel.log(logger, LogLevel.ERROR, "Error message");

        verify(logger).isErrorEnabled();
        verifyNoMoreInteractions(logger);
    }
}
