package org.cxxii.messages;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Pong implements MessageInterface_Delete {

    //HEADER
    /**
     * A 16-byte string (GUID) uniquely identifying the
     * message on the network.
     *
     * The Message ID of a Pong message MUST be the Message ID of the Ping_to_delete
     * message it is sent in reply to
     */
    private byte[] pingID;

    /**
     * Indicates the type of message
     * 0x00 = Ping_to_delete
     */
    private static final byte TYPE_ID = (byte) 0x01;

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
    private int payloadLength;


    // PAYLOAD
    /**
     * Port number. The port number on which the responding
     * host can accept incoming connections.
     */
    private int portNum;

    /**
     * IP Address. The IP address of the responding host.
     * Note: This field is in big-endian format.
     */
    private int ipAddress; //change to byte[]

    /**
     * Number of shared files. The number of files that the
     * servent with the given IP address and port is sharing
     * on the network
     */
    private int sharedFiles;

    /**
     * Number of kilobytes shared. The number of kilobytes
     * of data that the servent with the given IP address and
     * port is sharing on the network.
     */
    private int kilobytesShared;


    public Pong(byte[] pingID, byte timeToLive, byte hops) {
        this.pingID = pingID;
        this.timeToLive = timeToLive;
        this.hops = hops;
        this.payloadLength = 14;
        this.portNum = 12345;
        this.ipAddress = 654345; // change to byte array
        this.sharedFiles = 54321;
        this.kilobytesShared = 99999;
    }

    // think this is set to 35
    @Override
    public byte[] serializeMessage() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(23 + payloadLength); // 16 + 1 + 1 + 1 + 4 = 23 bytes for the message header
        buffer.put(convertUUID());
        buffer.put(TYPE_ID);
        buffer.put(timeToLive);
        buffer.put(hops);
        buffer.putInt(payloadLength); // pl len of a pong message is 14bytes?
        buffer.put((byte) ipAddress);
        buffer.putShort((short) portNum); // Convert port to 2 bytes
        buffer.putInt(sharedFiles);
        buffer.putInt(kilobytesShared);
        return buffer.array();
    }

    public byte[] getPingID() {
        return pingID;
    }


    @Override
    public byte[] convertUUID() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.put(getPingID());

        return byteBuffer.array();
    }

//    @Override  --
//    public byte[] convertUUID() {
//        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
//        byteBuffer.putLong(getPingID().getMostSignificantBits());
//        byteBuffer.putLong(getPingID().getLeastSignificantBits());
//
//        return byteBuffer.array();
//    }

    @Override
    public Payload payload() {
        return null;
    }

    private static byte addHop(byte hops) {

        return (byte) (hops + 1 );
    }

    private static byte reduceTTL(byte ttl) {

        return (byte) (ttl - 1 );
    }

    public static Pong processPingMessage(byte[] messageIDBytes, byte timeToLive, byte hops) {





        return new Pong(messageIDBytes, timeToLive, hops);
    }




}
