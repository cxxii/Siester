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

    //private static final String bootstrapServerUrl = "http://127.0.0.1:4545/gnutella/get_peers";
    private static final String bootstrapServerUrl = "http://192.168.1.22:4545/bootstrap";

    private final static Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);

    public static void pingBootstrapServer() throws IOException {
        LOGGER.info("Pinging Bootstrap Server");

        URL url = new URL(bootstrapServerUrl);
        HttpURLConnection connection = null;
        InputStream inputStream = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                // check if stream is empty
                inputStream = connection.getInputStream();

//                System.out.println(inputStream.available());

                // TODO - Finish this part
                if (inputStream.available() == 0) {
                    LOGGER.warn("Bootstrap hosts empty"); //

                } else {
                    LOGGER.info("Received hosts from bootstrap server");
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
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("ERROR closing stream", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        LOGGER.info("Contine");
    }
}
