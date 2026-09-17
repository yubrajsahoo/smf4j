package io.github.yubrajsahoo.smf4j.api.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class MetricsConstantTest {

    @Test
    @DisplayName("Test Constants And Private Constructor")
    void testConstantsAndPrivateConstructor() throws Exception {
        assertThat(MetricsConstant.NONE).isEqualTo("none");
        assertThat(MetricsConstant.DEFAULT_LOG_MESSAGE).isNotNull();

        Constructor<MetricsConstant> constructor = MetricsConstant.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        MetricsConstant instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }
}

