package nz.ac.ara.hc.logicmaze.model.classes;

import nz.ac.ara.hc.logicmaze.model.enums.Direction;
import nz.ac.ara.hc.logicmaze.model.records.Position;

/**
 * <h1>Eyeball</h1>
 * Contains the data for an eyeball.
 * 
 * @author Hadley Clark
 * @version 1.0.0
 * @since 2026-03-07
 */
class Eyeball {
	private Position position;
	private Direction direction;
	
	/**
	 * Instantiates a new Eyeball object.
	 * 
	 * @param row			row that the eyeball is on
	 * @param column		column that the eyeball is on
	 * @param direction		direction the eyeball is facing
	 */
	Eyeball(int row, int column, Direction direction) {
		this.setPosition(row, column);
		this.setDirection(direction);
	}
	
	/**
	 * Sets the eyeballs position
	 * to the given row and column.
	 * 
	 * @param row			row of the eyeball
	 * @param column		column of the eyeball
	 */
	void setPosition(int row, int column) {
		this.position = new Position(row, column);
	}
	
	/**
	 * Sets the eyeballs direction
	 * to the given direction.
	 * 
	 * @param direction	of the eyeball
	 */
	void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	/**
	 * Gets eyeballs row value.
	 * 
	 * @return eyeballs row
	 */
	int getRow() {
		return this.position.row();
	}
	
	/**
	 * Gets eyeballs column value.
	 * 
	 * @return eyeballs column
	 */
	int getColumn() {
		return this.position.column();
	}
	
	/**
	 * Gets the direction of the
	 * eyeball.
	 * 
	 * @return eyeball direction
	 */
	Direction getDirection() {
		return this.direction;
	}
}
