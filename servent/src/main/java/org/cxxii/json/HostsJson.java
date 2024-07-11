package org.cxxii.json;

import java.net.InetAddress;
import java.util.Objects;

public class HostsJson {

    private String ip;

    private int port;

    public HostsJson() {
    }

    public HostsJson(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HostsJson that = (HostsJson) o;
        return port == that.port && Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

    @Override
    public String toString() {
        return "HostsJson{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
