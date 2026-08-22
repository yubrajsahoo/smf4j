package io.github.yubrajsahoo.smf4jcore.constants;

import java.util.List;

/**
 * Common constants used across the SMF4J Core framework.
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
public final class MetricsConstant {

    /**
     * Default fallback string literal indicating an unset or unspecified metric attribute value.
     */
    public static final String NONE = "none";

    /**
     * Default message prefix used by {@link io.github.yubrajsahoo.smf4jcore.utils.MetricsLogger} when logging metrics.
     */
    public static final String DEFAULT_LOG_MESSAGE = "Metrics Logs For With->";

    /**
     * Default message prefix used by {@link io.github.yubrajsahoo.smf4jcore.utils.MetricsLogger}
     * when logging metrics that are disabled.
     */
    public static final String DEFAULT_DISABLED_LOG_MESSAGE = "Metrics Disabled For->";

    /**
     * List of allowed prefixes that indicate a SpEL expression rather than a literal value.
     * <p>
     * Supported prefixes:
     * <ul>
     *   <li>{@code @} – bean reference expressions</li>
     *   <li>{@code #} – variable reference expressions</li>
     *   <li>{@code T} – type reference expressions</li>
     * </ul>
     * </p>
     */
    public static final List<String> ALLOWED_SPEL_DESIGNS = List.of("@", "#", "T");

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private MetricsConstant() {
    }
}
