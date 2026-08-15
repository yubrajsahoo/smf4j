package io.github.yubrajsahoo.smf4jcore;

import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import io.github.yubrajsahoo.smf4jcore.config.Smf4jCoreMockConfig;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test application entry point for SMF4J Spring Boot integration tests.
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
@SpringBootTest(classes = {Smf4jCoreMockConfig.class, Smf4jAutoConfiguration.class})
public class Smf4jCoreTestApplication {
}
