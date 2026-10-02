package io.github.yubrajsahoo.smf4j.benchmark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
/**
 * Main Benchmark Application.
 */
public class BenchmarkApplication {

    /**
     * Default constructor.
     */
    public BenchmarkApplication() {}
    /**
     * Main method.
     * @param args the args
     */
    public static void main(String[] args) {
        SpringApplication.run(BenchmarkApplication.class, args);
    }
}
