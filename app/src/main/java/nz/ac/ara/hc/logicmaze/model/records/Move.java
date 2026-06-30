package nz.ac.ara.hc.logicmaze.model.records;

import nz.ac.ara.hc.logicmaze.model.enums.Direction;

/**
 * <h1>Move</h1>
 * The Move record is used to
 * hold the coordinates and direction
 * of moves.
 *
 * @author Hadley Clark
 * @version 1.0.0
 * @since 2026-06-02
 */
public record Move(int row, int column, Direction direction) {}
