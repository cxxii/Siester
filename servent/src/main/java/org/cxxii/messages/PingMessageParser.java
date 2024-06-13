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
    @Override
    public MessageAbstract parse(byte[] header, byte[] payload, InetSocketAddress addr) throws IOException {


        LOGGER.info("Parse start");
        byte[] id = Arrays.copyOfRange(header,0,15);
        byte typeId = header[16];
        byte ttl = header[17];
        byte hops = header[18];
        int payloadLength = ByteBuffer.wrap(Arrays.copyOfRange(header,19,23)).getInt();

        LOGGER.debug("ID of ping received " + Arrays.toString(id));

        PingMessage ping = new PingMessage(id, typeId, ttl, hops, payloadLength, payload);



        ping.process(id, typeId, ttl, hops, payloadLength, addr);





        //delete below just to make run need to fix - shouldnt need to return anything here just pass to method -- change to void in this method and interface
        MessageAbstract delete_me = new PingMessage();
        return delete_me;
    }
}

