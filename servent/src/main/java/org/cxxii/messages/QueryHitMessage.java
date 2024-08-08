package org.cxxii.messages;

import com.sun.net.httpserver.HttpServer;
import org.cxxii.share.FileDownloader;
import org.cxxii.share.FileServer;
import org.cxxii.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;



public class QueryHitMessage extends MessageAbstract {

    private final static Logger LOGGER = LoggerFactory.getLogger(QueryHitMessage.class);


    private static final int HEADER_LENGTH = 23;

    // HEADER
    private static final byte TYPE_ID = (byte) 0x81;

    private static int payloadLength;
    private byte[] messageID;
    private byte timeToLive;
    private byte hops;


    // PAYLOAD
    public byte numberOfHits;
    private short portNum;
    private byte[] ipAddress;
    private int speed;
    public List<Result> resultList;

    private QueryHitMessage(Builder builder) {
        this.messageID = UUIDtoByteArray(UUID.randomUUID());
        this.timeToLive = builder.timeToLive;
        this.hops = builder.hops;
        this.numberOfHits = (byte) builder.numberOfHits;
        this.portNum = (short) builder.portNum;
        this.ipAddress = builder.ipAddress;
        this.speed = builder.speed;
        this.resultList = builder.results;
        this.payloadLength = calculatePayloadLength();
    }

    private int calculatePayloadLength() {
        LOGGER.debug("Calculating Payload Length");
        int resultsLength = 0;
        for (Result result : resultList) {
            LOGGER.debug("Result Length for filenamee: " + result.fileName + " = " + result.fileName.length());
            resultsLength += 4 + 4 + result.fileName.getBytes(StandardCharsets.UTF_8).length + 1; // 4fileIndex, 4fileSize, file name length, +1 for null terminator

            LOGGER.debug("rez total len" + resultsLength);
            LOGGER.debug("rez filename len: " + result.fileName.getBytes(StandardCharsets.UTF_8).length);
        }
        LOGGER.debug("Total results length: " + resultsLength);
        return 1 + 2 + 4 + 4 + resultsLength; // 1 byte for numberOfHits, 2 bytes for portNum, 4 bytes for IP, 4 bytes for speed, and results length
    }

