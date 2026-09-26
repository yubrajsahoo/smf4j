# smf4j-api

The `smf4j-api` module provides the core annotations and domain models for the SMF4J library. It is a lightweight API module that defines the contract for metrics collection without pulling in heavy implementations, making it perfect to include in interfaces or client libraries without bloating them.

## Features

* **Annotations:** Contains `@Counter`, `@Timer`, `@Gauge`, and `@Tags` annotations for declarative metrics tracking.
* **Domain Models:** Defines the core data structures used by SMF4J to represent metric information.

## Installation

```xml
<dependency>
    <groupId>io.github.yubrajsahoo</groupId>
    <artifactId>smf4j-api</artifactId>
    <version>${smf4j.version}</version>
</dependency>
```

## Usage Example

```java
import io.github.yubrajsahoo.smf4j.api.annotation.Counter;
import io.github.yubrajsahoo.smf4j.api.annotation.Tags;

public class OrderService {

    @Counter(
        name = "orders.created",
        description = "Counts the total number of orders placed",
        tags = {
            @Tags(key = "currency", value = "#order.currency"),
            @Tags(key = "status", value = "#result.status")
        }
    )
    public Order createOrder(Order order) {
        // ... business logic ...
        return order;
    }
}
```

This module is typically used as a dependency in projects that need to define metrics using annotations. The actual processing and recording of metrics are handled by the `smf4j-engine` and `smf4j-core` modules.

## Build Commands
Run from the root of the project to build this module:
```bash
./mvnw clean install -pl smf4j-api
```