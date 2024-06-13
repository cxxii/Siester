package org.cxxii.messages;

import java.util.UUID;

// Potentially remove this and let messages be created w/ individual parts

public class Header {

    private UUID id;
    private String payloadType = null;
    private static int TTL = 7;
    private int hops;
    private int payloadLength;

//    public Header(String payloadType, int hops, int payloadLength) {
//        this.id = UUID.randomUUID();
//        this.payloadType = payloadType;
//        this.hops = hops;
//        this.payloadLength = payloadLength;
//    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPayloadType() {
        return payloadType;
    }

    public void setPayloadType(String payloadType) {
        this.payloadType = payloadType;
    }

    public static int getTTL() {
        return TTL;
    }

    public static void setTTL(int TTL) {
        Header.TTL = TTL;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(int payloadLength) {
        this.payloadLength = payloadLength;
    }


    public static byte[] createHeader(byte[] guid, byte func, byte ttl, byte hops, int payloadLength) {
        if (guid == null || guid.length != 16) {
            throw new IllegalArgumentException("GUID must be 16 bytes long.");
        }

        byte[] header = new byte[23];
        System.arraycopy(guid, 0, header, 0, 16);
        header[16] = func;
        header[17] = ttl;
        header[18] = hops;
        header[19] = (byte) (payloadLength & 0xFF);
        header[20] = (byte) ((payloadLength >> 8) & 0xFF);
        header[21] = (byte) ((payloadLength >> 16) & 0xFF);
        header[22] = (byte) ((payloadLength >> 24) & 0xFF);
        return header;
    }
}
