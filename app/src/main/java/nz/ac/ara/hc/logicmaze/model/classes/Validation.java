package nz.ac.ara.hc.logicmaze.model.classes;


/**
 * <h1>Validation</h1>
 * Contains static methods to validate
 * values and positions before actions
 * are done.
 * 
 * @author Hadley Clark
 * @version 1.0.0
 * @since 2026-03-07
 */
class Validation {
	static final int MINIMUM_LEVEL_VALUE = 1;
	
	/**
	 * Check if a levels dimensions are valid.
	 * <p>
	 * Makes sure the height and width is not
	 * less than the minimum value.
	 * 
	 * @param height	height of the level
	 * @param width		width of the level
	 * @return true if valid, else false
	 */
	static boolean isValidLevelDimensions(int height, int width) {
		return height >= MINIMUM_LEVEL_VALUE && width >= MINIMUM_LEVEL_VALUE;
	}
	
	/**
	 * Checks if a position is within the
	 * boundaries of a level.
	 * <p>
	 * Makes sure the row/column are 
	 * less than the level height/width,
	 * and they are not negative.
	 * 
	 * @param row		row of the level
	 * @param column	width of the level
	 * @param level		level to check
	 * @return true if within boundaries, else false
	 */
	static boolean isValidLevelAndPosition(int row, int column, Level level) {
		if (level == null) return false;	// level cant be null
		boolean isValidRow = row >= 0 && row < level.getHeight();
		boolean isValidColumn = column >= 0 && column < level.getWidth();
		return isValidRow && isValidColumn;
	}
	
	/**
	 * Checks the level and eyeball of
	 * the given level is not null.
	 * 
	 * @param level		level to check
	 * @return true if level and eyeball are valid, else false
	 */
	static boolean isValidLevelAndEyeball(Level level) {
		return level != null && level.hasEyeball();
	}
}
