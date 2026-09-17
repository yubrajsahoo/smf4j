/*
 * Copyright 2024 Yubraj Sahoo
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.yubrajsahoo.smf4j.core.mapper;

import io.github.yubrajsahoo.smf4j.api.domain.Tag;
import io.micrometer.core.instrument.Tags;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TagsMapperTest {

    @Test
    @DisplayName("Should return empty Tags when input list is null")
    void testMap_WithNullList_ReturnsEmptyTags() {
        Tags result = TagsMapper.map(null);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty Tags when input list is empty")
    void testMap_WithEmptyList_ReturnsEmptyTags() {
        Tags result = TagsMapper.map(Collections.emptyList());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should successfully map valid SMF4J Tags to Micrometer Tags")
    void testMap_WithValidTags_ReturnsMappedMicrometerTags() {
        List<Tag> input = Arrays.asList(
                Tag.builder().key("env").value("prod").build(),
                Tag.builder().key("region").value("us-east").build()
        );

        Tags result = TagsMapper.map(input);

        assertThat(result).hasSize(2);
        assertThat(result.stream().anyMatch(t -> t.getKey().equals("env") && t.getValue().equals("prod"))).isTrue();
        assertThat(result.stream().anyMatch(t -> t.getKey().equals("region") && t.getValue().equals("us-east"))).isTrue();
    }

    @Test
    @DisplayName("Should filter out null tag objects, null keys, and null values")
    void testMap_WithInvalidTags_FiltersOutNulls() {
        List<Tag> input = new ArrayList<>();
        input.add(Tag.builder().key("validKey").value("validValue").build());
        input.add(null); // Null tag object
        input.add(Tag.builder().key(null).value("someValue").build()); // Null key
        input.add(Tag.builder().key("someKey").value(null).build()); // Null value

        Tags result = TagsMapper.map(input);

        assertThat(result).hasSize(1);
        assertThat(result.stream().anyMatch(t -> t.getKey().equals("validKey") && t.getValue().equals("validValue"))).isTrue();
    }

    @Test
    @DisplayName("Should be able to instantiate via private constructor for code coverage")
    void testPrivateConstructor() throws Exception {
        Constructor<TagsMapper> constructor = TagsMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        TagsMapper instance = constructor.newInstance();
        assertThat(instance).isNotNull();
    }
}