# smf4j-core

The `smf4j-core` module acts as the bridge between SMF4J and the underlying metrics registry. It provides the core services to register and record metrics, integrating seamlessly with Micrometer.

## Features

* **Micrometer Integration:** Uses Micrometer's `MeterRegistry` to record counters, timers, and gauges.
* **Metric Services:** Provides implementations like `CounterMeterService`, `TimerMeterService`, and `GaugeMeterService` to handle the actual metric recording.
* **Spring Boot Auto-Configuration:** Automatically configures the necessary beans (`Smf4jCoreTestConfiguration`) when used in a Spring Boot application. If Micrometer is present, it will automatically connect to its `MeterRegistry`.

## Installation

```xml
<dependency>
    <groupId>io.github.yubrajsahoo</groupId>
    <artifactId>smf4j-core</artifactId>
    <version>0.0.1-SNAPSHOT</version> <!-- Replace with the latest release -->
</dependency>
```

## Proper Use Cases

You would typically use `smf4j-core` if you want to record metrics programmatically using the `MeterService` beans rather than using annotations. 
```java
@Service
public class CustomMetricPublisher {
    
    private final CounterMeterService counterService;
    
    public CustomMetricPublisher(CounterMeterService counterService) {
        this.counterService = counterService;
    }
    
    public void publishCustomCounter() {
        // Build metric and publish directly using the core services
    }
}
```

## Build Commands
Run from the root of the project to build this module (Fast Install):
```bash
./mvnw clean install -pl smf4j-core -DskipTests -Djacoco.skip=true -Dpitest.skip=true -Dsonar.skip=true
```
For other types of builds (tests, mutation coverage, sonar analysis), please refer to the [Root README](../README.md#building-the-library).