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

    private final static Logger LOGGER = LoggerFactory.getLogger(PongMessage.class);

    //HEADER
    private byte[] pingID;
    private static final byte TYPE_ID = (byte) 0x01;
    private byte timeToLive;
    private byte hops;
    private int payloadLength;


    // PAYLOAD
    private int portNum;
    private String ipAddress; //change to byte[]
    private byte[] ipAddr; //change to byte[]
    private byte sharedFiles;
    private int kilobytesShared;


    // CONSTRUCTORS

    public PongMessage(byte[] id, byte typeID, byte ttl, byte hops, byte payloadLength, int port, byte[] ip, byte sharedFiles, int kbShared) {
        this.pingID = id;
        this.timeToLive = ttl;
        this.hops = hops;
        this.ipAddr= ip;
        this.portNum = port;
        this.sharedFiles = sharedFiles;
        this.kilobytesShared = kbShared;
        this.payloadLength = payloadLength;
    }

    public PongMessage(byte timeToLive, byte hops, String ipAddress, int portNum, byte sharedFiles, int kilobytesShared, int payloadLength) {
        this.ipAddress = ipAddress;
        this.portNum = portNum;
        this.sharedFiles = sharedFiles;
        this.kilobytesShared = kilobytesShared;
        this.payloadLength = payloadLength;
    }

    public PongMessage(byte[] pingID, byte timeToLive, byte hops, String ipAddress, int portNum, byte sharedFiles, int kilobytesShared, int payloadLength) {
        this.pingID = pingID;
        this.timeToLive = timeToLive;
        this.hops = hops;
        this.ipAddress = ipAddress;
        this.portNum = portNum;
        this.sharedFiles = sharedFiles;
        this.kilobytesShared = kilobytesShared;
        this.payloadLength = payloadLength;
    }

    public PongMessage(byte[] pingID, byte timeToLive, byte hops, int payloadLength, int portNum, byte[] ipAddress, byte sharedFiles, int kilobytesShared) {
        this.pingID = pingID;
        this.timeToLive = timeToLive;
        this.hops = hops;
        this.payloadLength = payloadLength;
        this.portNum = portNum;
        this.ipAddr = ipAddress;
        this.sharedFiles = sharedFiles;
        this.kilobytesShared = kilobytesShared;
    }

//    public PongMessage(byte[] id, byte typeID, byte ttl, byte hops, byte payloadLength, int port, byte[] ip, byte sharedFiles, int kbShared) {
//        this.pingID = id;
//        this.timeToLive = ttl;
//        this.hops = hops;
//        this.payloadLength = payloadLength;
//        this.portNum = port;
//        this.ipAddr = ip;
//        this.sharedFiles = sharedFiles;
//        this.kilobytesShared = kbShared;
//    }

    public PongMessage() {
    }


    //GETTR N SETTR
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

    public byte[] getIpAddress() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ipAddress);

        return inetAddress.getAddress();
    }

    public byte getSharedFiles() {
        return (byte) sharedFiles;
    }

    public byte getKilobytesShared() {
        return (byte) kilobytesShared;
    }



    public static void respond(byte[] pingID, byte ttl, byte hops, InetSocketAddress addr) throws IOException {

        byte payloadLength = (byte) 13;

        int port = 8282; // change this to read from the conif file
        byte[] ip = getIp();
        byte sharedFiles = (byte) 66; // change to get from a saved data file
        int kbShared = 12354;

        PongMessage pong = new PongMessage(pingID, TYPE_ID, ttl, hops, payloadLength, port, ip, sharedFiles, kbShared);


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

        // Write the message type (Ping)
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

    @Override
    public void process() {
        System.out.println("processing pong");

    }

    @Override
    public void create() {

    }
}