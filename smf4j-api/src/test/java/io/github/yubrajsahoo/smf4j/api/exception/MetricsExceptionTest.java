package io.github.yubrajsahoo.smf4j.api.exception;

import io.github.yubrajsahoo.smf4j.api.constant.MetricsConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetricsExceptionTest {
    @Test
    @DisplayName("Test Constructors")
    void testConstructors() {
        MetricsException e1 = new MetricsException("msg");
        assertThat(e1.getMessage()).isEqualTo("msg");
        assertThat(e1.getMetrics()).isEqualTo(MetricsConstant.ERROR_METRICS);

        Exception cause = new RuntimeException("cause");
        MetricsException e2 = new MetricsException("msg2", cause);
        assertThat(e2.getMessage()).isEqualTo("msg2");
        assertThat(e2.getCause()).isSameAs(cause);
        assertThat(e2.getMetrics()).isEqualTo(MetricsConstant.ERROR_METRICS);

        MetricsException e3 = new MetricsException("msg3", "custom-metric");
        assertThat(e3.getMessage()).isEqualTo("msg3");
        assertThat(e3.getMetrics()).isEqualTo("custom-metric");

        MetricsException e4 = new MetricsException("msg4", cause, "custom-metric2");
        assertThat(e4.getMessage()).isEqualTo("msg4");
        assertThat(e4.getCause()).isSameAs(cause);
        assertThat(e4.getMetrics()).isEqualTo("custom-metric2");
    }
}

