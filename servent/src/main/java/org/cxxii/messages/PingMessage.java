package org.cxxii.messages;

import org.cxxii.server.ConnectionWorkerThread;
import org.cxxii.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class PingMessage extends MessageAbstract {

    private final static Logger LOGGER = LoggerFactory.getLogger(PingMessage.class);

    /**
     * A 16-byte string (GUID) uniquely identifying the
     * message on the network.
     */
    private UUID ID = UUID.randomUUID();

    private byte[] id;
    private byte typeId;
    private byte hops;
    private int payloadLength;
    private byte ttl;
    private byte[] payload;

    /**
     * Indicates the type of message
     * 0x00 = Ping
     */
    private final byte TYPE_ID = (byte) 0x00;

    /**
     * Time To Live. The number of times the message
     * will be forwarded by Gnutella servents before it is
     * removed from the network. Each servent will decrement
     * the TTL before passing it on to another servent. When
     * the TTL reaches 0, the message will no longer be
     * forwarded (and MUST not).
     */
//    private byte timeToLive = (byte) 0x07;

    /**
     * The number of times the message has been forwarded.
     */
//    private byte hops = (byte) 0x00;

    /**
     * The length of the message immediately following
     * this header. The next message header is located
     * exactly this number of bytes from the end of this
     * header
     */
    private final int PAYLOAD_LENGTH = 0;


    // CONSTRUCTOR FOR WHEN SENDING one will need the uuid generaed one will come with

    public PingMessage() {
        super((byte) 0x00, (byte) 0x07, (byte) 0x00, 0, null);
        this.ID = UUID.randomUUID();
    }

    // CONSTRUCTOR FOR WHEN RECIEVING
    public PingMessage(byte[] id, byte typeId, byte ttl, byte hops, int payloadLength, byte[] payload) {
        this.id = getId();
        this.typeId = getTypeId();
        this.ttl = getTimeToLive();
        this.hops = getHops();
        this.payloadLength = getPayloadLength();
        this.payload = getPayload();
    }

//    public PingMessage() {
//        super((byte) 0x00, (byte) 0x07, (byte) 0x00, 0, null);
//        this.ID = UUID.randomUUID();
//        this.id = toByteArray(ID);
//        this.typeId = TYPE_ID;
//        this.ttl = (byte) 0x07;
//        this.hops = (byte) 0x00;
//        this.payloadLength = 0;
//        this.payload = new byte[0];
//    }

//    public PingMessage(byte[] id, byte typeId, byte ttl, byte hops, int payloadLength, byte[] payload) {
//        super(typeId, ttl, hops, payloadLength, payload);
//        this.id = id;
//        this.typeId = typeId;
//        this.ttl = ttl;
//        this.hops = hops;
//        this.payloadLength = payloadLength;
//        this.payload = payload;
//    }

    public void pingCache() {

    }

    public byte[] getId() {
        return id;
    }

    public void setHops(byte hops) {
        this.hops = hops;
    }

    public void setTtl(byte ttl) {
        this.ttl = ttl;
    }

    @Override
    public byte getTypeId() {
        return typeId;
    }

    @Override
    public byte getHops() {
        return hops;
    }

    @Override
    public int getPayloadLength() {
        return payloadLength;
    }

    @Override
    public byte[] getPayload() {
        return payload;
    }

    public void process() {
        System.out.println("FFFF");
    }

    @Override
    public void create() {

    }

    private byte[] UUIDtoByteArray(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    public static byte[] serializeMessage(PingMessage message) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        // Write the UUID
        outputStream.write(message.UUIDtoByteArray(message.ID));

        // Write the message type (Ping)
        outputStream.write(Byte.toUnsignedInt(message.typeId));

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


    // start up pings of hosts
    public static void startPings() {
        try (BufferedReader br = new BufferedReader(new FileReader(FileManager.getHostCachePath().toFile()))) {
            String line;

            while ((line = br.readLine()) != null) {
                LOGGER.debug("sending" + line);

                String[] addyPort = line.split(":");

                try (Socket socket = new Socket(addyPort[0], Integer.parseInt(addyPort[1]));
                     OutputStream outputStream = socket.getOutputStream()) {

                    PingMessage ping = new PingMessage();

                    byte[] serializedPing = PingMessage.serializeMessage(ping);
                    System.out.println(Arrays.toString(serializedPing));

                    outputStream.write(serializedPing);
                    outputStream.flush();

                } catch (IOException e) {
                    LOGGER.error("Couldnt send ping " + line, e);
                }
            }
        } catch (IOException e) {
            LOGGER.error("could not read cache file", e);
        }
    }

    // the
    public void hostPingOnwards(PingMessage ping, SocketAddress addr) {
        try (BufferedReader br = new BufferedReader(new FileReader(FileManager.getHostCachePath().toFile()))) {
            String line;

            while ((line = br.readLine()) != null) {

                if (!Objects.equals(addr.toString(), line)) {

                    LOGGER.debug("sending ping to " + line);

                    String[] addyPort = line.split(":");

                    try (Socket socket = new Socket(addyPort[0], Integer.parseInt(addyPort[1]));
                         OutputStream outputStream = socket.getOutputStream()) {

                        byte[] serializedPing = PingMessage.serializeMessage(ping);
                        System.out.println(Arrays.toString(serializedPing));

                        outputStream.write(serializedPing);
                        outputStream.flush();

                    } catch (IOException e) {

                        LOGGER.error("Couldn't send ping " + line, e);
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.error("could not read cache file", e);
            LOGGER.error("Unable to ping onwards", e);
        }
    }




//    public MessageAbstract process(byte[] id, byte typeId, byte ttl, byte hops, int payloadLength, SocketAddress addr) {
//
//        // check the ttls and minus
//        if (ttl != 0) {
//            // call method to pass ping on
//            System.out.println("ttl is more than 1");
//
//            // pass info on to the ping factory to make pings and hest to my host cache
//        } else {
//            System.out.println("ttl is at 0");
//        }
//        // and one hop
//        // payload len should be 0
//        // keep addr to return message to
//
//
//        System.out.println("processing ping");
//
//        MessageAbstract ping = new PingMessage();
//
//        return message
//    }

    public void process(byte[] id, byte typeId, byte ttl, byte hops, int payloadLength, InetSocketAddress addr) throws IOException {

        PingMessage ping = new PingMessage(id, typeId, ttl, hops, payloadLength, null);

        if (ttl != 0) {
            ping.setHops((byte) (hops + 1));
            ping.setTtl((byte) (ttl - 1));

            ping.hostPingOnwards(ping, addr);

//            PongMessage pong = new PongMessage(id,ttl,hops,payloadLength)

            LOGGER.info("PING ID RECIEVED" + Arrays.toString(id));

            PongMessage.respond(id, ttl, hops, addr);


            System.out.println("ttl is more than 1");
            // pass info on to the ping factory to make pings and host to my host cache
        } else {
            System.out.println("ttl is at 0");
        }

        System.out.println("processing ping");





        // Decrement TTL and increment hops

    }

}

// IN THE GENERAL PONG CACHE TAKE THE 10 LATEST PONGS ONE FROM EACH FILE - GOOD PRACTICE TO HAVE DIFFERNT HOP VALUES!!
// GENERAL CACHE OR JUST A METHOD TO EXTRACT THE 10 ON DEMAND? MAYBE THIS!?!?

// NEED A FILE FOR HISTORIC HOSTS
// PING A HOST - GET A RESPONSE - SAVE THEIR INFO IN ALL-TIME HOST FILE AND CURRENT HOST FILE
// PONGS YOU RESPONDE WITH MATCH THE PING ID