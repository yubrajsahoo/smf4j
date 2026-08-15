package io.github.yubrajsahoo.smf4jcore.annotation;

import io.github.yubrajsahoo.smf4jcore.constants.MetricsConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a key-value metric tag or dimension pair used within metric annotations such as {@link Counter}.
 * <p>
 * The {@link #value()} attribute supports both static text values and Spring Expression Language (SpEL)
 * expressions to dynamically extract dimensional values from runtime method execution contexts.
 * </p>
 *
 * <h2>SpEL Examples</h2>
 * <ul>
 *   <li>{@code @Tags(key = "region", value = "us-east-1")} (static literal)</li>
 *   <li>{@code @Tags(key = "userId", value = "#userId")} (method argument)</li>
 *   <li>{@code @Tags(key = "status", value = "#result.status")} (return value property)</li>
 *   <li>{@code @Tags(key = "exception", value = "#error.class.simpleName")} (exception property)</li>
 * </ul>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see Counter
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Tags {

    /**
     * The tag key or dimension name.
     *
     * @return the tag key, defaults to {@value MetricsConstant#NONE}
     */
    String key() default MetricsConstant.NONE;

    /**
     * The tag value or SpEL expression.
     * <p>
     * Prefix with {@code #} to reference method parameter names, {@code #result}, or {@code #error}.
     * </p>
     *
     * @return the tag value or SpEL expression string, defaults to {@value MetricsConstant#NONE}
     */
    String value() default MetricsConstant.NONE;
}
