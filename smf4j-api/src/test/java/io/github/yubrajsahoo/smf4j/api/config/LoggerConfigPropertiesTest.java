package io.github.yubrajsahoo.smf4j.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoggerConfigProperties Unit Test")
class LoggerConfigPropertiesTest {

    @Test
    @DisplayName("Should return default values when fields are not set")
    void shouldReturnDefaultValues() {
        LoggerConfigProperties properties = new LoggerConfigProperties();

        assertEquals(LoggerConfigProperties.DEFAULT_LOG_LEVEL, properties.getLogLevel());
        assertEquals(LoggerConfigProperties.DEFAULT_LOG_MESSAGE, properties.getLogMessage());
        assertEquals(LoggerConfigProperties.DEFAULT_LOG_LEVEL, properties.getDisableLogLevel());
        assertEquals(LoggerConfigProperties.DEFAULT_DISABLED_LOG_MESSAGE, properties.getDisableLogMessage());
    }

    @Test
    @DisplayName("Should return default values when fields are set to empty strings")
    void shouldReturnDefaultValuesWhenEmpty() {
        LoggerConfigProperties properties = new LoggerConfigProperties();
        properties.setLogLevel("   ");
        properties.setLogMessage("");
        properties.setDisableLogLevel(" ");
        properties.setDisableLogMessage("\t");

        assertEquals(LoggerConfigProperties.DEFAULT_LOG_LEVEL, properties.getLogLevel());
        assertEquals(LoggerConfigProperties.DEFAULT_LOG_MESSAGE, properties.getLogMessage());
        assertEquals(LoggerConfigProperties.DEFAULT_LOG_LEVEL, properties.getDisableLogLevel());
        assertEquals(LoggerConfigProperties.DEFAULT_DISABLED_LOG_MESSAGE, properties.getDisableLogMessage());
    }

    @Test
    @DisplayName("Should return assigned values when fields are set")
    void shouldReturnAssignedValues() {
        LoggerConfigProperties properties = new LoggerConfigProperties();
        
        properties.setLogLevel("DEBUG");
        properties.setLogMessage("Custom Log->");
        properties.setDisableLogLevel("TRACE");
        properties.setDisableLogMessage("Custom Disabled Log->");

        assertEquals("DEBUG", properties.getLogLevel());
        assertEquals("Custom Log->", properties.getLogMessage());
        assertEquals("TRACE", properties.getDisableLogLevel());
        assertEquals("Custom Disabled Log->", properties.getDisableLogMessage());
    }
}
