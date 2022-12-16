package com.minesweeper;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Globals {
    public final static String folder = "./medialab";
    public final static String winLog = "./rounds.csv";

    public static void LOG(String s) {
        System.out.println(s);
    }

    public static void exit() {
        Platform.exit();
        System.exit(0);
    }

    public static void showRounds() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rounds");
        alert.setHeaderText("Last 5 Rounds");
        try {
            Scanner sc = new Scanner(new File(Globals.winLog));
            String[] output = new String[5];

            int count = 0;
            while (sc.hasNextLine()) {
                String[] line = sc.nextLine().split(",");
                output[(count++) % 5] = "Mines: " + line[0] + ", Tries: " + line[1] + ", Total Time: " + line[2]
                        + ", Winner: " + line[3];
            }
            sc.close();

            StringBuilder out = new StringBuilder();
            for (String round : output)
                if (round != null)
                    out.append(round).append("\n");

            alert.setContentText(String.valueOf(out));
        } catch (FileNotFoundException e) {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("No Games Played!");
        }

        alert.show();
    }
}