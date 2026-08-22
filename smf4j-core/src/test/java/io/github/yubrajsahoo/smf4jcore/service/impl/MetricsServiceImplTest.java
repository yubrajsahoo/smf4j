package io.github.yubrajsahoo.smf4jcore.service.impl;

import io.github.yubrajsahoo.smf4jcore.annotation.Counter;
import io.github.yubrajsahoo.smf4jcore.domain.CounterMetrics;
import io.github.yubrajsahoo.smf4jcore.enums.MetricsType;
import io.github.yubrajsahoo.smf4jcore.factory.MeterFactory;
import io.github.yubrajsahoo.smf4jcore.meter.service.MeterService;
import io.github.yubrajsahoo.smf4jcore.spel.SpelEvaluator;
import io.github.yubrajsahoo.smf4jcore.utils.JsonConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MetricsServiceImpl}.
 * <p>
 * Uses Mockito to mock {@link SpelEvaluator} and {@link MeterFactory} dependencies,
 * verifying the full metric recording pipeline: null-guard, SpEL tag evaluation,
 * domain model construction, enabled/disabled branching, and error resilience.
 * </p>
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 * @see MetricsServiceImpl
 * @see io.github.yubrajsahoo.smf4jcore.service.MetricsService
 */
@ExtendWith(MockitoExtension.class)
class MetricsServiceImplTest {

    @Mock
    private SpelEvaluator spelEvaluator;

    @Mock
    private MeterFactory meterFactory;

    @Mock
    private MeterService meterService;

    private MetricsServiceImpl metricsService;

    /**
     * Creates a fresh {@link MetricsServiceImpl} with mocked dependencies before each test.
     */
    @BeforeEach
    void setUp() {
        metricsService = new MetricsServiceImpl(spelEvaluator, meterFactory);
    }

    /**
     * Verifies that constructing with a {@code null} {@link SpelEvaluator}
     * throws a {@link NullPointerException} with an appropriate message.
     */
    @Test
    void constructor_withNullSpelEvaluator_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> new MetricsServiceImpl(null, meterFactory))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("spelEvaluator must not be null");
    }

    /**
     * Verifies that constructing with a {@code null} {@link MeterFactory}
     * throws a {@link NullPointerException} with an appropriate message.
     */
    @Test
    void constructor_withNullMeterFactory_shouldThrowNullPointerException() {
        assertThatThrownBy(() -> new MetricsServiceImpl(spelEvaluator, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("meterFactory must not be null");
    }

    /**
     * Verifies that passing a {@code null} {@link Counter} annotation
     * returns early without interacting with the meter factory.
     */
    @Test
    void record_withNullCounter_shouldNotThrow() {
        metricsService.record(null, new StandardEvaluationContext());

        verifyNoInteractions(meterFactory);
    }

    /**
     * Verifies that an enabled counter delegates to the resolved {@link MeterService}
     * with a correctly constructed {@link CounterMetrics} domain object.
     */
    @Test
    void record_withEnabledCounter_shouldDelegateToMeterService() {
        Counter counter = JsonConverter.fromJsonFile("/data/counter-annot-basic.json", Counter.class);
        StandardEvaluationContext context = new StandardEvaluationContext();

        when(meterFactory.getMeterService(MetricsType.COUNTER)).thenReturn(Optional.of(meterService));

        metricsService.record(counter, context);

        ArgumentCaptor<io.github.yubrajsahoo.smf4jcore.domain.Metrics> captor =
                ArgumentCaptor.forClass(io.github.yubrajsahoo.smf4jcore.domain.Metrics.class);
        verify(meterService).record(captor.capture());

        io.github.yubrajsahoo.smf4jcore.domain.Metrics recorded = captor.getValue();
        assertThat(recorded).isInstanceOf(CounterMetrics.class);
        assertThat(recorded.getName()).isEqualTo("test.counter");
    }

    /**
     * Verifies that a disabled counter skips meter factory lookup and recording entirely.
     */
    @Test
    void record_withDisabledCounter_shouldNotDelegateToMeterService() {
        Counter counter = JsonConverter.fromJsonFile("/data/counter-annot-disabled.json", Counter.class);
        StandardEvaluationContext context = new StandardEvaluationContext();

        metricsService.record(counter, context);

        verify(meterFactory, never()).getMeterService(any());
    }

    /**
     * Verifies that when no {@link MeterService} is registered for the counter type,
     * the service logs a warning but does not throw.
     */
    @Test
    void record_withNoMeterServiceFound_shouldNotThrow() {
        Counter counter = JsonConverter.fromJsonFile("/data/counter-annot-orphan.json", Counter.class);
        StandardEvaluationContext context = new StandardEvaluationContext();

        when(meterFactory.getMeterService(MetricsType.COUNTER)).thenReturn(Optional.empty());

        metricsService.record(counter, context);

        verify(meterService, never()).record(any());
    }

    /**
     * Verifies that each tag value is evaluated through the {@link SpelEvaluator}
     * and the results are forwarded to the meter service.
     */
    @Test
    void record_withTags_shouldEvaluateEachTagValue() {
        Counter counter = JsonConverter.fromJsonFile("/data/counter-annot-tagged.json", Counter.class);
        StandardEvaluationContext context = new StandardEvaluationContext();

        when(spelEvaluator.evaluate(eq("production"), any())).thenReturn("production");
        when(spelEvaluator.evaluate(eq("#region"), any())).thenReturn("us-west-2");
        when(meterFactory.getMeterService(MetricsType.COUNTER)).thenReturn(Optional.of(meterService));

        metricsService.record(counter, context);

        verify(spelEvaluator).evaluate(eq("production"), any());
        verify(spelEvaluator).evaluate(eq("#region"), any());
        verify(meterService).record(any());
    }

    /**
     * Verifies that an empty tags array skips SpEL evaluation entirely.
     */
    @Test
    void record_withEmptyTags_shouldNotCallSpelEvaluator() {
        Counter counter = JsonConverter.fromJsonFile("/data/counter-annot-no-tags.json", Counter.class);
        StandardEvaluationContext context = new StandardEvaluationContext();

        when(meterFactory.getMeterService(MetricsType.COUNTER)).thenReturn(Optional.of(meterService));

        metricsService.record(counter, context);

        verify(spelEvaluator, never()).evaluate(any(), any());
    }

    /**
     * Verifies that if the underlying {@link MeterService} throws an exception,
     * it is caught and logged rather than propagated to the caller.
     */
    @Test
    void record_whenMeterServiceThrows_shouldNotPropagateException() {
        Counter counter = JsonConverter.fromJsonFile("/data/counter-annot-error.json", Counter.class);
        StandardEvaluationContext context = new StandardEvaluationContext();

        when(meterFactory.getMeterService(MetricsType.COUNTER)).thenReturn(Optional.of(meterService));
        doThrow(new RuntimeException("registry error")).when(meterService).record(any());

        // Should catch and log, not propagate
        metricsService.record(counter, context);
    }

}
