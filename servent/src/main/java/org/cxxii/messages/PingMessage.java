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
    private final UUID MESSAGE_ID = UUID.randomUUID(); // this is in the constructor - delete?
    private static final byte TYPE_ID = 0x00;
    private static final byte PAYLOAD_LENGTH = 0x00000000;
    //private static final byte[] PAYLOAD = null;


    // INSTANCE
    private byte timeToLive = (byte) 0x07;
    private byte hops = (byte) 0x00;

    private static final int MAX_RETRIES = 2;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();



    /**
     * Used for initial Creation of pings
     */
    public PingMessage() {
        super(TYPE_ID, (byte) 0x07, (byte) 0x00, PAYLOAD_LENGTH);
    }

    @Override
    public MessageAbstract parse(byte[] header, byte[] payload, InetSocketAddress addr) throws IOException {
        return null;
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
        outputStream.write(message.UUIDtoByteArray(message.MESSAGE_ID));

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

    // TODO - REMOVE??
//    private static void sendPing(SocketAddr host, PingMessage ping) {
//
//        try (Socket socket = new Socket(host.getIp(), host.getPort());
//             OutputStream outputStream = socket.getOutputStream()) {
//
//            byte[] serializedPing = serializeMessage(ping);
//
//            outputStream.write(serializedPing);
//            outputStream.flush();
//
//            LOGGER.info("PING sent to: " + host.getIp() + ":" + host.getPort());
//
//        } catch (IOException e) {
//            LOGGER.error("FAILED to send PING to: " + host.getIp() + ":" + host.getPort(), e);
//        }
//    }


    // now doesn't ping same IP - must make sure images run on different ips on the network
    // tidy local ipstring getter
    public static void startPings() throws SocketException, UnknownHostException {

        String ipString = InetAddress.getByAddress(Network.getLocalIpAddress()).getHostAddress();

        List<SocketAddr> hosts = readHostCache();

        PingMessage ping = new PingMessage();

        if (hosts != null) {
            for (SocketAddr host : hosts) {
                if (!host.getIp().getHostAddress().equals(ipString)) {
                    sendPing(host, ping);
                }
            }
        } else {
            LOGGER.info("HOST CACHE IS EMPTY");
        }
    }

    private static void sendPing(SocketAddr host, PingMessage ping) {
        executorService.submit(() -> {
            for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
                try (Socket socket = new Socket(host.getIp(), host.getPort());
                     OutputStream outputStream = socket.getOutputStream()) {

                    byte[] serializedPing = serializeMessage(ping);

                    outputStream.write(serializedPing);
                    outputStream.flush();

                    LOGGER.info("PING sent to: " + host.getIp() + ":" + host.getPort());
                    LOGGER.info("PING ID: " + ping.MESSAGE_ID);
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

    private void pingOnwards(PingMessage ping, InetSocketAddress addr) {

        List<SocketAddr> hosts = readHostCache();

        if (ping.getTimeToLive() < 0)

            for (SocketAddr host : hosts) {
                sendPing(host, ping);
            }
    }

    private static boolean checkHostInCache(int port, InetAddress addr) {
        List<SocketAddr> sockets = readHostCache();

        for (SocketAddr socket : sockets) {
            if (socket.getIp().equals(addr) && socket.getPort() == port) {
                return true;
            }
        }

        return false;
    }



    //public void process(byte[] messageID, byte typeId, byte timeToLive, byte hops, byte payloadLength, InetSocketAddress addr) throws IOException {
    protected PingMessage process(InetSocketAddress addr) throws IOException {

        LOGGER.info("Processing PING.");


        // Check and saves host in file
        if (!checkHostInCache(addr.getPort(), addr.getAddress())) {
            Json.appendToHostCacheJson(addr);
        }


        if (this.getTimeToLive() != 0) {

            this.setHops((byte) (this.getTimeToLive () - 1));
            this.setHops((byte) (this.getHops () + 1));

            // Proliferate through network
            pingOnwards(this, addr);

        }


        // remember dont pong back to hosts own info
        // return 10 pongs from pongcaches (10 random atm)
        // PongMessage pong = new PongMessage(id,ttl,hops,payloadLength)



        // pass info on to the ping factory to make pings and host to my host cache



        // return hosts own pong
        PongMessage.respond(this.getBytesMessageID(), this.getTimeToLive(), this.getHops(), addr);


        return this;

    }

}

// IN THE GENERAL PONG CACHE TAKE THE 10 LATEST PONGS ONE FROM EACH FILE - GOOD PRACTICE TO HAVE DIFFERENT HOP VALUES!!
// GENERAL CACHE OR JUST A METHOD TO EXTRACT THE 10 ON DEMAND? MAYBE THIS!?!?

// NEED A FILE FOR HISTORIC HOSTS
// PING A HOST - GET A RESPONSE - SAVE THEIR INFO IN ALL-TIME HOST FILE AND CURRENT HOST FILE
// PONGS YOU RESPONDE WITH MATCH THE PING ID