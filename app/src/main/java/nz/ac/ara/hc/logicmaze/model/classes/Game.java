package nz.ac.ara.hc.logicmaze.model.classes;

import java.util.ArrayList;
import java.util.List;

import nz.ac.ara.hc.logicmaze.data.common.ErrorCode;
import nz.ac.ara.hc.logicmaze.data.common.Results;
import nz.ac.ara.hc.logicmaze.model.enums.Color;
import nz.ac.ara.hc.logicmaze.model.enums.Direction;
import nz.ac.ara.hc.logicmaze.model.enums.MoveError;
import nz.ac.ara.hc.logicmaze.model.enums.Shape;
import nz.ac.ara.hc.logicmaze.model.interfaces.ILevelHolder;
import nz.ac.ara.hc.logicmaze.model.records.Position;

/**
 * <h1>Game</h1>
 * The game class is the model for
 * the logic maze, it allows access
 * to the underlying logic that is 
 * needed to play the game.
 * 
 * @author Hadley Clark
 * @version 1.0.0
 * @since 2026-03-07
 */
public class Game implements ILevelHolder {
	private final List<Level> allLevels = new ArrayList<>();
	private Level currentLevel = null;

	private Game() {}	// private constructor so it cannot be initialized externally

	private static class GameHolder {
		// holds ONE version of the game object
		private static final Game GAME_INSTANCE = new Game();
	}

	public static Game getInstance() {
		// creates/returns the game instance
		return GameHolder.GAME_INSTANCE;
	}

	/**
	 * Creates a new level which is added to the
	 * list of all levels and set as the current
	 * level.
	 * <p>
	 * Uses {@link Validation#isValidLevelDimensions}
	 * to validate the levels dimensions.
	 *
	 * @param height height of the level
	 * @param width  width of the level
	 * @return ErrorCode if something was invalid, else null
	 */
	@Override
	public ErrorCode addLevel(int height, int width, int levelNumber) {
		// validate inputs
		if (levelNumber < 1) return ErrorCode.INVALID_LEVEL_NUMBER;
		if (this.getLevelIndex(levelNumber) >= 0) return ErrorCode.LEVEL_EXISTS;
		if (!Validation.isValidLevelDimensions(height, width)) return ErrorCode.INVALID_LEVEL;

		// adds and set to current level
		this.allLevels.add(new Level(height, width, levelNumber));
		this.currentLevel = this.allLevels.get(this.allLevels.size() - 1);
		return null;	// success
	}

	/**
	 * Gets the index of the level that matches
	 * the given level number.
	 *
	 * @param levelNumber number of the level
	 * @return Index of the level, or -1 if it does not exist
	 */
	private int getLevelIndex(int levelNumber) {
		// loops through all levels and returns if match found
		for (var i = 0; i < this.allLevels.size(); i++) {
			if (this.allLevels.get(i).getLevelNumber() == levelNumber) return i;
		}
		return -1;
	}

	/**
	 * Gets the level number of the current level.
	 *
	 * @return Results: ErrorCode if error, Integer if success
	 */
	public Results<Integer> getLevelNumber() {
		// validate level
		if (this.currentLevel == null) return Results.fail(ErrorCode.NO_LEVEL_LOADED);

		// returns level num
		return Results.success(this.currentLevel.getLevelNumber());
	}

	/**
	 * Removes the last added level from the game.
	 */
	public void removeLatestLevel() {
		int levelIndex = this.allLevels.size() - 1;
		if (levelIndex >= 0) this.allLevels.remove(levelIndex);
	}

	/**
	 * Gets the width of the current level.
	 *
	 * @return Results: ErrorCode if error, Integer if success
	 */
	@Override
	public Results<Integer> getLevelWidth() {
		// validate level
		if (this.currentLevel == null) return Results.fail(ErrorCode.NO_LEVEL_LOADED);

		// returns level width
		return Results.success(this.currentLevel.getWidth());
	}

	/**
	 * Gets the height of the current level.
	 *
	 * @return Results: ErrorCode if error, Integer if success
	 */
	@Override
	public Results<Integer> getLevelHeight() {
		// validate level
		if (this.currentLevel == null) return Results.fail(ErrorCode.NO_LEVEL_LOADED);

		// returns level height
		return Results.success(this.currentLevel.getHeight());
	}

	/**
	 * Sets the current level to the given number.
	 *
	 * @param levelNumber levels number
	 */
	@Override
	public ErrorCode setCurrentLevel(int levelNumber) {
		// check level exists
		int levelIndex = this.getLevelIndex(levelNumber);
		if (levelIndex < 0) return ErrorCode.INVALID_LEVEL_NUMBER; // return error

		// set level
		this.currentLevel = this.allLevels.get(levelIndex);
		return null;
	}

