package org.cxxii.messages;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class PongCache {


    private static byte[] ipAddress; //changed from to string to Inet
    private int portNum;
    private byte sharedFiles;
    private int kilobytesShared;
    private byte hops;
    private LocalDateTime dateTime;

    public PongCache(int portNum, byte sharedFiles, int kilobytesShared, byte hops, LocalDateTime dateTime) {
        this.portNum = portNum;
        this.sharedFiles = sharedFiles;
        this.kilobytesShared = kilobytesShared;
        this.hops = hops;
        this.dateTime = dateTime;
    }
}
