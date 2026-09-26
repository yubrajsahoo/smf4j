# SMF4J - Simple Metrics Facade for Java

**SMF4J (Simple Metrics Facade for Java)** is a lightweight library designed to simplify the collection and publishing of metrics in Java applications, particularly those using Spring Boot and Micrometer. It provides easy-to-use annotations like `@Counter`, `@Timer`, and `@Gauge` with support for dynamic tag evaluation using Spring Expression Language (SpEL).

## Modules

The project is structured into four main modules:

* **[smf4j-api](smf4j-api/README.md):** Contains the core annotations (`@Counter`, `@Timer`, `@Gauge`, `@Tags`) and domain models.
* **[smf4j-core](smf4j-core/README.md):** Provides the core metric services and integration with Micrometer.
* **[smf4j-engine](smf4j-engine/README.md):** Contains Spring AOP aspects and the SpEL evaluation engine to process annotations at runtime.
* **[smf4j-spring-boot-starter](smf4j-spring-boot-starter/README.md):** The all-in-one dependency for Spring Boot applications to easily auto-configure everything.

## Key Features

* **Annotation-Driven:** Collect metrics effortlessly by annotating your methods with `@Counter`, `@Timer`, `@Gauge`.
* **Dynamic Tags with SpEL:** Use Spring Expression Language to dynamically resolve tag values from method arguments, return values, or exceptions (e.g., `#result.status`, `#request.id`).
* **Micrometer Integration:** Seamlessly integrates with Micrometer to publish metrics to various monitoring systems (Prometheus, Datadog, etc.).
* **Spring Boot Auto-Configuration:** Easy setup in Spring Boot applications. The beans and aspects auto-configure just by adding the starter dependency.

## Installation

If you are using Spring Boot, add the starter dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.yubrajsahoo</groupId>
    <artifactId>smf4j-spring-boot-starter</artifactId>
    <version>0.1-SNAPSHOT</version> <!-- Replace with the latest release -->
</dependency>
```
*Note: The starter transitively pulls in all required modules (`api`, `core`, and `engine`) and seamlessly connects to your Spring Boot auto-configuration.*

## Quick Use Cases

### 1. Tracking API Request Counts and Statuses
You can easily track how many times an endpoint is called and bucket them by the returned status.
```java
@Counter(
    name = "api.requests.total",
    description = "Total number of API requests",
    tags = { @Tags(key = "status", value = "#result.status") }
)
public ApiResponse processRequest(ApiRequest request) { ... }
```

### 2. Measuring Execution Time (Timers)
Keep track of how long critical business methods or external API calls take to execute.
```java
@Timer(
    name = "db.query.execution.time",
    description = "Time taken to execute database queries",
    tags = { @Tags(key = "queryType", value = "#query.type") }
)
public QueryResult executeQuery(Query query) { ... }
```

### 3. Monitoring System States (Gauges)
Track the size of a cache or the number of active users currently logged in.
```java
@Gauge(
    name = "cache.active.sessions",
    description = "Number of active user sessions in the cache"
)
public int getActiveSessions() { ... }
```

## Detailed Documentation

For a comprehensive guide, including detailed SpEL context examples, programmatic metrics setup, and full architecture overview, please read the **[Full Project Documentation](./DOCUMENTATION.md)**.

## Building the Library

To build the library locally, run the following Maven wrapper command from the root directory:

```bash
# Clean, compile, and install the library into your local Maven repository
./mvnw clean install
```

To run the test suite and verify code coverage:
```bash
./mvnw clean test
```

To run mutation testing and verification:
```bash
./mvnw clean verify -P pitest
```

## License
This project is licensed under the Apache License, Version 2.0.