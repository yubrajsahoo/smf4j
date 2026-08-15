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

    public static final String DEFAULT_DISABLED_LOG_MESSAGE = "Metrics Disabled For->";

    public static final List<String> ALLOWED_SPEL_DESIGNS = List.of("@", "#", "T");

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private MetricsConstant() {
    }
}
