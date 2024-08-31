package org.cxxii.messages;

import com.fasterxml.jackson.databind.JsonNode;
import org.cxxii.server.Server;
import org.cxxii.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static org.cxxii.messages.PongMessage.readPongHitFile;

public class Bootstrap {
    private static final String bootstrapServerUrl = "http://192.168.1.22:4545/bootstrap";
    private static final String bootstrapHitsUrl = "http://192.168.1.22:4545/hits";

    //private static final String bootstrapServerUrl = "http://localhost:4545/bootstrap";

    private final static Logger LOGGER = LoggerFactory.getLogger(Bootstrap.class);
    private static int retries = 3;

    // TODO - Allow manual entry of pongcache if bootstrap is down

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

                // TODO - if 'servent' is reset with an complete cache it currently will not fulfill it
                if (inputStream.available() == 0) {
                    LOGGER.warn("Bootstrap hosts empty"); //

                    while (retries > 0) {

                        LOGGER.warn("Repinging BOOTSTRAP"); //
                        pingBootstrapServer();
                        retries--;

                    }

                } else {
                    LOGGER.info("Received hosts from bootstrap server");
                    FileManager.writeHostsToFile(inputStream);
                    LOGGER.info(inputStream.toString());
                }
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
    }

    public static void sendHitsToBootstrap() {
        LOGGER.info("Sending hit after hit");

        HttpURLConnection connection = null;

        try {
            URL url = new URL(bootstrapHitsUrl);
            LOGGER.info(url.toString());

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);

            OutputStream outputStream = connection.getOutputStream();



            JsonNode pongHitJson = readPongHitFile();
            if (pongHitJson != null) {
                LOGGER.debug("Pong Hit ");
                System.out.println(pongHitJson.toString());
               byte[] input = pongHitJson.toString().getBytes("UTF-8");
                //byte[] input = jsonText.getBytes();
                outputStream.write(input, 0, input.length);
            } else {
                LOGGER.warn("No pong hit data available to send");
            }
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                LOGGER.info("Successfully sent hits to bootstrap server");
            } else {
                LOGGER.warn("Failed to send hits to bootstrap server, response code: {}", responseCode);
            }

        } catch (IOException e) {
            LOGGER.error("Error sending hits to bootstrap server", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
