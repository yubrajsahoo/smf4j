# smf4j-engine

The smf4j-engine module is responsible for the runtime processing of SMF4J annotations. It uses Spring AOP to intercept method calls and Spring Expression Language (SpEL) to evaluate dynamic tags.

## Features

* **Aspect-Oriented Programming (AOP):** Intercepts methods annotated with @Counter, @Timer, etc., to collect metrics without cluttering business logic (CounterAspect, TimerAspect).
* **SpEL Evaluation:** Evaluates SpEL expressions in @Tags to dynamically resolve values from method arguments (#argName), return values (#result), or thrown exceptions (#error).
* **Gauge Processing:** Processes @Gauge annotations during application startup (GaugeAnnotationProcessor).
* **Programmatic Logging:** Offers the LogMetrics utility for recording metrics dynamically (e.g. LogMetrics.info(...) or LogMetrics.error(...)) anywhere in your code.
* **Auto-Configuration:** Configures the AOP aspects automatically through Smf4jEngineAutoConfiguration.

## Installation

For most users, this is the primary dependency to include, as it transitively brings in pi and core:

`xml
<dependency>
    <groupId>io.github.yubrajsahoo</groupId>
    <artifactId>smf4j-engine</artifactId>
    <version>0.0.1</version>
</dependency>
`

## Proper Use Cases

Use this module when your application is a Spring Boot application and you wish to use a completely declarative (annotation-based) approach to metric tracking. The AOP aspects provided in this module will handle all boilerplate method interception and tag resolution automatically.
Additionally, you can use the LogMetrics utility exposed by this module to explicitly record counter metrics dynamically in standard 	ry-catch blocks or programmatic routines without needing AOP annotations.

## Build Commands
Run from the root of the project to build this module (Fast Install):
`ash
./mvnw clean install -pl smf4j-engine -DskipTests -Djacoco.skip=true -Dpitest.skip=true -Dsonar.skip=true
`
For other types of builds (tests, mutation coverage, sonar analysis), please refer to the [Root README](../README.md#building-the-library).
