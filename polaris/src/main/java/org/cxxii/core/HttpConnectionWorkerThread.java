package org.cxxii.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpConnectionWorkerThread extends Thread {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpConnectionWorkerThread.class);
    private Socket socket;

    public HttpConnectionWorkerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        OutputStream outputStream = null;
        InputStream inputStream = null;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();


            //TODO we would read

            //TODO Do Writing


            String html = "<html><head><title>Server Ting</title></head><body><h2>Fuck</h2></body></html>";

            final String CRLF = "\r\n"; // 13,10

            String response =
                    "HTTP/1.1 200K" + CRLF + //status Line : HTTP VERSION RESPONSE_CODE RESPONSE_MESSAGE
                            "Content-Length: " + html.getBytes().length + CRLF + //header
                            CRLF +
                            html +
                            CRLF + CRLF;

            outputStream.write(response.getBytes());

            LOGGER.info("Connection Processing Finished.");

        } catch (IOException e) {
            LOGGER.info("Problem with communication", e);

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {}
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {}
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {}
            }
        }
    }
}
