package org.cxxii.server;

import org.cxxii.messages.*;
import org.cxxii.server.config.Config;
import org.cxxii.server.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cxxii.utils.FileManager;
import java.io.IOException;

import static org.cxxii.messages.PingMessage.startPings;

public class Server {
    private final static Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        try {

            // Loads config file
            loadConfigeration();

            // Performs file checks
            FileManager.performFileChecks();

            // Starts serever
            startServer();

            // check host caches etc
            checkAndPingHosts();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void startServer() throws IOException {
        Config config = ConfigManager.getInstance().getCurrentconfig();
        MessageFactoryImpl messageFactory = new MessageFactoryImpl();

        registerParsers(messageFactory);

        ServerListenerThread serverListenerThread = new ServerListenerThread(config.getPort(), config.getWebroot(), messageFactory);
        serverListenerThread.start();
    }


    public static Config loadConfigeration() {

        ConfigManager.getInstance().loadConfigFile("src/main/resources/serverconfig.json");
        Config conf = ConfigManager.getInstance().getCurrentconfig();

        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using Webroot: " + conf.getWebroot());

        return conf;
    }


    public static MessageFactoryImpl registerParsers(MessageFactoryImpl messageFactory) {

        messageFactory.setParser((byte) 0x00, new PingMessageParser());
        messageFactory.setParser((byte) 0x01, new PongMessageParser());
        messageFactory.setParser((byte) 0x40, new PushMessageParser());
        messageFactory.setParser((byte) 0x80, new QueryMessageParser());
        messageFactory.setParser((byte) 0x81, new QueryHitMessageParser());

        return messageFactory;
    }


    public static void checkAndPingHosts() throws IOException {

        if (FileManager.checkHostCacheSize() == 0) {

            LOGGER.info("Pinging Bootstrap Server");

            Bootstrap.pingBootstrapServer();

        } else {

            LOGGER.info("Hosts found in cache");
        }

        PingMessage.startPings();

    }
}
