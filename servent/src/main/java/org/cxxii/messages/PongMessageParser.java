package org.cxxii.messages;

import org.cxxii.server.SocketAddr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;


public class PongMessageParser implements MessageParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(PongMessageParser.class);
    @Override
    public PongMessage parse(byte[] header, byte[] payload, InetSocketAddress addr) throws IOException {

        LOGGER.debug("PONG PARSE START");
        byte timeToLive = header[1];
        byte hops = header[2];
        byte payloadLength = header[3];

        String ipAddress = "127.0.0.1";
        int portNum = 12345;
        byte sharedFiles = (byte) 10;
        int kilobytesShared = 2048;

        PongMessage pong = new PongMessage(timeToLive, hops, ipAddress, portNum, sharedFiles, kilobytesShared, payloadLength);

        return pong.process(addr);
    }
}
