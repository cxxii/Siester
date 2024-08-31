package org.cxxii.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.cxxii.json.FileResults;
import org.cxxii.messages.MessageFactoryImpl;
import org.cxxii.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FuzzySearch {

    private final static Logger LOGGER = LoggerFactory.getLogger(FuzzySearch.class);
    private static final int THRESHOLD = 3;

    public static List<FileResults> fuzzySearchFiles(String query) {

        List<FileResults> matchedFiles = new ArrayList<>();

        File directory = new File(String.valueOf(FileManager.getUploadDirPath()));

        if (!directory.isDirectory()) {
            System.out.println(directory + " is not a valid directory.");
           // return matchedFiles;
        }

        File[] files = directory.listFiles();

        LOGGER.debug("FILES" + Arrays.toString(files));

        if (files == null) {

            System.out.println("Failed to list files in " + directory);

            return matchedFiles;
        }

        int index = 1;

        for (File file : files) {

            if (isFuzzyMatch(file.getName(), query)) {

               matchedFiles.add(new FileResults(index,file.getName(), (int) file.length(), FilenameUtils.getExtension(file.getName())));

            }

            index++;
        }

        LOGGER.debug("FS " + matchedFiles.size());

        return matchedFiles;
    }


    public static boolean isFuzzyMatch(String fileName, String query) {

        int distance = LevenshteinDistance.calculate(fileName.toLowerCase(), query.toLowerCase());

        return distance <= THRESHOLD;
    }
}
