package io.github.yubrajsahoo.smf4j.api.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TagTest {
    @Test
    @DisplayName("Test Getters And Setters")
    void testGettersAndSetters() {
        Tag tag = new Tag();
        tag.setKey("k1");
        tag.setValue("v1");

        assertThat(tag.getKey()).isEqualTo("k1");
        assertThat(tag.getValue()).isEqualTo("v1");
    }

    @Test
    @DisplayName("Test Builder")
    void testBuilder() {
        Tag tag = Tag.builder()
                .key("bk1")
                .value("bv1")
                .build();

        assertThat(tag.getKey()).isEqualTo("bk1");
        assertThat(tag.getValue()).isEqualTo("bv1");
    }
}

