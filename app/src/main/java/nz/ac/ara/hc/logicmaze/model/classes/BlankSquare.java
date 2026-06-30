package nz.ac.ara.hc.logicmaze.model.classes;

import nz.ac.ara.hc.logicmaze.model.enums.Color;
import nz.ac.ara.hc.logicmaze.model.enums.Shape;

/**
 * <h1>BlankSqaure</h1>
 * The BlankSqaure class inherits from
 * the Square class and is used to
 * denote a square as blank.
 * 
 * @author Hadley Clark
 * @version 1.0.0
 * @since 2026-03-07
 */
public class BlankSquare extends Square {
	/**
	 * Instantiates a new BlankSquare object.
	 * <p>
	 * Does not take any parameters as a blank
	 * square cannot have a color or shape.
	 */
	public BlankSquare() {
		super(Color.BLANK, Shape.BLANK);
	}
}
