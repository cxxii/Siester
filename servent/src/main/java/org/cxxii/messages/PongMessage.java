package org.cxxii.messages;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.cxxii.utils.NodePongJson;
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
    private static final int HEADER_LENGTH = 23;
    private static final byte PAYLOAD_LENGTH = (byte) 14;




        // HEADERS
    private byte[] pingID;
    private int payloadLength;
    private byte timeToLive;
    private byte hops;

        // Payload
    private int portNum;
    private byte[] ipAddress;
    private String ipAddressString;

    private byte sharedFiles;
    private int kilobytesShared;


    // CONSTRUCTORS
    public PongMessage(byte[] pingID, byte TYPE_ID, byte timeToLive, byte hops, byte payloadLength, int portNum, byte[] ipAddress, byte sharedFiles, int kilobytesShared) {
        super(TYPE_ID, timeToLive, hops, payloadLength);
        this.pingID = pingID;
        this.portNum = portNum;
        this.ipAddress = ipAddress;
        this.sharedFiles = sharedFiles;
        this.kilobytesShared = kilobytesShared;
    }

    public PongMessage(byte timeToLive, byte hops, String ipAddressString, int portNum, byte sharedFiles, int kilobytesShared, byte payloadLength) {
        this.timeToLive = timeToLive;
        this.hops = hops;
        this.ipAddressString = ipAddressString;
        this.portNum = portNum;
        this.sharedFiles = sharedFiles;
        this.kilobytesShared = kilobytesShared;
        this.payloadLength = payloadLength;
    }



    //GETTR N SETTR

    public byte[] getPingID() {
        return pingID;
    }

    public int getPortNum() {
        return portNum;
    }

    public byte[] getIpAddress() {
        return ipAddress;
    }

    public byte getSharedFiles() {
        return sharedFiles;
    }

    public int getKilobytesShared() {
        return kilobytesShared;
    }

    public String getIpAddressString() {
        return ipAddressString;
    }

    // METHODS
    public static void respond(byte[] pingID, byte timeToLive, byte hops, InetSocketAddress addr) {
        LOGGER.info("RESPOND START");
        try {

            JsonObject hostDetails = Json.readJsonFromFile(FileManager.getHostDetailsPath().toString());
            int sharedFiles = hostDetails.get("NumSharedFiles").getAsInt();
            int kbShared = hostDetails.get("NumkilobytesShared").getAsInt(); // WTF

            JsonObject serverConfig = Json.readJsonFromClasspath("serverconfig.json");
            int port = serverConfig.get("port").getAsInt();
            byte[] ip = Network.getLocalIpAddress();


            PongMessage pong = new PongMessage(pingID, TYPE_ID, timeToLive, hops, PAYLOAD_LENGTH, port, ip, (byte) sharedFiles, kbShared); // TODO not getting payload len
            // ttl and hop nos set to 0,0

            pong.sendPongMessage(addr);

            LOGGER.info("SENT PONG: " + pong.toString() + " to " + addr.getHostString());

            LOGGER.info("RESPOND END");

        } catch (IOException e) {
            LOGGER.error("Error responding to ping", e);
        }
    }

    private void sendPongMessage(InetSocketAddress addr) {
        LOGGER.info("Sending PONG...");
        String ip = addr.getAddress().getHostAddress();
        int port = 6364; // TODO remove hardcode

        // Log details before sending
        LOGGER.debug("Attempting to PONG " + ip + " " + port);
        LOGGER.debug("Pong message ID: " + Arrays.toString(this.getPingID()));

        try (Socket socket = new Socket(ip, port);
             OutputStream outputStream = socket.getOutputStream()) {

            outputStream.write(this.serializeMessage());
            outputStream.close();

            LOGGER.info("Successfully sent Pong message to " + ip);


        } catch (IOException e) {
            LOGGER.error("Failed to send Pong message to " + ip, e);
        }
    }


    private byte[] serializeMessage() throws IOException {
        if (this.getPingID() == null) {
            throw new NullPointerException("pingID is null");
        }
        if (this.getIpAddress() == null) {
            throw new NullPointerException("ipAddressBytes is null");
        }

        ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH + PAYLOAD_LENGTH); // TODO - REMVE HARDCODE
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.put(this.getPingID());
        buffer.put(TYPE_ID);
        buffer.put(this.getTimeToLive()); // TODO - regain the 7 7 on ttll and hops
        buffer.put(this.getHops());
        buffer.putInt(PAYLOAD_LENGTH);// TODO remove - hardcode

        buffer.putShort((short)6364);// TODO - remove hardcode
        buffer.put(this.getIpAddress());
        buffer.putInt(this.getSharedFiles());
        buffer.putInt(this.getKilobytesShared());

        byte[] serializedMessage = buffer.array();

        LOGGER.debug("IP Address: " + Arrays.toString(this.getIpAddress()));
        LOGGER.debug("Port Number: " + this.getPortNum());
        LOGGER.debug("Shared Files: " + this.getSharedFiles()); // OK
        LOGGER.debug("Kilobytes Shared: " + this.getKilobytesShared()); // OK
        LOGGER.debug("Serialized message: " + Arrays.toString(serializedMessage));  // OK

        return serializedMessage;
    }


    public PongMessage process(InetSocketAddress addr) throws IOException {
        LOGGER.info("Processing PONG...");

        String filename = addr.getHostString().replace('.', '_') + ".json";
        File path = new File(String.valueOf(FileManager.getNodePongDirPath()));
        File file = new File(path, filename);


        FileManager.CheckAndCreateFilePublic(new File(path, filename));


        NodePongJson nodePongJson = new NodePongJson(this.portNum, this.ipAddressString, this.sharedFiles, this.kilobytesShared);

        JsonNode jsonNode = Json.toJson(nodePongJson);
        String prettyJson = Json.stringifyPretty(jsonNode);

        FileWriter writer = null;

        try {
             writer = new FileWriter(file);


            writer.append(prettyJson);

            LOGGER.debug("WRITTTEN TO FILE " + file.toString());
            LOGGER.debug(prettyJson);


        } catch (IOException e) {
            LOGGER.error("Could not write Pong data: " + file.getAbsolutePath());
        } finally {
            if (writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    LOGGER.error("Failed to close FileWriter", e);
                }
            }
        }


        return this;

    }


        //get the Ip and port as string
        // check if this exists in the file systems
        // no - use that to create file name
        // yes - overwrite oldest bit of info if there is less than 10 pong infomation (also add date time its written)


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
    }
}