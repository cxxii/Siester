package org.cxxii.messages;

import org.cxxii.json.FileResults;
import org.cxxii.network.Network;
import org.cxxii.server.SocketAddr;
import org.cxxii.utils.FileManager;
import org.cxxii.utils.HostCacheReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static org.cxxii.network.Network.getLocalIpAddress;
import static org.cxxii.search.FuzzySearch.fuzzySearchFiles;

public class QueryMessage extends MessageAbstract {

    private final static Logger LOGGER = LoggerFactory.getLogger(QueryMessage.class);

    private static final byte TYPE_ID = (byte) 0x80;
    public static final int HEADER_LENGTH = 23;

    //private byte PAYLOAD_LENGTH = 0x00000000;
    private int payloadLength;
    private byte timeToLive = (byte) 0x07;
    private byte hops = (byte) 0x00;


    private String query;
    private byte[] queryBytes;
    private int minSpeed;


    public void setPayloadLength(int payloadLength) {
        this.payloadLength = payloadLength;
    }

    @Override
    public byte getTimeToLive() {
        return timeToLive;
    }

    @Override
    public void setTimeToLive(byte timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public byte getHops() {
        return hops;
    }

    @Override
    public void setHops(byte hops) {
        this.hops = hops;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(int minSpeed) {
        this.minSpeed = minSpeed;
    }

    // CONSTRUCTOR

//    public QueryMessage(byte timeToLive, byte hops, byte payloadLength, int minSpeed, String query) {
//        super(TYPE_ID, (byte) 0x07, (byte) 0x00, payloadLength);
//        this.payloadLength = payloadLength;
//        this.timeToLive = timeToLive;
//        this.hops = hops;
//        this.minSpeed = minSpeed;
//        this.query = query;
//    }


    // RECEIVED QUERY (PARSE)
    public QueryMessage(byte[] bytesMessageID, byte typeId, byte timeToLive, byte hops, int payloadLength, byte minSpeed, byte[] queryBytes) {
        super(bytesMessageID, typeId, timeToLive, hops, (byte) payloadLength);
        this.payloadLength = payloadLength;
        this.timeToLive = timeToLive;
        this.hops = hops;
        this.queryBytes = queryBytes;
        this.minSpeed = minSpeed;
    }

    public QueryMessage(byte payloadLength, String query) {
        super(TYPE_ID, (byte) 0x07, (byte) 0x00, payloadLength);
        this.payloadLength = payloadLength;
        this.minSpeed = 0; // fix
        this.query = query;
    }


    private byte[] UUIDtoByteArray(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    private static int getPayloadLength(String query) {

        byte[] queryByteArray = query.getBytes();

        return queryByteArray.length;
    }

    private byte[] serializeMessage() throws IOException {
        if (this.getPayloadLength(this.query) == 0) {
            throw new NullPointerException("Query is null");
        }

        ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH + getPayloadLength(this.query)); // 23 + 13
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.put(UUIDtoByteArray(this.getMessageID()));

        buffer.put(TYPE_ID);

        buffer.put(this.getTimeToLive());

        buffer.put(this.getHops());

        buffer.putInt(getPayloadLength(this.query)); // change

        buffer.put(this.query.getBytes());

        return buffer.array();
    }


    private static void sendQuery(SocketAddr host, QueryMessage queryMessage) {

        try (Socket socket = new Socket(host.getIp(), host.getPort());
             OutputStream outputStream = socket.getOutputStream()) {

            byte[] serializedQuery = queryMessage.serializeMessage();

            outputStream.write(serializedQuery);
            outputStream.flush();

            System.out.println("QUERY: " + queryMessage + " sent to: " + host.getIp() + ":" + host.getPort());

            LOGGER.info("QUERY: " + queryMessage + " sent to: " + host.getIp() + ":" + host.getPort());

        } catch (IOException e) {
            LOGGER.error("FAILED to send QUERY to: " + host.getIp() + ":" + host.getPort(), e);
        }
    }


    private static String getUsersQuery(Scanner scanner) {

        System.out.println("Enter your search query");

        return scanner.nextLine();
    }

    public static void makeQuery(Scanner scanner) {

        List<SocketAddr> hosts = HostCacheReader.readHostCache();

        String usersQuery = getUsersQuery(scanner);

        byte queryLength = (byte) getPayloadLength(usersQuery);

        QueryMessage queryMessage = new QueryMessage(queryLength, usersQuery);

        for (SocketAddr host : hosts) {
            sendQuery(host, queryMessage);
}
    }


    public QueryMessage process(InetSocketAddress addr) throws IOException {
        LOGGER.info("Processing QUERY");

        // Update ttl and hops
        if (this.getTimeToLive() != 0) {
            ttlAndHopsIncrementor(this);
            // query on
            // query onward logic if any
        }

        //SEARCH
        List<FileResults> matchedFiles = fuzzySearchFiles(new String(this.queryBytes, StandardCharsets.UTF_8));

        LOGGER.info("QUERY PROCESS");

        LOGGER.debug("IPIPIPIPIP");
        byte[] localIpAddressBytes = Network.getLocalIpAddress();
        InetAddress ipip = InetAddress.getByAddress(localIpAddressBytes);

        if (!matchedFiles.isEmpty()) {
            LOGGER.debug("file found");

            QueryHitMessage.Builder builder = new QueryHitMessage.Builder()
                    .withMessageId(this.bytesMessageID)
                    .withTimeToLive(this.getTimeToLive())
                    .withHops(this.getHops())
                    .withNumberOfHits(matchedFiles.size())
                    .withPort(6364)
                    .withIpAddress(ipip.getHostAddress())// ???
                    .withSpeed(22222); // hook up later

            LOGGER.debug("QM matchesfiles " + matchedFiles.size());

            int index = 1;
            for (FileResults result: matchedFiles) {
                LOGGER.debug("num matches" + String.valueOf(matchedFiles.size()));
                LOGGER.debug("RESULT" + String.valueOf(index));
                QueryHitMessage.Result result1 = new QueryHitMessage.Result(index, result.getFilename(), result.getFilesize(), result.getFileType());
                builder.addResult(result1);
                index++;
            }

            QueryHitMessage message = builder.build();

            QueryHitMessage.sendQueryHit(addr, message);


        } else {

            System.out.println("No files found.");
        }

        return this;
    }
}

