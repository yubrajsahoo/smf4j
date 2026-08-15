# SMF4J (Simple Metrics Facade for Java)

<p align="center">
  <strong>A lightweight, declarative, annotation-driven metrics facade for Spring Boot and Micrometer.</strong>
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/io.github.yubrajsahoo/smf4j-core"><img src="https://img.shields.io/maven-central/v/io.github.yubrajsahoo/smf4j-core.svg?label=Maven%20Central&logo=apachemaven" alt="Maven Central" /></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License" /></a>
  <a href="https://openjdk.org/"><img src="https://img.shields.io/badge/Java-17%2B-orange.svg?logo=openjdk" alt="Java 17+" /></a>
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F.svg?logo=springboot&logoColor=white" alt="Spring Boot 3.x" /></a>
  <a href="https://github.com/yubrajsahoo/smf4j/releases"><img src="https://img.shields.io/github/v/release/yubrajsahoo/smf4j?include_prereleases&label=Release" alt="Latest Release" /></a>
  <a href="https://github.com/yubrajsahoo/smf4j/stargazers"><img src="https://img.shields.io/github/stars/yubrajsahoo/smf4j?style=social" alt="GitHub Stars" /></a>
</p>

---

## 📖 Overview

**SMF4J (Simple Metrics Facade for Java)** is a modern, non-invasive metrics instrumentation library built for Java and Spring Boot applications. It empowers developers to instrument their methods declaratively using intuitive annotations, eliminating boilerplate Micrometer code while offering powerful dynamic tag extraction using **Spring Expression Language (SpEL)**.

### Why SMF4J?

In traditional Spring Boot applications, publishing custom metrics often pollutes business logic with repetitive metric registration and incrementation code:

```java
// ❌ Traditional imperative approach - pollutes business logic
public Order processOrder(OrderRequest request) {
    try {
        Order order = orderRepository.save(request.toOrder());
        meterRegistry.counter("orders.processed", "currency", request.getCurrency(), "status", "SUCCESS").increment();
        return order;
    } catch (Exception ex) {
        meterRegistry.counter("orders.processed", "currency", request.getCurrency(), "status", "FAILED", "exception", ex.getClass().getSimpleName()).increment();
        throw ex;
    }
}
```

With **SMF4J**, you replace all that boilerplate with a single, clean, declarative annotation:

```java
// ✅ SMF4J declarative approach - clean and readable
@Counter(
    name = "orders.processed",
    description = "Tracks processed orders with dynamic dimensions",
    tags = {
        @Tags(key = "currency", value = "#request.currency"),
        @Tags(key = "status", value = "#result != null ? 'SUCCESS' : 'FAILED'"),
        @Tags(key = "exception", value = "#error != null ? #error.class.simpleName : 'none'")
    }
)
public Order processOrder(OrderRequest request) {
    return orderRepository.save(request.toOrder());
}
```

---

## ✨ Features

- 🎯 **Declarative Metrics**: Effortlessly instrument methods using annotations like `@Counter`.
- 🏷️ **Dynamic SpEL Tags**: Extract metric dimensions at runtime from method parameters (`#param`), return values (`#result`), or thrown exceptions (`#error`).
- ⚡ **High Performance**: Pre-compiled SpEL AST expression caching using thread-safe `ConcurrentHashMap` to ensure minimal runtime overhead on hot paths.
- 🔌 **Universal Micrometer Ecosystem**: Integrates transparently with any Micrometer-supported monitoring backend (Prometheus, Grafana, Datadog, InfluxDB, CloudWatch, StatsD, New Relic, OpenTelemetry).
- 🧩 **Zero-Configuration Spring Boot Integration**: Automatically registers required components via Spring Boot Auto-Configuration (`@AutoConfiguration`).
- 🛡️ **Non-Intrusive & Resilient**: Metrics evaluation failures never break or intercept the core business execution.

---

## 📋 Requirements

