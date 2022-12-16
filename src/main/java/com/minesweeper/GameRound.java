package com.minesweeper;

import java.util.ArrayList;
import java.util.Random;
import java.io.*;

public class GameRound {
    public final Scen scen;
    public int level;
    public int mines;
    public int time;
    public int hyper_mine;
    public int hyperRow;
    public int hyperCol;
    public int rows, columns;
    public int[][] board;
    public int[][] minesTable; // Contains the position of all the mines
    public int markedMines;
    public int cellsToOpen;

    public final int COVER_FOR_CELL = 10;
    public final int MARK_FOR_CELL = 10;
    public final int EMPTY_CELL = 0;
    public final int MINE_CELL = 9;
    public final int COVERED_MINE_CELL = MINE_CELL + COVER_FOR_CELL;
    public final int MARKED_MINE_CELL = COVERED_MINE_CELL + MARK_FOR_CELL;
    public final int RIGHT_FLAG_CELL = 30;
    public final int WRONG_FLAG_CELL = 31;
    public final int HYPER_MINE_CELL = 32;

    public GameRound(Scen inScen) {
        this.scen = inScen;
        final ArrayList<String> inOptions = this.scen.getOptions();

        this.level = Integer.parseInt(inOptions.get(0));
        this.mines = Integer.parseInt(inOptions.get(1));
        this.time = Integer.parseInt(inOptions.get(2));
        this.hyper_mine = Integer.parseInt(inOptions.get(3));
        this.markedMines = 0;

        if (this.level == 1) {
            this.rows = 9;
            this.columns = 9;
        } else {
            this.rows = 16;
            this.columns = 16;
        }
    }

