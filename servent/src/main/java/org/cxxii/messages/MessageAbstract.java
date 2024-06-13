package org.cxxii.messages;

import java.util.UUID;

public abstract class MessageAbstract {

    private UUID messageID;
    private byte[] bytesMessageID;
    private byte typeId;
    private byte timeToLive;
    private byte hops;
    private int payloadLength;
    private byte[] payload;


    /**
     * Constructor on NEW messages
     * @param typeId
     * @param timeToLive
     * @param hops
     * @param payloadLength
     * @param payload
     */
    public MessageAbstract(byte typeId, byte timeToLive, byte hops, int payloadLength, byte[] payload) {
        this.messageID = UUID.randomUUID();
        this.typeId = typeId;
        this.timeToLive = timeToLive;
        this.hops = hops;
        this.payloadLength = payloadLength;
        this.payload = payload;
    }

    /**
     * When passing a message onwards
     * @param bytesMessageID
     * @param typeId
     * @param timeToLive
     * @param hops
     * @param payloadLength
     * @param payload
     */
    public MessageAbstract(byte[] bytesMessageID, byte typeId, byte timeToLive, byte hops, int payloadLength, byte[] payload) {
        this.bytesMessageID = bytesMessageID;
        this.typeId = typeId;
        this.timeToLive = timeToLive;
        this.hops = hops;
        this.payloadLength = payloadLength;
        this.payload = payload;
    }

    public UUID getMessageID() {
        return messageID;
    }

    public byte[] getBytesMessageID() {
        return bytesMessageID;
    }

    public byte getTypeId() {
        return typeId;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(byte timeToLive) {
        this.timeToLive = timeToLive;
    }

    public byte getHops() {
        return hops;
    }

    public void setHops(byte hops) {
        this.hops = hops;
    }
}
