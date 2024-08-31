//package Json;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.io.TempDir;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.slf4j.Logger;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.nio.file.Path;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class NodePongDetailsTest {
//
//    // Mocked Logger
//    private static final Logger LOGGER = Mockito.mock(Logger.class);
//
//    @TempDir
//    Path tempDir;  // Temporary directory to write the test files
//
//    @Test
//    void testWriteNodePongDetailsSuccess() throws IOException {
//        // Arrange: Mock the FileManager.getHostDetailsPath() to return a temp file path
//        Path tempFile = tempDir.resolve("hostDetails.json");
//        try (MockedStatic<FileManager> fileManagerMock = Mockito.mockStatic(FileManager.class)) {
//            fileManagerMock.when(FileManager::getHostDetailsPath).thenReturn(tempFile);
//
//            // Act: Call the method under test
//            writeNodePongDetails();
//
//            // Assert: Verify that the file was created and contains the expected content
//            File createdFile = tempFile.toFile();
//            assertTrue(createdFile.exists(), "The file should be created");
//
//            // Read the file and verify its content
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            try (FileReader reader = new FileReader(createdFile)) {
//                OwnHostDetailsJson hostDetails = gson.fromJson(reader, OwnHostDetailsJson.class);
//                assertEquals(0, hostDetails.getFilesShared());
//                assertEquals(0, hostDetails.getKbShared());
//            }
//
//            // Verify logging
//            verify(LOGGER).info("Set Default host details: FilesShared = 0, KbShared = 0");
//        }
//    }
//
//    @Test
//    void testWriteNodePongDetailsIOException() {
//        // Arrange: Mock the FileManager.getHostDetailsPath() to return a temp file path
//        Path tempFile = tempDir.resolve("hostDetails.json");
//        try (MockedStatic<FileManager> fileManagerMock = Mockito.mockStatic(FileManager.class)) {
//            fileManagerMock.when(FileManager::getHostDetailsPath).thenReturn(tempFile);
//
//            // Act: Mock the FileWriter to throw an IOException
//            try (MockedStatic<FileWriter> fileWriterMock = Mockito.mockStatic(FileWriter.class)) {
//                fileWriterMock.when(() -> new FileWriter(tempFile.toFile())).thenThrow(new IOException("Mocked IOException"));
//
//                // Act and Assert: Verify that a RuntimeException is thrown and logged correctly
//                RuntimeException exception = assertThrows(RuntimeException.class, () -> {
//                    writeNodePongDetails();
//                });
//
//                // Verify exception message
//                assertTrue(exception.getMessage().contains("ERROR writing JSON host details"));
//
//                // Verify logging
//                verify(LOGGER).error(contains("Failed to write default host details"), any(IOException.class));
//            }
//        }
//    }
//
//    // Method under test (replicated for context in this testing code)
//    public static void writeNodePongDetails() throws IOException {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        OwnHostDetailsJson ownHostDetailsJson = new OwnHostDetailsJson(0, 0);
//
//        try (FileWriter writer = new FileWriter(FileManager.getHostDetailsPath().toFile())) {
//            gson.toJson(ownHostDetailsJson, writer);
//            LOGGER.info("Set Default host details: FilesShared = 0, KbShared = 0");
//        } catch (IOException e) {
//            LOGGER.error("Failed to write default host details: ", e);
//            throw new RuntimeException("ERROR writing JSON host details: ", e);
//        }
//    }
//
//    // Mocked classes and methods used for testing purposes
//    public static class OwnHostDetailsJson {
//        private int filesShared;
//        private int kbShared;
//
//        public OwnHostDetailsJson(int filesShared, int kbShared) {
//            this.filesShared = filesShared;
//            this.kbShared = kbShared;
//        }
//
//        public int getFilesShared() {
//            return filesShared;
//        }
//
//        public int getKbShared() {
//            return kbShared;
//        }
//    }
//
//    public static class FileManager {
//        public static Path getHostDetailsPath() {
//            // Actual implementation should return the correct path
//            return Path.of("actual/path");
//        }
//    }
//}
