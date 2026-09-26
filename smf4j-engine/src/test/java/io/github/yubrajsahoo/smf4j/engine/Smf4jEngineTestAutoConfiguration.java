package io.github.yubrajsahoo.smf4j.engine;

import io.github.yubrajsahoo.smf4j.core.Smf4jCoreAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@ImportAutoConfiguration({Smf4jCoreAutoConfiguration.class, Smf4jEngineAutoConfiguration.class})
public class Smf4jEngineTestAutoConfiguration {
}
