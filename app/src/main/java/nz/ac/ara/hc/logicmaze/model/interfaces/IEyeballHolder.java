package nz.ac.ara.hc.logicmaze.model.interfaces;

import nz.ac.ara.hc.logicmaze.model.enums.Direction;

public interface IEyeballHolder {
    public void addEyeball(int row, int column, Direction direction);
    public int getEyeballRow();
    public int getEyeballColumn();
    public Direction getEyeballDirection();
}