package org.cxxii.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MessageFactoryImpl implements MessageFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageFactoryImpl.class);

    private final static int HEADER_LENGTH = 23;
    private final static int EOF = -1;

    private final Map<Byte, MessageParser> parsers = new HashMap<>();

    public void setParser(byte functionId, MessageParser parser) {
        LOGGER.debug("Parser set " + functionId);
        if (parser == null) throw new NullPointerException("Msg parser is null");
        parsers.put(functionId, parser);
    }


    public MessageAbstract read(InputStream in, InetSocketAddress addr) throws IOException {
        LOGGER.info("Message received!");
        LOGGER.info("Reading Start...");
        byte[] header = new byte[HEADER_LENGTH];

        int bytesRead = 0;

        while (bytesRead < HEADER_LENGTH) {
            int result = in.read(header, bytesRead, HEADER_LENGTH - bytesRead);
            if (result == EOF) {
                throw new IOException("Incomplete header");
            }

            bytesRead += result;
        }

        // Read payload length
        byte[] payloadLengthBytes = Arrays.copyOfRange(header, 19, 23);
        LOGGER.debug("Payload len byte[] =" + Arrays.toString(payloadLengthBytes));
        ByteBuffer wrapped = ByteBuffer.wrap(payloadLengthBytes);

        int payloadLength = wrapped.getInt();
        LOGGER.debug("Payload len int = " + payloadLength);

        byte[] payload = new byte[payloadLength];
        int totalBytesRead = 0;

        while (totalBytesRead < payloadLength) {
            int bytesReadPayload = in.read(payload, totalBytesRead, payloadLength - totalBytesRead);
            if (bytesReadPayload == EOF) {
                throw new IOException("Unexpected end of stream while reading payload");
            }

            totalBytesRead += bytesReadPayload;
        }

        byte typeId = header[16];
        MessageParser parser = getParser(typeId);
        if (parser == null) throw new IOException("Unknown type ID: " + typeId);

        return parser.parse(header, payload, addr);
    }



    @Override
    public MessageAbstract createMessage(byte[] header, byte[] payload, SocketAddress addr) throws IOException {
        return null;
    }

    @Override
    public byte[] createHeader(byte[] guid, byte func, byte ttl, byte hops, int payloadLength) {
        return new byte[0];
    }

    //@Override
    public MessageAbstract createMessage(byte[] header, byte[] payload, InetSocketAddress addr) throws IOException {
        byte func = header[16];
        MessageParser parser = getParser(func);
        if (parser == null) throw new IOException("Unknown function ID: " + func);

        return parser.parse(header, payload, addr);
    }

    public MessageParser getParser(byte functionId) {
        return parsers.get(functionId);
    }
}

//    public void getMessage(byte typeID) {
//        if (typeID == (byte) 0x00) {
//            MessageFactory.createMessage()
//        }
//        //
//    }


    //
//    public static MessageAbstract createMessage(String type, byte timeToLive, byte hops, String ipAddress, int port, int numberOfFilesShared, int numberOfKilobytesShared) {
//        switch (type) {
//            case "PING":
//                return new PingMessage(timeToLive, hops);
//            case "PONG":
//                return new PongMessage(timeToLive, hops, ipAddress, port, numberOfFilesShared, numberOfKilobytesShared);
//            // Add cases for other message types as needed
//            default:
//                throw new IllegalArgumentException("Unknown message type: " + type);
//        }
//    }
//
//    public static MessageAbstract createMessage(String type, byte timeToLive, byte hops) {
//        switch (type) {
//            case "PING":
//                return new PingMessage(timeToLive, hops);
//            case "PONG":
//                System.out.println("PONG");
//            // Add cases for other message types as needed
//            default:
//                throw new IllegalArgumentException("Unknown message type: " + type);
//        }
//    }

