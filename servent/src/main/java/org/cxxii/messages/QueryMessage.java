package org.cxxii.messages;

import org.cxxii.network.Network;
import org.cxxii.server.SocketAddr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class QueryMessage extends MessageAbstract{

    // LOGGER
    private final static Logger LOGGER = LoggerFactory.getLogger(QueryMessage.class);

    // CONSTANTS

    // private final UUID MESSAGE_ID = UUID.randomUUID(); // this is in the constructor - delete?
    private static final byte TYPE_ID = (byte) 0x80;

    public static final int HEADER_LENGTH = 23;

    //private static final byte[] PAYLOAD = null;


    // INSTANCE
    //private byte PAYLOAD_LENGTH = 0x00000000;
    private byte payloadLength;
    private byte timeToLive = (byte) 0x07;
    private byte hops = (byte) 0x00;


    private String query;
    private int minSpeed;


    public void setPayloadLength(byte payloadLength) {
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

    public QueryMessage(byte timeToLive, byte hops, byte payloadLength, int minSpeed, String query) {
        super(TYPE_ID, (byte) 0x07, (byte) 0x00, payloadLength);
        this.payloadLength = payloadLength;
        this.timeToLive = timeToLive;
        this.hops = hops;
        this.minSpeed = minSpeed;
        this.query = query;
    }

    public QueryMessage(byte payloadLength, String query) {
        super(TYPE_ID, (byte) 0x07, (byte) 0x00, payloadLength);
        this.payloadLength = payloadLength;
        this.minSpeed = 0;
        this.query = query;
    }


    @Override
    public MessageAbstract parse(byte[] header, byte[] payload, InetSocketAddress addr) throws IOException {
        return null;
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



    public byte[] serializeMessage() throws IOException {
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

            LOGGER.info("QUERY: " + queryMessage + " sent to: " + host.getIp() + ":" + host.getPort());

        } catch (IOException e) {
            LOGGER.error("FAILED to send QUERY to: " + host.getIp() + ":" + host.getPort(), e);
        }
    }



    public static String getUsersQuery(){
        Scanner scanner = new Scanner(System.in);

        return scanner.nextLine();
    }

    public static void makeQuery() {

        List<SocketAddr> hosts = PingMessage.readHostCache(); // change

        String qq = getUsersQuery();

        byte pylen = (byte) getPayloadLength(qq);

        QueryMessage queryMessage = new QueryMessage(pylen, qq);

        for (SocketAddr host : hosts) {
            sendQuery(host, queryMessage);
        }
    }
}
