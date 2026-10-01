package io.github.yubrajsahoo.smf4j.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("LogMetricsProperties Unit Test")
class LogMetricsPropertiesTest {

    @Test
    @DisplayName("Should test LogMetricsProperties getters and setters")
    void shouldTestProperties() {
        LogMetricsProperties properties = new LogMetricsProperties();
        
        assertEquals("smf4j.log.metrics", properties.getName());
        assertEquals(LogMetricsProperties.NONE, properties.getDescription());

        properties.setName("testName");
        properties.setDescription("testDesc");

        assertEquals("testName", properties.getName());
        assertEquals("testDesc", properties.getDescription());
    }
}
