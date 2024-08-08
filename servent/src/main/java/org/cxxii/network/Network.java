package org.cxxii.network;

import com.google.gson.JsonObject;
import org.cxxii.messages.PongMessage;
import org.cxxii.utils.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

public class Network {



    private final static Logger LOGGER = LoggerFactory.getLogger(Network.class);

    public static byte[] getLocalIpAddress() throws SocketException {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface networkInterface : Collections.list(networkInterfaces)) {
            if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getAddress();
                    }
                }
            }
        }
        return null; // No suitable address found
    }

    public static String getLocalIpString() throws SocketException, UnknownHostException {

        return InetAddress.getByAddress(Network.getLocalIpAddress()).getHostAddress();
    }

    public static int getActivePort() {

        int port = 0;

        try {

            JsonObject serverConfig = Json.readJsonFromClasspath("serverconfig.json");
            port = serverConfig.get("port").getAsInt();

        } catch (IOException e) {

            LOGGER.error("Could not get port from serverconfig");

        }

        return port;
    }
}

