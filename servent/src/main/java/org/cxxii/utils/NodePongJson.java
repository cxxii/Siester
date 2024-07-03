package org.cxxii.utils;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class NodePongJson {

    private int portNum;
    private String ipAddress;
    private byte sharedFiles;
    private int kilobytesShared;

    public NodePongJson(int portNum, String ipAddress, byte sharedFiles, int kilobytesShared) {
        this.portNum = portNum;
        this.ipAddress = ipAddress;
        this.sharedFiles = sharedFiles;
        this.kilobytesShared = kilobytesShared;
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
}
