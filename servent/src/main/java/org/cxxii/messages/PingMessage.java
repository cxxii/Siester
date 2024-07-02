package org.cxxii.messages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import net.bytebuddy.description.method.MethodDescription;
import org.cxxii.network.Network;
import org.cxxii.server.SocketAddr;
import org.cxxii.utils.FileManager;
import org.cxxii.utils.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PingMessage extends MessageAbstract {

    // LOGGER
    private final static Logger LOGGER = LoggerFactory.getLogger(PingMessage.class);


    // CONSTANTS
    private static final byte TYPE_ID = 0x00;
    private static final byte PAYLOAD_LENGTH = 0x00000000;


    // INSTANCE
    private byte timeToLive = (byte) 0x07;
    private byte hops = (byte) 0x00;


    // TODO - CHANGE HOW THIS WORKS
    private static final int MAX_RETRIES = 2;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();



    /**
     * Used for initial Creation of pings
     */
    public PingMessage() {
        super(TYPE_ID, (byte) 0x07, (byte) 0x00, PAYLOAD_LENGTH);
    }


    /**
     * Used for the passing on Pings
     *
     * @param bytesMessageID
     * @param typeId
     * @param timeToLive
     * @param hops
     * @param payloadLength
     * @param payload
     */
    public PingMessage(byte[] bytesMessageID, byte typeId, byte timeToLive, byte hops, byte payloadLength) {
        super(bytesMessageID, typeId, timeToLive, hops, payloadLength);
    }


    // move?
    private byte[] UUIDtoByteArray(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }


    // move?
    private static byte[] serializeMessage(PingMessage message) throws IOException {
        LOGGER.info("Serialization began");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Write the UUID
        byte[] ping_ID_debug = message.UUIDtoByteArray(message.getMessageID());
        outputStream.write(ping_ID_debug);

        LOGGER.info("PING BYTE ID = " + Arrays.toString(ping_ID_debug));

        // Write the message type (Ping_to_delete)
        outputStream.write(Byte.toUnsignedInt(message.getTypeId()));

        // Write the TTL
        outputStream.write(Byte.toUnsignedInt(message.getTimeToLive()));

        // Write the Hops
        outputStream.write(Byte.toUnsignedInt(message.getHops()));

        // Write the payload length (4 bytes, big-endian)
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.putInt(message.getPayloadLength());
        outputStream.write(lengthBuffer.array());

        //outputStream.write(message.getPayload());


        return outputStream.toByteArray();
    }

    private int getPayloadLength() {
        return PAYLOAD_LENGTH;
    }

    //move to util class? // YES // used elsewhere
    // BUG - JSON bug in here - gson
    public static List<SocketAddr> readHostCache() {
        LOGGER.info("Reading hostcache");

        ObjectMapper objectMapper = new ObjectMapper();
        List<SocketAddr> addresses = null;

        try {
            addresses = objectMapper.readValue(new File(String.valueOf(FileManager.getHostCachePath().toFile())), new TypeReference<List<SocketAddr>>() {});
        } catch (IOException e) {
            LOGGER.error("Could not read hostcache", e);
        }

        return addresses;
    }


    public static void startPings() throws SocketException, UnknownHostException {
        LOGGER.info("PINGING cached hosts...");

        String ipString = InetAddress.getByAddress(Network.getLocalIpAddress()).getHostAddress();

        List<SocketAddr> hosts = readHostCache();

        if (hosts != null) {
            for (SocketAddr host : hosts) {
                if (!host.getIp().getHostAddress().equals(ipString)) {
                    PingMessage ping = new PingMessage();
                    ping.sendPing(host);
                }
            }
        }
    }

    private void sendPing(SocketAddr host) {

        executorService.submit(() -> {

            for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                try (Socket socket = new Socket(host.getIp(), host.getPort());
                     OutputStream outputStream = socket.getOutputStream()) {

                    byte[] serializedPing = serializeMessage(this);

                    outputStream.write(serializedPing);
                    outputStream.flush();

                    LOGGER.info("PING sent" + Arrays.toString(serializedPing) + "To: " + host.getIp());

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

    private void pingOnwards(InetSocketAddress addr) {
        LOGGER.info("PINGING ONWARDS");

        List<SocketAddr> hosts = readHostCache();

        if (this.getTimeToLive() < 0)

            for (SocketAddr host : hosts) {
                if (!host.getIp().equals(addr)) {
                    this.sendPing(host);
                }
            }
    }

    private static boolean checkHostInCache(InetAddress addr) {
        List<SocketAddr> sockets = readHostCache();

        for (SocketAddr socket : sockets) {
            if (socket.getIp().equals(addr)) {
                return true;
            }
        }

        return false;
    }


    protected PingMessage process(InetSocketAddress addr) throws IOException {

        LOGGER.info("Processing PING.");


        // Check and saves host in file
        if (!checkHostInCache(addr.getAddress())) {
            Json.appendToHostCacheJson(addr);
        }

        LOGGER.info("PING TTL = " + this.getTimeToLive()); // ok
        LOGGER.info("PING HOPS = " + this.getHops()); // ok
        if (this.getTimeToLive() != 0) {

            this.setHops((byte) (this.getTimeToLive() - 1));
            this.setHops((byte) (this.getHops() + 1)); //fucked

            // Proliferate through network
            //this.pingOnwards(addr);

        }


        // return 10 pongs from pongcaches (10 random atm)
        // PongMessage pong = new PongMessage(id,ttl,hops,payloadLength)



        // pass info on to the ping factory to make pings and host to my host cache



        // return hosts own pong
        LOGGER.info(" PINGMESSAGE - RESPONDING WITH" + Arrays.toString(this.getBytesMessageID()) + this.getTimeToLive() + this.getHops());
        PongMessage.respond(this.getBytesMessageID(), this.getTimeToLive(), this.getHops(), addr);


        return this;

    }

}

// IN THE GENERAL PONG CACHE TAKE THE 10 LATEST PONGS ONE FROM EACH FILE - GOOD PRACTICE TO HAVE DIFFERENT HOP VALUES!!
// GENERAL CACHE OR JUST A METHOD TO EXTRACT THE 10 ON DEMAND? MAYBE THIS!?!?

// NEED A FILE FOR HISTORIC HOSTS
// PING A HOST - GET A RESPONSE - SAVE THEIR INFO IN ALL-TIME HOST FILE AND CURRENT HOST FILE
// PONGS YOU RESPONDE WITH MATCH THE PING ID