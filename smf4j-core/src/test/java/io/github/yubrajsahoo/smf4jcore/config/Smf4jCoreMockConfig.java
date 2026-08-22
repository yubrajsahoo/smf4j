package io.github.yubrajsahoo.smf4jcore.config;

import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.helper.EvaluatorTestBean;
import io.github.yubrajsahoo.smf4jcore.meter.service.impl.CounterMeterService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class Smf4jCoreMockConfig {

    @Bean
    @Primary
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    @Primary
    public CounterMeterService counterMeterService() {
        CounterMeterService mock = Mockito.mock(CounterMeterService.class);
        Mockito.when(mock.getType()).thenReturn(MetricsType.COUNTER);
        return mock;
    }

    @Bean(name = "originalCounterMeterService")
    public CounterMeterService originalCounterMeterService(MeterRegistry meterRegistry) {
        return new CounterMeterService(meterRegistry);
    }

    @Bean
    public EvaluatorTestBean evaluatorTestBean() {
        return new EvaluatorTestBean();
    }
}
