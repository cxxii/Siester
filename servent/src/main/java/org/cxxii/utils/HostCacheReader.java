package org.cxxii.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cxxii.server.SocketAddr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class HostCacheReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(HostCacheReader.class);

    public static List<SocketAddr> readHostCache() {
        LOGGER.info("Reading hostcache");

        ObjectMapper objectMapper = new ObjectMapper();
        Path hostCachePath = FileManager.getHostCachePath();
        List<SocketAddr> addresses = Collections.emptyList();

        if (Files.exists(hostCachePath) && Files.isReadable(hostCachePath)) {
            try {
                if (Files.size(hostCachePath) > 0) {
                    addresses = objectMapper.readValue(hostCachePath.toFile(), new TypeReference<List<SocketAddr>>() {});
                } else {
                    LOGGER.warn("Hostcache file is empty: {}", hostCachePath);
                }
            } catch (IOException e) {
                LOGGER.error("Could not read hostcache", e);
            }
        } else {
            LOGGER.warn("Hostcache file does not exist or is not readable: {}", hostCachePath);
        }

        return addresses;
    }


    public static int getNetworkSize() {

        List<SocketAddr> hosts = readHostCache();

        return hosts.size();
    }
}
