package io.github.yubrajsahoo.smf4jcore.factory;

import io.github.yubrajsahoo.smf4jcore.Smf4jCoreTestApplication;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.meter.service.MeterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MeterFactoryTest extends Smf4jCoreTestApplication {

    @Autowired
    private MeterFactory meterFactory;

    @Test
    @DisplayName("Should Return Meter Service For Counter")
    void testGetMeterService_Counter() {
        Optional<MeterService> optionalMeterService = meterFactory.getMeterService(MetricsType.COUNTER);

        assertTrue(optionalMeterService.isPresent());

        MeterService meterService = optionalMeterService.get();

        assertNotNull(meterService);
        assertEquals(MetricsType.COUNTER, meterService.getType());
    }

    @Test
    @DisplayName("Should Not Return Meter Service")
    void testGetMeterService_Null() {
        Optional<MeterService> optionalMeterService = meterFactory.getMeterService(null);

        assertFalse(optionalMeterService.isPresent());
    }
}
