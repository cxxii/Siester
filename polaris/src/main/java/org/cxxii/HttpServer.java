package org.cxxii;

import org.cxxii.config.Config;
import org.cxxii.config.ConfigManager;
import org.cxxii.core.ServerListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class HttpServer {

    private final static Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    public static void main(String[] args) {

        ConfigManager.getInstance().loadConfigFile("src/main/resources/http.json");
        Config conf = ConfigManager.getInstance().getCurrentconfig();

        LOGGER.info("Server Starting...");

        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using Webroot: " + conf.getWebroot());


        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(conf.getPort(), conf.getWebroot());
            serverListenerThread.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
