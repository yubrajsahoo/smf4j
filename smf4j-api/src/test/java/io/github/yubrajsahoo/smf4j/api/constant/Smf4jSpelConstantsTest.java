package io.github.yubrajsahoo.smf4j.api.constant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Smf4jSpelConstants Unit Test")
class Smf4jSpelConstantsTest {

    @Test
    @DisplayName("Test constant values")
    void testConstantValues() {
        assertEquals("#methodName", Smf4jSpelConstants.METHOD_NAME);
        assertEquals("#methodSignature.declaringType.simpleName", Smf4jSpelConstants.CLASS_NAME);
        assertEquals("#methodSignature.declaringTypeName", Smf4jSpelConstants.FULLY_QUALIFIED_CLASS_NAME);
        assertEquals("#methodSignature.declaringType.simpleName + '.' + #methodName", Smf4jSpelConstants.CLASS_AND_METHOD_NAME);
        assertEquals("#a0", Smf4jSpelConstants.FIRST_ARG);
        assertEquals("#a1", Smf4jSpelConstants.SECOND_ARG);
        assertEquals("#result", Smf4jSpelConstants.RESULT);
        assertEquals("#error", Smf4jSpelConstants.ERROR);
        assertEquals("#error != null ? #error.class.simpleName : 'None'", Smf4jSpelConstants.ERROR_TYPE);
        assertEquals("#error != null ? #error.message : 'None'", Smf4jSpelConstants.ERROR_MESSAGE);
    }

    @Test
    @DisplayName("Test private constructor to ensure 100% code coverage")
    void testPrivateConstructor() throws Exception {
        Constructor<Smf4jSpelConstants> constructor = Smf4jSpelConstants.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor is not private");
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
