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
     * Default message prefix used by {@link io.github.yubrajsahoo.smf4jcore.logger.MetricsLogger} when logging metrics.
     */
    public static final String DEFAULT_LOG_MESSAGE = "Metrics Logs For With->";

    /**
     * Default message prefix used by {@link io.github.yubrajsahoo.smf4jcore.logger.MetricsLogger}
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
