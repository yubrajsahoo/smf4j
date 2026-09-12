# SMF4J Architecture & Refactoring Plan

## 1. Goal Description
The objective is to restructure the `smf4j` project into a multi-module architecture to support **Dynamic, Resource-Based Metrics Collection** while maintaining the existing annotation-based approach. 

- **Prometheus Integration:** SMF4J delegates metrics to Micrometer. Because Spring Boot Actuator natively auto-configures Prometheus when `micrometer-registry-prometheus` is present, there is no need to build a custom `smf4j-prometheus` module! Users just use standard Spring Boot Actuator.

## 2. Target Project Structure
The root `smf4j` POM is the parent of all submodules. 

```text
smf4j (Root POM - Parent)
├── smf4j-resource (JAR)      <-- Annotations, Configuration Domain & Resource Parsers
├── smf4j-core (JAR)          <-- Micrometer integration (MetricsService, MeterFactory)
└── smf4j-interceptors (JAR)  <-- Dynamic & Annotation-based AOP Advisors
```

**Dependency Flow:**
`smf4j-interceptors` -> depends on -> `smf4j-core` -> depends on -> `smf4j-resource`

*Note: If an end-user wants to use the AOP annotations or dynamic interceptors, they simply add `smf4j-interceptors` to their `pom.xml`, which will automatically pull in `smf4j-core` and `smf4j-resource`.*

## 3. Module Responsibilities

### 3.1. `smf4j-resource`
**Purpose:** Handle the definition and parsing of metric rules.
- **Dependencies:** None (or minimal utility libraries).
- **Components:**
  - `domain`: Core metric definition models (e.g., `MetricsConfig`, `MetricRule`).
  - `enums` & `constants`: Shared constants and enums (`MetricsType`).
  - `loaders`: Interfaces and implementations to parse `yaml`, `properties`, and `json` files into configuration objects.
  - `annotation`: Keep the `@Counter` and `@Timer` definitions here so they are available early in the dependency tree.

### 3.2. `smf4j-core`
**Purpose:** Act as the core programmatic bridge between SMF4J models and Micrometer.
- **Dependencies:** `smf4j-resource`, `micrometer-core`.
- **Components:**
  - `MeterFactory`
  - `MeterService` and implementations (`TimerMeterService`, `CounterMeterService`)
  - `MetricsService` (The central service to record metrics)

### 3.3. `smf4j-interceptors`
**Purpose:** Manage method interception (both annotation-based and dynamic resource-based) and trigger metrics collection via `smf4j-core`.
- **Dependencies:** `smf4j-core`, `spring-boot-starter-aop`, `spring-expression`.
- **Components:**
  - `aspect`: The existing `CounterAspect` and `TimerAspect`.
  - `dynamic`: Logic to programmatically register `DefaultPointcutAdvisor` and `MethodInterceptor` beans based on the configurations loaded by `smf4j-resource`.
  - `spel`: `SpelEvaluator` for resolving dynamic tags.

## 4. Execution Steps (Migration Plan)

### Step 1: Root POM Updates
- Add dependency management for all the new submodules to the root `smf4j` POM.
- Update `<modules>` to include `smf4j-resource`, `smf4j-core`, and `smf4j-interceptors`.

### Step 2: Submodule Creation & Code Migration
- Rename the existing `smf4j-core` directory to `smf4j-core-old` temporarily to easily move files around.
- Create directories and `pom.xml` files for `smf4j-core`, `smf4j-resource`, and `smf4j-interceptors`.
- Move Java packages from the original core module to their respective new submodules according to the responsibilities outlined in Section 3.
- Move tests to their respective submodules.
- Update `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` to reflect new locations of auto-configuration classes.

### Step 3: Implement Dynamic Interceptor Scaffolding
- Define the `MetricsConfig` and `MetricRule` domain classes in `smf4j-resource`.
- Create interfaces for `ResourceMetricsLoader`.
- Set up a dynamic AOP registrar in `smf4j-interceptors` that listens for the parsed rules and registers Spring `Advisor` beans.

### Step 4: Documentation Updates (`README.md`)
- Update the **Installation** section to instruct users to add `<artifactId>smf4j-interceptors</artifactId>` (instead of `smf4j-core`) to get full AOP and annotation capabilities.
- Add a new section under **Usage Guide** detailing the new **Dynamic Resource-Based Metrics** feature. Show an example `metrics.yml` and explain how it allows tracking metrics without modifying Java code.
- Update the **Architecture** section and Mermaid diagram to reflect the new `smf4j-resource`, `smf4j-core`, and `smf4j-interceptors` module split.

## 5. Verification
- Verify `mvn clean install` passes from the root directory.
- Verify JaCoCo coverage reports successfully aggregate across the new modules.
- Create a sample Spring Boot application demonstrating:
  1. Traditional `@Timer` annotation working.
  2. YAML/Properties-based dynamic method interception working.
  3. Prometheus metrics successfully appearing on `/actuator/prometheus` via standard Spring Boot integration.
