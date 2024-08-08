package org.cxxii.server;

import com.sun.net.httpserver.HttpServer;
import org.cxxii.Scheduler;
import org.cxxii.gui.CLI;
import org.cxxii.gui.QueryHitListener;
import org.cxxii.gui.SwingApp;
import org.cxxii.messages.*;
import org.cxxii.network.Network;
import org.cxxii.server.config.Config;
import org.cxxii.server.config.ConfigManager;
import org.cxxii.share.FileServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cxxii.utils.FileManager;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static org.cxxii.messages.PingMessage.startPings;

// TODO  - Move entry point to own class

public class Server {
    private final static Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void gogogo() {
        try {

            Scanner scanner = new Scanner(System.in);

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

            // Check n ping hosts
            Server.checkAndPingHosts();
            LOGGER.info("Host checks and pings completed.");

            // Log upload directory path for debugging
            LOGGER.info("Upload directory: " + FileManager.getUploadDirPath());

            // Start scheduled tasks
            // Scheduler.startPingCacheUpdates(0, 35, TimeUnit.SECONDS);
            Scheduler.startPingHostCache(0, 20, TimeUnit.SECONDS);
            Scheduler.startPongCacheUpdates(0, 20, TimeUnit.SECONDS);
            // Scheduler.startHostCounter(1,2,TimeUnit.SECONDS);
            Scheduler.startHitSender(4,40,TimeUnit.SECONDS);
            LOGGER.info("Scheduled tasks started.");

            // Enter CLI loop
//            CLI.loop();
//            LOGGER.info("CLI loop started.");

          //  SwingUtilities.invokeLater(SwingApp::createAndShowGUI);

        } catch (IOException e) {
            LOGGER.error("IOException encountered", e);
            throw new RuntimeException("Failed to start server due to IOException", e);
        } catch (Exception e) {
            LOGGER.error("Unexpected exception encountered", e);
            throw new RuntimeException("Failed to start server due to an unexpected error", e);
        }
    }

    public static void startServer() throws IOException {
        Config config = ConfigManager.getInstance().getCurrentconfig();
        MessageFactoryImpl messageFactory =  new MessageFactoryImpl();

        registerParsers(messageFactory);

        ServerListenerThread serverListenerThread = new ServerListenerThread(config.getPort(), config.getWebroot(), messageFactory);

        serverListenerThread.start();
    }

    public static void loadConfiguration() {
        LOGGER.info("Loading Configuration...");

        ConfigManager configManager = ConfigManager.getInstance();

        try (InputStream inputStream = Server.class.getClassLoader().getResourceAsStream("serverconfig.json")) {
            if (inputStream == null) {
                throw new FileNotFoundException("serverconfig.json not found in classpath");
            }
            configManager.loadConfigFile(inputStream);
            Config conf = configManager.getCurrentconfig();
            LOGGER.info("Using Port: " + conf.getPort());
            LOGGER.info("Using Webroot: " + conf.getWebroot());
        } catch (IOException e) {
            LOGGER.error("Failed to load configuration", e);
            throw new RuntimeException(e);
        }
    }

    private static MessageFactoryImpl registerParsers(MessageFactoryImpl messageFactory) {
        LOGGER.info("Registering Parsers...");

        SwingApp swingApp = new SwingApp();

        // Create the listener instance
        QueryHitListener queryHitListener = swingApp;

        //QueryHitListener queryHitListener = new MyQueryHitListener();

        QueryHitMessageParser queryHitMessageParser = new QueryHitMessageParser();
        queryHitMessageParser.setListener(queryHitListener);

        messageFactory.setParser((byte) 0x00, new PingMessageParser());
        messageFactory.setParser((byte) 0x01, new PongMessageParser());
        messageFactory.setParser((byte) 0x40, new PushMessageParser());
        messageFactory.setParser((byte) 0x80, new QueryMessageParser());
        messageFactory.setParser((byte) 0x81, queryHitMessageParser);

        return messageFactory;
    }

    public static void checkAndPingHosts() throws IOException {

        if (FileManager.checkHostCacheSize() == 0) {

            Bootstrap.pingBootstrapServer();

        } else {

            LOGGER.info("Hosts found in cache");
        }

        PingMessage.startPings();

    }
}
