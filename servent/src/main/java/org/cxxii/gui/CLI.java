package org.cxxii.gui;

import org.cxxii.messages.QueryMessage;
import org.cxxii.utils.HostCacheReader;

import java.util.Scanner;
import java.util.logging.Logger;

public class CLI {

    public static Scanner scanner = new Scanner(System.in);

    public static void loop() {
        boolean running = true;

        while (running) {
            menu();
            int choice = userInput();

            switch (choice) {
                case 1:
                    System.out.printf("Enter Search term: ");
                    QueryMessage.makeQuery(scanner);
                    break;
                case 2:
                    System.out.println("Network size: " + HostCacheReader.getNetworkSize());
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }

        System.out.println("Exiting program.");
    }

    public static int userInput() {
        int choice = -1;

        try {
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline left-over
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Clear the invalid input
        }
        System.out.println();

        return choice;
    }

    private static void menu() {
        System.out.println("*** MENU OPTIONS ***");
        System.out.println("1 - Search...");
        System.out.println("2 - Get network size");
        System.out.println("3 - QUIT...");
        System.out.printf("PRESS KEY... ");
    }

    public static int getFileSelection() {
        int choice = -1;

        try {
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline left-over
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // Clear the invalid input
        }
        System.out.println();

        return choice;
    }
}






//package org.cxxii.gui;
//
//import org.cxxii.messages.QueryMessage;
//import org.cxxii.utils.HostCacheReader;
//
//import java.util.Scanner;
//
//public class CLI {
//
//    public static void loop(Scanner scanner) {
//        boolean running = true;
//
//        while (running) {
//            menu();
//            int choice = userInput();
//
//            switch (choice) {
//                case 1:
//                    System.out.printf("Enter Search term: ");
//                    //String searchTerm = searchQuery();
//                    QueryMessage.makeQuery(scanner);
//                    break;
//                case 2:
//                    System.out.println("Network size: " + HostCacheReader.getNetworkSize());
//                    break;
//                case 3:
//                    running = false;
//                    break;
//                default:
//                    System.out.println("Invalid choice. Please try again.");
//                    break;
//            }
//        }
//
//        System.out.println("Exiting program.");
//    }
//
//    public static int userInput() {
//        Scanner scanner = new Scanner(System.in);
//        int choice = -1;
//
//        try {
//            choice = scanner.nextInt();
//        } catch (Exception e) {
//            System.out.println("Invalid input. Please enter a number.");
//            scanner.next(); // Clear the invalid input
//        }
//        System.out.println();
//
//
//        return choice;
//    }
//
//    private static void menu() {
//        System.out.println("*** MENU OPTIONS ***");
//        System.out.println("1 - Search...");
//        System.out.println("2 - Get network size");
//        System.out.println("3 - QUIT...");
//        System.out.printf("PRESS KEY... ");
//    }
//}
