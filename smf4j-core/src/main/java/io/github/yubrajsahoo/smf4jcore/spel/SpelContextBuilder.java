package io.github.yubrajsahoo.smf4jcore.spel;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Objects;

/**
 * Builder utility for constructing and populating {@link StandardEvaluationContext} instances
 * from an AOP {@link JoinPoint} execution.
 * <p>
 * Extracts method argument names and values, binding them into the SpEL context alongside
 * special variables:
 * </p>
 * <ul>
 *   <li>{@code #result} - The return value of the intercepted method invocation (if successful)</li>
 *   <li>{@code #error} - The {@link Throwable} thrown by the intercepted method invocation (if failed)</li>
 *   <li>Parameter names - Each method parameter by its declared name (e.g. {@code #orderId})</li>
 * </ul>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
public final class SpelContextBuilder {

    /**
     * Default constructor for {@link SpelContextBuilder}.
     */
    private SpelContextBuilder() {
    }

    /**
     * Constructs and populates a {@link StandardEvaluationContext} from an intercepted {@link JoinPoint},
     * method return value, and thrown exception.
     *
     * @param joinPoint the AOP join point representing the method invocation; may be {@code null}
     * @param result    the return value of the intercepted method; may be {@code null}
     * @param error     the exception thrown by the intercepted method; may be {@code null}
     * @return a fully populated {@link StandardEvaluationContext}
     */
    public static StandardEvaluationContext buildContext(JoinPoint joinPoint,
                                                         Object result,
                                                         Throwable error,
                                                         BeanResolver resolver) {

        StandardEvaluationContext context = new StandardEvaluationContext();

        if (Objects.nonNull(resolver)) {
            context.setBeanResolver(resolver);
        }

        updateMethodArguments(joinPoint, context);
        Throwable rootCause = error != null
                ? getRootCause(error)
                : null;

        context.setVariable("result", result);
        context.setVariable("error", error);
        context.setVariable("rootError", rootCause);
        return context;
    }

    /**
     * Method to update method arguments in context.
     *
     * @param joinPoint the joinpoint.
     * @param context   the context
     */
    private static void updateMethodArguments(JoinPoint joinPoint, StandardEvaluationContext context) {
        if (joinPoint != null && joinPoint.getSignature() instanceof MethodSignature signature) {
            String[] parameterNames = signature.getParameterNames();
            Object[] arguments = joinPoint.getArgs();

            if (parameterNames != null && arguments != null) {
                for (int i = 0; i < parameterNames.length && i < arguments.length; i++) {
                    context.setVariable(parameterNames[i], arguments[i]);
                }
            }
        }
    }

    /**
     * Recursively retrieves the root cause of a given {@link Throwable}.
     *
     * @param error the exception from which to extract the root cause; may be {@code null}
     * @return the root cause of the exception, or {@code null} if the exception is null or has no cause
     */
    private static Throwable getRootCause(Throwable error) {
        while (error.getCause() != null) {
            error = error.getCause();
        }
        return error;
    }
}