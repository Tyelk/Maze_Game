package nz.ac.ara.hc.logicmaze.model.interfaces;

import nz.ac.ara.hc.logicmaze.model.classes.Square;
import nz.ac.ara.hc.logicmaze.model.enums.Color;
import nz.ac.ara.hc.logicmaze.model.enums.Shape;

public interface ISquareHolder {
    public void addSquare(Square square, int row, int column);
    public Color getColorAt(int row, int column);
    public Shape getShapeAt(int row, int column);
}