package nz.ac.ara.hc.logicmaze.model.classes;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nz.ac.ara.hc.logicmaze.model.enums.Color;
import nz.ac.ara.hc.logicmaze.model.enums.Direction;
import nz.ac.ara.hc.logicmaze.model.enums.MoveError;
import nz.ac.ara.hc.logicmaze.model.enums.Shape;
import nz.ac.ara.hc.logicmaze.model.interfaces.IEyeballHolder;
import nz.ac.ara.hc.logicmaze.model.interfaces.IGoalHolder;
import nz.ac.ara.hc.logicmaze.model.interfaces.IMoving;
import nz.ac.ara.hc.logicmaze.model.interfaces.ISquareHolder;
import nz.ac.ara.hc.logicmaze.model.records.Move;
import nz.ac.ara.hc.logicmaze.model.records.Position;

/**
 * <h1>Level</h1>
 * Maintains the levels layout, goals, eyeball
 * and checks the movement logic for individual
 * levels.
 * 
 * @author Hadley Clark
 * @version 1.0.0
 * @since 2026-03-07
 */
class Level implements IGoalHolder, ISquareHolder, IEyeballHolder, IMoving {
	private final int levelNumber;
	private int totalCompletedGoals = 0;
	private final int height;
	private final int width;
	private final Square[][] levelLayout;	// holds all the squares within a level
	private Eyeball playerEyeball = null;
	private final List<Position> allGoals = new ArrayList<>();
	private final List<Position> hiddenGoals = new ArrayList<>();	// tracks hidden goals
    private final ArrayDeque<Move> pastMoves = new ArrayDeque<>();  // holds past moves
	private Position previousGoalPosition = null;	// holds the position of the previous goal square to be cleared
	
	/**
	 * Instantiates a new Level object.
	 * 
	 * @param height		height of the level
	 * @param width			width of the level
	 * @param levelNumber	number of the level
	 */
	Level(int height, int width, int levelNumber) {
		this.height = height;
		this.width = width;
		this.levelNumber = levelNumber;
		this.levelLayout = new Square[height][width];
	}
	
	/**
	 * Gets the height of the level.
	 * 
	 * @return height of the level
	 */
	int getHeight() {
		return this.height;
	}
	
	/**
	 * Gets the width of the level.
	 * 
	 * @return width of the level
	 */
	int getWidth() {
		return this.width;
	}

	/**
	 * Gets the number of the level.
	 *
	 * @return number of the level
	 */
	int getLevelNumber() { return this.levelNumber; }

	/**
	 * Adds goal to the level.
	 *
	 * @param row    row of the goal
	 * @param column column of the goal
	 */
	@Override
	public void addGoal(int row, int column) {
		this.allGoals.add(new Position(row, column));
	}

	/**
	 * Checks if a hidden goal is at the
	 * given position.
	 *
	 * @param targetRow    row of the goal
	 * @param targetColumn column of the goal
	 * @return true if goals is hidden, else false
	 */
	public boolean hasHiddenGoalAt(int targetRow, int targetColumn) {
		return this.hiddenGoals.stream().anyMatch(goal -> goal.row() == targetRow && goal.column() == targetColumn);
	}

	/**
	 * Gets the goal positions.
	 *
	 * @return READONLY List of Positions
	 */
	public List<Position> getGoalPositions() {
		return Collections.unmodifiableList(this.allGoals);
	}

	/**
	 * Add current position to past moves.
	 */
    private void addPastMove() {
        // create past move
        Move pastMove = new Move(this.playerEyeball.getRow(), this.playerEyeball.getColumn(), this.playerEyeball.getDirection());
        // push onto stack
        this.pastMoves.addLast(pastMove);
    }

	/**
	 * Undoes the last move made by the player.
	 *
	 * @return MoveError if no past moves, null if valid
	 */
    public MoveError undoPlayersLastMove() {
        // pops last item
        Move lastMove = this.pastMoves.pollLast();
        if (lastMove == null) return MoveError.NO_PAST_MOVES; // error if no past move

        // if the CURRENT player position is a goal
        boolean currentIsGoal = this.hasGoalAt(this.playerEyeball.getRow(), this.playerEyeball.getColumn());
        if (currentIsGoal) {
            // clear previous goal and deincrement goal count
            this.previousGoalPosition = null;
            this.totalCompletedGoals--;
        }

		// unhide if last move was a goal position
		boolean isGoal = this.hiddenGoals.removeIf(goal -> goal.row() == lastMove.row() && goal.column() == lastMove.column());
		// save previous goal
		if (isGoal) {
			this.previousGoalPosition = new Position(lastMove.row(), lastMove.column());
		}

        // move player back to position
        this.playerEyeball.setPosition(lastMove.row(), lastMove.column());
        this.playerEyeball.setDirection(lastMove.direction());

		// return success (no error)
        return null;
    }

