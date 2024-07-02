package org.cxxii.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.cxxii.utils.Json.writeDefaultHostDetails;

public class FileManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

    private static final String DIRECTORY_NAME = "siester";
    private static final String PONG_DIRECTORY = "siester/node_pongs";
    private static final String SHARED_DIRECTORY = "siester/shared";
    private static final String DOWNLOAD_DIRECTORY = "shared/download";
    private static final String UPLOAD_DIRECTORY = "shared/upload";
    private static final String HOST_DIRECTORY = "siester/host";
    private static final String LOG_DIRECTORY = "siester/logs";
    private static final String HOST_CACHE = "host_cache.json";
    private static final String PONG_CACHE = "pong_cache.json";
    private static final String HOST_DETAILS = "host_details.json";
    private static final String LOG_DETAILS = "logs.txt";
    private static final String USER_HOME = System.getProperty("user.home");

    static {
        System.setProperty("LOG_FILE", USER_HOME + File.separator + "logs" + File.separator + "application.log");
    }

    private static void checkAndCreateDir(File directory) {
        if (!directory.exists()) {
            try {
                if (directory.mkdir()) {
                    LOGGER.info("Created dir: " + directory.getAbsolutePath());
                }
            } catch (SecurityException e) {
                LOGGER.error("Failed to make dir: " + directory.getAbsolutePath(), e);
                throw new RuntimeException("ERROR while creating dir: " + directory.getAbsolutePath(), e);
            }
        } else {
            LOGGER.info("Directory exists at " + directory.getAbsolutePath());
        }
    }

    private static void checkAndCreateFile(File file) {
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    LOGGER.info("Created file: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                LOGGER.error("Failed to make file: " + file.getAbsolutePath(), e);
                throw new RuntimeException("ERROR while creating file: " + file.getAbsolutePath(), e);
            }
        } else {
            LOGGER.info("Directory exists file " + file.getAbsolutePath());
        }
    }


    public static void  performFileChecks() throws IOException {
        LOGGER.info("Performing file checks");

        // Get directory
        File mainDirectory = new File(USER_HOME, DIRECTORY_NAME);
        File pongDirectory = new File(USER_HOME, PONG_DIRECTORY);
        File hostDirectory = new File(USER_HOME, HOST_DIRECTORY);
        File logDirectory = new File(USER_HOME, LOG_DIRECTORY);
        File sharedDirectory = new File(USER_HOME, SHARED_DIRECTORY);
        File downloadDirectory = new File(USER_HOME, DOWNLOAD_DIRECTORY);
        File uploadDirectory = new File(USER_HOME, UPLOAD_DIRECTORY);


        // Checks n Creates Dir
        checkAndCreateDir(mainDirectory);
        checkAndCreateDir(pongDirectory);
        checkAndCreateDir(hostDirectory);
        checkAndCreateDir(logDirectory);
        checkAndCreateDir(sharedDirectory);
        checkAndCreateDir(uploadDirectory);
        checkAndCreateDir(downloadDirectory);

        // Checks n Creates Files
        checkAndCreateFile(new File(hostDirectory, HOST_CACHE));
        checkAndCreateFile(new File(mainDirectory, PONG_CACHE));
        checkAndCreateFile(new File(mainDirectory, HOST_DETAILS));
        checkAndCreateFile(new File(logDirectory, LOG_DETAILS));

        if (checkHostDetailsSize() == 0) {
            Json.writeDefaultHostDetails();
        }
    }

    public static long checkHostCacheSize() throws IOException {
        return Files.size(getPath(HOST_DIRECTORY, HOST_CACHE));
    }

    public static long checkHostDetailsSize() throws IOException {
        return Files.size(getHostDetailsPath());
    }



    public static Path getHostCachePath() {
        return getPath(HOST_DIRECTORY, HOST_CACHE);
    }

    public static Path getHostDetailsPath() {
        return getPath(DIRECTORY_NAME, HOST_DETAILS);
    }

    public static Path getNodePongDirPath() {
        return getPath(DIRECTORY_NAME, PONG_DIRECTORY);
    }
    public static Path getUploadDirPath() {
        return getPath(DIRECTORY_NAME, UPLOAD_DIRECTORY);
    }

    private static Path getPath(String directory, String fileName) {
        return Paths.get(USER_HOME, directory, fileName);
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
