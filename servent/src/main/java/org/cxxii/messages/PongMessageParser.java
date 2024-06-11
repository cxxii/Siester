package org.cxxii.messages;

import org.cxxii.server.SocketAddr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class PongMessageParser implements MessageParser {
    private final static Logger LOGGER = LoggerFactory.getLogger(PongMessageParser.class);
    @Override
    public MessageAbstract parse(byte[] header, byte[] payload, InetSocketAddress addr) throws IOException {
        LOGGER.debug("PONG PARSE START");
        byte ttl = header[1];
        byte hops = header[2];
        int payloadLength = header[3];

        String ipAddress = "127.0.0.1"; // Example static IP address, should parse from payload
        int portNum = 12345; // Example static port number, should parse from payload
        byte sharedFiles = (byte) 10; // Example static number of shared files, should parse from payload
        int kilobytesShared = 2048; // Example static kilobytes shared, should parse from payload

        return new PongMessage(ttl, hops, ipAddress, portNum, sharedFiles, kilobytesShared, payloadLength);
    }
}