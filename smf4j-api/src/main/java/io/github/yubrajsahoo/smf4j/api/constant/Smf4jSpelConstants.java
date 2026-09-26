package io.github.yubrajsahoo.smf4j.api.constant;

public final class Smf4jSpelConstants {
    private Smf4jSpelConstants() {
        //private constructure
    }

    /**
     * Resolves to the name of the intercepted method.
     */
    public static final String METHOD_NAME = "#methodName";

    /**
     * Resolves to the simple class name of the target class where the method is executing (e.g., "UserService").
     */
    public static final String CLASS_NAME = "#methodSignature.declaringType.simpleName";

    /**
     * Resolves to the fully qualified class name of the target class (e.g., "com.example.UserService").
     */
    public static final String FULLY_QUALIFIED_CLASS_NAME = "#methodSignature.declaringTypeName";

    /**
     * Resolves to a combination of the simple class name and method name (e.g., "UserService.getUser").
     */
    public static final String CLASS_AND_METHOD_NAME = "#methodSignature.declaringType.simpleName + '.' + #methodName";

    /**
     * Resolves to the first argument passed to the intercepted method.
     */
    public static final String FIRST_ARG = "#a0";

    /**
     * Resolves to the second argument passed to the intercepted method.
     */
    public static final String SECOND_ARG = "#a1";

    /**
     * Resolves to the returned result of the method execution.
     * Note: Only available in contexts evaluated after the method has successfully returned.
     */
    public static final String RESULT = "#result";

    /**
     * Resolves to the thrown Exception object during the method execution.
     * Note: Only available in error-handling contexts.
     */
    public static final String ERROR = "#error";

    /**
     * Resolves to the simple class name of the thrown exception (e.g., "IllegalArgumentException").
     * Safe against null errors (returns 'None' if no error occurred).
     * Highly useful for tagging metrics by the type of exception thrown.
     */
    public static final String ERROR_TYPE = "#error != null ? #error.class.simpleName : 'None'";

    /**
     * Resolves to the error message of the thrown exception.
     * Safe against null errors (returns 'None' if no error occurred).
     */
    public static final String ERROR_MESSAGE = "#error != null ? #error.message : 'None'";
}
