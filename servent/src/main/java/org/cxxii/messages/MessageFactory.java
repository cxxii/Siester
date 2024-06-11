package org.cxxii.messages;

import org.cxxii.server.SocketAddr;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public interface MessageFactory {

    //throw bad packet exceptions
//    @Override
//    public MessageAbstract read(InputStream in) throws IOException {
//        byte[] buf = new byte[23];
//        int length = in.read(buf);
//        if (length != 23) throw new IOException("Incomplete header");
//
//        byte func = buf[16];
//        MessageParser parser = getParser(func);
//        if (parser == null) throw new IOException("Unknown function ID: " + func);
//
//        return parser.parse(buf);
//    }
    MessageAbstract read(InputStream in, InetSocketAddress addr) throws IOException;

    MessageAbstract createMessage(byte[] header, byte[] payload, SocketAddress addr) throws IOException;

    byte[] createHeader(byte[] guid, byte func, byte ttl, byte hops, int payloadLength);


    //public static void createMessage(byte timeToLive, byte hops, String ipAddress, int port, int numberOfFilesShared, int numberOfKilobytesShared) {}


//    public static MessageAbstract createMessage(String type, byte timeToLive, byte hops) {
//        switch (type) {
//            case "PING":
//                return new PingMessage(timeToLive, hops);
//            case "PONG":
//                System.out.println("pong");
//            // Add cases for other message types as needed
//            default:
//                throw new IllegalArgumentException("Unknown message type: " + type);
//        }
//    }
}

