package com.minesweeper;

import java.util.Scanner;
import java.util.*;
import java.io.*;

public class Scen {
    private ArrayList<String> options;
    private String ID;

    public static class InvalidDescriptionException extends Exception {
        public InvalidDescriptionException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class InvalidValueException extends Exception {
        public InvalidValueException(String errorMessage) {
            super(errorMessage);
        }
    }

    public Scen() {
        this.options = null;
        this.ID = null;
    }

    public ArrayList<String> getOptions() {
        return this.options;
    }

    private void saveToFolder() throws Exception {
        saveToFolder(Globals.folder);
    }

    private void saveToFolder(String folderName) throws Exception {
        if (this.options != null) {
            File dir = new File(folderName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                // Check if the user puts all the inputs
                for (String word : this.options) {
                    if (word.isEmpty()) {
                        throw new InvalidDescriptionException("The options must be all filled\n");
                    }
                }
                // Check for the right inputs
                int level = Integer.parseInt(this.options.get(0));
                int mines = Integer.parseInt(this.options.get(1));
                int time = Integer.parseInt(this.options.get(2));
                int hyper_mine = Integer.parseInt(this.options.get(3));

                if (level == 1) {
                    if (((mines < 9) || (mines > 11)) || ((time < 120) || (time > 180)) || (hyper_mine == 1)) {
                        throw new InvalidDescriptionException(
                                "Look at the game rules on scenario's creation page!\n Your scenario didn't created!!\n");
                    }
                } else if (level == 2) {
                    if (((mines < 35) || (mines > 45)) || ((time < 240) || (time > 360))) {
                        throw new InvalidDescriptionException(
                                "Look at the game rules on scenario's creation page!\n Your scenario didn't created!!\n");
                    }
                } else
                    throw new InvalidDescriptionException("We have only 2 difficulty levels\n");

                // myObj creates the new Scenario-ID.txt file to the right path
                File myObj = new File(folderName + "/" + this.ID + ".txt");
                if (myObj.createNewFile()) {
                    Globals.LOG("File created: " + myObj.getName());
                } else {
                    Globals.LOG("File already exists.");
                }

                // Create the fileWriter that writes the user's inputs to the created
                // Scenario-ID.txt file
                FileWriter fileWriter = new FileWriter(myObj);
                String last = this.options.get(this.options.size() - 1);
                for (String word : this.options) {
                    if (word != last)
                        fileWriter.write(word + "\n");
                    else
                        fileWriter.write(word);
                }
                fileWriter.close();
            } catch (Exception e) {
                Globals.LOG("An error occurred.");
                throw new Exception(e.getMessage());
            }
        }
    }

    public Scen createNewScen(String scen_id, ArrayList<String> passed_options) throws Exception {
        this.ID = scen_id;
        this.options = passed_options;
        this.saveToFolder();
        return this;
    }

    public Scen loadFromFile(String filename) {
        String path = Globals.folder + "/" + filename + ".txt";
        File file = new File(path);
        Globals.LOG("File selected: " + path);
        this.options = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                this.options.add(line);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.ID = filename;
        return this;
    }

    public String getID() {
        return this.ID;
    }

}