//package Json;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.io.TempDir;
//import org.mockito.Mockito;
//import org.slf4j.Logger;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//
//import static org.cxxii.utils.Json.readJsonFromFile;
//import static org.junit.jupiter.api.Assertions.*;
//
//class JsonFileReaderTest {
//
//    private static final Logger LOGGER = Mockito.mock(Logger.class);
//
//    @TempDir
//    File tempDir;
//
//    @Test
//    void testValidJsonObject() throws Exception {
//        // Arrange: Create a temporary JSON file
//        File jsonFile = new File(tempDir, "valid.json");
//        try (FileWriter writer = new FileWriter(jsonFile)) {
//            writer.write("{\"key\": \"value\"}");
//        }
//
//        // Act: Call the method under test
//        JsonNode result = readJsonFromFile(jsonFile.getPath());
//
//        // Assert: Validate the content and structure of the JSON
//        assertNotNull(result);
//        assertTrue(result.isObject());
//        assertEquals("value", result.get("key").asText());
//    }
//
//    @Test
//    void testValidJsonArray() throws Exception {
//        // Arrange: Create a temporary JSON file with an array
//        File jsonFile = new File(tempDir, "valid_array.json");
//        try (FileWriter writer = new FileWriter(jsonFile)) {
//            writer.write("[1, 2, 3]");
//        }
//
//        // Act: Call the method under test
//        JsonNode result = readJsonFromFile(jsonFile.getPath());
//
//        // Assert: Validate the content and structure of the JSON
//        assertNotNull(result);
//        assertTrue(result.isArray());
//        assertEquals(3, result.size());
//    }
//
//    @Test
//    void testInvalidJsonContent() throws Exception {
//        // Arrange: Create a temporary JSON file with invalid content (a plain string)
//        File jsonFile = new File(tempDir, "invalid.json");
//        try (FileWriter writer = new FileWriter(jsonFile)) {
//            writer.write("\"just a string\"");
//        }
//
//        // Act and Assert: Verify that an exception is thrown
//        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
//            readJsonFromFile(jsonFile.getPath());
//        });
//
//        // Assert: Validate the exception message
//        assertEquals("The file does not contain a valid JSON object or array.", exception.getMessage());
//    }
//
//    @Test
//    void testFileNotFound() {
//        // Arrange: Create a file path that doesn't exist
//        String nonExistentFilePath = tempDir.getPath() + "/nonexistent.json";
//
//        // Act and Assert: Verify that an IOException is thrown
//        IOException exception = assertThrows(IOException.class, () -> {
//            readJsonFromFile(nonExistentFilePath);
//        });
//
//        // Assert: Validate the exception message
//        assertTrue(exception.getMessage().contains("Failed to parse JSON from file"));
//    }
//
//    @Test
//    void testIOExceptionDuringParsing() throws Exception {
//        // Arrange: Mock the ObjectMapper to throw an IOException
//        ObjectMapper mockObjectMapper = Mockito.mock(ObjectMapper.class);
//        File jsonFile = new File(tempDir, "dummy.json");
//        try (FileWriter writer = new FileWriter(jsonFile)) {
//            writer.write("{\"key\": \"value\"}");
//        }
//
//        Mockito.when(mockObjectMapper.readTree(jsonFile)).thenThrow(new IOException("Mocked IOException"));
//
//        // Act and Assert: Verify that an IOException is thrown
//        IOException exception = assertThrows(IOException.class, () -> {
//            readJsonFromFile(jsonFile.getPath());
//        });
//
//        // Assert: Validate the exception message
//        assertTrue(exception.getMessage().contains("Failed to parse JSON from file"));
//    }
//}
