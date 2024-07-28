package org.cxxii.messages;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

public abstract class MessageAbstract {

    private UUID messageID;
    byte[] bytesMessageID;
    private byte typeId;
    private byte timeToLive;
    private byte hops;
    private byte payloadLength;
    //private byte[] payload;


    /**
     * Constructor on NEW messages
     * @param typeId
     * @param timeToLive
     * @param hops
     * @param payloadLength
     * param payload -- add back
     */
    // removed payload - may create header/payload classes and create object through them but for now piece together element individually
    public MessageAbstract(byte typeId, byte timeToLive, byte hops, byte payloadLength) {
        this.messageID = UUID.randomUUID();
        this.typeId = typeId;
        this.timeToLive = timeToLive;
        this.hops = hops;
        this.payloadLength = payloadLength;
        //this.payload = payload;
    }

    /**
     * When passing a message onwards
     * @param bytesMessageID
     * @param typeId
     * @param timeToLive
     * @param hops
     * @param payloadLength
     * param payload -- add back
     */
    public MessageAbstract(byte[] bytesMessageID, byte typeId, byte timeToLive, byte hops, byte payloadLength) {
        this.bytesMessageID = bytesMessageID;
        this.typeId = typeId;
        this.timeToLive = timeToLive;
        this.hops = hops;
        this.payloadLength = payloadLength;
        //this.payload = payload;
    }


    public MessageAbstract() {
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

    public static void ttlAndHopsIncrementor(MessageAbstract messageAbstract) {

        messageAbstract.setTimeToLive((byte) (messageAbstract.getTimeToLive() - 1));
        messageAbstract.setHops((byte) (messageAbstract.getHops() + 1));
    }
}

