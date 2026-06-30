package nz.ac.ara.hc.logicmaze.viewmodel;

import static android.content.Context.MODE_PRIVATE;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;

import nz.ac.ara.hc.logicmaze.data.common.CommonConstants;

public class MainMenuViewModel extends AndroidViewModel {
    public MainMenuViewModel(Application application) {
        super(application);
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
