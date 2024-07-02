package org.cxxii.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.cxxii.network.Network;
import org.cxxii.server.Server;
import org.cxxii.server.SocketAddr;
import org.cxxii.server.config.Config;
import org.cxxii.utils.FileManager;
import org.cxxii.utils.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

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
//    public PongMessage(byte[] bytesMessageID, byte TYPE_ID, byte timeToLive, byte hops, byte payloadLength, int portNum, byte[] ipAddress, byte sharedFiles, int kilobytesShared) {
//        super(bytesMessageID, TYPE_ID, timeToLive, hops, payloadLength);
//        this.portNum = portNum;
//        this.ipAddress = ipAddress;
//        this.sharedFiles = sharedFiles;
//        this.kilobytesShared = kilobytesShared;
//    }

    public PongMessage(byte[] pingID, byte TYPE_ID, byte timeToLive, byte hops, byte payloadLength, int portNum, byte[] ipAddress, byte sharedFiles, int kilobytesShared) {
        super(TYPE_ID, timeToLive, hops, payloadLength);
        this.pingID = pingID;
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
        LOGGER.info("RESPOND START");
        try {

            JsonObject hostDetails = Json.readJsonFromFile(FileManager.getHostDetailsPath().toString());
            int sharedFiles = hostDetails.get("NumSharedFiles").getAsInt();
            int kbShared = hostDetails.get("NumkilobytesShared").getAsInt();

            JsonObject serverConfig = Json.readJsonFromClasspath("serverconfig.json");
            int port = serverConfig.get("port").getAsInt();
            byte[] ip = Network.getLocalIpAddress();


            PongMessage pong = new PongMessage(pingID, TYPE_ID, timeToLive, hops, PAYLOAD_LENGTH, port, ip, (byte) sharedFiles, kbShared); // not getting payload len
            // ttl and hop nos set to 0,0


            pong.sendPongMessage(addr);

            LOGGER.info("SENT PONG: " + pong.toString() + " to " + addr.getHostString());

            LOGGER.info("RESPOND END");

        } catch (IOException e) {
            LOGGER.error("Error responding to ping", e);
        }
    }

    private void sendPongMessage(InetSocketAddress addr) {
        LOGGER.info("SEND PONG MESSAGE START");
        String ip = addr.getAddress().getHostAddress();
        int port = 6364;

        // Log details before sending
        LOGGER.debug("Attempting to PONG " + ip + " " + port);
        LOGGER.debug("Pong message ID: " + Arrays.toString(this.getPingID()));
        LOGGER.debug("SENDPONGMESSAGE = " + this);

        try (Socket socket = new Socket(ip, port);
             OutputStream outputStream = socket.getOutputStream()) {

            outputStream.write(this.serializeMessage());
            outputStream.close();

            LOGGER.info("Successfully sent Pong message to " + ip + ":" + port);

            LOGGER.info("SEND PONG MESSAGE END");

        } catch (IOException e) {
            LOGGER.error("Failed to send Pong message to " + ip + ":" + port, e);
        }
    }


//    private static void sendPongMessage(PongMessage pong, InetSocketAddress addr) {
//        String ip = addr.getAddress().getHostAddress();
//        int port = 6364;
//        LOGGER.debug("TRYING TO SEND TO " + ip + " " + port);
//
//        try (Socket socket = new Socket(ip, port);
//             OutputStream outputStream = socket.getOutputStream()) {
//
//            outputStream.write(pong.serializeMessage());
//            outputStream.flush();
//
//        } catch (IOException e) {
//            LOGGER.error("Failed to send Pong message to " + ip + ":" + port, e);
//        }
//    }

//    private byte[] serializeMessage() throws IOException {
//        if (this.getPingID() == null) {
//            throw new NullPointerException("pingID is null");
//        }
//        if (this.getIpAddress() == null) {
//            throw new NullPointerException("ipAddressBytes is null");
//        }
//
//        ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH + PAYLOAD_LENGTH); // 23 + 13
//        buffer.order(ByteOrder.BIG_ENDIAN);
//
//
//        buffer.put(this.getPingID());
//
//        buffer.put(TYPE_ID);
//
//        buffer.put(this.getTimeToLive());
//
//        buffer.put(this.getHops());
//
//        buffer.putInt(PAYLOAD_LENGTH);
//
//        buffer.putShort(this.getPortNum()); // this might need to be 6364
//
//        buffer.put(Network.getLocalIpAddress());
//
//        buffer.put(this.getSharedFiles());
//
//        buffer.putInt(this.getKilobytesShared());
//
//        return buffer.array();
//    }

