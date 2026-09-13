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

package io.github.yubrajsahoo.smf4jcore.spel;

import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.helper.JsonConverter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@DisplayName("SpelEvaluator Unit Test")
@SpringBootTest(classes = Smf4jAutoConfiguration.class)
class SpelEvaluatorTest {

    @Autowired
    private SpelEvaluator spelEvaluator;


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
    @DisplayName("testEvaluate with literal expression")
    void testEvaluate_Literal() {
        String expression = "literalInput";
        String parsedExpression = spelEvaluator.evaluate(expression, buildContext(null, null));

        assertEquals(expression, parsedExpression);
    }

    @Test
    @DisplayName("testEvaluate with SpEL expression result string")
    void testEvaluate_Spel_Result() {
        String result = "SUCCESS";
        String expression = "#result";

        String parsedExpression = spelEvaluator.evaluate(expression, buildContext(result, null));

        assertEquals(result, parsedExpression);
    }

    @Test
    @DisplayName("testEvaluate with SpEL expression with result object")
    void testEvaluate_Spel_Result_Object() {
        CounterMetrics result = JsonConverter.read(
                "src/test/resources/json/counter-metrics.json", CounterMetrics.class
        );

        String expression = "#result.increment";

        String parsedExpression = spelEvaluator.evaluate(expression, buildContext(result, null));

        assertEquals("3", parsedExpression);
    }

    @Test
    @DisplayName("testEvaluate with SpEL expression with result object")
    void testEvaluate_Spel_Error() {
        RuntimeException exception = new RuntimeException("Demo Message");

        String expression = "#error.getMessage()";

        String parsedExpression = spelEvaluator.evaluate(expression, buildContext(null, exception));

        assertEquals(exception.getMessage(), parsedExpression);
    }

    @Test
    @DisplayName("testEvaluate with SpEL expression with bean reference (@)")
    void testEvaluate_Spel_Bean_Reference() throws org.springframework.expression.AccessException {
        CounterMetrics mockBean = new CounterMetrics();
        mockBean.setIncrement(5L);
        when(beanResolver.resolve(Mockito.any(), Mockito.eq("myBean"))).thenReturn(mockBean);

        String expression = "@myBean.increment";
        String parsedExpression = spelEvaluator.evaluate(expression, buildContext(null, null));

        assertEquals("5", parsedExpression);
    }

    @Test
    @DisplayName("testEvaluate with SpEL expression with type reference (T)")
    void testEvaluate_Spel_Type_Reference() {
        String expression = "T(java.lang.String).valueOf(100)";
        String parsedExpression = spelEvaluator.evaluate(expression, buildContext(null, null));

        assertEquals("100", parsedExpression);
    }

    @Test
    @DisplayName("evaluate should return none when expression is null, empty or blank")
    void testEvaluate_EmptyOrNull() {
        assertEquals("none", spelEvaluator.evaluate(null, buildContext(null, null)));
        assertEquals("none", spelEvaluator.evaluate("", buildContext(null, null)));
        assertEquals("none", spelEvaluator.evaluate("   ", buildContext(null, null)));
    }

    @Test
    @DisplayName("evaluate should return none when SpEL evaluates to null")
    void testEvaluate_NullResult() {
        String expression = "#nullVar"; // undefined variable evaluates to null
        assertEquals("none", spelEvaluator.evaluate(expression, buildContext(null, null)));
    }

    @Test
    @DisplayName("evaluate should return none when SpEL evaluation throws exception")
    void testEvaluate_Exception() {
        String expression = "#result.nonExistentMethod()"; // throws SpelEvaluationException
        assertEquals("none", spelEvaluator.evaluate(expression, buildContext("Success", null)));
    }

    @Test
    @DisplayName("evaluate should return none when SpEL parsing throws exception")
    void testEvaluate_ParseException() {
        String expression = "#invalid("; // throws SpelParseException
        assertEquals("none", spelEvaluator.evaluate(expression, buildContext(null, null)));
    }

    private StandardEvaluationContext buildContext(Object result, Throwable error) {
        return SpelContextBuilder.buildContext(joinPoint, result, error, beanResolver);
    }
}
