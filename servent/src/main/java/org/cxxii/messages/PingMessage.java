package org.cxxii.messages;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import net.bytebuddy.description.method.MethodDescription;
import org.cxxii.server.SocketAddr;
import org.cxxii.utils.FileManager;
import org.cxxii.utils.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class PingMessage extends MessageAbstract {

    // LOGGER
    private final static Logger LOGGER = LoggerFactory.getLogger(PingMessage.class);


    // CONSTANTS
    private final UUID MESSAGE_ID = UUID.randomUUID();
    private static final byte TYPE_ID = 0x00;
    private static final byte PAYLOAD_LENGTH = 0x00000000;
    //private static final byte[] PAYLOAD = null;


    // INSTANCE
    private byte timeToLive = (byte) 0x07;
    private byte hops = (byte) 0x00;


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

    //move to util class?
    private static List<SocketAddr> readHostCache() {
        LOGGER.info("Reading hostcache");

        Gson gson = new Gson();
        List<SocketAddr> addresses = null;

        try (BufferedReader br = new BufferedReader(new FileReader(FileManager.getHostCachePath().toFile()))) {

            JsonReader jsonReader = new JsonReader(br);
            Type listType = new TypeToken<List<SocketAddr>>() {
            }.getType();
            addresses = gson.fromJson(jsonReader, listType);

        } catch (IOException e) {

            LOGGER.error("Could not read hostcache", e);
        }

        return addresses;
    }

    private static void sendPing(SocketAddr host, PingMessage ping) {

        try (Socket socket = new Socket(host.getIp(), host.getPort());
             OutputStream outputStream = socket.getOutputStream()) {

            byte[] serializedPing = serializeMessage(ping);

            outputStream.write(serializedPing);
            outputStream.flush();

            LOGGER.info("PING sent to: " + host.getIp() + ":" + host.getPort());

        } catch (IOException e) {
            LOGGER.error("FAILED to send PING to: " + host.getIp() + ":" + host.getPort(), e);
        }
    }

    public static void startPings() {

        List<SocketAddr> hosts = readHostCache();

        PingMessage ping = new PingMessage();

        for (SocketAddr host : hosts) {
            sendPing(host, ping);
        }
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
    public PingMessage process(InetSocketAddress addr) throws IOException {

        if (this.getTimeToLive() != 0) {

            this.setHops((byte) (this.getTimeToLive () - 1));
            this.setHops((byte) (this.getHops () + 1));


            // save host to cache
            // check if in file first
            if (!checkHostInCache(addr.getPort(), addr.getAddress())) {
                Json.appendToHostCacheJson(addr);
            }


            // Proliferate through network
            pingOnwards(this, addr);

            // return hosts own pong
            PongMessage.respond(this.getBytesMessageID(), this.getTimeToLive(), this.getHops(), addr);




            // return 10 pongs from pongcaches (10 random atm)
            // PongMessage pong = new PongMessage(id,ttl,hops,payloadLength)



            System.out.println("ttl is more than 1");
            // pass info on to the ping factory to make pings and host to my host cache
        } else {
            System.out.println("ttl is at 0");
        }

        System.out.println("processing ping");

        return this;

    }

}

// IN THE GENERAL PONG CACHE TAKE THE 10 LATEST PONGS ONE FROM EACH FILE - GOOD PRACTICE TO HAVE DIFFERENT HOP VALUES!!
// GENERAL CACHE OR JUST A METHOD TO EXTRACT THE 10 ON DEMAND? MAYBE THIS!?!?

// NEED A FILE FOR HISTORIC HOSTS
// PING A HOST - GET A RESPONSE - SAVE THEIR INFO IN ALL-TIME HOST FILE AND CURRENT HOST FILE
// PONGS YOU RESPONDE WITH MATCH THE PING ID