import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;



// Will not implement
public class NetworkSpeedTester {

    private static final String TEST_URL = "http://example.com/speedtest"; // replace with valid server
    private static final int TEST_FILE_SIZE = 1024 * 1024; //test file

    public static int getNetworkSpeed() {
        try {
            URL url = new URL(TEST_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            long startTime = System.currentTimeMillis();

            InputStream inputStream = connection.getInputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            int totalBytesRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                totalBytesRead += bytesRead;
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime; //millis

            inputStream.close();
            connection.disconnect();

            // Convert to kbps
            double speedKbps = (totalBytesRead / 1024.0) / (duration / 1000.0);

            return (int) speedKbps;

        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
