package io.github.yubrajsahoo.smf4jcore.spel;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the {@link SpelContextBuilder} class, verifying the correct construction and population
 * of {@link StandardEvaluationContext} instances based on various scenarios of method invocation.
 */
public class SpelContextBuilderTest {

    @Test
    @DisplayName("Test buildContext with a error method invocation")
    public void testBuildContext_error() {
        Throwable error = new RuntimeException("Test exception", new IOException("File not found"));

        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, null,
                error, null);

        Throwable actualError = (Throwable) context.lookupVariable("error");
        Throwable rootError = (Throwable) context.lookupVariable("rootError");

        assertNotNull(actualError);
        assertEquals(RuntimeException.class, actualError.getClass());
        assertEquals("Test exception", actualError.getMessage());
        assertNotNull(rootError);
        assertEquals(IOException.class, rootError.getClass());
        assertEquals("File not found", rootError.getMessage());
    }

    @Test
    @DisplayName("Test buildContext with a successful method invocation")
    void testBuildContext_success() {
        String result = "Success";
        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, result,
                null, null);

        String actualResult = (String) context.lookupVariable("result");
        assertEquals("Success", actualResult);
    }

    @Test
    @DisplayName("Test buildContext with method parameters")
    void testBuildContext_withParameters() {
        JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        MethodSignature signature = Mockito.mock(MethodSignature.class);

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(new String[]{"param1", "param2"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{"value1", 42});

        StandardEvaluationContext context = SpelContextBuilder.buildContext(joinPoint, null,
                null, null);

        String param1Value = (String) context.lookupVariable("param1");
        Integer param2Value = (Integer) context.lookupVariable("param2");

        assertEquals("value1", param1Value);
        assertEquals(42, param2Value);
        assertNull(context.getBeanResolver());
    }

    @Test
    @DisplayName("Test buildContext with method for bean resolver")
    void testBuildContext_BeanResolver() {
        BeanResolver beanResolver = Mockito.mock(BeanResolver.class);
        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, null,
                null, beanResolver);

        assertEquals(beanResolver, context.getBeanResolver());
    }
}
