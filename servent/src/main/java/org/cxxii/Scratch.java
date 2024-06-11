package org.cxxii;

import org.cxxii.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

class Scratch {

    private final static Logger LOGGER = LoggerFactory.getLogger(Scratch.class);

    public static void main(String[] args) throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();

        for (byte b: ip.getAddress()) {
            System.out.printf("%02X", b);
        }
    }
}