	/**
	 * Adds a goal to the current level at the given position.
	 * <p>
	 * Uses {@link Validation#isValidLevelAndPosition}
	 * to validate the destination position.
	 *
	 * @param row    row index of the goal position
	 * @param column column index of the goal position
	 * @return ErrorCode if error, else null
	 */
	public ErrorCode addGoal(int row, int column) {
		// validate position and level
		if (!Validation.isValidLevelAndPosition(row, column, this.currentLevel)) return ErrorCode.INVALID_POSITION_OR_LEVEL;

		// only adds goal if no errors
		this.currentLevel.addGoal(row, column);
		return null;
	}

	/**
	 * Checks if level has hidden goal at given
	 * positon.
	 * <p>
	 * Uses {@link Validation#isValidLevelAndPosition}
	 * to validate the destination position.
	 *
	 * @param row    row index of the goal position
	 * @param column column index of the goal position
	 * @return Results: ErrorCode if error, Boolean if success
	 */
	public Results<Boolean> levelHasHiddenGoalAt(int row, int column) {
		// validate position and level
		if (!Validation.isValidLevelAndPosition(row, column, this.currentLevel)) return Results.fail(ErrorCode.INVALID_POSITION_OR_LEVEL);

		// only check if no errors
		return Results.success(this.currentLevel.hasHiddenGoalAt(row, column));
	}

	/**
	 * Undoes the last player move.
	 * <p>
	 * Uses {@link Validation#isValidLevelAndEyeball}
	 * to validate the game state.
	 *
	 * @return Results: ErrorCode if error, MoveError/null if success
	 */
    public Results<MoveError> undoPlayersLastMove() {
        // checks level and eyeball
        if (!Validation.isValidLevelAndEyeball(currentLevel)) return Results.fail(ErrorCode.INVALID_EYEBALL_OR_LEVEL);

        // undoes latest move
        return Results.success(this.currentLevel.undoPlayersLastMove());
    }

	/**
	 * Resets the level.
	 * <p>
	 * Uses {@link Validation#isValidLevelAndEyeball}
	 * to validate the game state.
	 *
	 * @return ErrorCode if error, else null
	 */
	public ErrorCode resetLevel() {
		// checks level and eyeball
		if (!Validation.isValidLevelAndEyeball(currentLevel)) return ErrorCode.INVALID_EYEBALL_OR_LEVEL;

		// only resets entire level if no errors
		this.currentLevel.resetLevel();
		return null;
	}

	/**
	 * Gets current levels positions of goals.
	 *
	 * @return Results: ErrorCode if error, List of Positions if success
	 */
	public Results<List<Position>> getGoalPositions() {
		// validate level
		if (this.currentLevel == null) return Results.fail(ErrorCode.NO_LEVEL_LOADED);

		// gets positions
		return Results.success(this.currentLevel.getGoalPositions());
	}

	/**
	 * Gets the current goal count.
	 *
	 * @return Results: ErrorCode if error, Integer if success
	 */
	public Results<Integer> getGoalCount() {
		// validate level
		if (this.currentLevel == null) return Results.fail(ErrorCode.NO_LEVEL_LOADED);

		// return goal count
		return Results.success(this.currentLevel.getGoalCount());
	}

	/**
	 * Gets the count of completed goals.
	 * 
	 * @return Results: ErrorCode if error, Integer if success
	 */
	public Results<Integer> getCompletedGoalCount() {
		// validate level
		if (this.currentLevel == null) return Results.fail(ErrorCode.NO_LEVEL_LOADED);

		// return completed goal count
		return Results.success(this.currentLevel.getCompletedGoalCount());
	}

	/**
	 * Adds a square to the current level at the given position.
	 * <p>
	 * Uses {@link Validation#isValidLevelAndPosition}
	 * to validate the destination position.
	 *
	 * @param square square to add
	 * @param row    row index of the square position
	 * @param column column index of the square position
	 * @return ErrorCode if error, else null
	 */
	public ErrorCode addSquare(Square square, int row, int column) {
		if (square == null) return ErrorCode.INVALID_SQUARE;
		// validate position and level
		if (!Validation.isValidLevelAndPosition(row, column, this.currentLevel)) return ErrorCode.INVALID_POSITION_OR_LEVEL;

		// only adds square if no errors
		this.currentLevel.addSquare(square, row, column);
		return null;
	}

