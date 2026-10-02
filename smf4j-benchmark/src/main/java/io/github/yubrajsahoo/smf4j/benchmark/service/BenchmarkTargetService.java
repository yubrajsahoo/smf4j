package io.github.yubrajsahoo.smf4j.benchmark.service;

import io.github.yubrajsahoo.smf4j.api.annotation.Counter;
import io.github.yubrajsahoo.smf4j.api.annotation.Gauge;
import io.github.yubrajsahoo.smf4j.api.annotation.Timer;
import org.springframework.stereotype.Service;

@Service
public class BenchmarkTargetService {

    public String methodWithoutMetrics() {
        return "success";
    }

    @Counter(name = "benchmark.counter", description = "Benchmark counter")
    public String methodWithCounter() {
        return "success";
    }

    @Timer(name = "benchmark.timer", description = "Benchmark timer")
    public String methodWithTimer() {
        return "success";
    }

    @Gauge(name = "benchmark.gauge", description = "Benchmark gauge")
    public double methodWithGauge() {
        return Math.random();
    }
}
