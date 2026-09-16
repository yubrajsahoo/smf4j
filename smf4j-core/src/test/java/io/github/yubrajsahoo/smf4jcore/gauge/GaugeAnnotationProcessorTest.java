/*
 *
 *  * Copyright 2024 Yubraj Sahoo
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.github.yubrajsahoo.smf4jcore.gauge;

import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import io.github.yubrajsahoo.smf4jcore.gauge.annotation.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("GaugeAnnotationProcessor Integration Test")
@SpringBootTest(classes = {Smf4jAutoConfiguration.class, GaugeAnnotationProcessorTest.TestConfig.class})
class GaugeAnnotationProcessorTest {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private TestBean testBean;

    @Autowired
    private TestFieldBean testFieldBean;

    @Autowired
    private TestMapBean testMapBean;

    @Test
    @DisplayName("Should process @Gauge annotation on method without SpEL")
    void testMethodAnnotationWithoutSpEL() {
        testBean.setGaugeValue(42);

        io.micrometer.core.instrument.Gauge gauge = meterRegistry.find("test.method.gauge").gauge();
        assertNotNull(gauge, "Gauge should be registered");
        assertEquals(42.0, gauge.value());
    }

    @Test
    @DisplayName("Should process @Gauge annotation on field with SpEL")
    void testFieldAnnotationWithSpEL() {
        testFieldBean.setItems(Arrays.asList("a", "b", "c"));

        io.micrometer.core.instrument.Gauge gauge = meterRegistry.find("test.field.gauge").gauge();
        assertNotNull(gauge, "Gauge should be registered");
        assertEquals(3.0, gauge.value());
    }

    @Test
    @DisplayName("Should process @Gauge annotation on field without SpEL")
    void testFieldAnnotationWithoutSpEL() {
        testFieldBean.setCount(10);

        io.micrometer.core.instrument.Gauge gauge = meterRegistry.find("test.direct.gauge").gauge();
        assertNotNull(gauge, "Gauge should be registered");
        assertEquals(10.0, gauge.value());
    }

    @Test
    @DisplayName("Should process @Gauge annotation on Map field for map size and nested list size")
    void testMapFieldAnnotation() {
        java.util.Map<String, List<String>> map = new java.util.HashMap<>();
        map.put("key1", Arrays.asList("a", "b"));
        map.put("key2", Arrays.asList("c"));
        
        testMapBean.setDataMap(map);

        io.micrometer.core.instrument.Gauge mapSizeGauge = meterRegistry.find("test.map.size").gauge();
        assertNotNull(mapSizeGauge, "Map size gauge should be registered");
        assertEquals(2.0, mapSizeGauge.value());

        io.micrometer.core.instrument.Gauge listSizeGauge = meterRegistry.find("test.map.list.size").gauge();
        assertNotNull(listSizeGauge, "List size gauge should be registered");
        assertEquals(2.0, listSizeGauge.value());
    }

    @Configuration
    static class TestConfig {
        @Bean
        public TestBean testBean() {
            return new TestBean();
        }

        @Bean
        public TestFieldBean testFieldBean() {
            return new TestFieldBean();
        }

        @Bean
        public TestMapBean testMapBean() {
            return new TestMapBean();
        }
    }

    static class TestBean {
        private int value = 0;

        public void setGaugeValue(int value) {
            this.value = value;
        }

        @Gauge(name = "test.method.gauge")
        public int getGaugeValue() {
            return value;
        }
    }

    static class TestFieldBean {
        @Gauge(name = "test.field.gauge", expression = "size()")
        private List<String> items;

        @Gauge(name = "test.direct.gauge")
        private int count;

        public void setItems(List<String> items) {
            this.items = items;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    static class TestMapBean {
        @Gauge(name = "test.map.size", expression = "size()")
        private java.util.Map<String, List<String>> dataMap;

        @Gauge(name = "test.map.list.size", expression = "get('key1')?.size() ?: 0")
        private java.util.Map<String, List<String>> dataMapForList;

        public void setDataMap(java.util.Map<String, List<String>> dataMap) {
            this.dataMap = dataMap;
            this.dataMapForList = dataMap;
        }
    }
}
