package io.github.yubrajsahoo.smf4j.engine.utils;

import io.github.yubrajsahoo.smf4j.api.constant.MetricsConstant;
import io.github.yubrajsahoo.smf4j.api.domain.Metrics;
import io.github.yubrajsahoo.smf4j.api.domain.Tag;
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
        new LogMetrics(metricsService);
    }

    @Test
    @DisplayName("Should log metrics with specific log level")
    void logWithSpecificLevel() {
        Tag customTag = new Tag("key", "value");
        LogMetrics.log("test.metric", "Test Description", true, LogLevel.DISABLED, customTag);

        ArgumentCaptor<Metrics> metricsCaptor = ArgumentCaptor.forClass(Metrics.class);
        verify(metricsService).recordCounter(metricsCaptor.capture(), eq(LogLevel.DISABLED));

        Metrics capturedMetrics = metricsCaptor.getValue();
        assertEquals("test.metric", capturedMetrics.getName());
        assertEquals("Test Description", capturedMetrics.getDescription());
        assertTrue(capturedMetrics.isEnable());
        assertEquals(2, capturedMetrics.getTags().size());
        assertTrue(capturedMetrics.getTags().contains(customTag));
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
}
