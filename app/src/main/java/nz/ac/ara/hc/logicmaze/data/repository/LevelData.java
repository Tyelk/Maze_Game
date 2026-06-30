package nz.ac.ara.hc.logicmaze.data.repository;

import nz.ac.ara.hc.logicmaze.model.classes.Square;
import nz.ac.ara.hc.logicmaze.model.enums.Direction;
import nz.ac.ara.hc.logicmaze.model.records.Position;

/**
 * <h1>LevelData</h1>
 * The LevelData record is used to
 * hold all the data needed to
 * create a level.
 *
 * @author Hadley Clark
 * @version 1.0.0
 * @since 2026-06-02
 */
public record LevelData(int height, int width, Position start, Direction dir, Square[] squares, Position[] goals) {
}
