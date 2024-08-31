package Json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JsonGeneratorTest {

    private static final ObjectMapper myObjectMapper = new ObjectMapper();

    @Test
    void testGenerateNonPrettyJson() throws JsonProcessingException {
        // Arrange: Create a simple object to serialize
        TestObject testObject = new TestObject("John", 30);

        // Act: Generate JSON with pretty flag set to false
        String json = generateJson(testObject, false);

        // Assert: Verify the JSON is compact and correct
        assertEquals("{\"name\":\"John\",\"age\":30}", json);
    }

    @Test
    void testGeneratePrettyJson() throws JsonProcessingException {
        // Arrange: Create a simple object to serialize
        TestObject testObject = new TestObject("John", 30);

        // Act: Generate JSON with pretty flag set to true
        String json = generateJson(testObject, true);

        // Assert: Verify the JSON is pretty-printed (contains new lines and indentation)
        String expectedJson = "{\n" +
                "  \"name\" : \"John\",\n" +
                "  \"age\" : 30\n" +
                "}";
        assertEquals(expectedJson, json);
    }

    @Test
    void testGenerateJsonWithNullObject() throws JsonProcessingException {
        // Act: Generate JSON from null object
        String json = generateJson(null, false);

        // Assert: Verify that the JSON output for null is "null"
        assertEquals("null", json);
    }

    @Test
    void testGenerateJsonWithCollection() throws JsonProcessingException {
        // Arrange: Create a simple list of strings to serialize
        List<String> list = Arrays.asList("apple", "banana", "orange");

        // Act: Generate JSON for a collection
        String json = generateJson(list, false);

        // Assert: Verify the JSON output is correct for the list
        assertEquals("[\"apple\",\"banana\",\"orange\"]", json);
    }

    @Test
    void testGenerateJsonWithMap() throws JsonProcessingException {
        // Arrange: Create a simple map to serialize
        Map<String, Integer> map = new HashMap<>();
        map.put("apple", 1);
        map.put("banana", 2);

        // Act: Generate JSON for a map
        String json = generateJson(map, false);

        // Assert: Verify the JSON output is correct for the map
        assertEquals("{\"banana\":2,\"apple\":1}", json);
    }

    // Helper method for testing purposes
    private static String generateJson(Object o, boolean pretty) throws JsonProcessingException {
        ObjectWriter objectWriter = myObjectMapper.writer();
        if (pretty) {
            objectWriter = objectWriter.with(SerializationFeature.INDENT_OUTPUT);
        }

        return objectWriter.writeValueAsString(o);
    }

    // Helper class for serialization
    private static class TestObject {
        public String name;
        public int age;

        public TestObject(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }
}
