package nz.ac.ara.hc.logicmaze.viewmodel;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import nz.ac.ara.hc.logicmaze.data.common.CommonConstants;
import nz.ac.ara.hc.logicmaze.data.common.ErrorCode;
import nz.ac.ara.hc.logicmaze.data.repository.GameRepository;
import nz.ac.ara.hc.logicmaze.data.common.Results;

public class LevelSelectViewModel extends AndroidViewModel {
    private GameRepository repository;

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LevelSelectViewModel(Application application) {
        super(application);
    }

    public void initialize(GameRepository repository) {
        this.repository = repository;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private void setErrorMessage(ErrorCode error) {
        // get error messages from resources
        errorMessage.setValue(getApplication().getString(error.getResourceId()));
    }

    public int loadLevelCount() {
        // abort if no repo
        if (repository == null) {
            this.setErrorMessage(ErrorCode.NO_REPO);
            return -1;
        };

        // get count
        Results<Integer> countResults = repository.getFileCount();

        // if error set message
        if (countResults.getError() != null) {
            this.setErrorMessage(countResults.getError());
            return -1;
        }
        // if no levels set message
        if (countResults.getData() < 1) {
            this.setErrorMessage(ErrorCode.UNKNOWN_ERROR);
        }

        // return count
        return countResults.getData();
    }

    public boolean selectLevel(int levelNumber) {
        // abort if no repo
        if (repository == null) {
            this.setErrorMessage(ErrorCode.NO_REPO);
            return false;
        };

        // load level
        ErrorCode result = repository.loadLevel(levelNumber);

        // if error set message
        if (result != null) {
            this.setErrorMessage(result);
            return false;
        }

        // return success
        return true;
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
