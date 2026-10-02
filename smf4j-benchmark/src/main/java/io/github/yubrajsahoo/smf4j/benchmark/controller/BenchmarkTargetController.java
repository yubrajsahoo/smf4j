package io.github.yubrajsahoo.smf4j.benchmark.controller;

import io.github.yubrajsahoo.smf4j.benchmark.service.BenchmarkTargetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/target")
public class BenchmarkTargetController {

    private final BenchmarkTargetService targetService;

    public BenchmarkTargetController(BenchmarkTargetService targetService) {
        this.targetService = targetService;
    }

    @GetMapping("/none")
    public String noMetrics() {
        return targetService.methodWithoutMetrics();
    }

    @GetMapping("/counter")
    public String withCounter() {
        return targetService.methodWithCounter();
    }

    @GetMapping("/timer")
    public String withTimer() {
        return targetService.methodWithTimer();
    }

    @GetMapping("/gauge")
    public double withGauge() {
        return targetService.methodWithGauge();
    }
}
