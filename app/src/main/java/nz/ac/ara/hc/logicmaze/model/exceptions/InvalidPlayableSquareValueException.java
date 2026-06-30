package nz.ac.ara.hc.logicmaze.model.exceptions;

import java.io.Serial;

/**
 * Thrown to indicate that a square-related issue has occurred.
 */
public class InvalidPlayableSquareValueException extends IllegalArgumentException {
	@Serial
    private static final long serialVersionUID = 2890784322315720902L;

	public InvalidPlayableSquareValueException(String message) {
		super(message);
	}
	
	public InvalidPlayableSquareValueException(String message, Throwable cause) {
		super(message, cause);
	}
}
