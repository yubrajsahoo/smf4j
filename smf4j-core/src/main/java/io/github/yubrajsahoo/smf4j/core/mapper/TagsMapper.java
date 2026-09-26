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

import java.util.List;

/**
 * Utility class for mapping SMF4J domain models to Micrometer equivalents.
 * <p>
 * This class provides static methods to convert internal tagging representations
 * into {@link io.micrometer.core.instrument.Tags} which can be directly used
 * by Micrometer's metric registries.
 * </p>
 */
public final class TagsMapper {

    private TagsMapper() {
        // private constructor to prevent instantiation
    }

    /**
     * Maps a list of SMF4J {@link Tag} objects into a Micrometer {@link Tags} collection.
     * <p>
     * Null lists, empty lists, and invalid tags (null keys or values) are safely filtered out.
     * </p>
     *
     * @param tagsList the list of SMF4J tags to be mapped
     * @return the constructed Micrometer {@link Tags}, or an empty collection if the input is null or empty
     */
    public static Tags map(List<Tag> tagsList) {
        if (tagsList == null || tagsList.isEmpty()) {
            return Tags.empty();
        }

        List<io.micrometer.core.instrument.Tag> micrometerTags = tagsList.stream()
                .filter(tag -> tag != null && tag.getKey() != null && tag.getValue() != null)
                .map(tag -> io.micrometer.core.instrument.Tag.of(tag.getKey(), tag.getValue()))
                .toList();

        return Tags.of(micrometerTags);
    }
}
