package org.cxxii.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;


public class PongMessageParser implements MessageParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(PongMessageParser.class);
    @Override
    public PongMessage parse(byte[] header, byte[] payload, InetSocketAddress addr) throws IOException {
        LOGGER.info("PONG Parse start...");
        byte[] messageId = Arrays.copyOfRange(header, 0, 16);
        byte typeId = header[16];
        byte timeToLive = header[17];
        byte hops = header[18];
        byte payloadLength = ByteBuffer.wrap(Arrays.copyOfRange(header, 19, 23)).get();

        int portNum = ByteBuffer.wrap(Arrays.copyOfRange(payload, 0, 2)).getShort();
        String ipAddress = InetAddress.getByAddress(Arrays.copyOfRange(payload, 2, 6)).getHostAddress();
        byte sharedFiles = ByteBuffer.wrap(Arrays.copyOfRange(payload, 6, 10)).get();
        int kilobytesShared = ByteBuffer.wrap(Arrays.copyOfRange(payload, 10, 14)).getInt();

        PongMessage pong = new PongMessage(timeToLive, hops, ipAddress, portNum, sharedFiles, kilobytesShared, payloadLength);

        LOGGER.debug("PONG Parse finish!");



        return pong.process(addr);

    }
}