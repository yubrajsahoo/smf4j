package io.github.yubrajsahoo.smf4j.engine.utils;

import io.github.yubrajsahoo.smf4j.api.constant.MetricsConstant;
import io.github.yubrajsahoo.smf4j.api.domain.Metrics;
import io.github.yubrajsahoo.smf4j.api.enums.LogLevel;
import io.github.yubrajsahoo.smf4j.engine.service.impl.CounterMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("LogMetrics Unit Test")
class LogMetricsTest {

    private CounterMetricsService metricsService;

    @BeforeEach
    void setUp() {
        metricsService = mock(CounterMetricsService.class);
        // Initialize the static fields of LogMetrics
        io.github.yubrajsahoo.smf4j.api.config.LogMetricsProperties properties = new io.github.yubrajsahoo.smf4j.api.config.LogMetricsProperties();
        properties.setName("default.metric");
        properties.setDescription("Default Description");
        new LogMetrics(metricsService, properties);
    }

    @Test
    @DisplayName("Should log metrics with specific log level")
    void logWithSpecificLevel() {
        LogMetrics.log("test.metric", "Test Description", true, LogLevel.DISABLED, "key", "value");

        ArgumentCaptor<Metrics> metricsCaptor = ArgumentCaptor.forClass(Metrics.class);
        verify(metricsService).recordCounter(metricsCaptor.capture(), eq(LogLevel.DISABLED));

        Metrics capturedMetrics = metricsCaptor.getValue();
        assertEquals("test.metric", capturedMetrics.getName());
        assertEquals("Test Description", capturedMetrics.getDescription());
        assertTrue(capturedMetrics.isEnable());
        assertEquals(2, capturedMetrics.getTags().size());
        assertTrue(capturedMetrics.getTags().stream().anyMatch(t -> "key".equals(t.getKey()) && "value".equals(t.getValue())));
        assertTrue(capturedMetrics.getTags().stream().anyMatch(t -> MetricsConstant.LEVEL.equals(t.getKey()) && LogLevel.DISABLED.name().equals(t.getValue())));
    }

    @Test
    @DisplayName("Should log metrics with DEBUG level")
    void debugLevel() {
        LogMetrics.debug("debug.metric", "Debug Description", false);

        ArgumentCaptor<Metrics> metricsCaptor = ArgumentCaptor.forClass(Metrics.class);
        verify(metricsService).recordCounter(metricsCaptor.capture(), eq(LogLevel.DEBUG));

        Metrics capturedMetrics = metricsCaptor.getValue();
        assertEquals("debug.metric", capturedMetrics.getName());
        assertFalse(capturedMetrics.isEnable());
        assertEquals(1, capturedMetrics.getTags().size());
    }

    @Test
    @DisplayName("Should log metrics with INFO level")
    void infoLevel() {
        LogMetrics.info("info.metric", "Info Description", true);

        ArgumentCaptor<Metrics> metricsCaptor = ArgumentCaptor.forClass(Metrics.class);
        verify(metricsService).recordCounter(metricsCaptor.capture(), eq(LogLevel.INFO));

        Metrics capturedMetrics = metricsCaptor.getValue();
        assertEquals("info.metric", capturedMetrics.getName());
        assertTrue(capturedMetrics.isEnable());
    }

    @Test
    @DisplayName("Should log metrics with WARN level")
    void warnLevel() {
        LogMetrics.warn("warn.metric", "Warn Description", true);

        ArgumentCaptor<Metrics> metricsCaptor = ArgumentCaptor.forClass(Metrics.class);
        verify(metricsService).recordCounter(metricsCaptor.capture(), eq(LogLevel.WARN));

        Metrics capturedMetrics = metricsCaptor.getValue();
        assertEquals("warn.metric", capturedMetrics.getName());
        assertTrue(capturedMetrics.isEnable());
    }

    @Test
    @DisplayName("Should log metrics with ERROR level")
    void errorLevel() {
        LogMetrics.error("error.metric", "Error Description", true);

        ArgumentCaptor<Metrics> metricsCaptor = ArgumentCaptor.forClass(Metrics.class);
        verify(metricsService).recordCounter(metricsCaptor.capture(), eq(LogLevel.ERROR));

        Metrics capturedMetrics = metricsCaptor.getValue();
        assertEquals("error.metric", capturedMetrics.getName());
        assertTrue(capturedMetrics.isEnable());
    }

