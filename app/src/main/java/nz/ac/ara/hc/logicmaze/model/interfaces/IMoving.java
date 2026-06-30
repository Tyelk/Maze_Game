package nz.ac.ara.hc.logicmaze.model.interfaces;

import nz.ac.ara.hc.logicmaze.model.enums.MoveError;

public interface IMoving {
    public MoveError canMoveTo(int destinationRow, int destinationColumn);
    public MoveError directionCheck(int destinationRow, int destinationColumn);
    public MoveError blankPathCheck(int destinationRow, int destinationColumn);
    public MoveError moveTo(int destinationRow, int destinationColumn);
}