package org.cxxii.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

//TODO Fix the socketarr refs

public class SocketAddr{
    private final InetAddress ip;
    private final int port;

    public SocketAddr(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }
}