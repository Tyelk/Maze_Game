package nz.ac.ara.hc.logicmaze.data.repository;

import java.util.List;

import nz.ac.ara.hc.logicmaze.data.common.ErrorCode;
import nz.ac.ara.hc.logicmaze.data.common.Results;
import nz.ac.ara.hc.logicmaze.model.classes.Game;
import nz.ac.ara.hc.logicmaze.model.classes.Square;
import nz.ac.ara.hc.logicmaze.model.records.Position;

/**
 * <h1>GameRepository</h1>
 * The GameRepository class is responsible
 * for populating the Game object with
 * levels.
 *
 * @author Hadley Clark
 * @version 1.0.0
 * @since 2026-06-02
 */
public class GameRepository {
    private final GameFileReader fileReader;
    private static final String LEVEL_FOLDER_NAME = "levels/";
    private static final String LEVEL_FILE_NAME = "level_";
    private static final String LEVEL_FILE_EXTENSION = ".csv";

    public GameRepository(GameFileReader fileReader) {
        this.fileReader = fileReader;
    }

    public Results<Integer> getFileCount() {
        return this.fileReader.getLevelFileCount(LEVEL_FOLDER_NAME);
    }

    /**
     * Loads a level into the game
     * <p>
     * Sets the given level as the current level in the game.
     * If the level is not in the game it is loaded from
     * storage and added to the game.
     *
     * @param levelNumber number of level
     * @return ErrorCode if error, else null
     */
    public ErrorCode loadLevel(int levelNumber) {
        // set level in game
        ErrorCode setError = Game.getInstance().setCurrentLevel(levelNumber);
        if (setError == null) return null;  // if no error then level exists

        // get data for level
        String filePath = LEVEL_FOLDER_NAME + LEVEL_FILE_NAME + levelNumber + LEVEL_FILE_EXTENSION;
        Results<List<String>> gameData = this.fileReader.getLevelData(filePath);
        if (gameData.getError() != null) return gameData.getError(); // return error

        // translate level data
        Results<LevelData> translateResults = LevelTranslator.translateLevel(gameData.getData());
        if (translateResults.getError() != null) return translateResults.getError(); // return error

        // create level
        ErrorCode createError = this.createLevel(translateResults.getData(), levelNumber);
        if (createError != null) {
            // if error while creating level remove it from the game
            Game.getInstance().removeLatestLevel();
            return createError; // return error
        }

        // success ONLY if no error
        return null;
    }

    /**
     * Creates a level in the game from the given data
     *
     * @param leveldata data for the level
     * @param levelNumber number of the level
     * @return ErrorCode if error, else null
     */
    private ErrorCode createLevel(LevelData leveldata, int levelNumber) {
        // add level
        ErrorCode addError = Game.getInstance().addLevel(leveldata.height(), leveldata.width(), levelNumber);
        if (addError != null) return addError; // return error

        // add squares
        int row = 0, column = 0;
        for (Square sq : leveldata.squares()) {
            ErrorCode addSquareError = Game.getInstance().addSquare(sq, row, column);
            if (addSquareError != null) return addSquareError; // return error

            column++;
            if (column >= leveldata.width()) {
                // go to next line if width exceeded
                column = 0;
                row++;
            }
        }

        // add goals
        for (Position goal : leveldata.goals()) {
            ErrorCode addGoalError = Game.getInstance().addGoal(goal.row(), goal.column());
            if (addGoalError != null) return addGoalError; // return error
        }

        // add eyeball
        Position startPos = leveldata.start();
        ErrorCode addEyeballError = Game.getInstance().addEyeball(startPos.row(), startPos.column(), leveldata.dir());
        if (addEyeballError != null) return addEyeballError; // return error

        // success ONLY if no error
        return null;
    }
}
