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

package helper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonConverter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        SimpleModule module = new SimpleModule();

        module.addDeserializer(Tags.class, new JsonDeserializer<>() {
            @Override
            public Tags deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                JsonNode node = p.getCodec().readTree(p);
                List<Tag> tags = new ArrayList<>();
                if (node.isArray()) {
                    for (JsonNode element : node) {
                        String key = element.get("key").asText();
                        String value = element.get("value").asText();
                        tags.add(Tag.of(key, value));
                    }
                }
                return Tags.of(tags);
            }
        });

        module.addSerializer(Tags.class, new JsonSerializer<>() {
            @Override
            public void serialize(Tags tags, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeStartArray();
                for (Tag tag : tags) {
                    gen.writeStartObject();
                    gen.writeStringField("key", tag.getKey());
                    gen.writeStringField("value", tag.getValue());
                    gen.writeEndObject();
                }
                gen.writeEndArray();
            }
        });

        module.addAbstractTypeMapping(io.github.yubrajsahoo.smf4jcore.counter.annotation.Counter.class, CounterImpl.class);
        module.addAbstractTypeMapping(io.github.yubrajsahoo.smf4jcore.timer.annotation.Timer.class, TimerImpl.class);
        module.addAbstractTypeMapping(io.github.yubrajsahoo.smf4jcore.core.annotation.Tags.class, TagsImpl.class);

        MAPPER.registerModule(module);
    }

    public static class CounterImpl implements io.github.yubrajsahoo.smf4jcore.counter.annotation.Counter {
        private String name;
        private String description = io.github.yubrajsahoo.smf4jcore.core.constant.MetricsConstant.NONE;
        private io.github.yubrajsahoo.smf4jcore.core.annotation.Tags[] tags = new io.github.yubrajsahoo.smf4jcore.core.annotation.Tags[0];
        private long increment = 1;
        private boolean enable = true;

        @Override
        public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return io.github.yubrajsahoo.smf4jcore.counter.annotation.Counter.class;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String description() {
            return description;
        }

        @Override
        public io.github.yubrajsahoo.smf4jcore.core.annotation.Tags[] tags() {
            return tags;
        }

        @Override
        public long increment() {
            return increment;
        }

        @Override
        public boolean enable() {
            return enable;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setTags(io.github.yubrajsahoo.smf4jcore.core.annotation.Tags[] tags) {
            this.tags = tags;
        }

        public void setIncrement(long increment) {
            this.increment = increment;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }

    public static class TimerImpl implements io.github.yubrajsahoo.smf4jcore.timer.annotation.Timer {
        private String name;
        private String description = io.github.yubrajsahoo.smf4jcore.core.constant.MetricsConstant.NONE;
        private io.github.yubrajsahoo.smf4jcore.core.annotation.Tags[] tags = new io.github.yubrajsahoo.smf4jcore.core.annotation.Tags[0];
        private boolean enable = true;

        @Override
        public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return io.github.yubrajsahoo.smf4jcore.timer.annotation.Timer.class;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String description() {
            return description;
        }

        @Override
        public io.github.yubrajsahoo.smf4jcore.core.annotation.Tags[] tags() {
            return tags;
        }

        @Override
        public boolean enable() {
            return enable;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setTags(io.github.yubrajsahoo.smf4jcore.core.annotation.Tags[] tags) {
            this.tags = tags;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }

    public static class TagsImpl implements io.github.yubrajsahoo.smf4jcore.core.annotation.Tags {
        private String key = io.github.yubrajsahoo.smf4jcore.core.constant.MetricsConstant.NONE;
        private String value = io.github.yubrajsahoo.smf4jcore.core.constant.MetricsConstant.NONE;

        @Override
        public Class<? extends java.lang.annotation.Annotation> annotationType() {
            return io.github.yubrajsahoo.smf4jcore.core.annotation.Tags.class;
        }

        @Override
        public String key() {
            return key;
        }

        @Override
        public String value() {
            return value;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static <T> T read(String filePath, Class<T> clazz) {
        try {
            return MAPPER.readValue(new File(filePath), clazz);
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON file: " + filePath, e);
        }
    }

    public static void write(String filePath, Object value) {
        try {
            MAPPER.writeValue(new File(filePath), value);
        } catch (IOException e) {
            throw new RuntimeException("Error writing JSON file: " + filePath, e);
        }
    }
}
