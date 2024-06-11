package org.cxxii.messages;

import org.cxxii.server.Server;
import org.cxxii.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Bootstrap {

    private static final String bootstrapServerUrl = "http://127.0.0.1:4545/gnutella/get_peers";

    private final static Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    public static void pingBootstrapServer() throws IOException {
        URL url = new URL(bootstrapServerUrl);
//        HttpURLConnection connection = null;
//        Scanner scanner = null;

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
//            connection.setConnectTimeout(5000); // Set timeout for connection
//            connection.setReadTimeout(5000); // Set timeout for reading data

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                // check if stream is empty

                InputStream inputStream = connection.getInputStream();

                System.out.println(inputStream.available());

                // TODO - Finish this part
                if (inputStream.available() == 0) {
                    LOGGER.warn("Bootstrap hosts empty"); //

                } else {
                    LOGGER.info("Received hosts from bootstrap server");
//                    FileManager.writeFile(inputStream);
                    FileManager.writeHostsToFile(inputStream);
                }


//                Scanner scanner = new Scanner(connection.getInputStream());
//                StringBuilder response = new StringBuilder();
//                while (scanner.hasNextLine()) {
//                    response.append(scanner.nextLine());
//                }
//
//                System.out.println(response.toString());
////                return response.toString();
            } else {
                throw new IOException("Failed to ping bootstrap server. Response code: " + responseCode);
            }
        } catch (IOException e) {
            LOGGER.error("Error pinging bootstrap server", e);
            throw e;
        }
    }
}
