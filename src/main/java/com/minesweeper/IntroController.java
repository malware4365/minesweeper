package com.minesweeper;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class IntroController {
    private Scen scen;
    @FXML
    Parent root;
    @FXML
    Button createButton;
    @FXML
    Button clearButtonClick;
    @FXML
    Button startButton;
    @FXML
    MenuItem menuCreate;
    @FXML
    MenuItem menuStart;
    @FXML
    MenuItem IntroMenuRounds;
    @FXML
    TextField scen_id;
    @FXML
    TextField level;
    @FXML
    TextField mines;
    @FXML
    TextField time;
    @FXML
    TextField hyper_mine;
    @FXML
    private Label scenLabel1;
    @FXML
    private Label scenLabel2;
    @FXML
    private Label scenLabel3;
    @FXML
    GridPane test1;

    final FileChooser fc = new FileChooser();

    // MenuItem "Create" on click creates new scene for input scenario's data by the
    // user
    @FXML
    public void menuCreate() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("create_game.fxml"));
        Scene create_scene = new Scene(fxmlLoader.load());
        Stage create_stage = new Stage();
        create_stage.setTitle("Create a new Scenario-ID for your game");
        create_stage.setScene(create_scene);
        create_stage.show();
    }

    @FXML
    public void clearButtonClick() throws IOException {
        scen_id.clear();
        level.clear();
        mines.clear();
        time.clear();
        hyper_mine.clear();
    }

    @FXML
    public void createButtonClick() throws IOException {
        // Close the stage of menuCreate when createButton is clicked
        Stage create_stage = (Stage) createButton.getScene().getWindow();
        create_stage.close();

        String scen_id_str = scen_id.getText();
        String level_str = level.getText();
        String mines_str = mines.getText();
        String time_str = time.getText();
        String hyper_mine_str = hyper_mine.getText();

        ArrayList<String> user_options = new ArrayList<String>();
        user_options.add(level_str);
        user_options.add(mines_str);
        user_options.add(time_str);
        user_options.add(hyper_mine_str);

        try {
            scen = new Scen().createNewScen(scen_id_str, user_options);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Your Scenario is created, now you have to Load it!!!");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("ERROR OCCURED! RETRY!");
            alert.setContentText(e.getMessage());
            alert.show();
        }

    }

    // MenuItem "Load" on click loads filechooser to load the scenario-id.txt file
    @FXML
    public void menuLoad() throws IOException {
        fc.setTitle("Choose a Scenario-ID game.");
        File file = fc.showOpenDialog(null);
        String filename = file.getName().split("[.]")[0];
        scen = new Scen().loadFromFile(filename);
        scenLabel2.setText(filename);
        startButton.setDisable(false);
        menuStart.setDisable(false);
        ;
        scenLabel3.setText("Click start to play");
    }

    @FXML
    public void menuStart() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        Parent mineSweeperLayout = loader.load();
        GameController gameController = loader.getController();

        // New game round is created according to the selected scenario's id options
        GameRound game = new GameRound(scen);
        gameController.startGame(game);
        Main.primaryStage.setTitle("Game in Progress");
        Main.primaryStage.setScene(new Scene(mineSweeperLayout));
    }

    @FXML
    public void introRounds() throws IOException {
        Globals.showRounds();
    }

    @FXML
    public void menuExit() {
        Globals.LOG("The game will exit now!!");
        Globals.exit();
    }

}