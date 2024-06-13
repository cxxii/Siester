package org.cxxii.messages;

import org.cxxii.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class PingMessage extends MessageAbstract {

    // LOGGER
    private final static Logger LOGGER = LoggerFactory.getLogger(PingMessage.class);


    // CONSTANTS
    private final UUID MESSAGE_ID = UUID.randomUUID();
    private static final byte TYPE_ID = (byte) 0x00;
    private static final int PAYLOAD_LENGTH = 0;
    private static final byte[] PAYLOAD = null;


    // INSTANCE
    private byte timeToLive = (byte) 0x07;
    private byte hops = (byte) 0x00;


    /**
     * Used for initial Creation of pings
     */
    public PingMessage() {
        super(TYPE_ID, (byte) 0x07, (byte) 0x00, PAYLOAD_LENGTH, PAYLOAD);
    }


    /**
     * Used for the passing on Pings
     * @param bytesMessageID
     * @param typeId
     * @param timeToLive
     * @param hops
     * @param payloadLength
     * @param payload
     */
    public PingMessage(byte[] bytesMessageID, byte typeId, byte timeToLive, byte hops, int payloadLength, byte[] payload) {
        super(bytesMessageID, typeId, timeToLive, hops, payloadLength, payload);
    }


    private byte[] UUIDtoByteArray(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }


    private byte[] serializeMessage(PingMessage message) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        // Write the UUID
        outputStream.write(message.UUIDtoByteArray(message.MESSAGE_ID));

        // Write the message type (Ping)
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


    // might move this class
    // pings hosts cache on start up
    // Jsonify the reading
    public void startPings() {
        try (BufferedReader br = new BufferedReader(new FileReader(FileManager.getHostCachePath().toFile()))) {
            String line;

            while ((line = br.readLine()) != null) {
                LOGGER.debug("sending" + line);

                String[] addyPort = line.split(":");

                try (Socket socket = new Socket(addyPort[0], Integer.parseInt(addyPort[1]));
                     OutputStream outputStream = socket.getOutputStream()) {

                    PingMessage ping = new PingMessage();

                    byte[] serializedPing = ping.serializeMessage(ping);

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

    // move elsewhere?
    // the
    private void pingOnwards(PingMessage ping, InetSocketAddress addr) {
        try (BufferedReader br = new BufferedReader(new FileReader(FileManager.getHostCachePath().toFile()))) {
            String line;

            while ((line = br.readLine()) != null) {

                if (!Objects.equals(addr.toString(), line)) {

                    LOGGER.debug("sending ping to " + line);

                    String[] addyPort = line.split(":");

                    try (Socket socket = new Socket(addyPort[0], Integer.parseInt(addyPort[1]));
                         OutputStream outputStream = socket.getOutputStream()) {

                        byte[] serializedPing = serializeMessage(ping);

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

    public void process(byte[] messageID, byte typeId, byte timeToLive, byte hops, int payloadLength, InetSocketAddress addr) throws IOException {

        PingMessage ping = new PingMessage(messageID, typeId, timeToLive, hops, payloadLength, PAYLOAD);

        if (timeToLive != 0) {

            // helper method to add and reduce these
            ping.setHops((byte) (hops + 1));
            ping.setTimeToLive((byte) (timeToLive - 1));

            // Proliferate through network
            pingOnwards(this, addr);

            // return hosts own pong
            PongMessage.respond(messageID, timeToLive, hops, addr);




            // return 10 pongs from pongcaches (10 random atm)
            // PongMessage pong = new PongMessage(id,ttl,hops,payloadLength)

            LOGGER.info("PING ID RECIEVED" + Arrays.toString(messageID));




            System.out.println("ttl is more than 1");
            // pass info on to the ping factory to make pings and host to my host cache
        } else {
            System.out.println("ttl is at 0");
        }

        System.out.println("processing ping");

    }

}

// IN THE GENERAL PONG CACHE TAKE THE 10 LATEST PONGS ONE FROM EACH FILE - GOOD PRACTICE TO HAVE DIFFERNT HOP VALUES!!
// GENERAL CACHE OR JUST A METHOD TO EXTRACT THE 10 ON DEMAND? MAYBE THIS!?!?

// NEED A FILE FOR HISTORIC HOSTS
// PING A HOST - GET A RESPONSE - SAVE THEIR INFO IN ALL-TIME HOST FILE AND CURRENT HOST FILE
// PONGS YOU RESPONDE WITH MATCH THE PING ID