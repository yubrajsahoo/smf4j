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

package io.github.yubrajsahoo.smf4jcore.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import helper.JsonConverter;
import io.github.yubrajsahoo.smf4jcore.annotation.Counter;
import io.github.yubrajsahoo.smf4jcore.autoconfigure.Smf4jAutoConfiguration;
import io.github.yubrajsahoo.smf4jcore.logger.impl.DefaultMetricsLogger;
import io.github.yubrajsahoo.smf4jcore.spel.SpelContextBuilder;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = Smf4jAutoConfiguration.class)
class MetricsServiceTest {
    @Autowired
    MetricsService metricsService;

    @Autowired
    MeterRegistry meterRegistry;

    @Mock
    private BeanResolver beanResolver;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private MethodSignature methodSignature;

    private ListAppender<ILoggingEvent> listAppender;


    @BeforeEach
    void setUp() {
        meterRegistry.clear();
        Mockito.reset(joinPoint, methodSignature, beanResolver);
        when(joinPoint.getSignature()).thenReturn(methodSignature);

        //varify log for DefaultMetricsLogger
        Logger logger = (Logger) LoggerFactory.getLogger(DefaultMetricsLogger.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(DefaultMetricsLogger.class);
        logger.detachAppender(listAppender);
        listAppender.clearAllFilters();
    }

    @Test
    @DisplayName("Should Start Timer")
    void testStart() {
        Timer.Sample start = metricsService.start();
        assertNotNull(start);
    }

    @Test
    @DisplayName("Should Not Store Any Counter Metrics Due to Counter Null")
    void testRecordMetrics_Counter_Null() {
        metricsService.recordMetrics((Counter) null, buildContext(null, null));

        //should not store metrics
        io.micrometer.core.instrument.Counter savedCounter = meterRegistry.find("http.requests.total")
                .counter();

        assertNull(savedCounter);

    }

    @Test
    @DisplayName("Should Not Store Any Counter Metrics Due to disabled")
    void testRecordMetrics_Counter_Disabled() {
        Counter counter = JsonConverter.read(
                "src/test/resources/json/counter-disabled.json", Counter.class
        );

        metricsService.recordMetrics(counter, buildContext(null, null));

        //should log disabled log
        String formattedMessage = listAppender.list.get(0).getFormattedMessage();
        String expectedMessage = "Metrics Disabled For->name=http.requests.total->" +
                "method=GET->outcome=SUCCESS->description=Total incoming HTTP requests->increment=3";

        assertEquals(expectedMessage, formattedMessage);

        //should not store metrics
        io.micrometer.core.instrument.Counter savedCounter = meterRegistry.find("http.requests.total")
                .counter();

        assertNull(savedCounter);
    }

    @Test
    @DisplayName("Should Not Store Any Timer Metrics Due to Sample Null")
    void testRecordMetrics_Sample_Null() {
        io.github.yubrajsahoo.smf4jcore.annotation.Timer timer = JsonConverter.read(
                "src/test/resources/json/timer-enabled.json",
                io.github.yubrajsahoo.smf4jcore.annotation.Timer.class
        );

        metricsService.recordMetrics(
                (Timer.Sample) null,
                timer,
                buildContext(null, null)
        );

        //should not store metrics
        io.micrometer.core.instrument.Timer savedTimer = meterRegistry.find("http.requests.total")
                .timer();

        assertNull(savedTimer);
    }

    @Test
    @DisplayName("Should Not Store Any Timer Metrics Due to Timer Null")
    void testRecordMetrics_Timer_Null() {
        metricsService.recordMetrics(
                mock(Timer.Sample.class),
                null,
                buildContext(null, null)
        );

        //should not store metrics
        io.micrometer.core.instrument.Timer savedTimer = meterRegistry.find("http.requests.total")
                .timer();

        assertNull(savedTimer);
    }

    @Test
    @DisplayName("Should Not Store Any Timer Metrics Due to disabled")
    void testRecordMetrics_Timer_Disabled() {
        Timer.Sample sample = metricsService.start();

        io.github.yubrajsahoo.smf4jcore.annotation.Timer timer = JsonConverter.read(
                "src/test/resources/json/timer-disabled.json",
                io.github.yubrajsahoo.smf4jcore.annotation.Timer.class
        );

        metricsService.recordMetrics(sample, timer, buildContext(null, null));

        //should log disabled log
        String formattedMessage = listAppender.list.get(0).getFormattedMessage();
        String expectedMessage = "Metrics Disabled For->name=http.requests.total->" +
                "method=GET->outcome=SUCCESS->description=Total incoming HTTP requests";

        assertEquals(expectedMessage, formattedMessage);

        //should not store metrics
        io.micrometer.core.instrument.Counter savedCounter = meterRegistry.find("http.requests.total")
                .counter();

        assertNull(savedCounter);
    }

    private StandardEvaluationContext buildContext(Object result, Throwable error) {
        return SpelContextBuilder.buildContext(joinPoint, result, error, beanResolver);
    }
}