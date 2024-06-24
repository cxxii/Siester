package org.cxxii.search;

import jdk.jfr.Threshold;
import org.cxxii.utils.FileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FuzzySearch {
    private static final int THRESHOLD = 4;

    public static List<Long> fuzzySearchFiles(String query) {
        List<Long> matchedFiles = new ArrayList<>();


        File directory = new File(String.valueOf(FileManager.getUploadDirPath()));

        if (!directory.isDirectory()) {
            System.out.println(directory + " is not a valid directory.");
            return matchedFiles;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            System.out.println("Failed to list files in " + directory);
            return matchedFiles;
        }

        for (File file : files) {
            if (isFuzzyMatch(file.getName(), query)) {
                matchedFiles.add(file.length());
            }
        }

        return matchedFiles;
    }

    public static boolean isFuzzyMatch(String fileName, String query) {

        int distance = LevenshteinDistance.calculate(fileName.toLowerCase(), query.toLowerCase());

        return distance <= THRESHOLD;
    }
}