    private static byte[] UUIDtoByteArray(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    public static class Builder {
        private byte[] messageId;
        private byte timeToLive;
        private byte hops;
        private int numberOfHits;
        private int portNum;
        private byte[] ipAddress;
        private int speed;
        private List<Result> results = new ArrayList<>();

        public Builder withMessageId(byte[] messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder withTimeToLive(byte timeToLive) {
            this.timeToLive = timeToLive;
            return this;
        }

        public Builder withHops(byte hops) {
            this.hops = hops;
            return this;
        }

        public Builder withNumberOfHits(int numberOfHits) {
            this.numberOfHits = numberOfHits;
            return this;
        }

        public Builder withPort(int portNum) {
            this.portNum = portNum;
            return this;
        }

        public Builder withIpAddress(String ipAddress) throws UnknownHostException {
            this.ipAddress = InetAddress.getByName(ipAddress).getAddress();
            return this;
        }

        public Builder withSpeed(int speed) {
            this.speed = speed;
            return this;
        }

        public Builder addResult(Result result) {
            this.results.add(result);
            return this;
        }

        public Builder withResults(List<Result> resultSet) {
            this.results = resultSet;
            return this;
        }

        public QueryHitMessage build() {
            return new QueryHitMessage(this);
        }


    }


    public static class Result {
        private int fileIndex;
        private int fileSize;
        private String fileName;

        private String fileType;

        public Result(int fileIndex, String fileName, int fileSize, String fileType) {
            this.fileIndex = fileIndex;
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.fileType = fileType;
        }


        public int getFileIndex() {
            return fileIndex;
        }

        public int getFileSize() {
            return fileSize;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileType() {
            return fileType;
        }
    }

    private static void startServer() throws IOException {
        LOGGER.debug("Starting Download Server");
        HttpServer server = HttpServer.create(new InetSocketAddress(6699), 0);
        LOGGER.debug("DL SERVER RUNNING AT");
        LOGGER.debug(String.valueOf(server.getAddress()));

        server.createContext("/", new FileServer.FileHandler(FileManager.getUploadDirPath().toString()));
        server.setExecutor(null); // creates a default executor
        server.start();
        System.out.println("Server started on port " + 6699);
    }


    public static void sendQueryHit(InetSocketAddress addr, QueryHitMessage message) {
        //LOGGER.debug("QUERY HIT - SQH");

        try (Socket socket = new Socket(addr.getAddress(), 6364);
             OutputStream outputStream = socket.getOutputStream()) {

            byte[] serializedQueryHit = serializeMessage(message);

            outputStream.write(serializedQueryHit);

            outputStream.flush();

            LOGGER.info("Query Hit Sent: " + Arrays.toString(serializedQueryHit) + " Destination: " + addr.getAddress());

            startServer();

        } catch (IOException e) {
            LOGGER.error("FAILED to send QueryHit to: " + addr.getHostString() + ":" + addr.getPort(), e);
        }
    }


    private static byte[] serializeMessage(QueryHitMessage message) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH + message.calculatePayloadLength());
        buffer.order(ByteOrder.BIG_ENDIAN);
        LOGGER.debug("BUFFER " + buffer.remaining());

        buffer.put(message.messageID);
        LOGGER.debug("Serialized messageID: " + Arrays.toString(message.messageID));

        buffer.put(TYPE_ID);
        LOGGER.debug("Serialized TYPE_ID: " + TYPE_ID);

        buffer.put(message.timeToLive);
        LOGGER.debug("Serialized timeToLive: " + message.timeToLive);

        buffer.put(message.hops);
        LOGGER.debug("Serialized hops: " + message.hops);

        buffer.putInt(message.payloadLength);
        LOGGER.debug("Serialized payloadLength: " + message.payloadLength);

        buffer.put(message.numberOfHits);
        LOGGER.debug("Serialized numberOfHits: " + message.numberOfHits);

        buffer.putShort(message.portNum);
        LOGGER.debug("Serialized portNum: " + message.portNum);

        buffer.put(message.ipAddress);
        LOGGER.debug("Serialized ipAddress: " + Arrays.toString(message.ipAddress));

        buffer.putInt(message.speed);
        LOGGER.debug("Serialized speed: " + message.speed);

        for (Result result : message.resultList) {
            buffer.putInt(result.fileIndex);
            LOGGER.debug("Serialized fileIndex: " + result.fileIndex);

            buffer.putInt(result.fileSize);
            LOGGER.debug("Serialized fileSize: " + result.fileSize);

            byte[] fileNameBytes = result.fileName.getBytes(StandardCharsets.UTF_8);
            buffer.put(fileNameBytes);
            LOGGER.debug("Serialized fileName 01" + Arrays.toString(fileNameBytes));
            LOGGER.debug("Serialized fileName: " + result.fileName);

            buffer.put((byte) 0x00);  // Null terminator for the filename
            LOGGER.debug("Serialized fileName null terminator");
        }

        LOGGER.debug(String.valueOf(buffer));


        return buffer.array();
    }

    public List<String> fileName() throws UnknownHostException {

        List<String> listRez = new ArrayList<>();

        for (Result result : this.resultList) {
            System.out.println("filename = " + result.fileName);
            System.out.println("filesize = " + result.fileSize);
            System.out.println("from = " + InetAddress.getByAddress(this.ipAddress).getHostAddress() + ":" + this.portNum);
            System.out.println("\n");


            String url = "http://" + InetAddress.getByAddress(this.ipAddress).getHostAddress() + ":" + 6699 + "/" + result.fileName;

            listRez.add(url);
        }

        return listRez;
    }

    public QueryHitMessage process() throws IOException {
        List<String> listRez = new ArrayList<>();
        int index = 0;

        for (Result result : this.resultList) {
            System.out.println("index = " + index);
            System.out.println("filename = " + result.fileName);
            System.out.println("filesize = " + result.fileSize);
            System.out.println("from = " + InetAddress.getByAddress(this.ipAddress).getHostAddress() + ":" + this.portNum);
            System.out.println("\n");

            index++;

            String url = "http://" + InetAddress.getByAddress(this.ipAddress).getHostAddress() + ":" + 6699 + "/" + result.fileName;

            listRez.add(url);
        }

//        Scanner scanner = ScannerSingleton.getInstance();
//        int fileIndex = -1;
//
////        while (true) {
//            System.out.println("Index of file you wish to download... ");
//            try {
//                System.out.println("1111");
//                System.out.println(Thread.currentThread());
//                if (scanner.hasNext()) {
//                    System.out.println(Thread.currentThread());
//                    System.out.println("22222");
//                    if (scanner.hasNextInt()) {
//                        fileIndex = scanner.nextInt();
//                        scanner.nextLine(); // consume newline character
//                        if (fileIndex >= 0 && fileIndex < listRez.size()) {
//                            System.out.println("GOOD");
//
//                        } else {
//                            System.out.println("Invalid index. Please enter a number between 0 and " + (listRez.size() - 1) + ".");
//                        }
//
//                    } else {
//                        System.out.println("Invalid input. Please enter a valid integer.");
//                        scanner.next(); // clear the invalid input
//                    }
//
//                } else {
//                    System.out.println("3333331");
//                }
//            } catch (Exception e) {
//                System.out.println("An error occurred while reading input. Please try again.");
//                scanner.next(); // clear the invalid input
//            }
//        }

//        String downloadUrl = listRez.get(fileIndex);
//        String outputFilePath = FileManager.getDownloadDirPath().toString() + "/" + resultList.get(fileIndex).fileName;
//        System.out.println("DOWNLOADING FILE\n" + downloadUrl);
        //FileDownloader.downloadFile(downloadUrl, outputFilePath);

        return this;
    }
    // cd ~/siester/shared/upload/ && echo "fuxksdhd" > nelly.txt && cd ~/siester/logs && > application.log
}

