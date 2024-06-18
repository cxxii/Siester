package filemanager;

import org.cxxii.utils.FileManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileManagerTest {

    private static final String USER_HOME = System.getProperty("user.home");
    private static final Path MAIN_DIR_PATH = Paths.get(USER_HOME, "siester");
    private static final Path PONG_DIR_PATH = Paths.get(USER_HOME, "ClientPongs");
    private static final Path HOST_CACHE_PATH = Paths.get(USER_HOME, "siester", "host_cache.json");

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(MAIN_DIR_PATH);
        Files.createDirectories(PONG_DIR_PATH);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(HOST_CACHE_PATH);
        Files.deleteIfExists(MAIN_DIR_PATH.resolve("pong_cache.json"));
        Files.deleteIfExists(MAIN_DIR_PATH.resolve("host_details.json"));
        Files.deleteIfExists(MAIN_DIR_PATH);
        Files.deleteIfExists(PONG_DIR_PATH);
    }

    @Test
    void testPerformFileChecks() throws IOException {
        FileManager.performFileChecks();
        assertTrue(Files.exists(MAIN_DIR_PATH));
        assertTrue(Files.exists(PONG_DIR_PATH));
        assertTrue(Files.exists(HOST_CACHE_PATH));
        assertTrue(Files.exists(MAIN_DIR_PATH.resolve("pong_cache.json")));
        assertTrue(Files.exists(MAIN_DIR_PATH.resolve("host_details.json")));
    }

    @Test
    void testCheckHostCacheSizeEmpty() throws IOException {
        FileManager.performFileChecks();
        assertEquals(0, FileManager.checkHostCacheSize());
    }

    @Test
    void testCheckHostCacheSize() throws IOException {
        FileManager.performFileChecks();
        Files.write(HOST_CACHE_PATH, "test".getBytes());
        assertEquals(4, FileManager.checkHostCacheSize());
    }

    @Test
    void testWriteHostsToFile() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("test data".getBytes());
        FileManager.writeHostsToFile(inputStream);
        assertEquals("test data", Files.readString(HOST_CACHE_PATH));
    }
}
