package org.cxxii.gui;

import org.cxxii.messages.QueryMessage;

import java.util.Scanner;

public class CLI {


    public static void loop() {



        while (true) {
            menu();
        }

    }

    private static int userInput() {
        Scanner scanner = new Scanner(System.in);

        int x = scanner.nextInt();
        System.out.println();

        return x;
    }

    private static String searchQuery() {
        Scanner scanner = new Scanner(System.in);

        return scanner.nextLine();
    }



    private static void menu() {

        System.out.println("*** MENU OPTIONS ***");
        System.out.println("1 - Search...");
        System.out.println("2 - XYZ...");
        System.out.println("3 - QUIT...");
        System.out.printf("PRESS KEY... ");

        switch (userInput()) {
            case 1:
                System.out.printf("Enter Search term:  ");
//                String sq = searchQuery();
                QueryMessage.makeQuery();
            case 2:

            case 3:
                System.exit(0);
        }
    }
}
