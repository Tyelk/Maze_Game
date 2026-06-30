package nz.ac.ara.hc.logicmaze.data.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nz.ac.ara.hc.logicmaze.data.common.ErrorCode;
import nz.ac.ara.hc.logicmaze.data.common.Results;
import nz.ac.ara.hc.logicmaze.model.classes.BlankSquare;
import nz.ac.ara.hc.logicmaze.model.classes.PlayableSquare;
import nz.ac.ara.hc.logicmaze.model.classes.Square;
import nz.ac.ara.hc.logicmaze.model.enums.Color;
import nz.ac.ara.hc.logicmaze.model.enums.Direction;
import nz.ac.ara.hc.logicmaze.model.enums.Shape;
import nz.ac.ara.hc.logicmaze.model.exceptions.InvalidPlayableSquareValueException;
import nz.ac.ara.hc.logicmaze.model.records.Position;

/**
 * <h1>LevelTranslator</h1>
 * The LevelTranslator class is responsible
 * for converting raw data into usable
 * data that can be used to create levels.
 *
 * @author Hadley Clark
 * @version 1.0.0
 * @since 2026-06-02
 */
public class LevelTranslator {
    private static final String PLAYER_START_REGEX = "^[<>^v]$";
    private static final char GOAL_VALUE = '1';

    /**
     * Create square from given data
     * <p>
     * Creates LevelData objects containing all the
     * necessary data to create individual levels.
     *
     * @param cellData raw cell data
     * @return Square if data is valid, else null
     */
    private static Square createSquare(String cellData) {
        // set color
        Color color = switch (cellData.charAt(0)) {
            case 'B' -> Color.BLUE;
            case 'R' -> Color.RED;
            case 'Y' -> Color.YELLOW;
            case 'G' -> Color.GREEN;
            case 'P' -> Color.PURPLE;
            default -> Color.BLANK;
        };
        // set shape
        Shape shape = switch (cellData.charAt(1)) {
            case 'D' -> Shape.DIAMOND;
            case 'C' -> Shape.CROSS;
            case 'S' -> Shape.STAR;
            case 'F' -> Shape.FLOWER;
            case 'L' -> Shape.LIGHTNING;
            default -> Shape.BLANK;
        };

        // create and return square
        if (color == Color.BLANK || shape == Shape.BLANK) {
            return new BlankSquare();
        } else {
            try {
                return new PlayableSquare(color, shape);
            } catch (InvalidPlayableSquareValueException e) {
                // return null if known error occurs
                return null;
            }
        }
    }

    /**
     * Translates raw data into level data
     * <p>
     * Creates LevelData objects containing all the
     * necessary data to create individual levels.
     *
     * @param rawData raw level data
     * @return Results: ErrorCode if error, LevelData if success
     */
    public static Results<LevelData> translateLevel(List<String> rawData) {
        // turn list into 2d list for easier navigation
        List<List<String>> twoDimensionalLevel = new ArrayList<>();
        for (String line : rawData) {
            twoDimensionalLevel.add(Arrays.asList(line.split(",")));
        }

        // if not playable then return error
        if (!isLevelPlayable(twoDimensionalLevel)) return Results.fail(ErrorCode.MALFORMED_LEVEL_DATA);

        // checks start direction
        List<Square> squares = new ArrayList<>();
        List<Position> goals = new ArrayList<>();
        Position start = null;
        Direction dir = null;

        // loop through each row and column
        for (var row = 0; row < twoDimensionalLevel.size(); row++) {
            for (var column = 0; column < twoDimensionalLevel.get(row).size(); column++) {
                // get each cell
                String cell = twoDimensionalLevel.get(row).get(column);

                // create square
                Square newSquare = createSquare(cell);
                if (newSquare == null) return Results.fail(ErrorCode.MALFORMED_LEVEL_DATA);
                else squares.add(newSquare);    // add if no error

                char state = cell.charAt(2);
                // if goal add goal position
                if (state == '1') {
                    goals.add(new Position(row, column));
                } else if (String.valueOf(state).matches(PLAYER_START_REGEX)) {
                    // if direction set start position and direction
                    start = new Position(row, column);
                    dir = switch (state) {
                        case '<' -> Direction.LEFT;
                        case '>' -> Direction.RIGHT;
                        case 'v' -> Direction.DOWN;
                        default -> Direction.UP;
                    };
                }
            }
        }

        // create and return level data
        int height = twoDimensionalLevel.size();
        int width = twoDimensionalLevel.get(0).size();
        Square[] squareArr = squares.toArray(new Square[0]);
        Position[] goalArr = goals.toArray(new Position[0]);
        return Results.success(new LevelData(height, width, start, dir, squareArr, goalArr));
    }

    /**
     * Checks if a levels layout is playable
     * <p>
     * Checks each cell to ensure it is translatable
     * and that the level has at least one goal and
     * no more than one starting position
     *
     * @param cells individual cells of a level in 2d list
     * @return true if level is valid, else false
     */
    private static boolean isLevelPlayable(List<List<String>> cells) {
        if (cells.isEmpty()) return false; // returns early if data is empty

        // checks cell is blank (3 underscores) OR has shape, color, and state
        String cellRegex = "^_{3}$|^[BRYGP][DCSFL][01<>^v]$";
        Integer width = null;
        boolean hasGoal = false;
        boolean hasStart = false;

        // loops through each line in level
        for (List<String> lineOfCells : cells) {
            // first line sets width, if any following lines have a different width level is invalid
            if (width == null) width = lineOfCells.size();
            else if (lineOfCells.size() != width) return false;

            // loops through each cell in line
            for (String cell : lineOfCells) {
                // returns false if any cell is invalid format
                if (!cell.matches(cellRegex)) return false;
                // checks level has goal
                if(cell.charAt(2) == GOAL_VALUE) hasGoal = true;
                // checks there is a starting position
                if(Character.toString(cell.charAt(2)).matches(PLAYER_START_REGEX)) {
                    if (hasStart) return false; // level CANNOT have two starting positions
                    hasStart = true;
                }
            }
        }

        // if no errors occurred and there is a goal and start then the level is valid
        return hasGoal && hasStart;
    }
}
