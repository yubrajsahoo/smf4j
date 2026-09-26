# smf4j-spring-boot-starter

This is the official Spring Boot Starter for **SMF4J (Simple Metrics Facade for Java)**. 

Including this starter in your Spring Boot application will automatically configure all the necessary beans, AOP aspects, and Micrometer integrations to start collecting metrics via annotations (`@Counter`, `@Timer`, `@Gauge`).

## Installation

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.yubrajsahoo</groupId>
    <artifactId>smf4j-spring-boot-starter</artifactId>
    <version>${smf4j.version}</version>
</dependency>
```

This starter transitively pulls in `smf4j-api`, `smf4j-core`, and `smf4j-engine`, along with the base `spring-boot-starter` and `micrometer-core`.

## Build Commands
Run from the root of the project to build this module:
```bash
./mvnw clean install -pl smf4j-spring-boot-starter
```
