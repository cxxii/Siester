package org.cxxii.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

public class OldMessageParser_Delete {

    private final static Logger LOGGER = LoggerFactory.getLogger(OldMessageParser_Delete.class);

    public static void parse(InputStream inputStream, OutputStream outputStream) throws IOException {

        LOGGER.info("Parsing Message");

        // PARSE HEADER
        byte[] header = inputStream.readNBytes(23);
        ByteBuffer headerBuffer = ByteBuffer.wrap(header);

        // Field Extraction
        byte[] messageIDBytes = new byte[16];
        headerBuffer.get(messageIDBytes);
        UUID messageID; //maybe method to turn bytes back into uuid

        byte payloadDescriptor = headerBuffer.get();
        byte timeToLive = headerBuffer.get();
        byte hops = headerBuffer.get();
        int payloadLength = headerBuffer.getInt();


        switch (payloadDescriptor) {
            case 0x00: // Ping message received
                LOGGER.info("Ping Received");
//                Pong.processPingMessage(messageIDBytes, timeToLive, hops, payloadLength);
//                processPingMessage(messageIDBytes);

//                Pong pongResponse = processPingMessage(messageIDBytes, timeToLive, hops);
//                sendPongResponse(pongResponse, outputStream);
                break;
//            case 0x01:
//                // Pong message
//                Pong.processPongMessage(messageID, ttl, hops, payload);
//                processPongMessage(messageIDBytes);
//                break;
//            case 0x80:
//                // Query message
//                processQueryMessage(messageID, ttl, hops, payload);
//                break;
//            case 0x81:
//                // Query Hit message
//                processQueryHitMessage(messageID, ttl, hops, payload);
//                break;
            default:
                LOGGER.warn("Unknown message type: " + payloadDescriptor);
        }
    }

    private static Pong processPingMessage(byte[] messageIDBytes) {

        return new Pong(messageIDBytes, (byte) 0x01, (byte) 0x00);
    }

    private static void sendPongResponse(Pong pong, OutputStream outputStream) throws IOException {
        byte[] pongBytes = pong.serializeMessage();
        outputStream.write(pongBytes);
        outputStream.flush();
        LOGGER.info("Pong Sent");
    }
}
