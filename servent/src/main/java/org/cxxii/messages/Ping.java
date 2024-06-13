package org.cxxii.messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Ping extends MessageAbstract {

    /**
     * A 16-byte string (GUID) uniquely identifying the
     * message on the network.
     */
    private final UUID ID = UUID.randomUUID();

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
    private byte timeToLive = (byte) 0x07;

    /**
     * The number of times the message has been forwarded.
     */
    private byte hops = (byte) 0x00;

    /**
     * The length of the message immediately following
     * this header. The next message header is located
     * exactly this number of bytes from the end of this
     * header
     */
    private final int PAYLOAD_LENGTH = 0;

    public Ping(byte timeToLive, byte hops, int payloadLength, String payload, byte timeToLive1, byte hops1) {
        super((byte) 0x00, timeToLive, hops, (byte) 0, null);
    }

    public UUID getID() {
        return ID;
    }

    public byte getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(byte timeToLive) {
        this.timeToLive = timeToLive;
    }

    public byte getHops() {
        return hops;
    }


    public void setHops(byte hops) {
        this.hops = hops;
    }

    public byte getTYPE_ID() {
        return TYPE_ID;
    }

    public int getPAYLOAD_LENGTH() {
        return PAYLOAD_LENGTH;
    }



    /**
     * This method converts the instances UUID from UUID to a Byte[]
     *
     * @return returns the byte[] of the instances UUID
     */
    public byte[] convertUUID() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(getID().getMostSignificantBits());
        byteBuffer.putLong(getID().getLeastSignificantBits());

        return byteBuffer.array();
    }

//    @Override
//    public Payload payload() {
//        return null;
//    }


    /**
     * @return
     * @throws IOException
     */

    public byte[] serializeMessage() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Write the UUID
        outputStream.write(convertUUID());

        // Write the message type (Ping)
        outputStream.write(getTYPE_ID());

        // Write the TTL
        outputStream.write(getTimeToLive());

        // Write the Hops
        outputStream.write(getHops());

        // Write the payload length (4 bytes, big-endian)
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.putInt(getPAYLOAD_LENGTH());
        outputStream.write(lengthBuffer.array());

        return outputStream.toByteArray();
    }
}

    //These will extend the Message class and implement the specific functionality and attributes for each message type.

//    Serialization:
//
//Ensure that your Header, Payload, and Message classes are serializable if you need to transmit these objects over the network.

//    Factory Pattern:
//
//Consider using a factory pattern to create instances of Message subclasses. This can help encapsulate the creation logic and make your code more maintainable.

//    Error Handling:
//
//Implement robust error handling and validation within your message processing methods to handle malformed messages gracefully.
//Extensibility:
//
//Design your classes to be easily extensible for future message types. This ensures that adding new message types in the future will be straightforward.

//    CONVERT TO GOOD TYPE
//    CREATE HEADER
//    CREATE PAYLOAD
//PROTOBUF FOR SERIALIZATION
