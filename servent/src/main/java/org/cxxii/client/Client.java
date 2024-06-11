package org.cxxii.client;

import org.cxxii.messages.Ping;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    String serverAddress = "localhost"; // Address of the server
    int port = 65432; // Port on which the server is listening

//    Ping myping = new Ping();

    public static void start(Ping aping) throws IOException{
        byte[] msg = aping.serializeMessage();
        for (byte b : msg) {
            System.out.print(Integer.toHexString(b & 0xFF) + " ");
        }

        System.out.println("\n");

        try {
            // Create a socket to connect to the server
            Socket socket = new Socket("localhost", 65432);

            // Get the output stream of the socket
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(msg);

            System.out.println(msg.length);

            System.out.println("Message sent to the server: " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}