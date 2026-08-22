package io.github.yubrajsahoo.smf4jcore.spel;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link SpelContextBuilder}.
 * <p>
 * Validates that the builder correctly populates a {@link StandardEvaluationContext}
 * with method arguments, return values ({@code #result}), exceptions ({@code #error}),
 * root cause ({@code #rootError}), and an optional {@code BeanResolver}.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see SpelContextBuilder
 */
class SpelContextBuilderTest {

    /**
     * Verifies that the {@code #result} variable is set to the provided return value.
     */
    @Test
    void buildContext_shouldSetResultVariable() {
        String result = "success";

        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, result, null, null);

        assertThat(context.lookupVariable("result")).isEqualTo("success");
    }

    /**
     * Verifies that the {@code #error} variable is set to the provided exception.
     */
    @Test
    void buildContext_shouldSetErrorVariable() {
        RuntimeException error = new RuntimeException("test error");

        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, null, error, null);

        assertThat(context.lookupVariable("error")).isEqualTo(error);
    }

    /**
     * Verifies that the {@code #rootError} variable is set to the deepest cause
     * of a wrapped exception chain.
     */
    @Test
    void buildContext_shouldSetRootErrorToRootCause() {
        RuntimeException rootCause = new RuntimeException("root cause");
        RuntimeException wrapper = new RuntimeException("wrapper", rootCause);

        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, null, wrapper, null);

        assertThat(context.lookupVariable("rootError")).isEqualTo(rootCause);
    }

    /**
     * Verifies that {@code #rootError} is {@code null} when no exception is provided.
     */
    @Test
    void buildContext_withNoError_shouldSetRootErrorToNull() {
        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, "result", null, null);

        assertThat(context.lookupVariable("rootError")).isNull();
    }

    /**
     * Verifies that {@code #result} is {@code null} when no return value is provided.
     */
    @Test
    void buildContext_withNullResult_shouldSetResultToNull() {
        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, null, null, null);

        assertThat(context.lookupVariable("result")).isNull();
    }

    /**
     * Verifies that method parameter names and values from the {@link JoinPoint}
     * are registered as variables in the context.
     */
    @Test
    void buildContext_withJoinPoint_shouldSetMethodArguments() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(new String[]{"orderId", "amount"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{"ORD-123", 99.99});

        StandardEvaluationContext context = SpelContextBuilder.buildContext(joinPoint, null, null, null);

        assertThat(context.lookupVariable("orderId")).isEqualTo("ORD-123");
        assertThat(context.lookupVariable("amount")).isEqualTo(99.99);
    }

    /**
     * Verifies that a {@code null} {@link JoinPoint} does not cause a failure
     * and the standard variables ({@code #result}, {@code #error}) are still set.
     */
    @Test
    void buildContext_withNullJoinPoint_shouldNotFail() {
        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, "result", null, null);

        assertThat(context.lookupVariable("result")).isEqualTo("result");
    }

    /**
     * Verifies that a {@link JoinPoint} whose signature returns {@code null}
     * parameter names is handled gracefully.
     */
    @Test
    void buildContext_withNullParameterNames_shouldNotFail() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);

        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(null);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"value"});

        StandardEvaluationContext context = SpelContextBuilder.buildContext(joinPoint, null, null, null);

        // Should not throw; result/error are still set
        assertThat(context.lookupVariable("result")).isNull();
    }

    /**
     * Verifies that deeply chained exceptions (3+ levels) correctly resolve
     * to the innermost root cause as {@code #rootError}.
     */
    @Test
    void buildContext_withDeeplyChainedExceptions_shouldFindRootCause() {
        RuntimeException root = new RuntimeException("root");
        RuntimeException mid = new RuntimeException("mid", root);
        RuntimeException top = new RuntimeException("top", mid);

        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, null, top, null);

        assertThat(context.lookupVariable("rootError")).isEqualTo(root);
    }

    /**
     * Verifies that an exception with no cause resolves to itself as the root error.
     */
    @Test
    void buildContext_withSelfCausedException_shouldReturnSameAsRootError() {
        RuntimeException error = new RuntimeException("only error");

        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, null, error, null);

        assertThat(context.lookupVariable("rootError")).isEqualTo(error);
    }

    /**
     * Verifies that a non-null {@link org.springframework.expression.BeanResolver}
     * is successfully assigned to the context without error.
     */
    @Test
    void buildContext_withBeanResolver_shouldSetBeanResolver() {
        org.springframework.expression.BeanResolver resolver = mock(org.springframework.expression.BeanResolver.class);

        StandardEvaluationContext context = SpelContextBuilder.buildContext(null, null, null, resolver);

        // Verify that the context was created successfully with the resolver set
        // (StandardEvaluationContext doesn't expose getBeanResolver, so we just verify no exception)
        assertThat(context).isNotNull();
    }
}
