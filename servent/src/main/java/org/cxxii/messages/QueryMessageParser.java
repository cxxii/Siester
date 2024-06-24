package org.cxxii.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class QueryMessageParser implements MessageParser{


    //LOGGER
    private final static Logger LOGGER = LoggerFactory.getLogger(QueryMessageParser.class);



    // remove  byte arrays
    @Override
    public QueryMessage parse(byte[] header, byte[] payload, InetSocketAddress addr) throws IOException {

        LOGGER.info("QUERY PARSE START");

        byte[] messageId = Arrays.copyOfRange(header, 0,15);
        byte typeID = header[16];
        byte timeToLive = header[17];
        byte hops = header[18];
        int payloadLength = ByteBuffer.wrap(Arrays.copyOfRange(header, 19, 23)).getInt();
        byte minSpeed = payload[0];

        byte[] queryBytes =  Arrays.copyOfRange(payload, 0, payloadLength);

        LOGGER.info("ID of QUERY received " + Arrays.toString(messageId));

        QueryMessage queryMessage = new QueryMessage(messageId, typeID, timeToLive, hops, payloadLength, minSpeed, queryBytes);


        return queryMessage.process(addr);

    }
}
