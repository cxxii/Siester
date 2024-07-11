package org.cxxii.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.cxxii.json.HostsJson;
import org.cxxii.messages.PongMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import static org.cxxii.utils.FileManager.getHostCachePath;

public class HostCacheWriter {

    private final static Logger LOGGER = LoggerFactory.getLogger(HostCacheWriter.class);

    public static synchronized void appendToHostJson(HostsJson newNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<HostsJson> nodeList = new HashSet<>();

        // Read existing JSON list from file
        if (Files.exists(getHostCachePath()) && Files.isReadable(getHostCachePath())) {
            try {
                nodeList = objectMapper.readValue(getHostCachePath().toFile(), new TypeReference<Set<HostsJson>>() {});
            } catch (IOException e) {
                LOGGER.error("Could not read JSON file", e);
            }
        } else {
            LOGGER.warn("JSON file does not exist or is not readable");
        }

        // Add the new node
        if (nodeList.add(newNode)) {
            LOGGER.info("Added new node to the set: {}", newNode);
        } else {
            LOGGER.info("Node already exists in the set: {}", newNode);
        }

        // Write the updated list back to the file
        try {
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            objectWriter.writeValue(getHostCachePath().toFile(), nodeList);
            LOGGER.info("Updated JSON file with new node set");
        } catch (IOException e) {
            LOGGER.error("Could not write to JSON file", e);
        }
    }


}
