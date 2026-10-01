package io.github.yubrajsahoo.smf4j.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Smf4jMetricsProperties Unit Test")
class Smf4jMetricsPropertiesTest {

    @Test
    @DisplayName("Should return default values when fields are not set")
    void shouldReturnDefaultValues() {
        Smf4jMetricsProperties properties = new Smf4jMetricsProperties();

        assertEquals(Smf4jMetricsProperties.DEFAULT_LOG_LEVEL, properties.getLogLevel());
        assertEquals(Smf4jMetricsProperties.DEFAULT_LOG_MESSAGE, properties.getLogMessage());
        assertEquals(Smf4jMetricsProperties.DEFAULT_LOG_LEVEL, properties.getDisableLogLevel());
        assertEquals(Smf4jMetricsProperties.DEFAULT_DISABLED_LOG_MESSAGE, properties.getDisableLogMessage());
    }

    @Test
    @DisplayName("Should return default values when fields are set to empty strings")
    void shouldReturnDefaultValuesWhenEmpty() {
        Smf4jMetricsProperties properties = new Smf4jMetricsProperties();
        properties.setLogLevel("   ");
        properties.setLogMessage("");
        properties.setDisableLogLevel(" ");
        properties.setDisableLogMessage("\t");

        assertEquals(Smf4jMetricsProperties.DEFAULT_LOG_LEVEL, properties.getLogLevel());
        assertEquals(Smf4jMetricsProperties.DEFAULT_LOG_MESSAGE, properties.getLogMessage());
        assertEquals(Smf4jMetricsProperties.DEFAULT_LOG_LEVEL, properties.getDisableLogLevel());
        assertEquals(Smf4jMetricsProperties.DEFAULT_DISABLED_LOG_MESSAGE, properties.getDisableLogMessage());
    }

    @Test
    @DisplayName("Should return assigned values when fields are set")
    void shouldReturnAssignedValues() {
        Smf4jMetricsProperties properties = new Smf4jMetricsProperties();
        
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
