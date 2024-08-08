package org.cxxii.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cxxii.network.Network;
import org.cxxii.server.SocketAddr;
import org.cxxii.utils.FileManager;
import org.cxxii.utils.HostCacheReader;
import org.cxxii.utils.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingMessage extends MessageAbstract {

    private final static Logger LOGGER = LoggerFactory.getLogger(PingMessage.class);
    private static final byte TYPE_ID = 0x00;
    private static final byte PAYLOAD_LENGTH = 0x00;
    private static final int MAX_RETRIES = 2;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public PingMessage() {
        super(TYPE_ID, (byte) 0x07, (byte) 0x00, PAYLOAD_LENGTH);
    }

    public PingMessage(byte[] bytesMessageID, byte typeId, byte timeToLive, byte hops, byte payloadLength) {
        super(bytesMessageID, typeId, timeToLive, hops, payloadLength);
    }

    private int getPayloadLength() {
        return PAYLOAD_LENGTH;
    }

    public static void startPings() throws SocketException, UnknownHostException {
        LOGGER.info("Pinging all known hosts...");

        PongMessage.clearHostList(); // OK


        String ipString = InetAddress.getByAddress(Network.getLocalIpAddress()).getHostAddress();
        List<SocketAddr> hosts = HostCacheReader.readHostCache();

        LOGGER.info("Known network size: " + HostCacheReader.getNetworkSize());

        if (hosts != null) {
            for (SocketAddr host : hosts) {
                if (!host.getIp().getHostAddress().equals(ipString)) {
                    LOGGER.info("Creating PING");
                    PingMessage ping = new PingMessage();
                    ping.sendPing(host);
                }
            }
        }
    }


    // move?
    private static byte[] serializeMessage(PingMessage message) throws IOException {
        LOGGER.info("Serializaing PING");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        outputStream.write(UUIDtoByteArray(message.getMessageID()));
        outputStream.write(Byte.toUnsignedInt(message.getTypeId()));
        outputStream.write(Byte.toUnsignedInt(message.getTimeToLive()));
        outputStream.write(Byte.toUnsignedInt(message.getHops()));


        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);

        lengthBuffer.putInt(message.getPayloadLength());
        outputStream.write(lengthBuffer.array());

        return outputStream.toByteArray();
    }


    // DEPRECATED??????
    public static void pingPongCache() {
        LOGGER.info("****** RUNNING PING PONG CACHE ******");

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(FileManager.getPongCachePath().toFile());

            for (JsonNode node : rootNode) {

                String ipAddress = node.get("ipAddress").asText();

                SocketAddr addr = new SocketAddr(InetAddress.getByName(ipAddress), node.get("portNum").asInt());

                PingMessage ping = new PingMessage();

                LOGGER.debug(addr.toString());

                ping.sendPing(addr);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] UUIDtoByteArray(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }




    private void sendPing(SocketAddr host) {
        executorService.submit(() -> {
            for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                try (Socket socket = new Socket(host.getIp(), host.getPort());
                     OutputStream outputStream = socket.getOutputStream()) {

                    byte[] serializedPing = serializeMessage(this);
                    outputStream.write(serializedPing);
                    outputStream.flush();
                    LOGGER.info("PING Sent: " + Arrays.toString(serializedPing) + " Destination: " + host.getIp());
                    break;
                } catch (IOException e) {
                    LOGGER.error("Attempt " + attempt + " FAILED to send PING to: " + host.getIp() + ":" + host.getPort(), e);
                    if (attempt == MAX_RETRIES) {
                        LOGGER.error("All attempts to ping the host failed.");
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        });
    }


    private static boolean checkHostInCache(InetAddress addr) {
        List<SocketAddr> sockets = HostCacheReader.readHostCache();

        for (SocketAddr socket : sockets) {
            if (socket.getIp().equals(addr)) {
                return true;
            }
        }

        return false;
    }

    protected PingMessage process(InetSocketAddress addr) throws IOException {
        LOGGER.info("Processing PING...");

        if (!checkHostInCache(addr.getAddress())) {
            Json.appendToHostCacheJson(addr);
        }

        if (this.getTimeToLive() != 0) {
            ttlAndHopsIncrementor(this);
        }

        PongMessage.respond(this.getBytesMessageID(), this.getTimeToLive(), this.getHops(), addr);
        PongMessage.sendPongCache(addr, this);

        return this;
    }
}

