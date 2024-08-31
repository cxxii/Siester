package org.cxxii.share;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.cxxii.json.FileResults;
import org.cxxii.messages.QueryHitMessage;
import org.cxxii.server.Server;
import org.cxxii.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;





public class FileDownloader {

    private final static Logger LOGGER = LoggerFactory.getLogger(FileDownloader.class);

    public static void downloadFile(String url, String localFilename) throws IOException {
        System.out.println("DOWNLOADING FILE");
        System.out.println(url);

        String name = getFileNameFromUrl(localFilename);

        String here = FileManager.getDownloadDirPath().toString() + "/" + name;

        long startTime = System.currentTimeMillis();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (InputStream inputStream = entity.getContent();
                         OutputStream outputStream = new FileOutputStream(here)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    EntityUtils.consume(entity);
                }
            }
        }

        long endTime = System.currentTimeMillis();

        LOGGER.info("START TIME" + String.valueOf(startTime));
        LOGGER.info("END TIME" + String.valueOf(endTime));
        LOGGER.info("DURATION" + String.valueOf(endTime - startTime));

        System.out.println("Download completed in " + (endTime - startTime) + " milliseconds.");

        System.out.println("DOWNLOADED...");
    }

    private static String sanitizeFileName(String fileName) {
        // Replace invalid characters with underscores or other valid characters
        return fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    public static String getFileNameFromUrl(String url) {
        // Find the position of the last slash
        int lastSlashIndex = url.lastIndexOf('/');

        // Extract and return the substring after the last slash
        if (lastSlashIndex >= 0 && lastSlashIndex < url.length() - 1) {
            return url.substring(lastSlashIndex + 1);
        } else {
            // Handle the case where there are no slashes or the URL is empty
            return url;
        }
    }
}