	/**
	 * Resets the level to the starting state.
	 * <p>
	 * Returns the player to the starting positon,
	 * clears past moves, unhides goals, clears
	 * completed goals, clears previous position.
	 */
    public void resetLevel() {
		// get position of first move
		Move firstMove = this.pastMoves.peekFirst();
		if (firstMove != null) {
			// move player to first position
			this.playerEyeball.setPosition(firstMove.row(), firstMove.column());
			this.playerEyeball.setDirection(firstMove.direction());
			this.pastMoves.clear();	// clear ALL past moves
		}

		// unhide all goals
		this.hiddenGoals.clear();
		// reset goal count
		this.totalCompletedGoals = 0;
		// clear past goal
		this.previousGoalPosition = null;
    }

    /**
     * Gets the number of goals within
     * the level.
     *
     * @return count of goals
     */
	@Override
	public int getGoalCount() {
		return this.allGoals.size();
	}
	
	/**
	 * Checks if a goal is at the given
	 * row and column.
	 * 
	 * @param targetRow 	row to check
	 * @param targetColumn	column to check
	 * @return true if a goal is at the given position, false if no goal at position
	 */
	@Override
	public boolean hasGoalAt(int targetRow, int targetColumn) {
		// checks goals to see if the row and position match
		return this.allGoals.stream().anyMatch(goal -> goal.row() == targetRow && goal.column() == targetColumn);
	}

	/**
	 * Gets the total amount of completed goals.
	 * 
	 * @return count of total goals completed
	 */
	@Override
	public int getCompletedGoalCount() {
		return this.totalCompletedGoals;
	}

	/**
	 * Adds a square at the given position.
	 *
	 * @param square square to be added
	 * @param row    row position of the new square
	 * @param column column position of the new square
	 */
	@Override
	public void addSquare(Square square, int row, int column) {
		this.levelLayout[row][column] = square;
	}

	/**
	 * Gets the color of a square
	 * at a given position.
	 * <p>
	 * This method returns the
	 * color of the square at the
	 * position if the position has
	 * a square, otherwise it
	 * returns null if there is no
	 * square.
	 *
	 * @param row    row position of the new square
	 * @param column column position of the new square
	 * @return color of the square, or null if no square at given position
	 */
	@Override
	public Color getColorAt(int row, int column) {
		return switch (this.levelLayout[row][column]) {
			case Square s 	-> s.getColor();
			case null 		-> null;
		};
	}

	/**
	 * Gets the shape of a square 
	 * at a given position.
	 * <p>
	 * This method returns the
	 * shape of the square at the
	 * position if the position has
	 * a square, otherwise it
	 * returns null if there is no
	 * square.
	 * 
	 * @param row		row position of the new square
	 * @param column	column position of the new square
	 * @return shape of the square, or null if no square at given position
	 */
	@Override
	public Shape getShapeAt(int row, int column) {
		return switch (this.levelLayout[row][column]) {
			case Square s 	-> s.getShape();
			case null 		-> null;
		};
	}
	
	/**
	 * Checks if the level has an eyeball.
	 * 
	 * @return true if there is an eyeball, else false
	 */
	boolean hasEyeball() {
		return this.playerEyeball != null;
	}
	
	/**
	 * Adds an eyeball at the given position.
	 *
	 * @param row       row position of the new eyeball
	 * @param column    column position of the new eyeball
	 * @param direction direction of the new eyeball
	 */
	@Override
	public void addEyeball(int row, int column, Direction direction) {
		this.playerEyeball = new Eyeball(row, column, direction);
	}

	/**
	 * Gets the row of the eyeball 
	 * within the current level.
	 * 
	 * @return row of the eyeball
	 */
	@Override
	public int getEyeballRow() {
		return this.playerEyeball.getRow();
	}

	/**
	 * Gets the column of the eyeball 
	 * within the current level.
	 * 
	 * @return column of the eyeball
	 */
	@Override
	public int getEyeballColumn() {
		return this.playerEyeball.getColumn();
	}

	/**
	 * Gets the direction of the eyeball 
	 * within the current level.
	 * 
	 * @return direction of the eyeball
	 */
	@Override
	public Direction getEyeballDirection() {
		return this.playerEyeball.getDirection();
	}

