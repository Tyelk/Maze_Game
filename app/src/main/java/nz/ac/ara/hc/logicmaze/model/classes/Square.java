package nz.ac.ara.hc.logicmaze.model.classes;

import nz.ac.ara.hc.logicmaze.model.enums.Color;
import nz.ac.ara.hc.logicmaze.model.enums.Shape;

/**
 * <h1>Square</h1>
 * The Square class holds the following data
 * color, shape.
 * 
 * @author Hadley Clark
 * @version 1.0.0
 * @since 2026-03-07
 */
public abstract class Square {
	protected Color color;
	protected Shape shape;
	
	/**
	 * Instantiates a new Square object.
	 * 
	 * @param color	color of new square
	 * @param shape	shape of new square
	 */
	Square(Color color, Shape shape) {
		this.color = color;
		this.shape = shape;
	}
	
	/**
	 * Gets the color of the square.
	 * 
	 * @return color of square
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Gets the shape of the square.
	 * 
	 * @return shape of square
	 */
	public Shape getShape() {
		return this.shape;
	}
}
