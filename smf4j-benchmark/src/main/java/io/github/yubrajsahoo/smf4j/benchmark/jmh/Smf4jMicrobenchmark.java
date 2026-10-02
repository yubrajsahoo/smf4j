package io.github.yubrajsahoo.smf4j.benchmark.jmh;

import io.github.yubrajsahoo.smf4j.benchmark.BenchmarkApplication;
import io.github.yubrajsahoo.smf4j.benchmark.service.BenchmarkTargetService;
import org.openjdk.jmh.annotations.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class Smf4jMicrobenchmark {

    private ConfigurableApplicationContext context;
    private BenchmarkTargetService targetService;

    @Setup(Level.Trial)
    public void setup() {
        context = SpringApplication.run(BenchmarkApplication.class, "--server.port=0", "--spring.main.web-environment=false");
        targetService = context.getBean(BenchmarkTargetService.class);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @Benchmark
    public String baseline_no_metrics() {
        return targetService.methodWithoutMetrics();
    }

    @Benchmark
    public String with_counter() {
        return targetService.methodWithCounter();
    }

    @Benchmark
    public String with_timer() {
        return targetService.methodWithTimer();
    }

    @Benchmark
    public double with_gauge() {
        return targetService.methodWithGauge();
    }
}
