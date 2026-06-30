package nz.ac.ara.hc.logicmaze.viewmodel;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.CountDownTimer;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

import nz.ac.ara.hc.logicmaze.R;
import nz.ac.ara.hc.logicmaze.data.common.CommonConstants;
import nz.ac.ara.hc.logicmaze.data.common.ErrorCode;
import nz.ac.ara.hc.logicmaze.data.common.Results;
import nz.ac.ara.hc.logicmaze.model.classes.Game;
import nz.ac.ara.hc.logicmaze.model.enums.Color;
import nz.ac.ara.hc.logicmaze.model.enums.Direction;
import nz.ac.ara.hc.logicmaze.model.enums.MoveError;
import nz.ac.ara.hc.logicmaze.model.enums.Shape;
import nz.ac.ara.hc.logicmaze.model.records.Position;

public class PlayViewModel extends AndroidViewModel {
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> fatalError = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> gameEnd = new MutableLiveData<>(false);
    private final MutableLiveData<Long> secondsSinceStart = new MutableLiveData<>(0L);

    private int playerId = -1;
    private int moveCount = 0;
    private CountDownTimer levelTimer;
    private boolean isTimerRunning = false;
    private boolean isGameStarted = false;

    public PlayViewModel(Application application) {
        super(application);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<Boolean> getFatalError() {
        return fatalError;
    }
    public LiveData<Boolean> getGameEnd() {
        return gameEnd;
    }
    public LiveData<Long> getSecondsSinceStart() {
        return secondsSinceStart;
    }

    public void resetFatalError() {
        fatalError.setValue(false);
    }
    public void setPlayerId(int newId) {
        this.playerId = newId;
    }
    public int getPlayerId() {
        return this.playerId;
    }

    public int getMoveCount() {
        return this.moveCount;
    }

    private void setErrorMessage(ErrorCode error) {
        // get error messages from resources
        errorMessage.setValue(getApplication().getString(error.getResourceId()));
    }

    private void handleFatalError(ErrorCode error) {
        setErrorMessage(error);
        fatalError.setValue(true);
    }

    public Integer getLevelNumber() {
        Results<Integer> results = Game.getInstance().getLevelNumber();

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
        }

        // return value
        return results.getData();
    }

    public Integer getGoalCount() {
        Results<Integer> results = Game.getInstance().getGoalCount();

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
        }