	/**
	 * Gets the color of a square at the given position.
	 * <p>
	 * Uses {@link Validation#isValidLevelAndPosition}
	 * to validate the destination position
	 * and level.
	 *
	 * @param row    row index of the square position
	 * @param column column index of the square position
	 * @return Results: ErrorCode if error, Color if success
	 */
	public Results<Color> getLevelColorAt(int row, int column) {
		// validate position and level
		if (!Validation.isValidLevelAndPosition(row, column, this.currentLevel)) return Results.fail(ErrorCode.INVALID_POSITION_OR_LEVEL);

		// return color
		return Results.success(this.currentLevel.getColorAt(row, column));
	}

	/**
	 * Gets the shape of a square at the given position.
	 * <p>
	 * Uses {@link Validation#isValidLevelAndPosition}
	 * to validate the destination position
	 * and level.
	 *
	 * @param row    row index of the square position
	 * @param column column index of the square position
	 * @return Results: ErrorCode if error, Shape if success
	 */
	public Results<Shape> getLevelShapeAt(int row, int column) {
		// validate position and level
		if (!Validation.isValidLevelAndPosition(row, column, this.currentLevel)) return Results.fail(ErrorCode.INVALID_POSITION_OR_LEVEL);

		// return color
		return Results.success(this.currentLevel.getShapeAt(row, column));
	}

	/**
	 * Adds an eyeball to the current level at the given position.
	 * <p>
	 * Uses {@link Validation#isValidLevelAndPosition}
	 * to validate the destination position.
	 *
	 * @param row       row index of the eyeball position
	 * @param column    column index of the eyeball position
	 * @param direction direction of eyeball
	 * @return ErrorCode if error, else null
	 */
	public ErrorCode addEyeball(int row, int column, Direction direction) {
		if (direction == null) return ErrorCode.INVALID_DIRECTION;
		// validate position and level
		if (!Validation.isValidLevelAndPosition(row, column, this.currentLevel)) return ErrorCode.INVALID_POSITION_OR_LEVEL;

		// adds eyeball
		this.currentLevel.addEyeball(row, column, direction);
		return null;
	}

	/**
	 * Gets the row of the eyeball.
	 * <p>
	 * Uses {@link Validation#isValidLevelAndEyeball}
	 * to validate the level and eyeball.
	 * 
	 * @return Results: ErrorCode if error, Integer if success
	 */
	public Results<Integer> getLevelEyeballRow() {
		// checks level and eyeball
		if (!Validation.isValidLevelAndEyeball(currentLevel)) return Results.fail(ErrorCode.INVALID_EYEBALL_OR_LEVEL);

		// return row
		return Results.success(this.currentLevel.getEyeballRow());
	}

	/**
	 * Gets the column of the eyeball.
	 * <p>
	 * Uses {@link Validation#isValidLevelAndEyeball}
	 * to validate the level and eyeball.
	 * 
	 * @return Results: ErrorCode if error, Integer if success
	 */
	public Results<Integer> getLevelEyeballColumn() {
		// checks level and eyeball
		if (!Validation.isValidLevelAndEyeball(currentLevel)) return Results.fail(ErrorCode.INVALID_EYEBALL_OR_LEVEL);

		// return column
		return Results.success(this.currentLevel.getEyeballColumn());
	}

	/**
	 * Gets the direction of the eyeball.
	 * <p>
	 * Uses {@link Validation#isValidLevelAndEyeball}
	 * to validate the level and eyeball.
	 * 
	 * @return Results: ErrorCode if error, Direction if success
	 */
	public Results<Direction> getLevelEyeballDirection() {
		// checks level and eyeball
		if (!Validation.isValidLevelAndEyeball(currentLevel)) return Results.fail(ErrorCode.INVALID_EYEBALL_OR_LEVEL);

		// return direction
		return Results.success(this.currentLevel.getEyeballDirection());
	}

	/**
	 * Move eyeball to given position.
	 * <p>
	 * Uses {@link Validation#isValidLevelAndPosition}
	 * to validate the destination position.
	 *
	 * @param destinationRow    row index of the position
	 * @param destinationColumn column index of the position
	 * @return Results: ErrorCode if error, MoverError/null if success
	 */
	public Results<MoveError> movePlayerTo(int destinationRow, int destinationColumn) {
		// validate position and level
		if (!Validation.isValidLevelAndPosition(destinationRow, destinationColumn, this.currentLevel)) return Results.fail(ErrorCode.INVALID_POSITION_OR_LEVEL);
		// checks eyeball
		if (!this.currentLevel.hasEyeball()) return Results.fail(ErrorCode.NO_EYEBALL);

		// only calls move if no error
		return Results.success(this.currentLevel.moveTo(destinationRow, destinationColumn));
	}
}
