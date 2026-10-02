package io.github.yubrajsahoo.smf4j.benchmark.controller;

import io.github.yubrajsahoo.smf4j.benchmark.service.BenchmarkTargetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for benchmark targets.
 */
@RestController
@RequestMapping("/api/target")
public class BenchmarkTargetController {

    private final BenchmarkTargetService targetService;

    /**
     * Constructs a new BenchmarkTargetController.
     *
     * @param targetService the benchmark target service
     */
    public BenchmarkTargetController(BenchmarkTargetService targetService) {
        this.targetService = targetService;
    }

    /**
     * Endpoint without metrics.
     *
     * @return the result
     */
    @GetMapping("/none")
    public String noMetrics() {
        return targetService.methodWithoutMetrics();
    }

    /**
     * Endpoint with counter metrics.
     *
     * @return the result
     */
    @GetMapping("/counter")
    public String withCounter() {
        return targetService.methodWithCounter();
    }

    /**
     * Endpoint with timer metrics.
     *
     * @return the result
     */
    @GetMapping("/timer")
    public String withTimer() {
        return targetService.methodWithTimer();
    }

    /**
     * Endpoint with gauge metrics.
     *
     * @return the result
     */
    @GetMapping("/gauge")
    public double withGauge() {
        return targetService.methodWithGauge();
    }
}
