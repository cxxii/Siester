package org.cxxii.server;

import org.cxxii.messages.MessageFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListenerThread extends Thread {
    private final static Logger LOGGER = LoggerFactory.getLogger(ServerListenerThread.class);

    private int port;
    private String webRoot;
    ServerSocket serverSocket;
    private final MessageFactoryImpl messageFactory;

    public ServerListenerThread(int port, String webRoot, MessageFactoryImpl messageFactory) throws IOException {
        this.port = port;
        this.webRoot = webRoot;
        this.serverSocket = new ServerSocket(this.port);
        this.messageFactory = messageFactory;
    }

    @Override
    public void run() {

        try {

            while (serverSocket.isBound() && !serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();

                LOGGER.info(" * Connection Accepted: " + socket.getInetAddress());

                ConnectionWorkerThread workerThread = new ConnectionWorkerThread(socket, messageFactory);
                workerThread.start();


            }
        } catch (IOException e) {
            LOGGER.info("Problem with setting socket", e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {}
            }
        }
    }
}