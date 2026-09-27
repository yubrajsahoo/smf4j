package io.github.yubrajsahoo.smf4j.api.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MetricsConstant Unit Test")
class MetricsConstantTest {

    @Test
    @DisplayName("Test Constants Values")
    void testConstantsValues() {
        assertThat(MetricsConstant.NONE).isEqualTo("none");
        assertThat(MetricsConstant.DEFAULT_LOG_LEVEL).isEqualTo("INFO");
        assertThat(MetricsConstant.DEFAULT_LOG_MESSAGE).isEqualTo("Metrics Logs For With->");
        assertThat(MetricsConstant.DEFAULT_DISABLED_LOG_MESSAGE).isEqualTo("Metrics Disabled For->");
        assertThat(List.of("@", "#", "T")).containsExactlyElementsOf(MetricsConstant.ALLOWED_SPEL_DESIGNS);
        assertThat(MetricsConstant.JOIN_POINT).isEqualTo("joinPoint");
        assertThat(MetricsConstant.METHOD_SIGNATURE).isEqualTo("methodSignature");
        assertThat(MetricsConstant.METHOD_NAME).isEqualTo("methodName");
        assertThat(MetricsConstant.RESULT).isEqualTo("result");
        assertThat(MetricsConstant.ERROR).isEqualTo("error");
        assertThat(MetricsConstant.ROOT_ERROR).isEqualTo("rootError");
        assertThat(MetricsConstant.ERROR_METRICS).isEqualTo("ERROR");
    }

    @Test
    @DisplayName("Test Private Constructor")
    void testPrivateConstructor() throws Exception {
        Constructor<MetricsConstant> constructor = MetricsConstant.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        MetricsConstant instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }
}
