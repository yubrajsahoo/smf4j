package io.github.yubrajsahoo.smf4jcore.spel;

import io.github.yubrajsahoo.smf4jcore.constants.MetricsConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe evaluator for Spring Expression Language (SpEL) expressions with parsed expression caching.
 * <p>
 * Evaluates metric tag expressions against a {@link StandardEvaluationContext}. Expressions that do not
 * start with {@code #} or {@code @} are treated as literal values without incurring parsing overhead.
 * Parsed expressions are cached in a {@link ConcurrentHashMap} for high performance.
 * If expression evaluation fails or yields null, a default fallback ({@value MetricsConstant#NONE}) is returned.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @see StandardEvaluationContext
 * @see ExpressionParser
 * @since 0.0.1
 */
public class SpelEvaluator {

    private static final Logger log = LoggerFactory.getLogger(SpelEvaluator.class);

    private final ExpressionParser parser;
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

    /**
     * Constructs a new {@link SpelEvaluator} with the given Spring {@link ExpressionParser}.
     *
     * @param expressionParser the Spring expression parser used to compile SpEL expressions
     */
    public SpelEvaluator(ExpressionParser expressionParser) {
        this.parser = expressionParser;
    }

    /**
     * Evaluates the given SpEL expression against the supplied {@link StandardEvaluationContext}.
     * <p>
     * If the expression is empty or blank, or does not start with {@code #} / {@code @}, it is returned directly
     * or defaulted to {@value MetricsConstant#NONE}.
     * </p>
     *
     * @param expression the SpEL expression string or literal text to evaluate
     * @param context    the evaluation context containing variables such as parameters, result, or error
     * @return the evaluated string representation of the expression value, or {@value MetricsConstant#NONE} if evaluation yields null or fails
     */
    public String evaluate(String expression, StandardEvaluationContext context) {
        if (!StringUtils.hasText(expression)) {
            return MetricsConstant.NONE;
        }
        if (MetricsConstant.ALLOWED_SPEL_DESIGNS.stream().noneMatch(s -> s.equals(expression))) {
            return expression;
        }

        try {
            Expression parsedExpression = expressionCache.computeIfAbsent(expression, parser::parseExpression);

            Object value = parsedExpression.getValue(context);

            return value != null
                    ? value.toString()
                    : MetricsConstant.NONE;

        } catch (Exception e) {
            log.warn("Failed to evaluate SpEL expression '{}': {}", expression, e.getMessage());
            return MetricsConstant.NONE;
        }
    }
}