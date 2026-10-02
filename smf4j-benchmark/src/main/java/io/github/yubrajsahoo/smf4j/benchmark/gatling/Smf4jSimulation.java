package io.github.yubrajsahoo.smf4j.benchmark.gatling;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class Smf4jSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8090")
            .acceptHeader("application/json");

    ScenarioBuilder scnNoMetrics = scenario("No Metrics Scenario")
            .exec(http("request_no_metrics").get("/api/target/none"));

    ScenarioBuilder scnCounter = scenario("Counter Scenario")
            .exec(http("request_counter").get("/api/target/counter"));

    ScenarioBuilder scnTimer = scenario("Timer Scenario")
            .exec(http("request_timer").get("/api/target/timer"));

    ScenarioBuilder scnGauge = scenario("Gauge Scenario")
            .exec(http("request_gauge").get("/api/target/gauge"));

    public Smf4jSimulation() {
        setUp(
                scnNoMetrics.injectOpen(constantUsersPerSec(50).during(Duration.ofSeconds(10))),
                scnCounter.injectOpen(constantUsersPerSec(50).during(Duration.ofSeconds(10))),
                scnTimer.injectOpen(constantUsersPerSec(50).during(Duration.ofSeconds(10))),
                scnGauge.injectOpen(constantUsersPerSec(50).during(Duration.ofSeconds(10)))
        ).protocols(httpProtocol);
    }
}
