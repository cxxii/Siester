package org.cxxii.gui;

import org.cxxii.messages.QueryMessage;
import org.cxxii.utils.HostCacheReader;
import org.cxxii.utils.ScannerSingleton;

import java.util.Scanner;
import java.util.logging.Logger;

public class CLI {



    public static void loop() {
        boolean running = true;

        while (running) {
            menu();

            Scanner scanner = ScannerSingleton.getInstance();  // Get a new Scanner instance each iteration
            int choice = -1;

            while (true) {
                try {
                    if (scanner.hasNextInt()) {
                        choice = scanner.nextInt();
                        scanner.nextLine();
                        break;
                    } else {
                        System.out.println("Invalid input. Please enter a valid option.");
                        scanner.next();
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred while reading input. Please try again.");
                    scanner.next();
                }
            }

            switch (choice) {
                case 1:
                    boolean subMenu1 = true;
                    while (subMenu1) {



                        System.out.println(Thread.currentThread());
                        System.out.println("search term...");
                        System.out.println(Thread.currentThread());
                        String searchQuery = scanner.nextLine();
                        QueryMessage.makeQuery(searchQuery);



                        System.out.println("Press 0 to go back to the main menu.");
                        int subChoice = scanner.nextInt();
                        scanner.nextLine();
                        if (subChoice == 0) {
                            subMenu1 = false;
                        }
                    }
                    break;
                case 2:
                    boolean subMenu2 = true;
                    while (subMenu2) {
                        System.out.println("Network size: " + HostCacheReader.getNetworkSize());
                        System.out.println("Press 0 to go back to the main menu.");
                        int subChoice = scanner.nextInt();
                        scanner.nextLine();
                        if (subChoice == 0) {
                            subMenu2 = false;
                        }
                    }
                    break;
                case 3:
                    System.out.println("Downloads");
                case 4:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        System.out.println("Exiting program.");
    }

    private static void menu() {
        System.out.println("*** MENU OPTIONS ***");
        System.out.println("1 - Search...");
        System.out.println("2 - Get network size");
        System.out.println("3 - Show downloads");
        System.out.println("4 - QUIT...");
        System.out.print("PRESS KEY... ");
    }
}
