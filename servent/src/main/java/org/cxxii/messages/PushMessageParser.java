package org.cxxii.messages;

import java.io.IOException;
import java.net.InetSocketAddress;

public class PushMessageParser implements MessageParser{
    @Override
    public void parse(byte[] header, byte[] payload, InetSocketAddress addr) throws IOException {
    }
}
