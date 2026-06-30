package nz.ac.ara.hc.logicmaze.model.classes;

import nz.ac.ara.hc.logicmaze.model.enums.Color;
import nz.ac.ara.hc.logicmaze.model.enums.Shape;
import nz.ac.ara.hc.logicmaze.model.exceptions.InvalidPlayableSquareValueException;

/**
 * <h1>PlayableSquare</h1>
 * The PlayableSquare class inherits from
 * the Square class and is used to
 * denote a square as playable.
 * 
 * @author Hadley Clark
 * @version 1.0.0
 * @since 2026-03-07
 */
public class PlayableSquare extends Square {
	/**
	 * Instantiates a new PlayableSquare object.
	 * <p>
	 * The color and shape are validated before
	 * the object is created to ensure they
	 * are not blank because a playable square
	 * cannot have a blank shape or color.
	 * 
	 * @param color		color of new square
	 * @param shape		shape of new square
	 */
	public PlayableSquare(Color color, Shape shape) {
		super(PlayableSquare.validateColor(color), PlayableSquare.validateShape(shape));
	}
	
	/**
	 * Validates that the color is NOT blank or null.
	 * 
	 * @param color									color of the square
	 * @throws InvalidPlayableSquareValueException	thrown if color is BLANK or null
	 */
	private static Color validateColor(Color color) {
		if (color == null) throw new InvalidPlayableSquareValueException("Playable square construction error: color cannot be null");
		if(color == Color.BLANK) throw new InvalidPlayableSquareValueException("Playable square construction error: color cannot be BLANK");
		return color;
	}
	
	/**
	 * Validates that the shape is NOT blank or null.
	 * 
	 * @param shape									shape of the square
	 * @throws InvalidPlayableSquareValueException	thrown if shape is BLANK or null
	 */
	private static Shape validateShape(Shape shape) {
		if (shape == null) throw new InvalidPlayableSquareValueException("Playable square construction error: shape cannot be null");
		if(shape == Shape.BLANK) throw new InvalidPlayableSquareValueException("Playable square construction error: shape cannot be BLANK");
		return shape;
	}
	
	/**
	 * Sets the squares color
	 * to the given color.
	 * 
	 * @param color	color of the square
	 */
	public void setColor(Color color) {
		this.color = PlayableSquare.validateColor(color);
	}
	
	/**
	 * Sets the squares shape
	 * to the given shape.
	 * 
	 * @param shape	shape of the square
	 */
	public void setShape(Shape shape) {
		this.shape = PlayableSquare.validateShape(shape);
	}
}
