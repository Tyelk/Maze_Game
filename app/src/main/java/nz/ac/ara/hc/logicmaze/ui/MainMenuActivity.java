package nz.ac.ara.hc.logicmaze.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import nz.ac.ara.hc.logicmaze.R;
import nz.ac.ara.hc.logicmaze.viewmodel.MainMenuViewModel;

public class MainMenuActivity extends AppCompatActivity {
    private MainMenuViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // create view model
        viewModel = new ViewModelProvider(this).get(MainMenuViewModel.class);

        // PLAY button
        Button btnPlay = findViewById(R.id.btn_main_play);
        btnPlay.setOnClickListener(view -> {
            // navigate to level select
            Intent intent = new Intent(MainMenuActivity.this, LevelSelectActivity.class);
            startActivity(intent);
        });

        // TUTORIAL button
        Button btnTutorial = findViewById(R.id.btn_main_tutorial);
        btnTutorial.setOnClickListener(view -> {
            this.showTutorialOverlay();
        });

        // SETTINGS button
        Button btnSettings = findViewById(R.id.btn_main_settings);
        btnSettings.setOnClickListener(view -> {
            this.showSettingsOverlay();
        });

        // QUIT button
        Button btnQuit = findViewById(R.id.btn_main_quit);
        btnQuit.setOnClickListener(view -> {
            finishAffinity();   // close game
        });
    }

    public void showSettingsOverlay() {
        // create dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.overlay_settings);

        // make the background transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // SOUND EFFECTS button
        SwitchCompat soundToggleBtn = dialog.findViewById(R.id.switch_settings_overlay);
        soundToggleBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.viewModel.setSoundPreference(isChecked);   // save change to preferences
        });
        // get the sound setting and apply to sound toggle button
        boolean isSoundOn = this.viewModel.getSoundPreference();
        soundToggleBtn.setChecked(isSoundOn);

        // CLOSE button
        Button closeBtn = dialog.findViewById(R.id.btn_settings_overlay_close);
        closeBtn.setOnClickListener(view -> {
            dialog.dismiss(); // close overlay
        });

        dialog.show();
    }

    public void showTutorialOverlay() {
        // create dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.overlay_tutorial);

        // make the background transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // CLOSE button
        Button closeBtn = dialog.findViewById(R.id.btn_tutorial_overlay_close);
        closeBtn.setOnClickListener(view -> {
            dialog.dismiss(); // close overlay
        });

        dialog.show();
    }
}