| Technology | Minimum Version | Recommended Version |
| :--- | :--- | :--- |
| **Java** | 17 | 17 or 21 (LTS) |
| **Spring Boot** | 3.0.0 | 3.2.x / 3.3.x / 3.4.x / 3.5.x |
| **Micrometer** | 1.10.0 | 1.12+ |

---

## 📦 Installation

Add `smf4j-core` to your project build configuration:

### Maven

```xml
<dependency>
    <groupId>io.github.yubrajsahoo</groupId>
    <artifactId>smf4j-core</artifactId>
    <version>0.0.1</version>
</dependency>
```

### Gradle (Groovy)

```groovy
implementation 'io.github.yubrajsahoo:smf4j-core:0.0.1'
```

### Gradle (Kotlin DSL)

```kotlin
implementation("io.github.yubrajsahoo:smf4j-core:0.0.1")
```

---

## 🚀 Quick Start

### 1. Add Spring Boot Starter Actuator & Micrometer (e.g. Prometheus)

In your `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### 2. Annotate Your Spring Beans

Annotate any method in a Spring-managed bean (`@Service`, `@Component`, `@RestController`, etc.) with `@Counter`:

```java
package com.example.service;

import io.github.yubrajsahoo.smf4jcore.annotation.Counter;
import io.github.yubrajsahoo.smf4jcore.annotation.Tags;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Counter(
        name = "payments.processed",
        description = "Total number of payment transactions processed",
        tags = {
            @Tags(key = "gateway", value = "#gateway"),
            @Tags(key = "status", value = "#result.status")
        }
    )
    public PaymentResponse processPayment(String gateway, PaymentRequest request) {
        // Business logic here...
        return new PaymentResponse("COMPLETED", request.getAmount());
    }
}
```

### 3. Expose & View Metrics

Enable the Prometheus actuator endpoint in `application.yml` or `application.properties`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
```

Access your metrics endpoint at `http://localhost:8080/actuator/prometheus`:

```text
# HELP payments_processed_total Total number of payment transactions processed
# TYPE payments_processed_total counter
payments_processed_total{gateway="stripe",status="COMPLETED"} 1.0
```

---

## 💡 Usage Guide & Examples

### 1. Simple Counter

Count method executions with static tags:

```java
@Counter(
    name = "user.login.attempts",
    description = "Number of login attempts",
    tags = {
        @Tags(key = "environment", value = "production"),
        @Tags(key = "client", value = "web")
    }
)
public void login(String username, String password) {
    // Authenticate user...
}
```

### 2. Dynamic SpEL Tags from Method Arguments

Extract fields, nested properties, or method arguments dynamically:

```java
@Counter(
    name = "cart.item.added",
    description = "Counts items added to cart",
    tags = {
        @Tags(key = "userId", value = "#userId"),
        @Tags(key = "category", value = "#item.category"),
        @Tags(key = "itemSku", value = "#item.sku")
    }
)
public void addItemToCart(String userId, CartItem item) {
    // Add item to cart...
}
```

### 3. Capturing Method Return Value (`#result`)

Access the returned object and its getters via `#result`:

```java
@Counter(
    name = "orders.placed",
    tags = {
        @Tags(key = "orderId", value = "#result.id"),
        @Tags(key = "currency", value = "#result.currency"),
        @Tags(key = "status", value = "#result.status")
    }
)
public Order placeOrder(OrderDTO dto) {
    return orderRepository.save(new Order(dto));
}
```

### 4. Capturing Exceptions and Errors (`#error`)

When an exception occurs during method execution, SMF4J intercepts the exception and makes it accessible via `#error`:

```java
@Counter(
    name = "api.requests.total",
    tags = {
        @Tags(key = "endpoint", value = "/api/v1/checkout"),
        @Tags(key = "error", value = "#error != null ? #error.class.simpleName : 'none'")
    }
)
public CheckoutResponse checkout(CheckoutRequest request) {
    // Throws PaymentFailedException or returns successfully
    return checkoutService.process(request);
}
```

