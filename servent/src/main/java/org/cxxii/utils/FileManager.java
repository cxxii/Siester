package org.cxxii.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

    private static final String DIRECTORY_NAME = "siester";
    private static final String PONG_DIRECTORY = "siester/clientpongs";
    private static final String HOST_CACHE = "hostCache.json";
    private static final String PONG_CACHE = "pongCache.json";
    private static final String USER_HOME = System.getProperty("user.home");


    public static void performFileChecks() {
        LOGGER.info("Performing file checks");

        // Get directory
        File mainDirectory = new File(USER_HOME, DIRECTORY_NAME);
        File pongDirectory = new File(USER_HOME, PONG_DIRECTORY);

        // Check directory
        if (!mainDirectory.exists()) {
            createDirectory(mainDirectory);
        } else {
            LOGGER.info("Directory already exists: " + mainDirectory.getAbsolutePath());
        }

        if (!pongDirectory.exists()) {
            createDirectory(pongDirectory);
        } else {
            LOGGER.info("Directory already exists: " + pongDirectory.getAbsolutePath());
        }

        // Check host cache file
        File hostCacheFile = new File(mainDirectory, HOST_CACHE);
        if (!hostCacheFile.exists()) {
            createFile(hostCacheFile);
        } else {
            LOGGER.info("Host cache file found: " + hostCacheFile.getAbsolutePath());
        }

        // Check pong cache file
        File pongCacheFile = new File(mainDirectory, PONG_CACHE);
        if (!pongCacheFile.exists()) {
            createFile(pongCacheFile);
        } else {
            LOGGER.info("Pong cache file found: " + pongCacheFile.getAbsolutePath());
        }
    }

    public static long checkHostCacheSize() throws IOException {

        return Files.size(getHostCachePath());

    }

    public static Path getHostCachePath() {
        return Paths.get(USER_HOME, DIRECTORY_NAME, HOST_CACHE);
    }

    private static void createDirectory(File directory) {
        if (directory.mkdirs()) {
            LOGGER.info("Directory created: " + directory.getAbsolutePath());
        } else {
            LOGGER.error("Failed to create directory: " + directory.getAbsolutePath());
            throw new RuntimeException("Failed to create required directory. Please create it manually: " + directory.getAbsolutePath());
        }
    }

    private static void createFile(File file) {
        try {
            if (file.createNewFile()) {
                LOGGER.info("File created: " + file.getAbsolutePath());
            } else {
                LOGGER.error("File already exists (unexpected): " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            LOGGER.error("Error creating file: " + file.getAbsolutePath(), e);
            throw new RuntimeException("Error creating file: " + file.getAbsolutePath(), e);
        }
    }



    public static void writeHostsToFile(InputStream inputStream) throws IOException {

        try {
            Files.write(getHostCachePath(), inputStream.readAllBytes());
            LOGGER.info("Hosts written to host cache successfully");
        } catch (IOException e) {
            LOGGER.error("Error writing host cache file", e);
            throw new IOException("Error writing host cache file", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Error closing InputStream", e);
                }
            }
        }
    }
}
