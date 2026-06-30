package nz.ac.ara.hc.logicmaze.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import nz.ac.ara.hc.logicmaze.R;
import nz.ac.ara.hc.logicmaze.data.repository.GameFileReader;
import nz.ac.ara.hc.logicmaze.data.repository.GameRepository;
import nz.ac.ara.hc.logicmaze.viewmodel.LevelSelectViewModel;

public class LevelSelectActivity extends AppCompatActivity {
    private LevelSelectViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_level_select);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // MENU button
        ImageButton btnHome = findViewById(R.id.img_btn_level_select_menu);
        btnHome.setOnClickListener(view -> {
            finish();   // close page (goes back to main menu)
        });

        // SETTINGS button
        ImageButton btnSettings = findViewById(R.id.img_btn_level_select_settings);
        btnSettings.setOnClickListener(view -> {
            this.showSettingsOverlay();
        });

        // setup viewmodel
        viewModel = new ViewModelProvider(this).get(LevelSelectViewModel.class);
        GameFileReader fileReader = new GameFileReader(getApplicationContext());
        GameRepository repository = new GameRepository(fileReader);
        viewModel.initialize(repository);

        // observe error messages
        viewModel.getErrorMessage().observe(this, errorMsg -> {
            // display message in toast
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        });

        this.displayLevelButtons();
    }

    private void displayLevelButtons() {
        int levelCount = viewModel.loadLevelCount();
        if (levelCount <= 0) return;    // abort if no levels found

        GridLayout levelsGridLayout = findViewById(R.id.gridlayout_levels);
        levelsGridLayout.removeAllViews(); // remove any old buttons

        // calculates margin for each button
        int marginSize = 8;
        int margin = (int) (marginSize * getResources().getDisplayMetrics().density);

        for (int i = 0; i < levelCount; i++) {
            int levelNumber = i + 1; // levels start at 1

            // create button
            Button btn = new androidx.appcompat.widget.AppCompatButton(this) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    // calculate button size
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    int width = getMeasuredWidth();
                    // set height as width to make button square
                    setMeasuredDimension(width, width);
                }
            };

            // apply style to button
            btn.setBackground(androidx.core.content.ContextCompat.getDrawable(this, R.drawable.btn_level_select));
            btn.setText(String.valueOf(levelNumber));
            btn.setTextSize(32);
            btn.setTypeface(null, android.graphics.Typeface.BOLD);
            btn.setGravity(android.view.Gravity.CENTER);

            // create parameters for button
            GridLayout.LayoutParams btnParams = new GridLayout.LayoutParams();
            btnParams.width = 0;
            btnParams.height = 0;

            // put button in next available grid
            btnParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);  // make each column equal width
            btnParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED);

            // set margins
            btnParams.setMargins(margin, margin, margin, margin);
            btn.setLayoutParams(btnParams); // set buttons parameters

            // set listener
            btn.setOnClickListener(v -> {
                Toast.makeText(LevelSelectActivity.this, "Loading Level " + levelNumber, Toast.LENGTH_SHORT).show();
                if (viewModel.selectLevel(levelNumber)) {
                    // navigate to play
                    Intent intent = new Intent(this, PlayActivity.class);
                    startActivity(intent);
                }
            });

            // add button to grid
            levelsGridLayout.addView(btn);
        }
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
}