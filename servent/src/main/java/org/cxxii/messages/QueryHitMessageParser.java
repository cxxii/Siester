package org.cxxii.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueryHitMessageParser implements MessageParser {

    private final static Logger LOGGER = LoggerFactory.getLogger(QueryHitMessageParser.class);

    @Override
    public QueryHitMessage parse(byte[] header, byte[] payload, InetSocketAddress addr) throws IOException {

        LOGGER.info("QUERY HIT PARSE START");

        byte[] messageId = Arrays.copyOfRange(header, 0, 16);
        LOGGER.debug("Parsed messageId: " + Arrays.toString(messageId));

        byte typeID = header[16];
        LOGGER.debug("Parsed typeID: " + typeID);

        byte timeToLive = header[17];
        LOGGER.debug("Parsed timeToLive: " + timeToLive);

        byte hops = header[18];
        LOGGER.debug("Parsed hops: " + hops);

        int payloadLength = ByteBuffer.wrap(Arrays.copyOfRange(header, 19, 23)).order(ByteOrder.BIG_ENDIAN).getInt();
        LOGGER.debug("Parsed payloadLength: " + payloadLength);

        byte numberOfHits = payload[0];
        LOGGER.debug("Parsed numberOfHits: " + numberOfHits);

        short portNum = ByteBuffer.wrap(Arrays.copyOfRange(payload, 1, 3)).order(ByteOrder.BIG_ENDIAN).getShort();
        LOGGER.debug("Parsed portNum: " + portNum);

        byte[] ipAddressBytes = Arrays.copyOfRange(payload, 3, 7);
        String ipAddress = InetAddress.getByAddress(ipAddressBytes).getHostAddress();
        LOGGER.debug("Parsed ipAddress: " + ipAddress);

        int speed = ByteBuffer.wrap(Arrays.copyOfRange(payload, 7, 11)).order(ByteOrder.BIG_ENDIAN).getInt();
        LOGGER.debug("Parsed speed: " + speed);

        LOGGER.debug("Complete payload: " + Arrays.toString(payload));

        List<QueryHitMessage.Result> resultSet = new ArrayList<>();
        int currentIndex = 11;


        for (int i = 0; i < numberOfHits; i++) {

            int fileIndex = ByteBuffer.wrap(Arrays.copyOfRange(payload, currentIndex, currentIndex + 5)).getInt();
            currentIndex += 4;

            int fileSize = ByteBuffer.wrap(Arrays.copyOfRange(payload, currentIndex, currentIndex + 5)).getInt(); // Ensure correct interpretation of file size as unsigned
            currentIndex += 4;

            int fileNameStartIndex = currentIndex;
            while (payload[currentIndex] != (byte) 0x00) {
                currentIndex++;
            }

            LOGGER.debug("pl2" + payload[currentIndex]);


            String fileName = new String(Arrays.copyOfRange(payload, fileNameStartIndex, currentIndex), StandardCharsets.UTF_8);
            byte[] fn = Arrays.copyOfRange(payload, fileNameStartIndex, currentIndex);

            LOGGER.debug("File Nname bytes" + Arrays.toString(fn));

            currentIndex++; // Skip the null terminator

            resultSet.add(new QueryHitMessage.Result(fileIndex, fileName, fileSize, ""));

            LOGGER.debug("AA" + String.valueOf(resultSet.size()));

        }

        LOGGER.debug(String.valueOf(resultSet.size()));

        QueryHitMessage queryHitMessage = new QueryHitMessage.Builder()
                .withMessageId(messageId)
                .withTimeToLive(timeToLive)
                .withHops(hops)
                .withNumberOfHits(numberOfHits)
                .withPort(portNum)
                .withIpAddress(ipAddress)
                .withSpeed(speed)
                .withResults(resultSet)
                .build();

        return queryHitMessage.process();
    }
}
