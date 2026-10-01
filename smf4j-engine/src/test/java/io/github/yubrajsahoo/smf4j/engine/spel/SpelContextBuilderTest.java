/*
 *
 *  * Copyright 2024 Yubraj Sahoo
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.github.yubrajsahoo.smf4j.engine.spel;

import config.Smf4jEngineTestConfiguration;
import io.github.yubrajsahoo.smf4j.api.constant.MetricsConstant;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("SpelContextBuilder Unit Test")
@SpringBootTest(classes = Smf4jEngineTestConfiguration.class)
class SpelContextBuilderTest {

    @Mock
    private BeanResolver beanResolver;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    @BeforeEach
    void setUp() {
        Mockito.reset(joinPoint, methodSignature, beanResolver);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
    }

    @Test
    @DisplayName("testBuildContext with all valid inputs")
    void testBuildContext() {
        String[] paramNames = {"userId", "isActive"};
        Object[] args = {123L, true};

        when(methodSignature.getParameterNames()).thenReturn(paramNames);
        when(methodSignature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(args);

        Object result = "Success";
        Throwable rootCause = new IllegalArgumentException("Root cause");
        Throwable error = new RuntimeException("Outer error", rootCause);

        StandardEvaluationContext context = SpelContextBuilder.buildContext(
                joinPoint, result, error, beanResolver
        );

        assertEquals(123L, context.lookupVariable("userId"));
        assertEquals(true, context.lookupVariable("isActive"));
        
        Object[] resolvedArgs = (Object[]) context.lookupVariable("args");
        assertNotNull(resolvedArgs);
        assertEquals(123L, resolvedArgs[0]);
        assertEquals(true, resolvedArgs[1]);
        
        assertEquals(123L, context.lookupVariable("a0"));
        assertEquals(true, context.lookupVariable("a1"));
        assertEquals(123L, context.lookupVariable("p0"));
        assertEquals(true, context.lookupVariable("p1"));

        assertEquals("Success", context.lookupVariable(MetricsConstant.RESULT));
        assertSame(error, context.lookupVariable(MetricsConstant.ERROR));
        assertSame(rootCause, context.lookupVariable(MetricsConstant.ROOT_ERROR));
        assertSame(beanResolver, context.getBeanResolver());
        assertSame(joinPoint, context.lookupVariable(MetricsConstant.JOIN_POINT));
        assertSame(methodSignature, context.lookupVariable(MetricsConstant.METHOD_SIGNATURE));
        assertEquals("testMethod", context.lookupVariable(MetricsConstant.METHOD_NAME));
    }

    @Test
    @DisplayName("buildContext should handle null joinPoint")
    void buildContext_withNullJoinPoint() {
        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, "Success", null, null);

        assertEquals("Success", context.lookupVariable(MetricsConstant.RESULT));
        assertNull(context.lookupVariable(MetricsConstant.ERROR));
        assertNull(context.lookupVariable(MetricsConstant.ROOT_ERROR));
        assertNull(context.getBeanResolver());
        assertNull(context.lookupVariable(MetricsConstant.JOIN_POINT));
        assertNull(context.lookupVariable(MetricsConstant.METHOD_SIGNATURE));
        assertNull(context.lookupVariable(MetricsConstant.METHOD_NAME));
    }

    @Test
    @DisplayName("buildContext should handle non-MethodSignature gracefully")
    void buildContext_withNonMethodSignature() {
        Signature plainSignature = mock(Signature.class);
        when(joinPoint.getSignature()).thenReturn(plainSignature);

        StandardEvaluationContext context = SpelContextBuilder.buildContext(joinPoint, null, null, null);

        assertNull(context.lookupVariable(MetricsConstant.RESULT)); // Just verify context creation didn't throw
        assertSame(joinPoint, context.lookupVariable(MetricsConstant.JOIN_POINT));
        assertNull(context.lookupVariable(MetricsConstant.METHOD_SIGNATURE));
        assertNull(context.lookupVariable(MetricsConstant.METHOD_NAME));
    }

    @Test
    @DisplayName("buildContext should handle null parameters or arguments gracefully")
    void buildContext_withNullArgsAndParams() {
        when(methodSignature.getParameterNames()).thenReturn(null);
        when(joinPoint.getArgs()).thenReturn(null);
        when(methodSignature.getName()).thenReturn("testMethod");

        StandardEvaluationContext context = SpelContextBuilder.buildContext(joinPoint, null, null, null);

        assertNull(context.lookupVariable(MetricsConstant.RESULT));
        assertSame(joinPoint, context.lookupVariable(MetricsConstant.JOIN_POINT));
        assertSame(methodSignature, context.lookupVariable(MetricsConstant.METHOD_SIGNATURE));
        assertEquals("testMethod", context.lookupVariable(MetricsConstant.METHOD_NAME));
    }

    @Test
    @DisplayName("buildContext should handle null arguments gracefully")
    void buildContext_withNullArgs() {
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"userId"});
        when(joinPoint.getArgs()).thenReturn(null);
        when(methodSignature.getName()).thenReturn("testMethod");

        StandardEvaluationContext context = SpelContextBuilder.buildContext(joinPoint, null, null, null);

        assertNull(context.lookupVariable("userId"));
        assertSame(joinPoint, context.lookupVariable(MetricsConstant.JOIN_POINT));
        assertSame(methodSignature, context.lookupVariable(MetricsConstant.METHOD_SIGNATURE));
        assertEquals("testMethod", context.lookupVariable(MetricsConstant.METHOD_NAME));
    }

    @Test
    @DisplayName("buildContext should handle mismatched arguments and parameters lengths")
    void buildContext_withMismatchedArgsAndParams() {
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"userId", "isActive"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{123L}); // only 1 arg provided
        when(methodSignature.getName()).thenReturn("testMethod");

        StandardEvaluationContext context = SpelContextBuilder.buildContext(joinPoint, null, null, null);

        assertEquals(123L, context.lookupVariable("userId"));
        assertNull(context.lookupVariable("isActive"));
        assertSame(joinPoint, context.lookupVariable(MetricsConstant.JOIN_POINT));
        assertSame(methodSignature, context.lookupVariable(MetricsConstant.METHOD_SIGNATURE));
        assertEquals("testMethod", context.lookupVariable(MetricsConstant.METHOD_NAME));
    }

    @Test
    @DisplayName("buildContext should correctly resolve error without nested cause")
    void buildContext_withErrorNoCause() {
        Throwable error = new RuntimeException("Single error");

        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, null, error, null);

        assertSame(error, context.lookupVariable(MetricsConstant.ERROR));
        assertSame(error, context.lookupVariable(MetricsConstant.ROOT_ERROR));
        assertNull(context.lookupVariable(MetricsConstant.JOIN_POINT));
        assertNull(context.lookupVariable(MetricsConstant.METHOD_SIGNATURE));
        assertNull(context.lookupVariable(MetricsConstant.METHOD_NAME));
    }

    @Test
    @DisplayName("Verify actual SpEL evaluation works for #args[0], #a0, and #parameterName")
    void testSpelEvaluationForArguments() {
        // Arrange
        String[] paramNames = {"userId", "isActive"};
        Object[] args = {456L, false};
        
        when(methodSignature.getParameterNames()).thenReturn(paramNames);
        when(methodSignature.getName()).thenReturn("updateUser");
        when(joinPoint.getArgs()).thenReturn(args);
        
        StandardEvaluationContext context = SpelContextBuilder.buildContext(
                joinPoint, null, null, null
        );
        
        org.springframework.expression.ExpressionParser parser = new org.springframework.expression.spel.standard.SpelExpressionParser();
        
        // Act & Assert
        // Test args array access
        assertEquals(456L, parser.parseExpression("#args[0]").getValue(context));
        assertEquals(false, parser.parseExpression("#args[1]").getValue(context));
        
        // Test short parameter index access
        assertEquals(456L, parser.parseExpression("#a0").getValue(context));
        assertEquals(false, parser.parseExpression("#a1").getValue(context));
        assertEquals(456L, parser.parseExpression("#p0").getValue(context));
        
        // Test named parameter access
        assertEquals(456L, parser.parseExpression("#userId").getValue(context));
        assertEquals(false, parser.parseExpression("#isActive").getValue(context));
        
        // Test method name access
        assertEquals("updateUser", parser.parseExpression("#methodName").getValue(context));
    }

    @Test
    @DisplayName("Test private constructor")
    void testPrivateConstructor() throws Exception {
        java.lang.reflect.Constructor<SpelContextBuilder> constructor = SpelContextBuilder.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
