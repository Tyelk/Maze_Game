package nz.ac.ara.hc.logicmaze.model.enums;

import nz.ac.ara.hc.logicmaze.R;

/**
 * Defines errors used to communicate game state
 * and movement results.
 */
public enum MoveError {
    SAME_POSITION(R.string.move_error_same_position),
    DIFFERENT_SHAPE_OR_COLOR(R.string.move_error_different_shape_or_color),
    BACKWARDS_MOVE(R.string.move_error_backwards_move),
    MOVE_OVER_BLANK(R.string.move_error_move_over_blank),
    MOVE_DIAGONALLY(R.string.move_error_move_diagonal),
    NO_PAST_MOVES(R.string.move_error_no_past_moves);

    private final int resourceId;

    MoveError(int stringCode) {
        this.resourceId = stringCode;
    }

    public int getResourceId() {
        return this.resourceId;
    }
}