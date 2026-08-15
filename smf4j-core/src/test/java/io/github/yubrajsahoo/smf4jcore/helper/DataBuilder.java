package io.github.yubrajsahoo.smf4jcore.helper;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Helper utility class for reading and writing JSON test fixtures.
 *
 * @author Yubraj Sahoo
 * @version 0.0.1
 * @since 0.0.1
 */
public final class DataBuilder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private DataBuilder() {
        // Utility class - prevent instantiation
    }

    /**
     * Reads a JSON file from the filesystem path and deserializes it to the specified class type.
     *
     * @param fileName the relative or absolute file path to the JSON file
     * @param clazz    the target class type
     * @param <T>      the target object type
     * @return the deserialized object instance
     * @throws RuntimeException if an I/O error occurs while reading or parsing the file
     */
    public static <T> T fromFile(String fileName, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(Files.readAllBytes(Path.of(fileName)), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serializes the given object as pretty-printed JSON and writes it to the specified destination file.
     *
     * @param object the object to serialize
     * @param file   the target file path
     * @throws IllegalStateException if an I/O error occurs while creating directories or writing the file
     */
    public static void toFile(Object object, String file) {
        try {
            Path path = Path.of(file);
            Path parent = path.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            OBJECT_MAPPER
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(path.toFile(), object);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to write data to JSON file: " + file,
                    e
            );
        }
    }
}