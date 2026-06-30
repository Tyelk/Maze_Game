package nz.ac.ara.hc.logicmaze;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import nz.ac.ara.hc.logicmaze.model.classes.Game;
import nz.ac.ara.hc.logicmaze.ui.MainMenuActivity;

@RunWith(AndroidJUnit4.class)
public class MainMenuActivityTest {
    @Rule
    public ActivityScenarioRule<MainMenuActivity> activityRule =
            new ActivityScenarioRule<>(MainMenuActivity.class);

    @Before
    public void navigateToEighthLevel() {
        // start from main menu to ensure everything loads correctly
        onView(withId(R.id.btn_main_play)).perform(click());
        // select level 8
        onView(withText("8")).perform(click());
    }

    @After
    public void resetLevel() {
        // reset the level after each test to ensure no progress is carried over
        Game.getInstance().resetLevel();
    }

    @Test
    public void levelNumberIsEight() {
        // check level number appears
        onView(withId(R.id.textview_play_level)).check(matches(withText("Level 8")));
    }

    @Test
    public void tappingSquareMovesPlayer() {
        // check player starts at bottom middle
        onView(withContentDescription("2-1")).check(matches(hasDescendant(withTagValue(is("eyeball")))));
        // move player
        onView(withContentDescription("2-0")).perform(click());
        // check player moved
        onView(withContentDescription("2-0")).check(matches(hasDescendant(withTagValue(is("eyeball")))));
        // check player no longer at previous position
        onView(withContentDescription("2-1")).check(matches(not(hasDescendant(withTagValue(is("eyeball"))))));
    }

    @Test
    public void timerStartsAfterFirstMove() {
        // check timer starts at 0
        onView(withId(R.id.textview_play_timer)).check(matches(withText("00:00")));
        // move player
        onView(withContentDescription("2-0")).perform(click());
        // check timer is no longer 0
        onView(withId(R.id.textview_play_timer)).check(matches(not(withText("00:00"))));
    }

    @Test
    public void moveCountIncrementsOnMove() {
        // get and format move count string
        Context context = ApplicationProvider.getApplicationContext();
        String startText = context.getString(R.string.game_move_text, 0);
        String changedText = context.getString(R.string.game_move_text, 1);

        // check move count start at 0
        onView(withId(R.id.textview_play_move)).check(matches(withText(startText)));
        // move player
        onView(withContentDescription("2-2")).perform(click());
        // check move count increased
        onView(withId(R.id.textview_play_move)).check(matches(withText(changedText)));
    }

    @Test
    public void undoRevertsMove() {
        // move player
        onView(withContentDescription("2-0")).perform(click());
        // check player moved
        onView(withContentDescription("2-0")).check(matches(hasDescendant(withTagValue(is("eyeball")))));
        // click undo button
        onView(withId(R.id.btn_play_undo)).perform(click());
        // check player moved back
        onView(withContentDescription("2-1")).check(matches(hasDescendant(withTagValue(is("eyeball")))));
    }

    @Test
    public void goalCountIncrementsWhenGoalHit() {
        // get and format goal count string
        Context context = ApplicationProvider.getApplicationContext();
        String startText = context.getString(R.string.game_goal_text, 0, 2);
        String changedText = context.getString(R.string.game_goal_text, 1, 2);

        // check goals hit count starts at 0
        onView(withId(R.id.textview_play_goal)).check(matches(withText(startText)));
        // move player onto goal
        onView(withContentDescription("1-1")).perform(click());
        // check goals hit count is increased
        onView(withId(R.id.textview_play_goal)).check(matches(withText(changedText)));
    }

    @Test
    public void gameEndOverlayShownWhenAllGoalsHit() {
        // move player
        onView(withContentDescription("1-1")).perform(click());
        // move player
        onView(withContentDescription("0-1")).perform(click());
        // move player
        onView(withContentDescription("0-2")).perform(click());
        // check game end overlay is visible
        onView(withId(R.id.textview_game_end_overlay_title)).inRoot(isDialog()).check(matches(isDisplayed()));
    }

    @Test
    public void gameEndPlayAgainOptionRestartsTheGame() {
        // move player
        onView(withContentDescription("1-1")).perform(click());
        // move player
        onView(withContentDescription("0-1")).perform(click());
        // move player
        onView(withContentDescription("0-2")).perform(click());
        // select play again button
        onView(withId(R.id.btn_game_end_overlay_play_again)).perform(click());

        // check player starts at bottom middle
        onView(withContentDescription("2-1")).check(matches(hasDescendant(withTagValue(is("eyeball")))));
        // check timer starts at 0
        onView(withId(R.id.textview_play_timer)).check(matches(withText("00:00")));
        // get and format move count string
        Context context = ApplicationProvider.getApplicationContext();
        String startText = context.getString(R.string.game_move_text, 0);
        // check move count starts at 0
        onView(withId(R.id.textview_play_move)).check(matches(withText(startText)));
    }

    @Test
    public void pauseResetOptionRestartsTheGame() {
        // move player
        onView(withContentDescription("1-1")).perform(click());
        // move player
        onView(withContentDescription("0-1")).perform(click());
        // pause game
        onView(withId(R.id.img_btn_play_settings)).perform(click());
        // select reset button
        onView(withId(R.id.btn_play_pause_overlay_reset)).perform(click());

        // check player starts at bottom middle
        onView(withContentDescription("2-1")).check(matches(hasDescendant(withTagValue(is("eyeball")))));
        // check timer starts at 0
        onView(withId(R.id.textview_play_timer)).check(matches(withText("00:00")));
        // get and format move count string
        Context context = ApplicationProvider.getApplicationContext();
        String startText = context.getString(R.string.game_move_text, 0);
        // check move count starts at 0
        onView(withId(R.id.textview_play_move)).check(matches(withText(startText)));
    }
}