package org.cxxii;

import org.cxxii.gui.CLI;
import org.cxxii.server.Server;
import org.cxxii.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            LOGGER.info("Starting server setup...");

            // Load configuration file
            Server.loadConfiguration();
            LOGGER.info("Configuration loaded successfully.");

            // Perform file checks
            FileManager.performFileChecks();
            LOGGER.info("File checks completed.");

            // Start the server
            Server.startServer();
            LOGGER.info("Server started successfully.");

            // Check and ping hosts
            Server.checkAndPingHosts();
            LOGGER.info("Host checks and pings completed.");

            // Log upload directory path for debugging
            LOGGER.info("Upload directory: " + FileManager.getUploadDirPath());

            // Start scheduled tasks
            //Scheduler.startPingCacheUpdates(0, 35, TimeUnit.SECONDS);
            Scheduler.startPingHostCache(0, 60, TimeUnit.SECONDS);
            Scheduler.startPongCacheUpdates(0, 60, TimeUnit.SECONDS);
            LOGGER.info("Scheduled tasks started.");

            // Enter CLI loop
            CLI.loop();
            LOGGER.info("CLI loop started.");

        } catch (IOException e) {
            LOGGER.error("IOException encountered", e);
            throw new RuntimeException("Failed to start server due to IOException", e);
        } catch (Exception e) {
            LOGGER.error("Unexpected exception encountered", e);
            throw new RuntimeException("Failed to start server due to an unexpected error", e);
        }
    }
}
