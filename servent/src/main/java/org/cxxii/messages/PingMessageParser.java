package org.cxxii.messages;

import org.cxxii.server.ConnectionWorkerThread;
import org.cxxii.server.SocketAddr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PingMessageParser implements MessageParser {

    private final static Logger LOGGER = LoggerFactory.getLogger(PingMessageParser.class);



    // dont technically need a payload in ping parser

    @Override
    public PingMessage parse(byte[] header, byte[] payload, InetSocketAddress addr) throws IOException {


        LOGGER.info("Parse start");
        byte[] messageId = Arrays.copyOfRange(header, 0, 15);
        byte typeId = header[16];
        byte timeToLive = header[17];
        byte hops = header[18];
        byte payloadLength = ByteBuffer.wrap(Arrays.copyOfRange(header, 19, 23)).get();

        LOGGER.debug("ID of ping received " + Arrays.toString(messageId));

        PingMessage ping = new PingMessage(messageId, typeId, timeToLive, hops, payloadLength);

        return ping.process(addr);
    }
}

