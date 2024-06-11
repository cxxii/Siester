package org.cxxii.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cxxii.messages.Pong;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PongCache {
    private List<Pong> pongs;

    public PongCache() {
        this.pongs = new ArrayList<>();
    }

    public void addPong(Pong pong) {
        if (pongs.size() >= 10) {
            pongs.remove(0); // Remove the oldest Pong
        }
        pongs.add(pong);
    }

    public void saveToFile(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(filename), this);
    }

    public static PongCache loadFromFile(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filename), PongCache.class);
    }

    // Getters and setters
}

// check if