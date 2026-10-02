package io.github.yubrajsahoo.smf4j.benchmark.constant;

/**
 * Constants used across the benchmark module.
 */
public class BenchmarkConstants {

    private BenchmarkConstants() {
        // Prevent instantiation
    }

    /** Repository name */
    public static final String REPO_NAME = "load-test-report";
    /** Project directory name */
    public static final String PROJECT_DIR_NAME = "smf4j";
    /** Gatling directory name */
    public static final String GATLING_DIR_NAME = "gatling";
    
    /** JMH result file name */
    public static final String JMH_RESULT_FILE_NAME = "jmh-result.json";
    
    /** Status key */
    public static final String STATUS_KEY = "status";
    /** Status success */
    public static final String STATUS_SUCCESS = "success";
    /** Status error */
    public static final String STATUS_ERROR = "error";
    
    /** Message key */
    public static final String MESSAGE_KEY = "message";
    /** Report file key */
    public static final String REPORT_FILE_KEY = "reportFile";
    /** Report directory key */
    public static final String REPORT_DIR_KEY = "reportDir";
    
    /** Gatling main class */
    public static final String GATLING_MAIN_CLASS = "io.gatling.app.Gatling";
    /** Gatling simulation class */
    public static final String GATLING_SIMULATION_CLASS = "io.github.yubrajsahoo.smf4j.benchmark.gatling.Smf4jSimulation";
    
    /** GitHub raw base URL */
    public static final String GITHUB_RAW_BASE_URL = "https://raw.githubusercontent.com/yubrajsahoo/load-test-report/main/smf4j/";
}
