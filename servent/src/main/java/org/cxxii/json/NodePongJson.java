package org.cxxii.json;

import org.cxxii.messages.PongMessage;
import org.cxxii.network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class NodePongJson {

    private final static Logger LOGGER = LoggerFactory.getLogger(NodePongJson.class);

    private int portNum;
    private String ipAddress;
    private byte sharedFiles;
    private int kilobytesShared;
    private byte hops;

    public NodePongJson(int portNum, String ipAddress, byte sharedFiles, int kilobytesShared, byte hops) {
        this.portNum = portNum;
        this.ipAddress = ipAddress;
        this.sharedFiles = sharedFiles;
        this.kilobytesShared = kilobytesShared;
        this.hops = hops;
    }
    public NodePongJson() {
    }

    public int getPortNum() {
        return portNum;
    }

    public void setPortNum(int portNum) {
        this.portNum = portNum;
    }

    public String getIpAddress() {

        return ipAddress;
    }


    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public byte getSharedFiles() {
        return sharedFiles;
    }

    public void setSharedFiles(byte sharedFiles) {
        this.sharedFiles = sharedFiles;
    }

    public int getKilobytesShared() {
        return kilobytesShared;
    }

    public void setKilobytesShared(int kilobytesShared) {
        this.kilobytesShared = kilobytesShared;
    }

    public byte getHops() {
        return hops;
    }

    public void setHops(byte hops) {
        this.hops = hops;
    }
    @Override
    public String toString() {
        return "NodePongJson{" +
                "portNum=" + portNum +
                ", ipAddress='" + ipAddress + '\'' +
                ", sharedFiles=" + sharedFiles +
                ", kilobytesShared=" + kilobytesShared +
                ", hops=" + hops +
                '}';
    }
}


