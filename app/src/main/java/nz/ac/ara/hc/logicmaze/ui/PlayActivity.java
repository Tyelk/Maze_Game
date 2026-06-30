package nz.ac.ara.hc.logicmaze.ui;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.Locale;

import nz.ac.ara.hc.logicmaze.R;
import nz.ac.ara.hc.logicmaze.model.records.Position;
import nz.ac.ara.hc.logicmaze.viewmodel.PlayViewModel;

public class PlayActivity extends AppCompatActivity {
    private PlayViewModel viewModel;
    private GridLayout mazeGrid;
    private TextView levelDisplay;
    private TextView goalDisplay;
    private TextView moveDisplay;
    private TextView timerDisplay;

    private MediaPlayer successSoundPlayer;
    private MediaPlayer failSoundPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // create view model
        viewModel = new ViewModelProvider(this).get(PlayViewModel.class);

        // get UI elements
        mazeGrid = findViewById(R.id.gridlayout_play);
        levelDisplay = findViewById(R.id.textview_play_level);
        goalDisplay = findViewById(R.id.textview_play_goal);
        moveDisplay = findViewById(R.id.textview_play_move);
        timerDisplay = findViewById(R.id.textview_play_timer);

        // load sounds
        successSoundPlayer = MediaPlayer.create(this, R.raw.success);
        failSoundPlayer = MediaPlayer.create(this, R.raw.fail);

        // SETTINGS button
        ImageButton settingsBtn = findViewById(R.id.img_btn_play_settings);
        settingsBtn.setOnClickListener(view -> {
            this.handlePause();
        });

        // UNDO button
        Button undoBtn = findViewById(R.id.btn_play_undo);
        undoBtn.setOnClickListener(view -> {
            this.undoMove();
        });

        // observe error messages
        viewModel.getErrorMessage().observe(this, this::handleError);

        // observe fatal errors
        viewModel.getFatalError().observe(this, this::handleFatalError);

        // observe game over
        viewModel.getGameEnd().observe(this, this::handleGameEnd);

        // observe game timer
        viewModel.getSecondsSinceStart().observe(this, this::handleTimerUpdate);

