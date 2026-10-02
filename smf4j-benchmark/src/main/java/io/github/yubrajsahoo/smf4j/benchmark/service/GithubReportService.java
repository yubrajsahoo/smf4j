package io.github.yubrajsahoo.smf4j.benchmark.service;

import io.github.yubrajsahoo.smf4j.benchmark.constant.BenchmarkConstants;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Path;

/**
 * Service to manage uploading and fetching benchmark reports from GitHub.
 */
@Service
public class GithubReportService {

    private final RestTemplate restTemplate;

    /**
     * Constructs a new GithubReportService.
     */
    public GithubReportService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Commits and pushes the generated report using the local git repository.
     *
     * @param repoDir       the repository directory path
     * @param commitMessage the commit message to use
     * @throws Exception if an error occurs during git operations
     */
    public void uploadReport(Path repoDir, String commitMessage) throws Exception {
        runCommand(repoDir, "git", "add", ".");
        runCommand(repoDir, "git", "commit", "-m", commitMessage);
        runCommand(repoDir, "git", "push");
    }

    /**
     * Fetches a report directly from the GitHub raw content URL.
     * @param fileName The path/name of the file (e.g., "jmh-result.json")
     * @return The raw string content of the file from GitHub
     */
    public String getReportFromGithub(String fileName) {
        String url = BenchmarkConstants.GITHUB_RAW_BASE_URL + fileName;
        return restTemplate.getForObject(url, String.class);
    }

    private void runCommand(Path dir, String... command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(dir.toFile());
        pb.inheritIO();
        Process p = pb.start();
        int exitCode = p.waitFor();
        if (exitCode != 0) {
            // Ignore commit error if there is nothing to commit, but throw for push errors
            if (!command[1].equals("commit")) {
                throw new RuntimeException("Command failed with exit code " + exitCode + ": " + String.join(" ", command));
            }
        }
    }
}
