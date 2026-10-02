package io.github.yubrajsahoo.smf4j.benchmark.service;

import io.github.yubrajsahoo.smf4j.api.annotation.Counter;
import io.github.yubrajsahoo.smf4j.api.annotation.Gauge;
import io.github.yubrajsahoo.smf4j.api.annotation.Timer;
import org.springframework.stereotype.Service;

/**
 * Service used as a target for benchmarking different metric annotations.
 */
@Service
public class BenchmarkTargetService {

    /**
     * Default constructor for {@link BenchmarkTargetService}.
     */
    public BenchmarkTargetService() {
    }

    /**
     * Method without any metrics.
     *
     * @return success string
     */
    public String methodWithoutMetrics() {
        return "success";
    }

    /**
     * Method annotated with @Counter.
     *
     * @return success string
     */
    @Counter(name = "benchmark.counter", description = "Benchmark counter")
    public String methodWithCounter() {
        return "success";
    }

    /**
     * Method annotated with @Timer.
     *
     * @return success string
     */
    @Timer(name = "benchmark.timer", description = "Benchmark timer")
    public String methodWithTimer() {
        return "success";
    }

    /**
     * Method annotated with @Gauge.
     *
     * @return a random double value
     */
    @Gauge(name = "benchmark.gauge", description = "Benchmark gauge")
    public double methodWithGauge() {
        return Math.random();
    }
}
