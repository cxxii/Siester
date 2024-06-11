package org.cxxii.messages;


import java.io.IOException;

public interface MessageInterface_Delete {

    // think this is set to 35
    byte[] serializeMessage() throws IOException;
    byte[] convertUUID();

    Payload payload();


}







//    public static final byte PING = (byte) 0x0;
//
//    public static final byte PING_REPLY = (byte) 0x1;

//    HEADER FIELDS
//    The message header is 23 bytes divided into the following fields.
//
//    Bytes:  Description:
//    0-15    Message ID/GUID (Globally Unique ID)
//    16      Payload Type
//    17      TTL (Time To Live)
//    18      Hops
//    19-22   Payload Length





//    public static byte[] serializeMessage(UUID id, byte payloadType) throws IOException {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream out = new ObjectOutputStream(bos);
//
//        out.writeObject(id);
//        out.writeObject(payloadType);
//
//        out.flush();
//
//        return bos.toByteArray();
//    }

//    public static Object deserializeMessage(byte[] msg) throws IOException, ClassNotFoundException {
//        ByteArrayInputStream bis = new ByteArrayInputStream(msg);
//        ObjectInputStream in = new ObjectInputStream(bis);
//        return in.readObject();
//    }

//    public static void getType(byte[] msg, int n) {
//        System.out.println(msg[n]);
//    }


    //    methods needs for the ping/pong


//This class will contain common properties and methods shared by all message types, such as a header and payload.
//It should define abstract methods for processing the message
// , which concrete subclasses (Ping, Pong, Query) will implement.







//An incoming Ping message with TTL = 1 and Hops = 0 or 1 is
//      used to probe the remote host of a connection, and MUST
//      always be replied to with a pong having information about the
//      host who received the ping.

// An incoming Ping message with TTL = 2 and Hops = 0 is a
//      "Crawler Ping" used to scan the network. It and SHOULD be
//      replied to with pongs containing information about the host
//      receiving the ping and all other hosts it is connected to. The
//      information about neighbour nodes can be provided either by
//      creating pongs on their behalf, or by forwarding the ping to
//      them, and forward the pongs returned to the crawler.