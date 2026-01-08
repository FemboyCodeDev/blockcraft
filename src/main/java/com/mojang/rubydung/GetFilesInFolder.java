package com.mojang.rubydung;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GetFilesInFolder {
    public static void main(String[] args) {
        // Specify the path to the folder you want to search for files
        String folderPath = "/path/to/folder";

        // Create a new File object to represent the folder
        File folder = new File(folderPath);

        // Check if the folder exists
        if (folder.exists()) {
            // Get a list of all files in the folder
            List<String> filenames = getFilenamesInFolder(folder);

            // Print out each filename
            for (String filename : filenames) {
                System.out.println(filename);
            }
        } else {
            System.out.println("The specified folder does not exist.");
        }
    }

    public static List<String> getFilenamesInFolder(File folder) {
        List<String> filenames = new ArrayList<>();

        // Check if the folder exists
        if (folder.exists()) {
            // Get a list of all files in the folder
            File[] fileArray = folder.listFiles();
            if (fileArray != null) {
                for (File file : fileArray) {
                    if (file.isFile()) {
                        filenames.add(file.getName());
                    }
                }
            } else {
                System.out.println("Error: Unable to list files in " +
                        folder.getAbsolutePath());
            }
        } else {
            System.out.println("The specified folder does not exist.");
        }

        return filenames;
    }
}
