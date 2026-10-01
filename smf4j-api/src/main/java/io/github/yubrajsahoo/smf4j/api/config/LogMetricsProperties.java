package io.github.yubrajsahoo.smf4j.api.config;

public class LogMetricsProperties {
    public static final String NONE = "none";

    private String name = "smf4j.log.metrics";
    private String description = NONE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
