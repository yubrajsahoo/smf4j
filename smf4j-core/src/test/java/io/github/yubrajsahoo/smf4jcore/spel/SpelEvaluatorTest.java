package io.github.yubrajsahoo.smf4jcore.spel;

import io.github.yubrajsahoo.smf4jcore.constants.MetricsConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link SpelEvaluator}.
 * <p>
 * Validates expression evaluation against a {@link StandardEvaluationContext},
 * including literal passthrough, variable resolution, nested property access,
 * type expressions, expression caching, and graceful fallback on errors.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see SpelEvaluator
 * @see MetricsConstant#NONE
 */
class SpelEvaluatorTest {

    private SpelEvaluator spelEvaluator;
    private StandardEvaluationContext context;

    /**
     * Initialises a {@link SpelEvaluator} with a fresh {@link SpelExpressionParser}
     * and an empty {@link StandardEvaluationContext} before each test.
     */
    @BeforeEach
    void setUp() {
        ExpressionParser parser = new SpelExpressionParser();
        spelEvaluator = new SpelEvaluator(parser);
        context = new StandardEvaluationContext();
    }

    /**
     * Verifies that a {@code null} expression returns the fallback value {@value MetricsConstant#NONE}.
     */
    @Test
    void evaluate_withNullExpression_shouldReturnNone() {
        String result = spelEvaluator.evaluate(null, context);
        assertThat(result).isEqualTo(MetricsConstant.NONE);
    }

    /**
     * Verifies that an empty string expression returns the fallback value.
     */
    @Test
    void evaluate_withEmptyExpression_shouldReturnNone() {
        String result = spelEvaluator.evaluate("", context);
        assertThat(result).isEqualTo(MetricsConstant.NONE);
    }

    /**
     * Verifies that a blank (whitespace-only) expression returns the fallback value.
     */
    @Test
    void evaluate_withBlankExpression_shouldReturnNone() {
        String result = spelEvaluator.evaluate("   ", context);
        assertThat(result).isEqualTo(MetricsConstant.NONE);
    }

    /**
     * Verifies that a literal string (not starting with {@code #}, {@code @}, or {@code T})
     * is returned as-is without SpEL parsing.
     */
    @Test
    void evaluate_withLiteralValue_shouldReturnLiteral() {
        String result = spelEvaluator.evaluate("us-east-1", context);
        assertThat(result).isEqualTo("us-east-1");
    }

    /**
     * Verifies that a {@code #variable} expression resolves the named variable
     * from the evaluation context.
     */
    @Test
    void evaluate_withHashVariable_shouldResolveFromContext() {
        context.setVariable("userId", "user-42");

        String result = spelEvaluator.evaluate("#userId", context);
        assertThat(result).isEqualTo("user-42");
    }

    /**
     * Verifies that a nested property access expression (e.g. {@code #result.status})
     * resolves to the property value of the context variable.
     */
    @Test
    void evaluate_withNestedProperty_shouldResolveNestedValue() {
        context.setVariable("result", new TestResult("SUCCESS"));

        String result = spelEvaluator.evaluate("#result.status", context);
        assertThat(result).isEqualTo("SUCCESS");
    }

    /**
     * Verifies that a variable explicitly set to {@code null} returns the fallback value.
     */
    @Test
    void evaluate_withNullVariable_shouldReturnNone() {
        context.setVariable("missing", null);

        String result = spelEvaluator.evaluate("#missing", context);
        assertThat(result).isEqualTo(MetricsConstant.NONE);
    }

    /**
     * Verifies that referencing an undefined variable returns the fallback value
     * rather than throwing an exception.
     */
    @Test
    void evaluate_withUndefinedVariable_shouldReturnNone() {
        String result = spelEvaluator.evaluate("#undefinedVar", context);
        assertThat(result).isEqualTo(MetricsConstant.NONE);
    }

    /**
     * Verifies that a {@code T(type).member} expression is evaluated correctly,
     * e.g. accessing {@code Math.PI}.
     */
    @Test
    void evaluate_withSpelTypeExpression_shouldEvaluate() {
        String result = spelEvaluator.evaluate("T(java.lang.Math).PI", context);
        assertThat(result).isEqualTo(String.valueOf(Math.PI));
    }

    /**
     * Verifies that evaluating the same expression twice returns consistent results,
     * exercising the internal expression cache.
     */
    @Test
    void evaluate_shouldCacheExpressions() {
        context.setVariable("x", "hello");

        String result1 = spelEvaluator.evaluate("#x", context);
        String result2 = spelEvaluator.evaluate("#x", context);

        assertThat(result1).isEqualTo("hello");
        assertThat(result2).isEqualTo("hello");
    }

    /**
     * Verifies that a non-string variable (e.g. {@link Integer}) is converted
     * to its string representation via {@code toString()}.
     */
    @Test
    void evaluate_withIntegerVariable_shouldReturnStringRepresentation() {
        context.setVariable("count", 42);

        String result = spelEvaluator.evaluate("#count", context);
        assertThat(result).isEqualTo("42");
    }

    /**
     * Verifies that an expression referencing a deeply nested path on a null variable
     * returns the fallback value rather than throwing.
     */
    @Test
    void evaluate_withInvalidSpelExpression_shouldReturnNone() {
        String result = spelEvaluator.evaluate("#invalid.deeply.nested.path", context);
        assertThat(result).isEqualTo(MetricsConstant.NONE);
    }

    /**
     * Simple test POJO for validating nested property access in SpEL expressions.
     */
    public static class TestResult {
        private final String status;

        /**
         * Constructs a new {@link TestResult} with the given status.
         *
         * @param status the status value
         */
        public TestResult(String status) {
            this.status = status;
        }

        /**
         * Returns the status value.
         *
         * @return the status
         */
        public String getStatus() {
            return status;
        }
    }
}
