//package Json;
//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import org.junit.jupiter.api.Test;
//
//import java.io.*;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//public class JsonReaderTest {
//
//    @Test
//    void testReadJsonFromClasspathSuccess() throws IOException {
//        // Act: Call the method under test with the resource path
//        JsonObject result = readJsonFromClasspath("test.json");
//
//        // Assert: Check if the JSON was parsed correctly
//        assertEquals("value", result.get("key").getAsString());
//    }
//
//    @Test
//    void testReadJsonFromClasspathFileNotFound() {
//        // Act and Assert: Verify that a FileNotFoundException is thrown
//        FileNotFoundException thrownException = assertThrows(FileNotFoundException.class, () -> {
//            readJsonFromClasspath("nonexistent.json");
//        });
//
//        // Assert: Check exception message
//        assertEquals("nonexistent.json not found in classpath", thrownException.getMessage());
//    }
//
//    @Test
//    void testReadJsonFromClasspathIOException() {
//        // This test is not feasible due to limitations with mocking class loaders
//        // Consider testing file read errors in a different way or using integration tests
//    }
//
//    public static JsonObject readJsonFromClasspath(String classpath) throws IOException {
//        try (InputStream inputStream = JsonReaderTest.class.getClassLoader().getResourceAsStream(classpath);
//             Reader reader = new InputStreamReader(inputStream)) {
//            if (inputStream == null) {
//                throw new FileNotFoundException(classpath + " not found in classpath");
//            }
//            return JsonParser.parseReader(reader).getAsJsonObject();
//        }
//    }
//}
