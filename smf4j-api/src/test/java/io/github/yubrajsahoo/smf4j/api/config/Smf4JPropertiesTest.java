package io.github.yubrajsahoo.smf4j.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Smf4JProperties Unit Test")
class Smf4JPropertiesTest {

    @Test
    @DisplayName("Should test Smf4JProperties getters and setters")
    void shouldTestProperties() {
        Smf4JProperties properties = new Smf4JProperties();
        
        assertNotNull(properties.getLogMetrics());
        assertNotNull(properties.getLoggerConfig());

        LogMetricsProperties logMetrics = new LogMetricsProperties();
        logMetrics.setName("testName");
        logMetrics.setDescription("testDesc");

        LoggerConfigProperties loggerConfig = new LoggerConfigProperties();
        loggerConfig.setLogLevel("DEBUG");

        properties.setLogMetrics(logMetrics);
        properties.setLoggerConfig(loggerConfig);

        assertEquals("testName", properties.getLogMetrics().getName());
        assertEquals("testDesc", properties.getLogMetrics().getDescription());
        assertEquals("DEBUG", properties.getLoggerConfig().getLogLevel());
    }
}
