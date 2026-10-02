package io.github.yubrajsahoo.smf4j.api.config;

/**
 * Configuration properties for SMF4J log metrics.
 * <p>
 * This class encapsulates the configuration related to the naming and description
 * of log metrics. In a Spring Boot environment, it typically binds to the
 * {@code smf4j.log-metrics} prefix.
 * </p>
 */
public class LogMetricsProperties {

    /**
     * Constant representing an empty or disabled description.
     */
    public static final String NONE = "none";

    private boolean enabled = true;

    /**
     * The name assigned to the log metrics.
     * Default is {@code "smf4j.log.metrics"}.
     */
    private String name = "smf4j.log.metrics";

    /**
     * A description for the log metrics.
     * Default is {@value #NONE}.
     */
    private String description = NONE;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Retrieves the name of the log metrics.
     *
     * @return the metric name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the log metrics.
     *
     * @param name the metric name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the description of the log metrics.
     *
     * @return the metric description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the log metrics.
     *
     * @param description the metric description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