	/**
	 * Checks if the destination is diagonal
	 * to the eyeballs current position.
	 * 
	 * @param destinationRow	row position of desired move 
	 * @param destinationColumn	column position of desired move
	 * @return MoveError if move is invalid, null if valid
	 */
	private MoveError diagonalMoveCheck(int destinationRow, int destinationColumn) {
        // get row and column
		int currentRow = this.playerEyeball.getRow();
		int currentColumn = this.playerEyeball.getColumn();

		// diagonal if different row AND column
		if (destinationRow != currentRow && destinationColumn != currentColumn) return MoveError.MOVE_DIAGONALLY;
		return null;	// success
	}
	
	/**
	 * Checks if the destination is the
	 * same shape or color as the eyeballs
	 * current position.
	 * 
	 * @param destinationRow	row position of desired move 
	 * @param destinationColumn	column position of desired move
	 * @return MoveError if move is invalid, null if valid
	 */
	private MoveError sameShapeOrColorCheck(int destinationRow, int destinationColumn) {
		// get shape and color of destination and current position
		Color destColor = this.getColorAt(destinationRow, destinationColumn);
		Shape destShape = this.getShapeAt(destinationRow, destinationColumn);
		Color currentColor = this.getColorAt(this.getEyeballRow(), this.getEyeballColumn());
		Shape currentShape = this.getShapeAt(this.getEyeballRow(), this.getEyeballColumn());

		// check color and shape
		if (destColor != currentColor && destShape != currentShape) return MoveError.DIFFERENT_SHAPE_OR_COLOR;
		return null;	// success
	}

	/**
	 * Checks the destination is not the
	 * same position as the players
	 * current position.
	 *
	 * @param destinationRow    row position of desired move
	 * @param destinationColumn column position of desired move
	 * @return MoveError if move is invalid, null if valid
	 */
	private MoveError samePositionCheck(int destinationRow, int destinationColumn) {
        // get row and column
		int currentRow = this.playerEyeball.getRow();
		int currentColumn = this.playerEyeball.getColumn();
		if (destinationRow == currentRow && destinationColumn == currentColumn) return MoveError.SAME_POSITION;
		return null;	// success
	}

	/**
	 * Checks if the players eyeball is
	 * facing a direction that can move to
	 * the given destination.
	 * <p>
	 * Checks the destination square is
	 * not behind the current position of
	 * the eyeball.
	 *
	 * @param destinationRow    row position of desired move
	 * @param destinationColumn column position of desired move
	 * @return MoveError if direction is invalid, null if valid
	 */
	@Override
	public MoveError directionCheck(int destinationRow, int destinationColumn) {
		// check if destination is behind eyeballs current position
		boolean isBehindPlayer = switch (this.getEyeballDirection()) {
				// if direction is up and the destination row is below(greater) the current row then its behind
	        case UP    -> destinationRow > this.getEyeballRow();
	        	// if direction is down and the destination row is above(less) the current row then its behind
	        case DOWN  -> destinationRow < this.getEyeballRow();
	        	// if direction is left and the destination column is right(greater) of the current column then its behind
	        case LEFT  -> destinationColumn > this.getEyeballColumn();
	        	// if direction is right and the destination column is left(less) of the current column then its behind
	        case RIGHT -> destinationColumn < this.getEyeballColumn();
	        	// default to true to prevent anything sneaking through
        };

		if (isBehindPlayer) return MoveError.BACKWARDS_MOVE;
		return null;	// null if direction is valid
	}

	/**
	 * Checks squares on a row between the
	 * start and end position for blank squares
	 * and hidden goals.
	 *
	 * @param row		row to check
	 * @param start		start of path
	 * @param end		end of path
	 * @return true if no blank/hidden squares, false if any blanks/hidden squares
	 */
	private boolean isBlankFreeRow(int row, int start, int end) {
		// loop through each column in row from start to end
		for (int column = start; column <= end; column++) {
			boolean isBlank = this.levelLayout[row][column] instanceof BlankSquare;
			boolean isHidden = this.hasHiddenGoalAt(row, column);
			if (isBlank || isHidden) return false;	// false if any blank or hidden
		}
		return true;
	}

	/**
	 * Checks squares on a column between the
	 * start and end position for blank squares
	 * and hidden goals.
	 *
	 * @param column	column to check
	 * @param start		start of path
	 * @param end		end of path
	 * @return true if no blank/hidden squares, false if any blanks/hidden squares
	 */
	private boolean isBlankFreeColumn(int column, int start, int end) {
		// loop through each row in column from start to end
		for (int row = start; row <= end; row++) {
			boolean isBlank = this.levelLayout[row][column] instanceof BlankSquare;
			boolean isHidden = this.hasHiddenGoalAt(row, column);
			if (isBlank || isHidden) return false;  // false if any blank or hidden
		}
		return true;
	}
	
