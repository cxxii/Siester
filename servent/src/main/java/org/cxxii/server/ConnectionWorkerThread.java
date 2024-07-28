package org.cxxii.server;

import org.cxxii.messages.MessageFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;


public class ConnectionWorkerThread extends Thread {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConnectionWorkerThread.class);

    private final Socket socket;
    private final MessageFactoryImpl messageFactory;

    public ConnectionWorkerThread(Socket socket, MessageFactoryImpl messageFactory) {
        this.socket = socket;
        this.messageFactory = messageFactory;
    }

    @Override
    public void run() {
        LOGGER.info("Thread Started for {}", socket.getInetAddress());

        try (InputStream inputStream = socket.getInputStream()) {

            InetAddress inetAddress = socket.getInetAddress();
            int port = socket.getPort();

            InetSocketAddress socketAddr = new InetSocketAddress(inetAddress, port);

            messageFactory.read(inputStream, socketAddr);

            LOGGER.info("Connection Processing Finished.");

        } catch (IOException e) {

            LOGGER.info("Problem with communication", e);

        } finally {
            if (socket != null) {
                try {
                    socket.close();

                    LOGGER.info("Socket closed for " + socket.getInetAddress());
                } catch (IOException e) {
                    LOGGER.error("Error closing socket", e);
                }
            }
        }
    }
}
