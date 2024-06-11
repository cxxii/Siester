package org.cxxii.messages;

import org.cxxii.utils.FileManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Handshake {

    public static void pingHosts() throws IOException {
        Path path = FileManager.getHostCachePath();

        BufferedReader br = new BufferedReader(new FileReader(path.toFile()));

        for (String line = br.readLine(); line != null; line = br.readLine()) {
            System.out.println("f");

        }


    }





}
