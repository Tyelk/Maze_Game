package nz.ac.ara.hc.logicmaze.model.interfaces;

import nz.ac.ara.hc.logicmaze.data.common.ErrorCode;
import nz.ac.ara.hc.logicmaze.data.common.Results;

public interface ILevelHolder {
    public ErrorCode addLevel(int height, int width, int levelNumber);
    public Results<Integer> getLevelWidth();
    public Results<Integer> getLevelHeight();
    public ErrorCode setCurrentLevel(int levelNumber);
}