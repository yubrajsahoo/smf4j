/*
 * Copyright 2024 Yubraj Sahoo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.yubrajsahoo.smf4j.api.constant;

import java.util.List;

/**
 * Common constants used across the SMF4J API.
 */
public final class MetricsConstant {

    /**
     * Default fallback string literal indicating an unset or unspecified metric attribute value.
     */
    public static final String NONE = "none";

    /**
     * Default message prefix used when logging metrics.
     */
    public static final String DEFAULT_LOG_MESSAGE = "Metrics Logs For With->";

    /**
     * Default message prefix used when logging metrics that are disabled.
     */
    public static final String DEFAULT_DISABLED_LOG_MESSAGE = "Metrics Disabled For->";

    /**
     * List of allowed prefixes that indicate a SpEL expression rather than a literal value.
     */
    public static final List<String> ALLOWED_SPEL_DESIGNS = List.of("@", "#", "T");

    /**
     * Variable name used in SpEL evaluation contexts to represent the AOP join point.
     */
    public static final String JOIN_POINT = "joinPoint";

    /**
     * Variable name used in SpEL evaluation contexts to represent the method signature.
     */
    public static final String METHOD_SIGNATURE = "methodSignature";

    /**
     * Variable name used in SpEL evaluation contexts to represent the method name.
     */
    public static final String METHOD_NAME = "methodName";

    /**
     * Variable name used in SpEL evaluation contexts to represent the return value of an intercepted method.
     */
    public static final String RESULT = "result";

    /**
     * Variable name used in SpEL evaluation contexts to represent the exception thrown by an intercepted method.
     */
    public static final String ERROR = "error";

    /**
     * Variable name used in SpEL evaluation contexts to represent the root cause of an exception thrown by an intercepted method.
     */
    public static final String ROOT_ERROR = "rootError";

    public static final String ERROR_METRICS = "ERROR";


    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private MetricsConstant() {
    }
}
