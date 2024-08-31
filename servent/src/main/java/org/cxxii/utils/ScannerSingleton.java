package org.cxxii.utils;

import java.util.Scanner;

public class ScannerSingleton {

    // Removed static scanner instance

    private ScannerSingleton() {}

    public static Scanner getInstance() {
        return new Scanner(System.in);  // Provide a new Scanner instance each time
    }
}
