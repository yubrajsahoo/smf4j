package io.github.yubrajsahoo.smf4j.benchmark.jmh;

import io.github.yubrajsahoo.smf4j.benchmark.BenchmarkApplication;
import io.github.yubrajsahoo.smf4j.benchmark.service.BenchmarkTargetService;
import org.openjdk.jmh.annotations.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * JMH Microbenchmark for testing SMF4J overhead.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class Smf4jMicrobenchmark {

    private ConfigurableApplicationContext context;
    private BenchmarkTargetService targetService;

    /**
     * Default constructor for Smf4jMicrobenchmark.
     */
    public Smf4jMicrobenchmark() {
    }

    /**
     * Sets up the Spring Boot context before benchmarking.
     */
    @Setup(Level.Trial)
    public void setup() {
        context = SpringApplication.run(BenchmarkApplication.class, "--server.port=0", "--spring.main.web-environment=false");
        targetService = context.getBean(BenchmarkTargetService.class);
    }

    /**
     * Tears down the Spring Boot context after benchmarking.
     */
    @TearDown(Level.Trial)
    public void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    /**
     * Benchmark for baseline with no metrics.
     *
     * @return result
     */
    @Benchmark
    public String baseline_no_metrics() {
        return targetService.methodWithoutMetrics();
    }

    /**
     * Benchmark for method with counter metric.
     *
     * @return result
     */
    @Benchmark
    public String with_counter() {
        return targetService.methodWithCounter();
    }

    /**
     * Benchmark for method with timer metric.
     *
     * @return result
     */
    @Benchmark
    public String with_timer() {
        return targetService.methodWithTimer();
    }

    /**
     * Benchmark for method with gauge metric.
     *
     * @return result
     */
    @Benchmark
    public double with_gauge() {
        return targetService.methodWithGauge();
    }
}
