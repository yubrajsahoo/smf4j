package io.github.yubrajsahoo.smf4jcore.core;

import io.github.yubrajsahoo.smf4jcore.core.constant.MetricsConstant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MetricsConstantTest {

    @Test
    @DisplayName("Test private constructor")
    void testPrivateConstructor() throws Exception {
        Constructor<MetricsConstant> constructor = MetricsConstant.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
