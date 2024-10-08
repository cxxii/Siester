package org.cxxii.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.*;
import org.cxxii.json.NodePongJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

    private static final String DIRECTORY_NAME = "siester";
    private static final String PONG_DIRECTORY = "siester/node_pongs";
    private static final String SHARED_DIRECTORY = "siester/shared";
    private static final String DOWNLOAD_DIRECTORY = "download";
    private static final String UPLOAD_DIRECTORY = "upload";
    private static final String DDD_DIRECTORY = "shared/upload";
    private static final String UUU_DIRECTORY = "shared/download";
    private static final String HOST_DIRECTORY = "siester/host";
    private static final String LOG_DIRECTORY = "siester/logs";
    private static final String HOST_CACHE = "host_cache.json";
    private static final String PONG_CACHE = "pong_cache.json";
    private static final String PONG_HIT = "pong_rank.json";
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
            LOGGER.info("File exists... " + file.getAbsolutePath());
        }
    }

    public static void CheckAndCreateFilePublic(File file) {
        checkAndCreateFile(file);
    }


    public static void performFileChecks() throws IOException {
        LOGGER.info("Performing file checks");

        // Get directory
        File mainDirectory = new File(USER_HOME, DIRECTORY_NAME);
        File hostDirectory = new File(USER_HOME, HOST_DIRECTORY);
        File logDirectory = new File(USER_HOME, LOG_DIRECTORY);
        File sharedDirectory = new File(USER_HOME, SHARED_DIRECTORY);
        File downloadDirectory = new File(sharedDirectory, DOWNLOAD_DIRECTORY);
        File uploadDirectory = new File(sharedDirectory, UPLOAD_DIRECTORY);
        File pongDirectory = new File(USER_HOME, PONG_DIRECTORY);

        // Checks n Creates Dir
        checkAndCreateDir(mainDirectory);
        checkAndCreateDir(hostDirectory);
        checkAndCreateDir(logDirectory);
        checkAndCreateDir(sharedDirectory);
        checkAndCreateDir(uploadDirectory);
        checkAndCreateDir(downloadDirectory);
        checkAndCreateDir(pongDirectory);

        // Checks n Creates Files
        checkAndCreateFile(new File(hostDirectory, HOST_CACHE));
        checkAndCreateFile(new File(mainDirectory, PONG_CACHE));
        checkAndCreateFile(new File(mainDirectory, HOST_DETAILS));
        checkAndCreateFile(new File(logDirectory, LOG_DETAILS));
        checkAndCreateFile(new File(mainDirectory, PONG_HIT));

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

    public static Path getPongHitPath() {
        return getPath(DIRECTORY_NAME, PONG_HIT);
    }

    public static Path getHostDetailsPath() {
        return getPath(DIRECTORY_NAME, HOST_DETAILS);
    }

    public static Path getPongCachePath() {
        return getPath(DIRECTORY_NAME, PONG_CACHE);
    }

    public static Path getNodePongDirPath() {
        return Paths.get(USER_HOME, PONG_DIRECTORY);
    }


    //rename ddd
    public static Path getUploadDirPath() {

        return getPath(DIRECTORY_NAME, DDD_DIRECTORY);
    }

    public static Path getDownloadDirPath() {

        return getPath(DIRECTORY_NAME, UUU_DIRECTORY);
    }

    private static Path getPath(String directory, String fileName) {
        return Paths.get(USER_HOME, directory, fileName);
    }


    public static void writeHostsToFile(InputStream inputStream) throws IOException {

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        JsonElement jsonElement = JsonParser.parseReader(inputStreamReader);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = gson.toJson(jsonElement);

        try {
            Files.writeString(getHostCachePath(), prettyJson);

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

    // TODO - remove random and improve
    public synchronized static void pongCacheGenerator() {
        LOGGER.debug("Generating fresh pongcache...");

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

        try {
            File nodePongDir = FileManager.getNodePongDirPath().toFile();
            File[] nodeCache = Objects.requireNonNull(nodePongDir.listFiles(), "Node pong directory is empty or does not exist");

            int numPongFiles = nodeCache.length;
            List<NodePongJson> pongList = new ArrayList<>();

            // Basic - network is small, send all hosts regardless
            if (numPongFiles < 10) {
                for (File node : nodeCache) {
                    List<NodePongJson> nodePongs = objectMapper.readValue(node, new TypeReference<List<NodePongJson>>() {});

                    Random random = new Random();

                    if (!nodePongs.isEmpty()) {
                        int randomPick = random.nextInt(nodePongs.size());
                        pongList.add(nodePongs.get(randomPick));
                        LOGGER.debug("Added to PONG LIST: {}", nodePongs.get(randomPick).toString());
                    } else {
                        LOGGER.warn("Node Pongs list is empty");
                    }
                }

                writePongCacheToFile(pongList, objectWriter);

            } else {
                LOGGER.debug("IMPLEMENT ADVANCED PONGCACHE SYS");

            }
        } catch (Exception e) {
            LOGGER.error("An error occurred during PONG CACHE generation", e);
        }
    }

    private static void writePongCacheToFile(List<NodePongJson> pongs, ObjectWriter objectWriter) {
        try (FileWriter fileWriter = new FileWriter(FileManager.getPongCachePath().toFile())) {
            String jsonString = objectWriter.writeValueAsString(pongs);
            fileWriter.write(jsonString);
            LOGGER.debug("Writing to file PONGCACHE");
        } catch (IOException e) {
            LOGGER.error("Error writing PONG CACHE to file", e);
        }
    }
}
