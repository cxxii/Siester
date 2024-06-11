package org.cxxii.messages;

import org.cxxii.server.SocketAddr;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public interface MessageParser {
    MessageAbstract parse(byte[] header, byte[] payload, InetSocketAddress addr) throws IOException;
}

