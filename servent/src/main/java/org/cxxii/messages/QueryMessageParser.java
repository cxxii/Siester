package org.cxxii.messages;

import java.io.IOException;
import java.net.InetSocketAddress;

public class QueryMessageParser implements MessageParser{
    @Override
    public MessageAbstract parse(byte[] header, byte[] payload, InetSocketAddress addr) throws IOException {
        return null;
    }
}
