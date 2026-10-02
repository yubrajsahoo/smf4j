package io.github.yubrajsahoo.smf4j.benchmark.constant;

public class BenchmarkConstants {

    private BenchmarkConstants() {
        // Prevent instantiation
    }

    public static final String REPO_NAME = "load-test-report";
    public static final String PROJECT_DIR_NAME = "smf4j";
    public static final String GATLING_DIR_NAME = "gatling";
    
    public static final String JMH_RESULT_FILE_NAME = "jmh-result.json";
    
    public static final String STATUS_KEY = "status";
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";
    
    public static final String MESSAGE_KEY = "message";
    public static final String REPORT_FILE_KEY = "reportFile";
    public static final String REPORT_DIR_KEY = "reportDir";
    
    public static final String GATLING_MAIN_CLASS = "io.gatling.app.Gatling";
    public static final String GATLING_SIMULATION_CLASS = "io.github.yubrajsahoo.smf4j.benchmark.gatling.Smf4jSimulation";
    
    public static final String GITHUB_RAW_BASE_URL = "https://raw.githubusercontent.com/yubrajsahoo/load-test-report/main/smf4j/";
}
