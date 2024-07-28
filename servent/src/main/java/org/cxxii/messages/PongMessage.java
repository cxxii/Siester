package org.cxxii.messages;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;
import org.cxxii.json.HostsJson;
import org.cxxii.json.NodePongJson;
import org.cxxii.network.Network;
import org.cxxii.utils.FileManager;
import org.cxxii.utils.HostCacheWriter;
import org.cxxii.utils.Json;
import org.cxxii.utils.PongCacheReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PongMessage extends MessageAbstract {

    private final static Logger LOGGER = LoggerFactory.getLogger(PongMessage.class);
    private static final byte TYPE_ID = (byte) 0x01;
    private static final int HEADER_LENGTH = 23;
    private static final byte PAYLOAD_LENGTH = (byte) 14;
    private byte[] pingID;
    private int payloadLength;
    private byte timeToLive;
    private byte hops;
    private int portNum;
    private byte[] ipAddress;
    private String ipAddressString;
    private byte sharedFiles;
    private int kilobytesShared;

    public PongMessage(byte[] pingID, byte TYPE_ID, byte timeToLive, byte hops, byte payloadLength, int portNum, byte[] ipAddress, byte sharedFiles, int kilobytesShared) {
        super(TYPE_ID, timeToLive, hops, payloadLength);
        this.pingID = pingID;
        this.portNum = portNum;
        this.ipAddress = ipAddress;
        this.sharedFiles = sharedFiles;
        this.kilobytesShared = kilobytesShared;
    }

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

    public PongMessage(byte[] bytesMessageID, byte timeToLive, byte hops, int portNum, String ipAddress, byte sharedFiles, int kilobytesShared) {
        this.bytesMessageID = bytesMessageID;
        this.portNum = portNum;
        this.ipAddressString = ipAddress;
        this.sharedFiles = sharedFiles;
        this.kilobytesShared = kilobytesShared;
        this.timeToLive = timeToLive;
        this.hops = hops;

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

    public static void respond(byte[] pingID, byte timeToLive, byte hops, InetSocketAddress addr) throws IOException {
        LOGGER.info("RESPOND START");

        int sharedFiles = 0;
        int kbShared = 0;

        JsonObject serverConfig = Json.readJsonFromClasspath("serverconfig.json");
        int port = Network.getActivePort();
        byte[] ip = Network.getLocalIpAddress();

        PongMessage pong = new PongMessage(pingID, TYPE_ID, timeToLive, hops, PAYLOAD_LENGTH, port, ip, (byte) sharedFiles, kbShared);

        pong.sendPongMessage(addr);
        LOGGER.info("SENT PONG: " + pong.toString() + " to " + addr.getHostString());
        LOGGER.info("RESPOND END");
    }

    public static void sendPongCache(InetSocketAddress addr, PingMessage ping) throws UnknownHostException {
        LOGGER.info("Sending Pongs from Cache to " + addr.getHostString());


        List<NodePongJson> listOfPongs = PongCacheReader.readPongCache();


        for (NodePongJson node : listOfPongs) {

            PongMessage pong = new PongMessage(
                    ping.getBytesMessageID(),
                    ping.getTimeToLive(),
                    node.getHops(),
                    node.getPortNum(),
                    node.getIpAddress(),
                    node.getSharedFiles(),
                    node.getKilobytesShared());


            pong.sendPongMessage(addr);
        }
    }

    private byte[] serializeMessage() throws IOException {
        if (this.getPingID() == null && this.getBytesMessageID() == null) {
            throw new NullPointerException("pingID is null");
        }


        ByteBuffer buffer = ByteBuffer.allocate(HEADER_LENGTH + PAYLOAD_LENGTH);
        buffer.order(ByteOrder.BIG_ENDIAN);

        buffer.put(pingID != null ? pingID : bytesMessageID);
        buffer.put(TYPE_ID);
        buffer.put(getTimeToLive());
        buffer.put(getHops());
        buffer.putInt(PAYLOAD_LENGTH);
        buffer.putShort((short) portNum);
        buffer.put(ipAddress != null ? ipAddress : InetAddress.getByName(ipAddressString).getAddress());
        buffer.putInt(sharedFiles);
        buffer.putInt(kilobytesShared);

        return buffer.array();
    }

    public void sendPongMessage(InetSocketAddress addr) {

        String ip = addr.getAddress().getHostAddress();
        int port = Network.getActivePort(); // TODO unfudge this

        try (Socket socket = new Socket(ip, port);
             OutputStream outputStream = socket.getOutputStream()) {

            outputStream.write(this.serializeMessage());
            LOGGER.info("Successfully sent Pong message to  {}:{}", ip, port);

        } catch (IOException e) {
            LOGGER.error("Failed to send Pong message to  {}:{}", ip, port, e);
        }
    }

    public PongMessage process(InetSocketAddress addr) {
        LOGGER.info("Processing PONG...");

        try {
            HostsJson hostsJson = new HostsJson(ipAddressString, portNum);
            HostCacheWriter.appendPongToHostCache(hostsJson);

            File file = getPongCacheFile(addr);
            NodePongJson nodePongJson = new NodePongJson(portNum, ipAddressString, sharedFiles, kilobytesShared, hops);

            List<NodePongJson> pongList = readExistingPongs(file);
            pongList.add(nodePongJson);

            writePongsToFile(file, pongList);

            LOGGER.info("Pong JSON appended to existing file.");
        } catch (IOException e) {
            LOGGER.error("Error processing pong", e);
        }

        return this;
    }

    // move?
    private File getPongCacheFile(InetSocketAddress addr) {
        String filename = addr.getHostString().replace('.', '_') + ".json";
        File path = new File(FileManager.getNodePongDirPath().toString());
        return new File(path, filename);
    }

    // move??
    private List<NodePongJson> readExistingPongs(File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
            return new ArrayList<>();
        }

        ObjectMapper mapper = new ObjectMapper();
        if (Files.size(file.toPath()) > 0) {
            JsonNode jsonNode = mapper.readTree(Files.newBufferedReader(file.toPath()));
            return mapper.convertValue(jsonNode, new TypeReference<List<NodePongJson>>() {});
        }

        return new ArrayList<>();
    }

    // move??
    private void writePongsToFile(File file, List<NodePongJson> pongList) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(pongList, writer);

            LOGGER.info("Writing PONG to file");
        }
    }

    @Override
    public String toString() {
        return "PongMessage{" +
                "pingID=" + Arrays.toString(pingID) +
                "PONG ID=" + Arrays.toString(bytesMessageID) +
                ", payloadLength=" + payloadLength +
                ", timeToLive=" + timeToLive +
                ", hops=" + hops +
                ", portNum=" + portNum +
                ", ipAddress=" + Arrays.toString(ipAddress) +
                ", ipAddressString='" + ipAddressString + '\'' +
                ", sharedFiles=" + sharedFiles +
                ", kilobytesShared=" + kilobytesShared +
                '}';
    }
}

