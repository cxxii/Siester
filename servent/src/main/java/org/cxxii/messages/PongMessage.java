package org.cxxii.messages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PongMessage extends MessageAbstract {

    // TODO:
    //  Stay consistent with ip type! need to change top to bottom


    // LOGGER
    private final static Logger LOGGER = LoggerFactory.getLogger(PongMessage.class);


    // CONSTANTS
    private static final byte TYPE_ID = (byte) 0x01;
    private static final byte PAYLOAD_LENGTH = (byte) 13;
    private static final byte[] PAYLOAD = null;


    // INSTANCE
        // HEADERS
    private byte[] pingID; //byte array as it wont be converted back to uuid
    private int PayloadLength;
    private byte timeToLive;
    private byte hops;

        // Payload
    private int portNum;
    private byte[] ipAddress; //changed from to string to Inet
    private byte sharedFiles;
    private int kilobytesShared;


    // CONSTRUCTORS
    /**
     * Use for replying to pings
     * @param bytesMessageID
     * @param TYPE_ID
     * @param timeToLive
     * @param hops
     * @param payloadLength
     * @param portNum
     * @param ipAddress
     * @param sharedFiles
     * @param kilobytesShared
     */
    public PongMessage(byte[] bytesMessageID, byte TYPE_ID, byte timeToLive, byte hops, byte payloadLength, int portNum, byte[] ipAddress, byte sharedFiles, int kilobytesShared) {
        super(bytesMessageID, TYPE_ID, timeToLive, hops, payloadLength);
        this.portNum = portNum;
        this.ipAddress = ipAddress;
        this.sharedFiles = sharedFiles;
        this.kilobytesShared = kilobytesShared;
    }

    //usage?
    public PongMessage(byte typeId, byte timeToLive, byte hops, byte payloadLength) {
        super(typeId, timeToLive, hops, payloadLength);
    }


    //To be removed..
    public PongMessage(byte ttl, byte hops, String ipAddress, int portNum, byte sharedFiles, int kilobytesShared, byte payloadLength) {
        super();
    }


    //GETTR N SETTR

    //remove
    private static byte[] getIp() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();

        return localHost.getAddress();
    }


    public byte[] getPingID() {
        return pingID;
    }

    public byte getPortNum() {
        return (byte) portNum;
    }


    public byte[] getIpAddress() {
        return ipAddress;
    }

    public byte getSharedFiles() {
        return (byte) sharedFiles;
    }

    public byte getKilobytesShared() {
        return (byte) kilobytesShared;
    }



    // METHODS

    public byte[] getMyIp() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByAddress(getIpAddress());

        return inetAddress.getAddress();
    }


    public static void respond(byte[] pingID, byte timeToLive, byte hops, InetSocketAddress addr) throws IOException {

        byte payloadLength = (byte) 13;

        int port = 8282; // change this to read from the conif file
        byte[] ip = this.getMyIp(); // FIXME! Drama... need to config from the holepunch i think????
        byte sharedFiles = (byte) 66; // change to get from a saved data file
        int kbShared = 12354;

        PongMessage pong = new PongMessage(pingID, TYPE_ID, timeToLive, hops, PAYLOAD_LENGTH, port, ip, sharedFiles, kbShared);


        String ipy = addr.getAddress().getHostAddress();

        //int porty = addr.getPort();
        int porty = 8080; // debugging keep above



        LOGGER.debug("TRYING TO SEND TO " + ipy + " " + porty);

        Socket socket = new Socket(ipy,porty);

        OutputStream outputStream = socket.getOutputStream();

        outputStream.write(pong.serializeMessage(pong));


    }

    public byte[] serializeMessage(PongMessage pong) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Write the ping id back
        byte[] pingID = pong.getPingID();
        if (pingID == null) {
            throw new NullPointerException("pingID is null");
        }
        outputStream.write(pingID);

        // Write the message type (Ping_to_delete)
        outputStream.write(Byte.toUnsignedInt(TYPE_ID));

        // Write the TTL
        outputStream.write(Byte.toUnsignedInt(pong.getTimeToLive()));

        // Write the Hops
        outputStream.write(Byte.toUnsignedInt(pong.getHops()));

        // Write the payload length (4 bytes, big-endian)
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.putInt(pong.getPayloadLength());
        outputStream.write(lengthBuffer.array());

        // Write the port number
        outputStream.write(Byte.toUnsignedInt(pong.getPortNum()));

        // Write the IP address
        byte[] ipAddressBytes = pong.getIpAddress();
        if (ipAddressBytes == null) {
            throw new NullPointerException("ipAddressBytes is null");
        }
        ByteBuffer ipBuffer = ByteBuffer.allocate(4);
        ipBuffer.put(ipAddressBytes);
        outputStream.write(ipBuffer.array());

        // Write the number of shared files
        outputStream.write(Byte.toUnsignedInt(pong.getSharedFiles()));

        // Write the number of kilobytes shared
        outputStream.write(Byte.toUnsignedInt(pong.getKilobytesShared()));

        return outputStream.toByteArray();

    }
}