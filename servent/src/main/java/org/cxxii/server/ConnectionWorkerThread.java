package org.cxxii.server;

import jdk.jfr.BooleanFlag;
import org.cxxii.messages.MessageAbstract;
import org.cxxii.messages.MessageFactoryImpl;
import org.cxxii.messages.PingMessage;
import org.cxxii.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


// Clean unnecessary shit
public class ConnectionWorkerThread extends Thread {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConnectionWorkerThread.class);
    private Socket socket;
    private final MessageFactoryImpl messageFactory;

    public ConnectionWorkerThread(Socket socket, MessageFactoryImpl messageFactory) {
        this.socket = socket;
        this.messageFactory = messageFactory;
    }

    @Override
    public void run() {

        OutputStream outputStream = null;
        InputStream inputStream = null;



        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            LOGGER.info("Thread Started for " + socket.getInetAddress());

            InetAddress inetAddress = this.socket.getInetAddress();
            int port = this.socket.getPort();

            InetSocketAddress socketAddr = new InetSocketAddress(inetAddress, port);

            MessageAbstract message = messageFactory.read(inputStream, socketAddr);


            //TODO we would read

            //TODO Do Writing


            LOGGER.info("Connection Processing Finished.");

        } catch (IOException e) {
            LOGGER.info("Problem with communication", e);

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }

            if (outputStream != null) try {
                outputStream.close();
            } catch (IOException e) {
            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}

