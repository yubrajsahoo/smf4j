package io.github.yubrajsahoo.smf4jcore.service;

import io.github.yubrajsahoo.smf4jcore.annotation.Counter;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Service interface for processing metric annotations and dispatching recordings to meter services.
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see io.github.yubrajsahoo.smf4jcore.service.impl.MetricsServiceImpl
 */
public interface MetricsService {

    /**
     * Processes and records a counter metric based on the metadata in {@link Counter}
     * and the given SpEL {@link StandardEvaluationContext}.
     *
     * @param counter the {@link Counter} annotation containing metric definition and metadata
     * @param context the SpEL evaluation context providing variables for dynamic tag resolution
     */
    void record(Counter counter, StandardEvaluationContext context);
}