### 5. Custom Increment Steps

Increment the counter by a custom fixed amount on each invocation:

```java
@Counter(
    name = "data.batch.records.processed",
    description = "Counts batch records processed",
    increment = 100
)
public void processBatch(List<Record> records) {
    // Processes batches of 100 items...
}
```

### 6. Toggle Metric Collection

Conditionally enable or disable metric recording using the `enable` property:

```java
@Counter(
    name = "audit.events",
    enable = true
)
public void auditAction(String action) {
    // Audit execution...
}
```

---

## 🏗️ Architecture

SMF4J leverages Spring AOP and Micrometer to provide a modular, high-throughput metrics pipeline:

```mermaid
flowchart TD
    A[Client Method Invocation] --> B[Spring AOP Proxy]
    B --> C{CounterAspect}
    C -->|@AfterReturning| D[SpelContextBuilder: Bind #result & Params]
    C -->|@AfterThrowing| E[SpelContextBuilder: Bind #error & Params]
    D --> F[MetricsService]
    E --> F[MetricsService]
    F --> G[SpelEvaluator AST Cache]
    G --> H[MeterFactory]
    H --> I[CounterMeterService]
    I --> J[Micrometer MeterRegistry]
    J --> K[(Prometheus / Datadog / Grafana)]
```

### Core Components

- **`@Counter` & `@Tags`**: Declarative annotations placed on target service methods.
- **`CounterAspect`**: Intercepts method returns and exceptions using Spring AspectJ.
- **`SpelContextBuilder`**: Dynamically constructs evaluation contexts binding method parameter names, arguments, `#result`, and `#error`.
- **`SpelEvaluator`**: Resolves expressions with thread-safe `ConcurrentHashMap` caching to prevent repeated parsing.
- **`MeterFactory`**: Decoupled factory utilizing `EnumMap<MetricsType, MeterService>` for extensible meter dispatch.
- **`CounterMeterService`**: Resolves or registers Micrometer counters and increments them safely.
- **`Smf4jAutoConfiguration`**: Seamlessly boots all dependencies with `@ConditionalOnMissingBean` fallback support.

---

## ⚙️ Customization & Extensibility

SMF4J provides default beans that can be customized or overridden in your Spring context:

```java
@Configuration
public class CustomMetricsConfig {

    // Override the default MeterRegistry if needed
    @Bean
    public MeterRegistry customMeterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }
}
```

---

## 🗺️ Roadmap

- [x] `@Counter` metric tracking with dynamic SpEL tags
- [x] `#result` and `#error` context evaluation
- [x] SpEL AST expression caching
- [x] Spring Boot 3 Auto-Configuration
- [ ] `@Timer` annotation for method execution latency and percentiles
- [ ] `@Gauge` annotation for sampling queue depth, cache size, and memory
- [ ] Standalone `smf4j-spring-boot-starter` wrapper artifact
- [ ] Integration with Micrometer Observation API and Distributed Tracing

---

## 🤝 Contributing

Contributions, bug reports, and feature suggestions are warmly welcomed!

1. Fork the repository on GitHub: [`https://github.com/yubrajsahoo/smf4j`](https://github.com/yubrajsahoo/smf4j)
2. Create your feature branch:
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. Commit your changes with clear messages:
   ```bash
   git commit -m "feat: Add Timer annotation support"
   ```
4. Push to the branch:
   ```bash
   git push origin feature/amazing-feature
   ```
5. Open a **Pull Request**.

### Running Tests Locally

Ensure the full test suite passes prior to submitting PRs:

```bash
mvn clean test
```

---

## 📄 License

This project is licensed under the **Apache License 2.0** - see the [LICENSE](LICENSE) file for details.

---

## 👤 Author & Maintainer

**Yubraj Sahoo**  
GitHub: [@yubrajsahoo](https://github.com/yubrajsahoo)  
Repository: [https://github.com/yubrajsahoo/smf4j](https://github.com/yubrajsahoo/smf4j)