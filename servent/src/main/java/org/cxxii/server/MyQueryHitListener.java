package org.cxxii.server;

import org.cxxii.gui.QueryHitListener;

import java.util.List;

public class MyQueryHitListener implements QueryHitListener {
    @Override
    public void onQueryHitReceived(List<String> fileNames) {
        // Handle the received query hits
        System.out.println("Query hits received: " + fileNames);
    }
}
