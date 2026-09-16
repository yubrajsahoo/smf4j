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

package io.github.yubrajsahoo.smf4jcore.core.spel;

import io.github.yubrajsahoo.smf4jcore.core.constant.MetricsConstant;
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
     * @param resolver  the bean resolver for resolving Spring beans in expressions; may be {@code null}
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

        if (Objects.nonNull(joinPoint)) {
            context.setVariable(MetricsConstant.JOIN_POINT, joinPoint);

            if (joinPoint.getSignature() instanceof MethodSignature signature) {
                context.setVariable(MetricsConstant.METHOD_SIGNATURE, signature);
                context.setVariable(MetricsConstant.METHOD_NAME, signature.getName());

                updateMethodArguments(joinPoint, context);
            }
        }

        Throwable rootCause = error != null
                ? getRootCause(error)
                : null;

        context.setVariable(MetricsConstant.RESULT, result);
        context.setVariable(MetricsConstant.ERROR, error);
        context.setVariable(MetricsConstant.ROOT_ERROR, rootCause);
        return context;
    }

    /**
     * Method to update method arguments in context.
     *
     * @param joinPoint the joinpoint.
     * @param context   the context
     */
    private static void updateMethodArguments(JoinPoint joinPoint, StandardEvaluationContext context) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] arguments = joinPoint.getArgs();

        if (parameterNames != null && arguments != null) {
            for (int i = 0; i < parameterNames.length && i < arguments.length; i++) {
                context.setVariable(parameterNames[i], arguments[i]);
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