    public void initBoard() {

        board = new int[this.rows][this.columns];
        // Create the minesTable. 1rst col = mineRow, 2nd col = minCol, 3rd col =
        // Hypermine or not (0 or 1)
        minesTable = new int[this.mines][3];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                board[i][j] = COVER_FOR_CELL;
            }
        }
        int set_mines = 0;
        var random = new Random();

        // Add mines to random positions on board (a mine cell has value = 19)
        while (set_mines < this.mines) {
            int pos_row = (int) (this.rows * random.nextDouble());
            int pos_col = (int) (this.columns * random.nextDouble());
            if ((pos_row < this.rows) && (pos_col < this.columns) && (board[pos_row][pos_col] != COVERED_MINE_CELL)) {
                board[pos_row][pos_col] = COVERED_MINE_CELL;
                minesTable[set_mines][0] = pos_row;
                minesTable[set_mines][1] = pos_col;
                set_mines++;
            }
        }

        // Check if we have to create a hyper_mine or not
        if (this.hyper_mine == 1) {
            // hyperPosition contains the position of hyper_mine inside the minesTable table
            int hyperPosition = (int) (this.minesTable.length * random.nextDouble());
            if (hyperPosition < this.minesTable.length) {
                hyperRow = this.minesTable[hyperPosition][0];
                hyperCol = this.minesTable[hyperPosition][1];
            }
            // Fills the minesTable 3rd column with 0 or 1 (0 = Not Hypermine, 1 =
            // Hypermine)
            for (int i = 0; i < this.minesTable.length; i++) {
                if (i == hyperPosition)
                    this.minesTable[i][2] = 1;
                else
                    this.minesTable[i][2] = 0;
            }
            System.out.println("Hyper_mine is ON and the position is: (" + hyperRow + "," + hyperCol + ")");
        } else {
            for (int i = 0; i < this.minesTable.length; i++) {
                this.minesTable[i][2] = 0;
            }
            System.out.println("Hyper_mine is OFF");
        }

        // Fill the board with the right values!!!
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                board[i][j] = board[i][j] + getNeighbours(i, j);
            }
        }
        Globals.LOG("Board filled by GameRound");

        cellsToOpen = this.rows * this.columns - this.mines;
    }

    // Returns a value that has to be added on a board's cell (board[i][j])
    private int getNeighbours(int i, int j) {
        int neighborMines = 0;
        if (board[i][j] == COVERED_MINE_CELL) {
            return 0;
        } else {
            for (int r = -1; r < 2; r++) {
                for (int c = -1; c < 2; c++) {
                    if (!(r == 0 && c == 0) && inBoard(i + r, j + c) && (board[i + r][j + c] == COVERED_MINE_CELL))
                        neighborMines++;
                }
            }
        }
        return neighborMines;
    }

    // Checks if the passed values are inside the created Board or not
    private boolean inBoard(int i, int j) {
        if (i >= this.rows || j >= this.columns || i < 0 || j < 0)
            return false;
        return true;
    }

    public void setFlag(int x, int y, int moves) {
        if (board[x][y] > MINE_CELL) {
            /*
             * Check if the hyper-mine is marked in less than 4 moves.
             * This will only happen in level 2 game-scenario
             */
            if ((board[x][y] == COVERED_MINE_CELL) && (moves <= 4) && (this.hyperRow == x) && (this.hyperCol == y)
                    && (this.level == 2)) {
                for (int i = 0; i < this.rows; i++) {
                    for (int j = 0; j < this.columns; j++) {

                        if ((i == x) || (j == y)) {
                            /*
                             * Every mine in the same row and column will take the mineCustom.png icon.
                             * We also increment the markedMine by one for every mine that we have.
                             */
                            if ((board[i][j] == COVERED_MINE_CELL) || (board[i][j] == MARKED_MINE_CELL)) {
                                board[i][j] = HYPER_MINE_CELL;
                                markedMines++;
                            }
                            // Checks if the cell is marked (> 19) or is just covered (=[10,19])
                            else if (board[i][j] > COVERED_MINE_CELL) {
                                /*
                                 * We have a wrong flagged cell.
                                 * We have to decrement the markedMines by one for every wrong flagged cell.
                                 * We have to decrement the cellsToOpen by one for every wrong flagged cell.
                                 */
                                board[i][j] = board[i][j] - COVER_FOR_CELL - MARK_FOR_CELL;
                                markedMines--;
                                cellsToOpen--;
                            } else {
                                /*
                                 * The cell is just covered so we just open it. NO USE OF open(x,y) ==> (NO
                                 * RECURSION, BUT JUST OPEN).
                                 * We have to decrement the cellsToOpen by one for every wrong flagged cell.
                                 */
                                board[i][j] -= COVER_FOR_CELL;
                                cellsToOpen--;
                            }

                        }
                    }
                }
            }

            // Check if a cell is just covered so rigth click will add a flag on it
            else if (board[x][y] <= COVERED_MINE_CELL) {
                if (markedMines < this.mines) {
                    board[x][y] += MARK_FOR_CELL;
                    markedMines++;
                }
            }

            // Deflag the cell
            else {
                board[x][y] -= MARK_FOR_CELL;
                markedMines--;
            }
        }
    }

    /*
     * This is the recursion mode open cell
     * Recursion happens when an empty cell is opened ==> findEmptyCells is called
     */
    public void openCell(int x, int y) throws IOException {
        if (board[x][y] > COVERED_MINE_CELL) {
            // A flaged cell is opened with left click
            board[x][y] -= COVER_FOR_CELL;
            board[x][y] -= MARK_FOR_CELL;

            // Whenever I reveal a flag cell then the markedMines has to be decremented by 1
            this.markedMines--;

            if (board[x][y] == MINE_CELL) {
                resultGame();
            }

            // If the opened cell is not a mine then we have to decrement cellsToOpen by one
            cellsToOpen--;

            if (board[x][y] == EMPTY_CELL) {
                findEmptyCells(x, y);
            }
        }
        if ((board[x][y] > MINE_CELL) && (board[x][y] < MARKED_MINE_CELL)) {
            board[x][y] -= COVER_FOR_CELL;

            // Whenever I reveal a cell that is just covered (not mine_cell) then the
            // markedMines has to be decremented by 1
            cellsToOpen--;

            if (board[x][y] == MINE_CELL) {
                resultGame();
            }
            if (board[x][y] == EMPTY_CELL) {
                findEmptyCells(x, y);

            }
        }

        // If cellToOpen is zero then the game is finished as WIN!!!
        if (cellsToOpen == 0) {
            resultGame();
        }

    }

    public boolean resultGame() throws IOException {
        boolean result;
        if (cellsToOpen == 0)
            result = true;
        else
            result = false;

        gameRoundFinish();
        return result;
    }

    public void gameRoundFinish() {
        for (int i = 0; i < this.rows; i++)
            for (int j = 0; j < this.columns; j++)
                // Check if a cell is already revealed
                if ((board[i][j] >= COVER_FOR_CELL)) {
                    if (board[i][j] == HYPER_MINE_CELL)
                        continue;
                    board[i][j] -= COVER_FOR_CELL;
                    if (board[i][j] == COVERED_MINE_CELL) {
                        board[i][j] = RIGHT_FLAG_CELL;
                    } else if (board[i][j] >= COVER_FOR_CELL) {
                        board[i][j] = WRONG_FLAG_CELL;
                    }
                }
    }

    private void findEmptyCells(int x, int y) throws IOException {
        if (board[x][y] > COVERED_MINE_CELL) {
            this.markedMines--;
        }
        for (int r = -1; r < 2; r++) {
            for (int c = -1; c < 2; c++) {
                if (!(r == 0 && c == 0) && inBoard(x + r, y + c)) {
                    openCell(x + r, y + c);
                }
            }
        }
    }

    public Scen getScen() {
        return scen;
    }

}