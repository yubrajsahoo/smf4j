# smf4j-spring-boot-starter

This is the official Spring Boot Starter for **SMF4J (Simple Metrics Facade for Java)**. 

Including this starter in your Spring Boot application will automatically configure all the necessary beans, AOP aspects, and Micrometer integrations to start collecting metrics via annotations (@Counter, @Timer, @Gauge), as well as initialize the LogMetrics utility class.

## Installation

Add the following dependency to your pom.xml:

`xml
<dependency>
    <groupId>io.github.yubrajsahoo</groupId>
    <artifactId>smf4j-spring-boot-starter</artifactId>
    <version>0.0.1</version>
</dependency>
`

This starter transitively pulls in smf4j-api, smf4j-core, and smf4j-engine, along with the base spring-boot-starter and micrometer-core.

## Configuration

You can configure default properties in your pplication.properties or pplication.yml via the Smf4JProperties bound class:

`yaml
smf4j:
  # Enable or disable the entire SMF4J library (default: true)
  enabled: true
  
  # Configuration for metrics logged via LogMetrics without explicit names
  log-metrics:
    enabled: true
    name: "smf4j.log.metrics"
    description: "none"
    
  # Configuration for the console output logger behavior
  logger-config:
    log-level: INFO
    log-message: "Metrics Logs For With->"
    disable-log-level: DEBUG
    disable-log-message: "Metrics Disabled For->"
`

## Build Commands
Run from the root of the project to build this module (Fast Install):
`ash
./mvnw clean install -pl smf4j-spring-boot-starter -DskipTests -Djacoco.skip=true -Dpitest.skip=true -Dsonar.skip=true
`
For other types of builds (tests, mutation coverage, sonar analysis), please refer to the [Root README](../README.md#building-the-library).
