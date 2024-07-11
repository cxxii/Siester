package org.cxxii.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.cxxii.json.HostsJson;
import org.cxxii.json.NodePongJson;
import org.cxxii.server.SocketAddr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.cxxii.utils.FileManager.getHostCachePath;

public class PongCacheReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PongCacheReader.class);


    public static List<NodePongJson> readPongCache() {
        LOGGER.info("Reading PongCache");

        ObjectMapper objectMapper = new ObjectMapper();
        Path pongCachePath = FileManager.getPongCachePath();
        List<NodePongJson> pongs = Collections.emptyList();

        if (Files.exists(pongCachePath) && Files.isReadable(pongCachePath)) {
            try {
                pongs = objectMapper.readValue(pongCachePath.toFile(), new TypeReference<List<NodePongJson>>() {});
            } catch (IOException e) {
                LOGGER.error("Could not read pongcache", e);
            }
        } else {
            LOGGER.warn("pongcache file does not exist or is not readable");
        }

        return pongs;
    }



}









