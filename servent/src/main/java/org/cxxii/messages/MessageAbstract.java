package org.cxxii.messages;

import java.util.UUID;

public abstract class MessageAbstract {

    private byte typeId;
    private UUID messageID;
    private byte timeToLive;
    private byte hops;
    private int payloadLength;
    private byte[] payload;

    private Header header;

    public MessageAbstract(Header header, byte typeId, byte timeToLive, byte hops, int payloadLength, byte[] payload) {
        this.header = header;
//        this.messageID = UUID.randomUUID();
        this.typeId = typeId;
        this.timeToLive = timeToLive;
        this.hops = hops;
        this.payloadLength = payloadLength;
        this.payload = payload;
    }


    public MessageAbstract(byte typeId, byte timeToLive, byte hops, int payloadLength, byte[] payload) {
        this.typeId = typeId;
        this.timeToLive = timeToLive;
        this.hops = hops;
        this.payloadLength = payloadLength;
        this.payload = payload;
    }

    public MessageAbstract() {
    }

    public byte getTypeId() {
        return typeId;
    }

    public UUID getMessageID() {
        return messageID;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public byte[] getPayload() {
        return payload;
    }

    public Header getHeader() {
        return header;
    }

    public byte getTimeToLive() {
        return timeToLive;
    }

    public byte getHops() {
        return hops;
    }



    //public abstract void process(byte[] id, byte typeId, byte ttl, byte hops, byte[] payloadLength, byte[] payload);
    public abstract void process();
    public abstract void create();
}
