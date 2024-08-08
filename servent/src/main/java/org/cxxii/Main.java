package org.cxxii;

import org.cxxii.gui.SwingApp;
import org.cxxii.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        Logger LOGGER = LoggerFactory.getLogger(Main.class);

        Server.gogogo();
    }
}
