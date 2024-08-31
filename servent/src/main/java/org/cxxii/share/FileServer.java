package org.cxxii.share;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.cxxii.utils.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class FileServer {
    public static class FileHandler implements HttpHandler {
        private final String directory;

        public FileHandler(String directory) {
            this.directory = directory;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String filePath = directory + exchange.getRequestURI().getPath();

            File file = new File(filePath);
            if (file.exists() && !file.isDirectory()) {
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                exchange.sendResponseHeaders(200, fileBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(fileBytes);
                os.close();
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        }
    }
}
