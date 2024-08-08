package org.cxxii.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.cxxii.json.HostsJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import static org.cxxii.utils.FileManager.getHostCachePath;

public class HostCacheWriter {

    private final static Logger LOGGER = LoggerFactory.getLogger(HostCacheWriter.class);

    public static synchronized void appendPongToHostCache(HostsJson host) {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<HostsJson> hostList = new HashSet<>();

        // Read existing JSON list from file
        if (Files.exists(getHostCachePath()) && Files.isReadable(getHostCachePath())) {
            try {
                hostList = objectMapper.readValue(getHostCachePath().toFile(), new TypeReference<Set<HostsJson>>() {});
                LOGGER.debug("READING HOSTCACHE 22222");
                LOGGER.debug(hostList.toString());
            } catch (IOException e) {
                LOGGER.error("Could not read JSON file", e);
            }
        } else {
            LOGGER.warn("JSON file does not exist or is not readable");
        }

        // Add the new node
        if (hostList.add(host)) {
            LOGGER.info("Added new node to the set: {}", host);
        } else {
            System.out.println("Node already exists in the set: {}" + host);
            LOGGER.info("Node already exists in the set: {}", host);
        }

        System.out.println("APPEND TO HOST CACHE" + hostList);

        // Write the updated list back to the file
        try {
            ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
            objectWriter.writeValue(getHostCachePath().toFile(), hostList);
            LOGGER.info("Updated JSON file with new node set");


        } catch (IOException e) {
            LOGGER.error("Could not write to JSON file", e);
        }
    }
}