        // return value
        return results.getData();
    }

    public Integer getCompletedGoalCount() {
        Results<Integer> results = Game.getInstance().getCompletedGoalCount();

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
        }

        // return value
        return results.getData();
    }

    public Integer getLevelHeight() {
        Results<Integer> results = Game.getInstance().getLevelHeight();

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
        }

        // return value
        return results.getData();
    }

    public Integer getLevelWidth() {
        Results<Integer> results = Game.getInstance().getLevelWidth();

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
        }

        // return value
        return results.getData();
    }

    public Integer getColor(int row, int column) {
        Results<Color> results = Game.getInstance().getLevelColorAt(row, column);

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
        }

        // return value
        return switch (results.getData()) {
            case Color.RED -> R.color.square_red;
            case Color.BLUE -> R.color.square_blue;
            case Color.YELLOW -> R.color.square_yellow;
            case Color.GREEN -> R.color.square_green;
            case Color.PURPLE -> R.color.square_purple;
            case null, default -> R.color.transparent;
        };
    }

    public Integer getShape(int row, int column) {
        Results<Shape> results = Game.getInstance().getLevelShapeAt(row, column);

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
        }

        // return value
        return switch (results.getData()) {
            case Shape.DIAMOND -> R.drawable.diamond;
            case Shape.CROSS -> R.drawable.cross;
            case Shape.STAR -> R.drawable.star;
            case Shape.FLOWER -> R.drawable.flower;
            case Shape.LIGHTNING -> R.drawable.lightning;
            case null, default -> R.color.transparent;
        };
    }

    public List<Position> getAllGoalPositions() {
        Results<List<Position>> results = Game.getInstance().getGoalPositions();

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
            return null;
        }

        // return goals (read only)
        return results.getData();
    }

    public Boolean isHiddenGoal(int row, int column) {
        Results<Boolean> results = Game.getInstance().levelHasHiddenGoalAt(row, column);

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
        }

        // return value
        return results.getData();
    }

    public Integer getPlayerRow() {
        Results<Integer> results = Game.getInstance().getLevelEyeballRow();

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
        }

        // return value
        return results.getData();
    }

    public Integer getPlayerColumn() {
        Results<Integer> results = Game.getInstance().getLevelEyeballColumn();

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
        }

        // return value
        return results.getData();
    }

    public Float getPlayerDirection() {
        Results<Direction> results = Game.getInstance().getLevelEyeballDirection();

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
        }

        // return value
        return switch (results.getData()) {
            case Direction.UP -> 270f;
            case Direction.DOWN -> 90f;
            case Direction.LEFT -> 180f;
            case null, default -> 0f;
        };
    }

    public boolean movePlayer(int row, int column) {
        Results<MoveError> results = Game.getInstance().movePlayerTo(row, column);

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
        }

        // if invalid move show error to user
        if (results.getData() != null) {
            errorMessage.setValue(getApplication().getString(results.getData().getResourceId()));
            return false;
        }

        // increment count and start game
        this.moveCount++;
        if (!this.isGameStarted) this.isGameStarted = true;

        // start timer
        this.startTimer();

        // successful move
        return true;
    }

    public void checkGameWin() {
        Integer goalCount = this.getGoalCount();
        Integer completedGoalCount = this.getCompletedGoalCount();
        if (goalCount == null || completedGoalCount == null) return;

        // if all goals have been hit the game is over
        if (completedGoalCount >= goalCount) {
            gameEnd.setValue(true);
        }
    }

    public boolean resetLevel() {
        ErrorCode results = Game.getInstance().resetLevel();

        // if error stop game
        if (results != null) {
            this.handleFatalError(results);
            return false;
        }

        // reset data
        this.moveCount = 0;
        this.isGameStarted = false;
        this.gameEnd.setValue(false);
        this.resetTimer();

        // successful reset
        return true;
    }

    public boolean undoMove() {
        Results<MoveError> results = Game.getInstance().undoPlayersLastMove();

        // if error stop game
        if (results.getError() != null) {
            this.handleFatalError(results.getError());
        }

        // if no previous moves show error
        if (results.getData() != null) {
            errorMessage.setValue(getApplication().getString(results.getData().getResourceId()));
            return false;
        }

        // successful reset
        return true;
    }

    public void startTimer() {
        if (this.isTimerRunning || !this.isGameStarted) return; // return if running or game is NOT started
        this.isTimerRunning = true;

        // timer with 1 second tick
        levelTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long timeUntilFinished) {
                // get current time and increment it if not null
                Long currentTime = secondsSinceStart.getValue();
                secondsSinceStart.setValue(currentTime != null ? currentTime + 1 : 0);
            }

            @Override
            public void onFinish() {}
        }.start();
    }

    public void pauseTimer() {
        if (levelTimer != null) levelTimer.cancel();
        this.isTimerRunning = false;
    }

    private void resetTimer() {
        this.pauseTimer();
        secondsSinceStart.setValue(0L);
    }

    public void setSoundPreference(boolean isEnabled) {
        // get settings
        SharedPreferences prefs = getApplication().getSharedPreferences(CommonConstants.PREFERENCES_NAME, MODE_PRIVATE);
        // save sound setting
        prefs.edit().putBoolean(CommonConstants.SOUND_SETTING_NAME, isEnabled).apply();
    }

    public boolean getSoundPreference() {
        // get the sound setting
        SharedPreferences prefs = getApplication().getSharedPreferences(CommonConstants.PREFERENCES_NAME, MODE_PRIVATE);
        return prefs.getBoolean(CommonConstants.SOUND_SETTING_NAME, false);    // default as false if not found
    }
}
