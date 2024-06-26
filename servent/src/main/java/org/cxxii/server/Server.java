package org.cxxii.server;

import org.cxxii.gui.CLI;
import org.cxxii.messages.*;
import org.cxxii.network.Network;
import org.cxxii.server.config.Config;
import org.cxxii.server.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cxxii.utils.FileManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import static org.cxxii.messages.PingMessage.startPings;

// TODO  - Move entry point to own class

public class Server {
    private final static Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        try {



            // Loads config file
            loadConfiguration(); // OK

            // Performs file checks
            FileManager.performFileChecks(); // OK

            // Starts serever
            startServer(); // OK

            // check host caches etc
            checkAndPingHosts(); // OK

            // DEBUGGING purpose
            String ipString = InetAddress.getByAddress(Network.getLocalIpAddress()).getHostAddress();
            LOGGER.debug("Local IP address: " + ipString);


//            CLI.loop();



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void startServer() throws IOException {
        Config config = ConfigManager.getInstance().getCurrentconfig();
        MessageFactoryImpl messageFactory =  new MessageFactoryImpl();

        registerParsers(messageFactory);

        ServerListenerThread serverListenerThread = new ServerListenerThread(config.getPort(), config.getWebroot(), messageFactory);
        serverListenerThread.start();
    }

    private static void loadConfiguration() {
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

        messageFactory.setParser((byte) 0x00, new PingMessageParser());
        messageFactory.setParser((byte) 0x01, new PongMessageParser());
        messageFactory.setParser((byte) 0x40, new PushMessageParser());
        messageFactory.setParser((byte) 0x80, new QueryMessageParser());
        messageFactory.setParser((byte) 0x81, new QueryHitMessageParser());

        return messageFactory;
    }

    private static void checkAndPingHosts() throws IOException {

        if (FileManager.checkHostCacheSize() == 0) {

            LOGGER.info("Pinging Bootstrap Server");

            Bootstrap.pingBootstrapServer();

        } else {

            LOGGER.info("Hosts found in cache");
        }

        PingMessage.startPings();

    }
}
