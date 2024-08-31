package org.cxxii.utils;

import org.apache.commons.io.FilenameUtils;
import org.cxxii.json.FileResults;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UploadManager {

    private static int indexValue = 1;


    public static void main(String[] args) {
        fileIndexer();
    }



    private static List<FileResults> fileIndexer() {
        List<FileResults> uploadedFiles = new ArrayList<>();

        File uploadDir = new File(String.valueOf(FileManager.getUploadDirPath()));

        File[] fileArr = uploadDir.listFiles();

        for (File file : fileArr) {
            uploadedFiles.add(new FileResults(indexValue++ ,file.getName(), (int) file.length(), FilenameUtils.getExtension(file.getName()))); // TODO look at this cast to int?
        }
        return uploadedFiles;
    }

//    DEPRECATED
//    private static void filePrinter() {
//
//
//        for (FileResults file :
//                matchedFiles) {
//            System.out.println("File Index: " + file.getIndex() +
//                    "\nFile Name: " + FilenameUtils.removeExtension(file.getFilename()) +
//                    "\nFile Type: " + FilenameUtils.getExtension(file.getFilename()) +
//                    "\nFile Size: " + file.getFilesize());
//        }
    }


