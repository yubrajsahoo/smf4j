package io.github.yubrajsahoo.smf4jcore.enums;

/**
 * Enumeration representing the supported categories of metrics in SMF4J.
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
public enum MetricsType {

    /**
     * Counter metric type, used for monotonically increasing values that track counts or frequencies.
     */
    COUNTER,

    /**
     * Timer metric type, used for tracking short-duration latencies and invocation rates.
     */
    TIMER,

    /**
     * Gauge metric type, used for sampling instantaneous values (e.g. queue size, cache memory, thread count).
     */
    GAUGE
}
