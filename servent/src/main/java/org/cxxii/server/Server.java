package org.cxxii.server;

import org.cxxii.messages.*;
import org.cxxii.server.config.Config;
import org.cxxii.server.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cxxii.utils.FileManager;
import java.io.IOException;

public class Server {
    private final static Logger LOGGER = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws IOException {
        // this is your main thread

        ConfigManager.getInstance().loadConfigFile("src/main/resources/serverconfig.json");
        Config conf = ConfigManager.getInstance().getCurrentconfig();

        FileManager.performFileChecks();

        LOGGER.info("Server Starting...");

        LOGGER.info("Using Port: " + conf.getPort());
        LOGGER.info("Using Webroot: " + conf.getWebroot());

        MessageFactoryImpl messageFactory = new MessageFactoryImpl();

        // Register parsers for different message types
        messageFactory.setParser((byte) 0x00, new PingMessageParser());
        messageFactory.setParser((byte) 0x01, new PongMessageParser());
        messageFactory.setParser((byte) 0x40, new PushMessageParser());
        messageFactory.setParser((byte) 0x80, new QueryMessageParser());
        messageFactory.setParser((byte) 0x81, new QueryHitMessageParser());

        try {
            ServerListenerThread serverListenerThread = new ServerListenerThread(conf.getPort(), conf.getWebroot(), messageFactory);
            serverListenerThread.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // check for hosts in host cache
        if (FileManager.checkHostCacheSize() == 0) {
            LOGGER.info("Pinging Bootstrap Server");
            Bootstrap.pingBootstrapServer();

        } else {
            LOGGER.info("Hosts found in cache");
        }

        // !!!! KEEP !!!!
        startPings();

        //message.process();
        //message.create();

        // begin pinging the hosts in the hostcache

//        Handshake.pingHosts();




//        try (InputStream in = new ByteArrayInputStream(getNetworkData())) {
//            MessageAbstract message = messageFactory.read(in);
//
//            if (message != null) {
//                message.process();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        InputStream in = new ByteArrayInputStream(getNetworkData());
//        MessageAbstract message = messageFactory.read(in);
//        if (message != null) {
//            System.out.println("Message processing");
//            message.process();
//        } else {
//            System.out.println("No message parsed.");
//        }


        // messageFactory.createHeader() ===== need this




    }


    // if host cache is empty then ping bootstrap and write hosts to hostcache

        // else
}
