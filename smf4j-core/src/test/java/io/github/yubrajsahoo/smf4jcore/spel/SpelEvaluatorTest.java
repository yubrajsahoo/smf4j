package io.github.yubrajsahoo.smf4jcore.spel;

import io.github.yubrajsahoo.smf4jcore.Smf4jCoreTestApplication;
import io.github.yubrajsahoo.smf4jcore.constants.MetricsConstant;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class SpelEvaluatorTest extends Smf4jCoreTestApplication {
    @Autowired
    private SpelEvaluator spelEvaluator;
    @Autowired
    private BeanResolver beanResolver;

    @Test
    @DisplayName("Test SpelEvaluator evaluate method with result expressions")
    void testEvaluate_error() {
        Exception exception = new RuntimeException("Error Message", new IOException("File Not Found"));

        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, null,
                exception, null);
        String expression = "#error.getMessage()";
        String expression2 = "#rootError.getMessage()";

        String evaluatedValue = spelEvaluator.evaluate(expression, context);
        String evaluatedValue2 = spelEvaluator.evaluate(expression2, context);

        assertEquals("Error Message", evaluatedValue,
                "The evaluated value should match the expected result error.");

        assertEquals("File Not Found", evaluatedValue2,
                "The evaluated value should match the expected result error.");
    }

    @Test
    @DisplayName("Test SpelEvaluator evaluate method with arguments")
    void testEvaluate_argument() {
        JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        MethodSignature methodSignature = Mockito.mock(MethodSignature.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"param1", "param2"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{1, 2});

        StandardEvaluationContext context = SpelContextBuilder.buildContext(joinPoint, null,
                null, null);

        String expression1 = "#param1";
        String expression2 = "#param2";

        int val1 = Integer.parseInt(spelEvaluator.evaluate(expression1, context));
        int val2 = Integer.parseInt(spelEvaluator.evaluate(expression2, context));

        assertEquals(1, val1);
        assertEquals(2, val2);
    }

    @Test
    @DisplayName("Test SpelEvaluator evaluate method with type param")
    void testEvaluate_T() {
        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, null,
                null, null);
        String expression = "T(io.github.yubrajsahoo.smf4jcore.constants.MetricsConstant.NONE)";

        String evalVal = spelEvaluator.evaluate(expression, context);

        assertEquals(MetricsConstant.NONE, evalVal);
    }

    @Test
    @DisplayName("Test SpelEvaluator evaluate method with at the rate")
    void testEvaluate_AtTheRate_Param() {
        JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        MethodSignature methodSignature = Mockito.mock(MethodSignature.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"param1", "param2"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{1, 2});

        StandardEvaluationContext context = SpelContextBuilder.buildContext(joinPoint, null,
                null, beanResolver);
        String expression = "@evaluatorTestBean.sum(#param1,#param2)";

        String evalValue = spelEvaluator.evaluate(expression, context);
        assertEquals("3", evalValue);
    }

    @Test
    @DisplayName("Test SpelEvaluator evaluate method with result")
    void testEvaluate_Result() {
        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, "ABC",
                null, null);

        String expression = "#result.length()";
        String evalValue = spelEvaluator.evaluate(expression, context);
        assertEquals("3", evalValue);
    }

    @Test
    @DisplayName("Test SpelEvaluator evaluate method with at the rate Exception")
    void testEvaluate_AtTheRate_Exception() {
        JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        MethodSignature methodSignature = Mockito.mock(MethodSignature.class);

        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getParameterNames()).thenReturn(new String[]{"param1", "param2"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{1, 2});

        StandardEvaluationContext context = SpelContextBuilder.buildContext(joinPoint, null,
                null, null);
        String expression = "@evaluatorTestBean.sum(#param1,#param2)";

        String evalValue = spelEvaluator.evaluate(expression, context);
        assertEquals(MetricsConstant.NONE, evalValue);
    }

    @Test
    @DisplayName("Test SpelEvaluator evaluate method with null expression")
    void testEvaluate_withNullExpression() {
        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, null,
                null, null);

        String evalValue = spelEvaluator.evaluate(null, context);
        assertEquals(MetricsConstant.NONE, evalValue);
    }

    @Test
    void testEvaluate_InvalidExpression() {
        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, null,
                null, null);

        String expression = "expression";

        String evalValue = spelEvaluator.evaluate(expression, context);
        assertEquals(expression, evalValue);
    }
}
