package io.github.yubrajsahoo.smf4jcore.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.micrometer.core.instrument.Tags;

import io.github.yubrajsahoo.smf4jcore.annotation.Counter;
import java.lang.annotation.Annotation;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Utility class to parse JSON files into Java objects for testing.
 * Supports deserializing Micrometer {@link Tags} and SMF4J {@link Counter} annotations.
 */
public class JsonConverter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        SimpleModule module = new SimpleModule();
        // Custom deserializer for Micrometer Tags (usually not a POJO)
        module.addDeserializer(Tags.class, new JsonDeserializer<>() {
            @Override
            public Tags deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                JsonNode node = p.getCodec().readTree(p);
                List<String> tagList = new ArrayList<>();
                if (node.isObject()) {
                    Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> field = fields.next();
                        tagList.add(field.getKey());
                        tagList.add(field.getValue().asText());
                    }
                }
                return Tags.of(tagList.toArray(new String[0]));
            }
        });
        
        // Custom deserializer for SMF4J Counter annotation
        module.addDeserializer(Counter.class, new JsonDeserializer<>() {
            @Override
            public Counter deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                JsonNode node = p.getCodec().readTree(p);

                String name = node.has("name") ? node.get("name").asText() : "";
                String description = node.has("description") ? node.get("description").asText() : "";
                long increment = node.has("increment") ? node.get("increment").asLong() : 1L;
                boolean enable = node.has("enable") ? node.get("enable").asBoolean() : true;

                JsonNode tagsNode = node.get("tags");
                List<io.github.yubrajsahoo.smf4jcore.annotation.Tags> tagsList = new ArrayList<>();
                if (tagsNode != null && tagsNode.isObject()) {
                    Iterator<Map.Entry<String, JsonNode>> fields = tagsNode.fields();
                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> field = fields.next();
                        String key = field.getKey();
                        String value = field.getValue().asText();

                        tagsList.add(new io.github.yubrajsahoo.smf4jcore.annotation.Tags() {
                            @Override
                            public Class<? extends Annotation> annotationType() {
                                return io.github.yubrajsahoo.smf4jcore.annotation.Tags.class;
                            }

                            @Override
                            public String key() {
                                return key;
                            }

                            @Override
                            public String value() {
                                return value;
                            }
                        });
                    }
                }
                io.github.yubrajsahoo.smf4jcore.annotation.Tags[] tagsArray = tagsList.toArray(new io.github.yubrajsahoo.smf4jcore.annotation.Tags[0]);

                return new Counter() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return Counter.class;
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
                    public io.github.yubrajsahoo.smf4jcore.annotation.Tags[] tags() {
                        return tagsArray;
                    }

                    @Override
                    public long increment() {
                        return increment;
                    }

                    @Override
                    public boolean enable() {
                        return enable;
                    }
                };
            }
        });
        
        MAPPER.registerModule(module);
    }

    /**
     * Reads a JSON file from the classpath and converts it to the specified class.
     *
     * @param filePath the path to the file in the classpath (e.g., "/data/metrics.json")
     * @param clazz    the target class
     * @param <T>      the type of the target class
     * @return the deserialized object
     */
    public static <T> T fromJsonFile(String filePath, Class<T> clazz) {
        try (InputStream is = JsonConverter.class.getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IllegalArgumentException("File not found in classpath: " + filePath);
            }
            return MAPPER.readValue(is, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON from " + filePath, e);
        }
    }
}
