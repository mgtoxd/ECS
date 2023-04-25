package uk.ac.soton.comp1206.game;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;

import java.util.ArrayList;
import java.util.Random;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    // 剩余生命
    private SimpleIntegerProperty life = new SimpleIntegerProperty(3);

    private static final Logger logger = LogManager.getLogger(Game.class);

    private Integer currLevel = 0;

    public SimpleIntegerProperty score = new SimpleIntegerProperty(0);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    public void setCurrLevel(Integer currLevel) {
        this.currLevel = currLevel;
    }

    public Integer getCurrLevel() {
        return currLevel;
    }

    // 升级
    public void levelUp() {
        currLevel++;
        if (currLevel > 14) {
            currLevel = new Random().nextInt(15);
        }
    }

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        //Get the new value for this block
        int previousValue = grid.get(x,y);
        int newValue = previousValue + 1;
        if (newValue  > GamePiece.PIECES) {
            newValue = 0;
        }

        //Update the grid with the new value
        grid.set(x,y,newValue);
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }


    public boolean canPlace(int x, int y, GamePiece currPiece) {
        for (int i = 0; i < currPiece.getBlocks().length; i++) {
            for (int j = 0; j < currPiece.getBlocks()[i].length; j++) {
                if (currPiece.getBlocks()[i][j] == 0) {
                    continue;
                }
                int x1 = x + j - 1;
                int y1 = y + i - 1;
                if (x1 < 0 || x1 >= grid.getCols() || y1 < 0 || y1 >= grid.getRows()) {
                    return false;
                }
                if (grid.get(x1, y1)!=0) {
                    return false;
                }
            }
        }
        return true;
    }


    // 放置
    public void place(int x, int y, GamePiece currPiece) {
        for (int i = 0; i < currPiece.getBlocks().length; i++) {
            for (int j = 0; j < currPiece.getBlocks()[i].length; j++) {
                if (currPiece.getBlocks()[i][j] == 0) {
                    continue;
                }
                int x1 = x + j - 1;
                int y1 = y + i - 1;
                grid.set(x1, y1, currPiece.getBlocks()[i][j]);
            }
        }
    }

    // 检查并且消行
    public void checkLines() {
        ArrayList<int[]> nonZeroRowsAndCols = findNonZeroRowsAndColumns(grid.getGrid());
        for (int[] index : nonZeroRowsAndCols) {
            if (index[0] == -1) {
                setRowOrColumnToZero(grid.getGrid(), index[1], false);
            } else if (index[1] == -1) {
                setRowOrColumnToZero(grid.getGrid(), index[0], true);
            }
        }
        logger.info("消行数：" + nonZeroRowsAndCols.size());
        score.set(score.get() + nonZeroRowsAndCols.size());
        logger.info("当前分数：" + score.get());
//        score.set(score.get() + nonZeroRowsAndCols.size());
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public void setScoreListener(ChangeListener<? super Number> listener) {
        score.addListener(listener);
    }

    public ArrayList<int[]> findNonZeroRowsAndColumns(SimpleIntegerProperty[][] grid) {
        ArrayList<int[]> results = new ArrayList<>();

        int numRows = grid.length;
        int numCols = grid[0].length;

        // Check each row
        for (int row = 0; row < numRows; row++) {
            boolean hasZeroes = false;
            for (int col = 0; col < numCols; col++) {
                if (grid[row][col].get() == 0) {
                    hasZeroes = true;
                    break;
                }
            }
            if (!hasZeroes) {
                results.add(new int[]{row, -1});
            }
        }

        // Check each column
        for (int col = 0; col < numCols; col++) {
            boolean hasZeroes = false;
            for (int row = 0; row < numRows; row++) {
                if (grid[row][col].get() == 0) {
                    hasZeroes = true;
                    break;
                }
            }
            if (!hasZeroes) {
                results.add(new int[]{-1, col});
            }
        }

        return results;
    }

    public void setRowOrColumnToZero(SimpleIntegerProperty[][] grid, int index, boolean isRow) {
        if (isRow) {
            for (int col = 0; col < grid[0].length; col++) {
                grid[index][col].set(0);
            }
        } else {
            for (int row = 0; row < grid.length; row++) {
                grid[row][index].set(0);
            }
        }
    }

    // 减少生命
    public void reduceLife() {
        life.set(life.get() - 1);
    }

    public void setLifeListener(ChangeListener<? super Number> listener) {
        this.life.addListener(listener);
    }

    public int getLife() {
        return life.get();
    }
}