//    private byte[] serializeMessage() throws IOException {
//        if (this.getPingID() == null) {
//            throw new NullPointerException("pingID is null");
//        }
//        if (this.getIpAddress() == null) {
//            throw new NullPointerException("ipAddressBytes is null");
//        }
//
//        // Correct buffer size calculation
//        final int HEADER_LENGTH = 23;
//        final int PAYLOAD_LENGTH = 13;
//        ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH + PAYLOAD_LENGTH); // 23 + 13 = 36 bytes
//        buffer.order(ByteOrder.BIG_ENDIAN);
//
//        buffer.put(this.getPingID()); // 16 bytes
//        buffer.put(TYPE_ID); // 1 byte
//        buffer.put(this.getTimeToLive()); // 1 byte
//        buffer.put(this.getHops()); // 1 byte
//        buffer.putInt(PAYLOAD_LENGTH); // 1 byte
//        buffer.putShort(this.getPortNum()); // 2 bytes
//        buffer.put(this.getIpAddress()); // 4 bytes
//        buffer.put(this.getSharedFiles()); // 1 byte
//        buffer.putInt(this.getKilobytesShared()); // 4 bytes
//
//        return buffer.array();
//    }

    private byte[] serializeMessage() throws IOException {
        if (this.getPingID() == null) {
            throw new NullPointerException("pingID is null");
        }
        if (this.getIpAddress() == null) {
            throw new NullPointerException("ipAddressBytes is null");
        }

        final int HEADER_LENGTH = 23;
//        final int PAYLOAD_LENGTH = 13;
        ByteBuffer buffer = ByteBuffer.allocate(37); // TODO - REMVE HARDCODE
        buffer.order(ByteOrder.BIG_ENDIAN);

        LOGGER.debug("Allocating buffer of size: " + (HEADER_LENGTH + PAYLOAD_LENGTH));

        buffer.put(this.getPingID()); // 16 bytes
        buffer.put(TYPE_ID); // 1 byte
        buffer.put(this.getTimeToLive()); // 1 byte  // regain the 7 7 on ttll and hops
        buffer.put(this.getHops()); // 1 byte
        buffer.putInt(14); // 4 bytes // TODO remove - hardcode

        buffer.putShort((short)6364); // 2 bytes // TODO - remove hardcode
        buffer.put(this.getIpAddress()); // 4 bytes
        buffer.put(this.getSharedFiles()); // 1 byte
        buffer.putInt(this.getKilobytesShared()); // 4 bytes

        byte[] serializedMessage = buffer.array();

        LOGGER.debug("PingID: " + Arrays.toString(this.getPingID()));
        LOGGER.debug("IP Address: " + Arrays.toString(this.getIpAddress()));
        LOGGER.debug("Port Number: " + this.getPortNum());
        LOGGER.debug("Shared Files: " + this.getSharedFiles()); // OK
        LOGGER.debug("Kilobytes Shared: " + this.getKilobytesShared()); // OK
        LOGGER.debug("Serialized message: " + Arrays.toString(serializedMessage));  // OK
        LOGGER.debug("Serialized message length: " + serializedMessage.length); // OK

        return serializedMessage;
    }

    public PongMessage process(InetSocketAddress addr) throws IOException {

        String filename =  addr.getHostString() + addr.getPort();

        File path = new File(String.valueOf(FileManager.getNodePongDirPath()));

        File file = new File(path, filename);

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    LOGGER.info("Created Pong file: " + file.getAbsolutePath());

                    try {
                        FileWriter writer = new FileWriter(file);

                        //finish


                    } catch (IOException e) {
                        LOGGER.error("Could not write Pong data: " + file.getAbsolutePath());
                    }

                }
            } catch (IOException e) {
                LOGGER.error("Failed to create Pong file: " + file.getAbsolutePath());
            }

        }


        //get the Ip and port as string
        // check if this exists in the file systems
        // no - use that to create file name
        // yes - overwrite oldest bit of info if there is less than 10 pong infomation (also add date time its written)


        return this;
    }

    // List<Pong> return
    private static void  readNodePong(File nodePong) {
        LOGGER.info("Reading Node Pong");

        Gson gson = new Gson();
        List<PongCache> pongs = null;

        try (BufferedReader br = new BufferedReader(new FileReader(nodePong))) {

            JsonReader jsonReader = new JsonReader(br);
            Type listType = new TypeToken<List<PongCache>>() {
            }.getType();
            pongs = gson.fromJson(jsonReader, listType);

        } catch (IOException e) {

            LOGGER.error("Could not read nodePong", e);
        }

        //return pongs;
    }

    @Override
    public String toString() {
        return "PongMessage{" +
                "pingID=" + Arrays.toString(pingID) +
                ", PayloadLength=" + PayloadLength +
                ", timeToLive=" + timeToLive +
                ", hops=" + hops +
                ", portNum=" + portNum +
                ", sharedFiles=" + sharedFiles +
                ", kilobytesShared=" + kilobytesShared +
                '}';
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