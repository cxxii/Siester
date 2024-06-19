package org.cxxii.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.cxxii.network.Network;
import org.cxxii.server.Server;
import org.cxxii.server.config.Config;
import org.cxxii.utils.FileManager;
import org.cxxii.utils.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class PongMessage extends MessageAbstract {

    // TODO:
    //  Stay consistent with ip type! need to change top to bottom


    // LOGGER
    private final static Logger LOGGER = LoggerFactory.getLogger(PongMessage.class);


    // CONSTANTS
    private static final byte TYPE_ID = (byte) 0x01;
    public static final int HEADER_LENGTH = 23;
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
    private static byte[] ipAddress; //changed from to string to Inet
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


    public byte[] getPingID() {
        return pingID;
    }

    public byte getPortNum() {
        return (byte) portNum;
    }

    public static byte[] getIpAddress() {
        return ipAddress;
    }

    public byte getSharedFiles() {
        return (byte) sharedFiles;
    }

    public byte getKilobytesShared() {
        return (byte) kilobytesShared;
    }



    // METHODS
    public static void respond(byte[] pingID, byte timeToLive, byte hops, InetSocketAddress addr) {
        try {
            // Load shared files and KB shared from host details
            JsonObject hostDetails = Json.readJsonFromFile(FileManager.getHostDetailsPath().toString());
            byte sharedFiles = hostDetails.get("sharedFiles").getAsByte();
            int kbShared = hostDetails.get("kbShared").getAsInt();

            // Load port and IP from server configuration
            JsonObject serverConfig = Json.readJsonFromClasspath("serverconfig.json");
            int port = serverConfig.get("port").getAsInt();
            byte[] ip = Network.getLocalIpAddress();

            // Create Pong message
            PongMessage pong = new PongMessage(pingID, TYPE_ID, timeToLive, hops, PAYLOAD_LENGTH, port, ip, sharedFiles, kbShared);

            // Send Pong message
            sendPongMessage(pong, addr);
        } catch (IOException e) {
            LOGGER.error("Error responding to ping", e);
        }
    }

    private static void sendPongMessage(PongMessage pong, InetSocketAddress addr) {
        String ip = addr.getAddress().getHostAddress();
        int port = addr.getPort();
        LOGGER.debug("TRYING TO SEND TO " + ip + " " + port);

        try (Socket socket = new Socket(ip, port);
             OutputStream outputStream = socket.getOutputStream()) {

            outputStream.write(pong.serializeMessage());
            outputStream.flush();

        } catch (IOException e) {
            LOGGER.error("Failed to send Pong message to " + ip + ":" + port, e);
        }
    }

    public byte[] serializeMessage() throws IOException {
        if (this.getPingID() == null) {
            throw new NullPointerException("pingID is null");
        }
        if (this.getIpAddress() == null) {
            throw new NullPointerException("ipAddressBytes is null");
        }

        ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH + PAYLOAD_LENGTH); // 23 + 13
        buffer.order(ByteOrder.BIG_ENDIAN);


        buffer.put(this.getPingID());

        buffer.put(TYPE_ID);

        buffer.put(this.getTimeToLive());

        buffer.put(this.getHops());

        buffer.putInt(PAYLOAD_LENGTH);

        buffer.putShort(this.getPortNum());

        buffer.put(Network.getLocalIpAddress());

        buffer.put(this.getSharedFiles());

        buffer.putInt(this.getKilobytesShared());

        return buffer.array();
    }

    public PongMessage process(InetSocketAddress addr){

        String filename =  addr.getHostString() + addr.getPort();



        File
        FileManager.getNodePongDirPath()


        //get the Ip and port as string
        // check if this exists in the file systems
        // no - use that to create file name
        // yes - overwrite oldest bit of info if there is less than 10 pong infomation (also add date time its written)






        return 0;
    }


    // TODO
    //  save incoming host info
    /**
     *
     * save to host cache - done
     * ping onwards - done
     * send own pong back - done
     * check existance of this ips pong cache file create or not
     * send 10 random pings back excluding from this servent - guessing this is fonor bia ip
     */

}