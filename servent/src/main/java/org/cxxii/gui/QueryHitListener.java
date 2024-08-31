package org.cxxii.gui;

import java.util.List;

public interface QueryHitListener {
    void onQueryHitReceived(List<String> fileNames);
}
