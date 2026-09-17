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

package helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for converting objects to JSON strings and vice-versa in tests.
 */
public final class JsonConverter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private JsonConverter() {
        // Private constructor for utility class
    }

    /**
     * Converts an object to its JSON string representation.
     *
     * @param obj the object to convert
     * @return the JSON string
     * @throws RuntimeException if a processing error occurs
     */
    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    /**
     * Converts a JSON string to an object of the specified type.
     *
     * @param json  the JSON string
     * @param clazz the class of the target object type
     * @param <T>   the target type
     * @return the deserialized object
     * @throws RuntimeException if a processing error occurs
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to object", e);
        }
    }

    /**
     * Converts a JSON file from the classpath to an object of the specified type.
     *
     * @param resourcePath the path to the resource file (e.g., "/json/file.json")
     * @param clazz        the class of the target object type
     * @param <T>          the target type
     * @return the deserialized object
     * @throws RuntimeException if a processing error occurs or file is not found
     */
    public static <T> T fromJsonFile(String resourcePath, Class<T> clazz) {
        try (java.io.InputStream is = JsonConverter.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            return OBJECT_MAPPER.readValue(is, clazz);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error reading JSON from file: " + resourcePath, e);
        }
    }

    /**
     * Gets the configured ObjectMapper instance.
     *
     * @return the ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