    @Test
    @DisplayName("Should log metrics with default name and description")
    void defaultLevel() {
        LogMetrics.info(true);

        ArgumentCaptor<Metrics> metricsCaptor = ArgumentCaptor.forClass(Metrics.class);
        verify(metricsService).recordCounter(metricsCaptor.capture(), eq(LogLevel.INFO));

        Metrics capturedMetrics = metricsCaptor.getValue();
        assertEquals("default.metric", capturedMetrics.getName());
        assertEquals("Default Description", capturedMetrics.getDescription());
        assertTrue(capturedMetrics.isEnable());
    }

    @Test
    @DisplayName("Should test remaining overloaded default methods")
    void testOtherDefaultMethods() {
        LogMetrics.debug(true);
        LogMetrics.warn(true);
        LogMetrics.error(true);
        LogMetrics.log(true, LogLevel.DISABLED);

        verify(metricsService).recordCounter(any(Metrics.class), eq(LogLevel.DEBUG));
        verify(metricsService).recordCounter(any(Metrics.class), eq(LogLevel.WARN));
        verify(metricsService).recordCounter(any(Metrics.class), eq(LogLevel.ERROR));
        verify(metricsService).recordCounter(any(Metrics.class), eq(LogLevel.DISABLED));
    }

    @Test
    @DisplayName("Should test overloaded methods with only tags (default enabled=true)")
    void testOverloadedTagsOnlyMethods() {
        LogMetrics.debug("k1", "v1");
        LogMetrics.info("k2", "v2");
        LogMetrics.warn("k3", "v3");
        LogMetrics.error("k4", "v4");
        LogMetrics.log(LogLevel.DISABLED, "k5", "v5");

        ArgumentCaptor<Metrics> metricsCaptor = ArgumentCaptor.forClass(Metrics.class);
        verify(metricsService, times(5)).recordCounter(metricsCaptor.capture(), any(LogLevel.class));

        metricsCaptor.getAllValues().forEach(metrics -> {
            assertTrue(metrics.isEnable());
            assertEquals("default.metric", metrics.getName());
            assertEquals("Default Description", metrics.getDescription());
            assertTrue(metrics.getTags().size() >= 2);
        });
    }

    @Test
    @DisplayName("Should handle null properties gracefully")
    void testNullProperties() {
        // Create new instance with null properties
        new LogMetrics(metricsService, null);
        LogMetrics.info(true);

        ArgumentCaptor<Metrics> metricsCaptor = ArgumentCaptor.forClass(Metrics.class);
        verify(metricsService).recordCounter(metricsCaptor.capture(), eq(LogLevel.INFO));

        Metrics capturedMetrics = metricsCaptor.getValue();
        assertNull(capturedMetrics.getName());
        assertNull(capturedMetrics.getDescription());
    }

    @Test
    @DisplayName("Should disable metrics if properties isEnabled is false")
    void testPropertiesDisabled() {
        io.github.yubrajsahoo.smf4j.api.config.LogMetricsProperties properties = new io.github.yubrajsahoo.smf4j.api.config.LogMetricsProperties();
        properties.setEnabled(false);
        new LogMetrics(metricsService, properties);

        LogMetrics.info("test.metric", "Test Description", true);

        ArgumentCaptor<Metrics> metricsCaptor = ArgumentCaptor.forClass(Metrics.class);
        verify(metricsService).recordCounter(metricsCaptor.capture(), eq(LogLevel.INFO));

        Metrics capturedMetrics = metricsCaptor.getValue();
        assertFalse(capturedMetrics.isEnable());
    }

    @Test
    @DisplayName("Should handle null tags gracefully")
    void handleNullTags() {
        LogMetrics.log("test", "test", true, LogLevel.INFO, (String[]) null);

        ArgumentCaptor<Metrics> metricsCaptor = ArgumentCaptor.forClass(Metrics.class);
        verify(metricsService).recordCounter(metricsCaptor.capture(), eq(LogLevel.INFO));

        Metrics capturedMetrics = metricsCaptor.getValue();
        // Since tags is null, it should just add the LEVEL tag
        assertEquals(1, capturedMetrics.getTags().size());
        assertTrue(capturedMetrics.getTags().stream().anyMatch(t -> MetricsConstant.LEVEL.equals(t.getKey()) && LogLevel.INFO.name().equals(t.getValue())));
    }

    @Test
    @DisplayName("Should throw exception when odd number of tags are provided")
    void invalidTags() {
        assertThrows(IllegalArgumentException.class, () -> {
            LogMetrics.log("test", "test", true, LogLevel.INFO, "keyOnly");
        });
    }
}
