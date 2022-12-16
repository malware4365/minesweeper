package com.minesweeper;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Node;

import java.io.*;
import java.util.*;

public class GameController {
    private Button b;
    @FXML
    private GameRound gcGame;
    @FXML
    MenuItem menuStartInGame;
    @FXML
    MenuItem menuExitInGame;
    @FXML
    MenuItem menuSolution; 
    @FXML
    MenuItem menuRounds;
    @FXML
    GridPane gridBoard;
    @FXML
    Label markedMinesLabel;
    @FXML
    Label gameMinesLabel;
    @FXML
    Label timeLabel;

    public int moves_count = 0;
    private int seconds;  
    public Timeline gc_timer;

    
    /* 
    We need this to write once the result to the csv file. With no this one, 
    when the game is finished and all cells appeared, for every mine-cell that we have,
    we have also a new record on csv file ==> 35mines=35 records that we DON'T want it!!!
    */
    public boolean boolTestForCSV;
    
    @FXML
    public void startGame(GameRound game) throws Exception{
        this.gcGame = game;
        this.seconds = game.time;
        this.boolTestForCSV = true;

        createBoard(); // in GameController
        game.initBoard(); // in GameRound
        gameMinesLabel.setText("Game Mines= " + game.mines);

        
        //Timer creation
        doTimer();

        //Create the mines.txt file that contains all the mines
        try {
            File minesTxtFile = new File (Globals.folder + "/mines.txt");
            FileWriter fileWriter = new FileWriter(minesTxtFile);
            for (int i = 0; i < game.minesTable.length; i++) {
                if (i != game.minesTable.length -1) {
                    fileWriter.write(Integer.toString(game.minesTable[i][0]) + "," + Integer.toString(game.minesTable[i][1]) + "," + Integer.toString(game.minesTable[i][2]) + "\n");
                }
                else {
                    fileWriter.write(Integer.toString(game.minesTable[i][0]) + "," + Integer.toString(game.minesTable[i][1]) + "," + Integer.toString(game.minesTable[i][2]));
                }
            }
            fileWriter.close();
        } catch (Exception e) {
            Globals.LOG("An error occurred.");
            throw new Exception(e.getMessage());
        }

        //PRINTS THE WHOLE BOARD HAVE TO DELETE IT
        for (int i = 0; i < game.rows; i++) {
            for (int j = 0; j < game.columns; j++) {
                if (j != game.columns - 1)
                System.out.print(game.board[i][j] + ",");
                else System.out.print(game.board[i][j]);
            }
            System.out.print("\n");
        }

        Globals.LOG("The game Started");
        Globals.LOG("Move counts == " + moves_count);

        for (int i = 0; i < gridBoard.getChildren().size(); i++) {
            ((ButtonBase) gridBoard.getChildren().get(i)).setOnMouseClicked(new EventHandler<MouseEvent>() {
                //private boolean boolTestForCSV;

                @Override
                public void handle(MouseEvent event) {
                    int x, y;
                    y = (int) ((Button) event.getSource()).getProperties().get("gridpane-column");
                    // System.out.println(y);
                    x = (int) ((Button) event.getSource()).getProperties().get("gridpane-row");
                    // System.out.println(x);

                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        try {
                            game.openCell(x, y);
                        } catch (IOException e) {
                            Globals.LOG("An error occurred.");
                            e.printStackTrace();
                        }
                        // Only left clicks counter as moves!!
                        moves_count++;
                        markedMinesLabel.setText("Marked Mines= " + game.markedMines);
                    }
                    if (event.getButton().equals(MouseButton.SECONDARY)) {
                        game.setFlag(x, y, moves_count);
                        markedMinesLabel.setText("Marked Mines= " + game.markedMines);
                    }
                    Globals.LOG("Move counts == " + moves_count);
                    
                    for (Node child : gridBoard.getChildren()) {
                        int i = (int) ((Button) child).getProperties().get("gridpane-row");
                        int j = (int) ((Button) child).getProperties().get("gridpane-column");

                        // Set and reset the flag
                        if (game.board[i][j] > game.COVERED_MINE_CELL) {
                            ((Button) child).setStyle("-fx-background-image: url(flag.png)");
                        } else if (game.board[i][j] > game.MINE_CELL) {
                            ((Button) child).setStyle("-fx-background-image: url(cellCovered.png)");
                        }

                        if (game.board[i][j] == 0) {
                            ((Button) child).setStyle("-fx-background-image: url(box0.png)");
                            ((Button) child).setDisable(true);
                            ((Button) child).setOpacity(1);
                        }
                        if (game.board[i][j] == 1) {
                            ((Button) child).setStyle("-fx-background-image: url(box1.png)");
                            ((Button) child).setDisable(true);
                            ((Button) child).setOpacity(1);
                        }
                        if (game.board[i][j] == 2) {
                            ((Button) child).setStyle("-fx-background-image: url(box2.png)");
                            ((Button) child).setDisable(true);
                            ((Button) child).setOpacity(1);
                        }
                        if (game.board[i][j] == 3) {
                            ((Button) child).setStyle("-fx-background-image: url(box3.png)");
                            ((Button) child).setDisable(true);
                            ((Button) child).setOpacity(1);
                        }
                        if (game.board[i][j] == 4) {
                            ((Button) child).setStyle("-fx-background-image: url(box4.png)");
                            ((Button) child).setDisable(true);
                            ((Button) child).setOpacity(1);
                        }
                        if (game.board[i][j] == 5) {
                            ((Button) child).setStyle("-fx-background-image: url(box5.png)");
                            ((Button) child).setDisable(true);
                            ((Button) child).setOpacity(1);
                        }
                        if (game.board[i][j] == 6) {
                            ((Button) child).setStyle("-fx-background-image: url(box6.png)");
                            ((Button) child).setDisable(true);
                            ((Button) child).setOpacity(1);
                        }
                        if (game.board[i][j] == 7) {
                            ((Button) child).setStyle("-fx-background-image: url(box7.png)");
                            ((Button) child).setDisable(true);
                            ((Button) child).setOpacity(1);
                        }
                        if (game.board[i][j] == 8) {
                            ((Button) child).setStyle("-fx-background-image: url(box8.png)");
                            ((Button) child).setDisable(true);
                            ((Button) child).setOpacity(1);
                        }
                        if (game.board[i][j] == game.MINE_CELL) {

                            if ((i == x) && (j == y)) {
                                ((Button) child).setStyle("-fx-background-image: url(mineExploded.png)");
                                ((Button) child).setDisable(true);
                                ((Button) child).setOpacity(1);
                            }
                            else {
                                ((Button) child).setStyle("-fx-background-image: url(mine.png)");
                                ((Button) child).setDisable(true);
                                ((Button) child).setOpacity(1);
                            }
                            try {
                                if (boolTestForCSV == true){
                                    gcGameEnd();
                                    boolTestForCSV = false;

                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setContentText("You LOSE !!!!");
                                    alert.show();
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        if (game.board[i][j] == game.RIGHT_FLAG_CELL) {
                            ((Button) child).setStyle("-fx-background-image: url(flag.png)");
                            ((Button) child).setDisable(true);
                            ((Button) child).setOpacity(1);
                        }
                        if (game.board[i][j] == game.WRONG_FLAG_CELL) {
                            ((Button) child).setStyle("-fx-background-image: url(wrongFlag.png)");
                            ((Button) child).setDisable(true);
                            ((Button) child).setOpacity(1);
                        }
                        if (game.board[i][j] == game.HYPER_MINE_CELL) {
                            ((Button) child).setStyle("-fx-background-image: url(mineCustom.png)");
                            ((Button) child).setDisable(true);
                            ((Button) child).setOpacity(1);
                        }
                        if (game.cellsToOpen == 0) {
                            try {
                                if (boolTestForCSV == true){
                                    gcGameEnd();
                                    boolTestForCSV = false;

                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setContentText("You WON !!!!");
                                    alert.show();
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }         
            });
        }
    }

    private void gcGameEnd() throws IOException {
        this.gc_timer.stop();
        storeRoundResult();
        menuSolution.setDisable(true);
    }

    private void doTimer() {
        Timeline timer = new Timeline();
        //We have to pass the new timer Timeline to the gc_timer in order to stop it when we click the inGameMenuStart. Otherwise the timeline of the new game,
        //countdowns faster and faster (one second per game) every time I start a new game with the same Scenario
        this.gc_timer = timer;
        timer.setCycleCount(Animation.INDEFINITE);
        timer.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
            
            if (seconds <= 0) {
                this.gc_timer.stop();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Time is finished!! Sorry, but you are late!!");
                alert.show();

                try {
                    this.gcGame.resultGame();
                    storeRoundResult();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                for (Node child : gridBoard.getChildren()) {
                    ((Button) child).setDisable(true);
                    ((Button) child).setOpacity(1);
                }
                
            }
            timeLabel.setText("Time: " + Integer.toString(seconds));
            seconds--;
        }));
        timer.play();
    }

    private void storeRoundResult() throws IOException {
        boolean result;
        
        if (this.gcGame.cellsToOpen == 0) result = true;
        else result = false;

        File winLog = new File(Globals.winLog);
        winLog.createNewFile();
        
        BufferedWriter csvWriter = new BufferedWriter(new FileWriter(winLog,true));

        String winner = result ? "Player" : "CPU";

        int tries = this.moves_count;

        ArrayList<String> row = new ArrayList<>(
            Arrays.asList(String.valueOf(gcGame.mines), String.valueOf(tries), String.valueOf(this.gcGame.time - this.seconds), winner));
        csvWriter.append(String.join(",", row));
        csvWriter.newLine();
        csvWriter.flush();
        csvWriter.close();
    }

    private void createBoard() {

        markedMinesLabel.setText(markedMinesLabel.getText() + this.gcGame.markedMines);

        for (int i = 0; i < this.gcGame.columns; i++) {
            for (int j = 0; j < this.gcGame.rows; j++) {
                b = new Button(" ");
                b.setMinSize(16, 16);
                b.setMaxSize(16, 16);
                b.setStyle("-fx-background-image: url(cellCovered.png)");
                gridBoard.add(b, i, j);
                GridPane.setMargin(b, new Insets(0, 0, 0, 0));
                GridPane.setHalignment(b, HPos.CENTER);
                GridPane.setValignment(b, VPos.CENTER);
            }
        }
        Globals.LOG("Board created by GameControler");
    }

    @FXML
    public void inGameMenuStart() throws Exception {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("New Game started with the same Scenario!!");
        alert.show();

        moves_count = 0;
        markedMinesLabel.setText("Marked Mines= ");
        menuSolution.setDisable(false);
        
        //The previous Timeline has to be stopped
        this.gc_timer.stop();;
        this.startGame(new GameRound(this.gcGame.getScen()));
    }

    @FXML
    private void inGameMenuExit() {
        Globals.exit();
    }

    @FXML 
    public void inGameMenuSolution() throws IOException {

        this.gcGame.resultGame();
        this.gc_timer.stop();
        storeRoundResult();
        //Disable the menuitem menuSolution so once it's clicked you can't click it again. So there is one record on .csv file
        menuSolution.setDisable(true);

        for (Node child : gridBoard.getChildren()) {
            int i = (int) ((Button) child).getProperties().get("gridpane-row");
            int j = (int) ((Button) child).getProperties().get("gridpane-column");

            // Set and reset the flag
            if (this.gcGame.board[i][j] > this.gcGame.COVERED_MINE_CELL) {
                ((Button) child).setStyle("-fx-background-image: url(flag.png)");
            } else if (this.gcGame.board[i][j] > this.gcGame.MINE_CELL) {
                ((Button) child).setStyle("-fx-background-image: url(cellCovered.png)");
            }
            if (this.gcGame.board[i][j] == 0) {
                ((Button) child).setStyle("-fx-background-image: url(box0.png)");
                ((Button) child).setDisable(true);
                ((Button) child).setOpacity(1);
            }
            if (this.gcGame.board[i][j] == 1) {
                ((Button) child).setStyle("-fx-background-image: url(box1.png)");
                ((Button) child).setDisable(true);
                ((Button) child).setOpacity(1);
            }
            if (this.gcGame.board[i][j] == 2) {
                ((Button) child).setStyle("-fx-background-image: url(box2.png)");
                ((Button) child).setDisable(true);
                ((Button) child).setOpacity(1);
            }
            if (this.gcGame.board[i][j] == 3) {
                ((Button) child).setStyle("-fx-background-image: url(box3.png)");
                ((Button) child).setDisable(true);
                ((Button) child).setOpacity(1);
            }
            if (this.gcGame.board[i][j] == 4) {
                ((Button) child).setStyle("-fx-background-image: url(box4.png)");
                ((Button) child).setDisable(true);
                ((Button) child).setOpacity(1);
            }
            if (this.gcGame.board[i][j] == 5) {
                ((Button) child).setStyle("-fx-background-image: url(box5.png)");
                ((Button) child).setDisable(true);
                ((Button) child).setOpacity(1);
            }
            if (this.gcGame.board[i][j] == 6) {
                ((Button) child).setStyle("-fx-background-image: url(box6.png)");
                ((Button) child).setDisable(true);
                ((Button) child).setOpacity(1);
            }
            if (this.gcGame.board[i][j] == 7) {
                ((Button) child).setStyle("-fx-background-image: url(box7.png)");
                ((Button) child).setDisable(true);
                ((Button) child).setOpacity(1);
            }
            if (this.gcGame.board[i][j] == 8) {
                ((Button) child).setStyle("-fx-background-image: url(box8.png)");
                ((Button) child).setDisable(true);
                ((Button) child).setOpacity(1);
            }
            if (this.gcGame.board[i][j] == this.gcGame.MINE_CELL) {
                    ((Button) child).setStyle("-fx-background-image: url(mine.png)");
                    ((Button) child).setDisable(true);
                    ((Button) child).setOpacity(1);
            }
            if (this.gcGame.board[i][j] == this.gcGame.RIGHT_FLAG_CELL) {
                ((Button) child).setStyle("-fx-background-image: url(flag.png)");
                ((Button) child).setDisable(true);
                ((Button) child).setOpacity(1);
            }
            if (this.gcGame.board[i][j] == this.gcGame.WRONG_FLAG_CELL) {
                ((Button) child).setStyle("-fx-background-image: url(wrongFlag.png)");
                ((Button) child).setDisable(true);
                ((Button) child).setOpacity(1);
            }
            if (this.gcGame.board[i][j] == this.gcGame.HYPER_MINE_CELL) {
                ((Button) child).setStyle("-fx-background-image: url(mineCustom.png)");
                ((Button) child).setDisable(true);
                ((Button) child).setOpacity(1);
            }
        }
    }

    @FXML
    public void inGameMenuRounds() {
        Globals.showRounds();
    }

}