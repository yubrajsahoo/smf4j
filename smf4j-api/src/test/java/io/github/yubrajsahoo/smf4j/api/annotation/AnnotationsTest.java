package io.github.yubrajsahoo.smf4j.api.annotation;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationsTest {

    @Counter(name = "cName", description = "cDesc", increment = 2, tags = {@Tags(key = "k1", value = "v1")}, enable = false)
    public void dummyMethodForCounter() {
        return;
    }

    @Gauge(name = "gName", description = "gDesc", expression = "10.0", tags = {@Tags(key = "k2", value = "v2")}, enable = false)
    public void dummyMethodForGauge() {
        return;
    }

    @Timer(name = "tName", description = "tDesc", tags = {@Tags(key = "k3", value = "v3")}, enable = false)
    public void dummyMethodForTimer() {
        return;
    }

    @Test
    void testCounterAnnotation() throws Exception {
        Method method = AnnotationsTest.class.getMethod("dummyMethodForCounter");
        Counter counter = method.getAnnotation(Counter.class);
        assertThat(counter.name()).isEqualTo("cName");
        assertThat(counter.description()).isEqualTo("cDesc");
        assertThat(counter.increment()).isEqualTo(2L);
        assertThat(counter.enable()).isFalse();
        assertThat(counter.tags()).hasSize(1);
        assertThat(counter.tags()[0].key()).isEqualTo("k1");
        assertThat(counter.tags()[0].value()).isEqualTo("v1");
    }

    @Test
    void testGaugeAnnotation() throws Exception {
        Method method = AnnotationsTest.class.getMethod("dummyMethodForGauge");
        Gauge gauge = method.getAnnotation(Gauge.class);
        assertThat(gauge.name()).isEqualTo("gName");
        assertThat(gauge.description()).isEqualTo("gDesc");
        assertThat(gauge.expression()).isEqualTo("10.0");
        assertThat(gauge.enable()).isFalse();
        assertThat(gauge.tags()).hasSize(1);
        assertThat(gauge.tags()[0].key()).isEqualTo("k2");
        assertThat(gauge.tags()[0].value()).isEqualTo("v2");
    }

    @Test
    void testTimerAnnotation() throws Exception {
        Method method = AnnotationsTest.class.getMethod("dummyMethodForTimer");
        Timer timer = method.getAnnotation(Timer.class);
        assertThat(timer.name()).isEqualTo("tName");
        assertThat(timer.description()).isEqualTo("tDesc");
        assertThat(timer.enable()).isFalse();
        assertThat(timer.tags()).hasSize(1);
        assertThat(timer.tags()[0].key()).isEqualTo("k3");
        assertThat(timer.tags()[0].value()).isEqualTo("v3");
    }
}