        this.initializeMaze();
    }

    private void handlePause() {
        this.viewModel.pauseTimer();
        this.showPauseOverlay();
    }

    private void showPauseOverlay() {
        // create dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.overlay_play_pause);

        // make the background transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // SOUND EFFECTS button
        SwitchCompat soundToggleBtn = dialog.findViewById(R.id.switch_play_pause_overlay);
        soundToggleBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.viewModel.setSoundPreference(isChecked);   // save change to preferences
        });
        // get the sound setting and apply to sound toggle button
        boolean isSoundOn = this.viewModel.getSoundPreference();
        soundToggleBtn.setChecked(isSoundOn);

        // CLOSE button
        Button closeBtn = dialog.findViewById(R.id.btn_play_pause_overlay_close);
        closeBtn.setOnClickListener(view -> {
            dialog.dismiss(); // close overlay
            this.viewModel.startTimer(); // resume timer
        });

        // RESET button
        Button resetBtn = dialog.findViewById(R.id.btn_play_pause_overlay_reset);
        resetBtn.setOnClickListener(view -> {
            this.reset();
            dialog.dismiss(); // close overlay
        });

        // QUIT button
        Button quitBtn = dialog.findViewById(R.id.btn_play_pause_overlay_quit);
        quitBtn.setOnClickListener(view -> {
            this.exit(); // go back to level select
        });

        dialog.show();
    }

    private void undoMove() {
        // undo move
        boolean isSuccessful = this.viewModel.undoMove();
        if (!isSuccessful) return;  // return if undo failed

        // update to show changes
        this.updateGame();
    }

    private void handleError(String errorMessage) {
        // show error message in toast
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        this.playFailSound();
    }

    private void playSuccessSound() {
        if(!this.viewModel.getSoundPreference()) return; // abort if sound off
        if (successSoundPlayer.isPlaying()) {
            // reset sound if currently playing
            successSoundPlayer.pause();
            successSoundPlayer.seekTo(0);
        }
        successSoundPlayer.start();
    }

    private void playFailSound() {
        if(!this.viewModel.getSoundPreference()) return; // abort if sound off
        if (failSoundPlayer.isPlaying()) {
            // reset sound if currently playing
            failSoundPlayer.pause();
            failSoundPlayer.seekTo(0);
        }
        failSoundPlayer.start();
    }

    private void handleFatalError(boolean isFatal) {
        if (!isFatal) return;   // return if NOT fatal
        viewModel.resetFatalError();    // clear fatal flag
        this.exit();    // go back to level select
    }

    private void handleGameEnd(boolean isGameEnded) {
        if (!isGameEnded) return;   // return if NOT over
        this.viewModel.pauseTimer();
        this.showGameEndOverlay();
        this.playSuccessSound();
    }

    private String formatTime(long secondsSinceStart) {
        long minutes = secondsSinceStart / 60;
        long seconds = secondsSinceStart % 60;
        // format time to have 2 digits for minutes and seconds
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void handleTimerUpdate(long secondsSinceStart) {
        // format resource string to show data
        this.timerDisplay.setText(formatTime(secondsSinceStart));
    }

    private void showGameEndOverlay() {
        // create dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.overlay_play_game_end);

        // make the background transparent
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // if error when getting goal count set to 0
        Integer goalCount = this.viewModel.getCompletedGoalCount();
        if (goalCount == null) goalCount = 0;
        // show goal results
        TextView goalsTextview = dialog.findViewById(R.id.textview_game_end_overlay_goals);
        goalsTextview.setText(getString(R.string.game_over_goal_text, goalCount));

        // show move results
        TextView movesTextView = dialog.findViewById(R.id.textview_game_end_overlay_moves);
        movesTextView.setText(getString(R.string.game_over_move_text, this.viewModel.getMoveCount()));

        // if error when getting time set to 0
        long time = viewModel.getSecondsSinceStart().getValue() != null ? viewModel.getSecondsSinceStart().getValue() : 0;
        String formattedTime = this.formatTime(time);
        // show time results
        TextView timeTextView = dialog.findViewById(R.id.textview_game_end_overlay_time);
        timeTextView.setText(getString(R.string.game_over_time_text, formattedTime));

        // REPLAY button
        Button replayBtn = dialog.findViewById(R.id.btn_game_end_overlay_play_again);
        replayBtn.setOnClickListener(view -> {
            this.reset();
            dialog.dismiss(); // close overlay
        });

        // QUIT button
        Button quitBtn = dialog.findViewById(R.id.btn_game_end_overlay_level_select);
        quitBtn.setOnClickListener(view -> {
            this.exit(); // go back to level select
        });

        // disable background close functionality
        dialog.setCancelable(false);
        dialog.show();
    }

    private void reset() {
        // reset the level data
        boolean isSuccessful = this.viewModel.resetLevel();
        if (!isSuccessful) return;  // return if reset failed

        // update to show changes
        this.updateGame();
    }

    private void exit() {
        this.reset();
        finish();   // go back to level select activity
    }

    private void initializeMaze() {
        this.loadPlayArea();
        this.loadPlayer();
        this.loadLevelNumber();
        this.updateGoalCount();
        this.updateMoveCount();
    }

    private ImageView createSquare(int row, int column, int padding, boolean isGoal) {
        // create square
        ImageView square = new ImageView(this);
        square.setAdjustViewBounds(true);

        // set shape
        Integer shapeId = viewModel.getShape(row, column);
        if (shapeId == null) return null; // return if value is missing
        square.setImageResource(shapeId);

        // set color
        Integer colorId = viewModel.getColor(row, column);
        if (colorId == null) return null; // return if value is missing
        int color = ContextCompat.getColor(this, colorId);
        square.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        // add padding
        square.setPadding(padding, padding, padding, padding);

        // add background color for goals
        if (isGoal) {
            // create goal background
            GradientDrawable roundedGoal = new GradientDrawable();
            roundedGoal.setShape(GradientDrawable.RECTANGLE);
            roundedGoal.setCornerRadius(100f);

            // set color of background
            int backgroundColor = ContextCompat.getColor(this, R.color.square_goal);
            roundedGoal.setColor(backgroundColor);

            // add background
            square.setBackground(roundedGoal);
        }

        return square;
    }

    private void loadPlayArea() {
        Integer height = viewModel.getLevelHeight();
        if (height == null) return; // return if value is missing
        Integer width = viewModel.getLevelWidth();
        if (width == null) return; // return if value is missing

        mazeGrid.removeAllViews();  // clear any old squares
        mazeGrid.setRowCount(height); // set grid dimensions
        mazeGrid.setColumnCount(width);

        // get goals
        List<Position> goalPositions = viewModel.getAllGoalPositions();
        if (goalPositions == null) return;  // return if error

        // calculate margins
        int marginSize = 8;
        int margin = (int) (marginSize * getResources().getDisplayMetrics().density);

        // calculate padding
        int pad = 6;
        int padding = (int) (pad * getResources().getDisplayMetrics().density);

        // loop through every row and column
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                // create container to hold all content in square
                FrameLayout squareContainer = new FrameLayout(this);
                squareContainer.setContentDescription(row + "-" + column);

                // check if square is goal
                boolean isGoal = false;
                for (Position pos : goalPositions) {
                    if (pos.row() == row && pos.column() == column) {
                        isGoal = true;
                        break;
                    }
                }

                // create square and add to container
                ImageView square = this.createSquare(row, column, padding, isGoal);
                if (square == null) return; // return if error
                squareContainer.addView(square);

                // create parameters
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();

                // set width and height
                params.width = 0;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(column, 1.0f); // columns of equal width
                params.rowSpec = GridLayout.spec(row);

                // add margin
                params.setMargins(margin, margin, margin, margin);

                // attach listener to move the eyeball when selected
                int rowPosition = row;
                int columnPosition = column;
                squareContainer.setOnClickListener(v -> {
                    this.handleSquareSelect(rowPosition, columnPosition);
                });

                // add parameters and insert into grid
                squareContainer.setLayoutParams(params);
                mazeGrid.addView(squareContainer);
            }
        }
    }

    private void loadPlayer() {
        // get player data
        Integer playerStartRow = viewModel.getPlayerRow();
        if (playerStartRow == null) return; // return if value is missing
        Integer playerStartColumn = viewModel.getPlayerColumn();
        if (playerStartColumn == null) return; // return if value is missing
        Float playerDirection = viewModel.getPlayerDirection();
        if (playerDirection == null) return; // return if value is missing

        // get square from grid
        int totalColumns = mazeGrid.getColumnCount();
        int squarePosition = (playerStartRow * totalColumns) + playerStartColumn;
        FrameLayout square = (FrameLayout) mazeGrid.getChildAt(squarePosition);

        if (square != null) {
            // create player eyeball
            ImageView playerView = new ImageView(this);
            playerView.setTag("eyeball");
            playerView.setAdjustViewBounds(true);
            playerView.setImageResource(R.drawable.eyeball);
            playerView.setRotation(playerDirection);    // direction

            // create id for player piece (to make it easier to find in grid)
            int playerId = View.generateViewId();
            playerView.setId(playerId);
            viewModel.setPlayerId(playerId);

            // add padding
            int padding = (int) (20 * getResources().getDisplayMetrics().density);
            playerView.setPadding(padding, padding, padding, padding);

            // insert into square
            square.addView(playerView);
        }
    }

    private void loadLevelNumber() {
        Integer number = this.viewModel.getLevelNumber();
        if (number == null) return; // return if value is missing

        // format resource string to show data
        this.levelDisplay.setText(getString(R.string.game_level_text, number));
    }

    private void handleSquareSelect(int row, int column) {
        // try to move player
        boolean isValidMove = viewModel.movePlayer(row, column);
        if (!isValidMove) return;   // if invalid then return

        // update to show changes
        this.updateGame();
        this.viewModel.checkGameWin();
    }

    private void updateGame() {
        this.updatePlayer();
        this.updateGoalVisibility();
        this.updateGoalCount();
        this.updateMoveCount();
    }

    private void updateGoalCount() {
        Integer goalCount = this.viewModel.getGoalCount();
        if (goalCount == null) return; // return if value is missing

        Integer completedGoalCount = this.viewModel.getCompletedGoalCount();
        if (completedGoalCount == null) return; // return if value is missing

        // format resource string to show data
        this.goalDisplay.setText(getString(R.string.game_goal_text, completedGoalCount, goalCount));
    }

    private void updateMoveCount() {
        int moveCount = this.viewModel.getMoveCount();
        this.moveDisplay.setText(getString(R.string.game_move_text, moveCount));
    }

    private void updatePlayer() {
        // get player
        ImageView playerView = mazeGrid.findViewById(viewModel.getPlayerId());

        if (playerView != null) {
            // get the square the player is in
            FrameLayout parentContainer = (FrameLayout) playerView.getParent();
            if (parentContainer != null) {
                // remove the player
                parentContainer.removeView(playerView);
            }
        }

        // get player data
        Integer playerRow = viewModel.getPlayerRow();
        if (playerRow == null) return; // return if value is missing
        Integer playerColumn = viewModel.getPlayerColumn();
        if (playerColumn == null) return; // return if value is missing
        Float playerDirection = viewModel.getPlayerDirection();
        if (playerDirection == null) return; // return if value is missing

        // get square from grid matching players position
        int totalColumns = mazeGrid.getColumnCount();
        int squarePosition = (playerRow * totalColumns) + playerColumn;
        FrameLayout square = (FrameLayout) mazeGrid.getChildAt(squarePosition);

        if (square != null && playerView != null) {
            // rotate player
            playerView.setRotation(playerDirection);
            // insert into square
            square.addView(playerView);
        }
    }

    private void updateGoalVisibility() {
        int gridColumns = mazeGrid.getColumnCount();

        List<Position> goalPositions = viewModel.getAllGoalPositions();
        if (goalPositions == null) return;  // return if error

        for (Position pos : goalPositions) {
            // loop through each position in goal list and get the square in the maze
            int squarePosition = (pos.row() * gridColumns) + pos.column();
            FrameLayout square = (FrameLayout) mazeGrid.getChildAt(squarePosition);
            if (square != null) {
                // check if square is hidden goal
                Boolean squareIsHidden = viewModel.isHiddenGoal(pos.row(), pos.column());
                if (squareIsHidden == null) return; // return if error

                if (squareIsHidden) {
                    square.setVisibility(View.INVISIBLE);   // if hidden HIDE goal
                } else {
                    square.setVisibility(View.VISIBLE);   // if not UNHIDE goal
                }
            }
        }
    }
}