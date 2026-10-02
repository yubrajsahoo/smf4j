package io.github.yubrajsahoo.smf4j.benchmark.controller;

import io.github.yubrajsahoo.smf4j.benchmark.constant.BenchmarkConstants;
import io.github.yubrajsahoo.smf4j.benchmark.jmh.Smf4jMicrobenchmark;
import io.github.yubrajsahoo.smf4j.benchmark.service.GithubReportService;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/benchmark")
/**
 * API Controller.
 */
public class BenchmarkApiController {

    private final GithubReportService githubReportService;

    /**
     * Constructor.
     * @param githubReportService service
     */
    public BenchmarkApiController(GithubReportService githubReportService) {
        this.githubReportService = githubReportService;
    }

    @PostMapping("/jmh")
    /**
     * Run JMH benchmark.
     * @return response entity
     */
    public ResponseEntity<Map<String, String>> runJmhBenchmark() {
        Map<String, String> response = new HashMap<>();
        try {
            // Find the load-test-report repository directory (assuming it's a sibling of smf4j)
            String userDir = System.getProperty("user.dir");
            Path baseDir = Paths.get(userDir);
            if (baseDir.endsWith("smf4j-benchmark")) {
                baseDir = baseDir.getParent().getParent();
            } else if (baseDir.endsWith("smf4j")) {
                baseDir = baseDir.getParent();
            } else {
                baseDir = baseDir.getParent().getParent();
            }
            Path reportDir = baseDir.resolve(BenchmarkConstants.REPO_NAME).resolve(BenchmarkConstants.PROJECT_DIR_NAME);
            File dir = reportDir.toFile();
            if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException("Could not create report directory: " + dir.getAbsolutePath());
            }

            File resultFile = reportDir.resolve(BenchmarkConstants.JMH_RESULT_FILE_NAME).toFile();

            Options opt = new OptionsBuilder()
                    .include(Smf4jMicrobenchmark.class.getSimpleName())
                    .resultFormat(ResultFormatType.JSON)
                    .result(resultFile.getAbsolutePath())
                    .build();

            new Runner(opt).run();
            
            githubReportService.uploadReport(reportDir.getParent(), "Add JMH benchmark report");

            response.put(BenchmarkConstants.STATUS_KEY, BenchmarkConstants.STATUS_SUCCESS);
            response.put(BenchmarkConstants.MESSAGE_KEY, "JMH Benchmark completed and pushed to Git successfully.");
            response.put(BenchmarkConstants.REPORT_FILE_KEY, resultFile.getAbsolutePath());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put(BenchmarkConstants.STATUS_KEY, BenchmarkConstants.STATUS_ERROR);
            response.put(BenchmarkConstants.MESSAGE_KEY, e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/gatling")
    /**
     * Run Gatling benchmark.
     * @return response entity
     */
    public ResponseEntity<Map<String, String>> runGatlingBenchmark() {
        Map<String, String> response = new HashMap<>();
        try {
            String userDir = System.getProperty("user.dir");
            Path baseDir = Paths.get(userDir);
            if (baseDir.endsWith("smf4j-benchmark")) {
                baseDir = baseDir.getParent().getParent();
            } else if (baseDir.endsWith("smf4j")) {
                baseDir = baseDir.getParent();
            } else {
                baseDir = baseDir.getParent().getParent();
            }
            Path reportDir = baseDir.resolve(BenchmarkConstants.REPO_NAME).resolve(BenchmarkConstants.PROJECT_DIR_NAME).resolve(BenchmarkConstants.GATLING_DIR_NAME);
            File dir = reportDir.toFile();
            if (!dir.exists() && !dir.mkdirs()) {
                throw new RuntimeException("Could not create report directory: " + dir.getAbsolutePath());
            }

            String classpath = System.getProperty("java.class.path");
            String javaHome = System.getProperty("java.home");
            String javaBin = Paths.get(javaHome, "bin", "java").toString();

            ProcessBuilder pb = new ProcessBuilder(
                    javaBin,
                    "-cp", classpath,
                    BenchmarkConstants.GATLING_MAIN_CLASS,
                    "-s", BenchmarkConstants.GATLING_SIMULATION_CLASS,
                    "-rf", reportDir.toFile().getAbsolutePath()
            );
            pb.inheritIO();
            Process p = pb.start();
            int exitCode = p.waitFor();

            if (exitCode == 0) {
                java.io.File[] files = dir.listFiles(java.io.File::isDirectory);
                if (files != null && files.length > 0) {
                    java.util.Arrays.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                    java.io.File latestDir = files[0];
                    java.io.File globalStats = new java.io.File(latestDir, "js/global_stats.json");
                    if (globalStats.exists()) {
                        java.io.File latestStats = new java.io.File(dir, "latest-stats.json");
                        java.nio.file.Files.copy(globalStats.toPath(), latestStats.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                githubReportService.uploadReport(reportDir.getParent().getParent(), "Add Gatling benchmark report");

                response.put(BenchmarkConstants.STATUS_KEY, BenchmarkConstants.STATUS_SUCCESS);
                response.put(BenchmarkConstants.MESSAGE_KEY, "Gatling Load Test completed and pushed to Git successfully.");
                response.put(BenchmarkConstants.REPORT_DIR_KEY, reportDir.toFile().getAbsolutePath());
                return ResponseEntity.ok(response);
            } else {
                throw new RuntimeException("Gatling exited with code: " + exitCode);
            }
        } catch (Exception e) {
            response.put(BenchmarkConstants.STATUS_KEY, BenchmarkConstants.STATUS_ERROR);
            response.put(BenchmarkConstants.MESSAGE_KEY, e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/github/report")
    /**
     * Get report from Github.
     * @param fileName the file name
     * @return response entity
     */
    public ResponseEntity<String> getReportFromGithub(@RequestParam(name = "fileName", defaultValue = BenchmarkConstants.JMH_RESULT_FILE_NAME) String fileName) {
        try {
            String content = githubReportService.getReportFromGithub(fileName);
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching from GitHub: " + e.getMessage());
        }
    }
}
