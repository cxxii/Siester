//package downloading;
//
//import com.sun.net.httpserver.HttpServer;
//import org.cxxii.share.FileServer;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.concurrent.Executors;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class FileServerTest {
//
//
//
//    private HttpServer server;
//    private HttpClient client;
//    private final int port = 8080;
//    private final String testDirectory = "test_files";
//
//    @BeforeEach
//    public void setUp() throws IOException {
//        // Set up the test server
//        server = HttpServer.create(new InetSocketAddress(port), 0);
//        server.setExecutor(Executors.newFixedThreadPool(2));
//        server.createContext("/", new FileServer.FileHandler(testDirectory));
//        server.start();
//
//        // Set up the HttpClient
//        client = HttpClient.newHttpClient();
//
//        // Create a test file directory
//        Files.createDirectories(Path.of(testDirectory));
//
//        // Create a sample file for testing
//        Files.writeString(Path.of(testDirectory, "sample.txt"), "Hello, this is a test file.");
//    }
//
//    @AfterEach
//    public void tearDown() throws IOException {
//        // Stop the server
//        server.stop(0);
//
//        // Clean up the test files
//        Files.deleteIfExists(Path.of(testDirectory, "sample.txt"));
//        Files.deleteIfExists(Path.of(testDirectory));
//    }
//
//    @Test
//    public void testFileExists() throws Exception {
//        // Test case when the file exists
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:" + port + "/sample.txt"))
//                .GET()
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        assertEquals(200, response.statusCode());
//        assertEquals("Hello, this is a test file.", response.body());
//    }
//
//    @Test
//    public void testFileNotFound() throws Exception {
//        // Test case when the file does not exist
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:" + port + "/nonexistent.txt"))
//                .GET()
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        assertEquals(404, response.statusCode());
//    }
//
//}
//
