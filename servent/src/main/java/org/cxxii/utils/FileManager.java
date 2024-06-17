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
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

    private static final String DIRECTORY_NAME = "siester";
    private static final String PONG_DIRECTORY = "ClientPongs";
    private static final String HOST_CACHE = "host_cache.json";
    private static final String PONG_CACHE = "pong_cache.json";
    private static final String HOST_DETAILS = "host_details.json";
    private static final String USER_HOME = System.getProperty("user.home");

    private static void checkAndCreateDir(File directory) {
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                LOGGER.info("Created Dir: " + directory.getAbsolutePath());
            } else {
                LOGGER.error("Failed to create dir: " + directory.getAbsolutePath());
            }
        } else {
            LOGGER.info("Directory exists at " +  directory.getAbsolutePath() );
        }
    }

    private static void checkAndCreateFile(File file) {
        if (!file.exists()) {
            if (file.mkdirs()) {
                LOGGER.info("Created Dir: " + file.getAbsolutePath());
            } else {
                LOGGER.error("Failed to create dir: " + file.getAbsolutePath());
            }
        } else {
            LOGGER.info("Directory exists at " +  file.getAbsolutePath() );
        }
    }

    public static void performFileChecks() {
        LOGGER.info("Performing file checks");

        // Get directory
        File mainDirectory = new File(USER_HOME, DIRECTORY_NAME);
        File pongDirectory = new File(USER_HOME, PONG_DIRECTORY);


        // Checks n Creates Dir
        checkAndCreateDir(mainDirectory);
        checkAndCreateDir(pongDirectory);

        // Checks n Creates Files
        checkAndCreateFile(new File(mainDirectory, HOST_CACHE));
        checkAndCreateFile(new File(mainDirectory, PONG_CACHE));
        checkAndCreateFile(new File(mainDirectory, HOST_DETAILS));

    }

    public static long checkHostCacheSize() throws IOException {

        return Files.size(getHostCachePath());

    }

    public static Path getHostCachePath() {

        return Paths.get(USER_HOME, DIRECTORY_NAME, HOST_CACHE);
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