	/**
	 * Checks if the path between the
	 * players eyeball and the destination
	 * has any blank squares or hidden goals.
	 * <p>
	 * This method checks all squares between
	 * the current eyeball position and the
	 * desired destination. If any are blank
	 * or hidden then the path is not valid.
	 *
	 * @param destinationRow    row position of desired move
	 * @param destinationColumn column position of desired move
	 * @return MoveError if blank or hidden goal is blocking the path, null if path is clear
	 */
	@Override
	public MoveError blankPathCheck(int destinationRow, int destinationColumn) {
		boolean isClearPath;
		int playerRow = this.getEyeballRow();
		int playerColumn = this.getEyeballColumn();

		if(playerRow == destinationRow) {
			// if movement on same row then needs to check all squares on the row
			int startColumn = Math.min(destinationColumn, playerColumn);
			int endColumn = Math.max(destinationColumn, playerColumn);
			isClearPath = this.isBlankFreeRow(playerRow, startColumn, endColumn);
		}
		else {	
			// else movement is on same column and needs to check all squares on column
			int startRow = Math.min(destinationRow, playerRow);
			int endRow = Math.max(destinationRow, playerRow);
			isClearPath = this.isBlankFreeColumn(playerColumn, startRow, endRow);
		}

		if (!isClearPath) return MoveError.MOVE_OVER_BLANK;
		return null;	// null if clear path
	}

	/**
	 * Checks if the players eyeball can
	 * move to the desired destination.
	 * <p>
	 * This method checks the move is not
	 * diagonal, the player is facing the
	 * correct direction, there are no
	 * blanks in the path, the destination
	 * is the same shape/color, and it is
	 * not the same position.
	 *
	 * @param destinationRow    row position of desired move
	 * @param destinationColumn column position of desired move
	 * @return MoveError if move is invalid, null if valid
	 */
	@Override
	public MoveError canMoveTo(int destinationRow, int destinationColumn) {
		// check diagonal
		MoveError diagonalError = this.diagonalMoveCheck(destinationRow, destinationColumn);
		if (diagonalError != null) return diagonalError;

		// check direction
		MoveError directionError = this.directionCheck(destinationRow, destinationColumn);
		if (directionError != null) return directionError;

		// check blanks
		MoveError blankFreeError = this.blankPathCheck(destinationRow, destinationColumn);
		if (blankFreeError != null) return blankFreeError;

		// check shape and color
		MoveError shapeOrColorError = this.sameShapeOrColorCheck(destinationRow, destinationColumn);
		if (shapeOrColorError != null) return shapeOrColorError;

		// check same position
		MoveError sameError = this.samePositionCheck(destinationRow, destinationColumn);
		if (sameError != null) return sameError;

		return null;	// success
	}

	/**
     * Moves the player eyeball to the given
     * destination if the move is valid.
	 * <p>
     * This method only moves the player
     * eyeball if the movement is valid.
     * If the square is a goal the goal
	 * count will be incremented and its
	 * position is saved and will be
	 * added to the hidden goals on the
	 * next move.
     *
     * @param destinationRow    row position of desired move
     * @param destinationColumn column position of desired move
     * @return MoveError if move is invalid, null if move has been completed
     */
	@Override
	public MoveError moveTo(int destinationRow, int destinationColumn) {
		// if move is invalid then return early with error
		MoveError error = this.canMoveTo(destinationRow, destinationColumn);
		if (error != null) return error;

        // save past move
        this.addPastMove();

		// change direction of eyeball
		int playerCurrentRow = this.getEyeballRow();
		int playerCurrentColumn = this.getEyeballColumn();
		if(destinationRow < playerCurrentRow) {
			this.playerEyeball.setDirection(Direction.UP);
		}
		else if(destinationRow > playerCurrentRow) {
			this.playerEyeball.setDirection(Direction.DOWN);
		}
		else if(destinationColumn < playerCurrentColumn) {
			this.playerEyeball.setDirection(Direction.LEFT);
		}
		else if(destinationColumn > playerCurrentColumn) {
			this.playerEyeball.setDirection(Direction.RIGHT);
		}
		
		// set new player eyeball position
		this.playerEyeball.setPosition(destinationRow, destinationColumn);
		
		// if moving off a goal square add previous goal position to hidden
		if(this.previousGoalPosition != null) {
			int row = this.previousGoalPosition.row();
			int column = this.previousGoalPosition.column();
			this.hiddenGoals.add(new Position(row, column));	// adds goal to hidden
			this.previousGoalPosition = null;	// clears previous position
		}
		
		// update goal count and remove goal if moved onto goal square
		if(this.hasGoalAt(destinationRow, destinationColumn)) {
			this.totalCompletedGoals++;
			// save square position to be cleared next move
			this.previousGoalPosition = new Position(destinationRow, destinationColumn);
		}

		// success
        return null;
    }
}
