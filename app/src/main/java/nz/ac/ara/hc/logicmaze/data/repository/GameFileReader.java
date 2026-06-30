package nz.ac.ara.hc.logicmaze.data.repository;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import nz.ac.ara.hc.logicmaze.data.common.ErrorCode;
import nz.ac.ara.hc.logicmaze.data.common.Results;

/**
 * <h1>GameFileReader</h1>
 * The GameFileReader class is responsible
 * for reading and returning the contents
 * of files.
 *
 * @author Hadley Clark
 * @version 1.0.0
 * @since 2026-06-02
 */
public class GameFileReader {
    private final Context context;

    public GameFileReader(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Gets the number of files in the given folder
     *
     * @param folderName name of folder
     * @return Results: ErrorCode if error, Integer if success
     */
    public Results<Integer> getLevelFileCount(String folderName) {
        // return fail if missing context
        if (this.context == null) return Results.fail(ErrorCode.READER_UNINITIALIZED);
        AssetManager assetManager = this.context.getAssets();

        try {
            // use asset manager to find files
            String[] levelFiles = assetManager.list(folderName);

            // return fail if something is wrong
            if (levelFiles == null) {
                return Results.fail(ErrorCode.UNKNOWN_ERROR);
            }
            if (levelFiles.length == 0) {
                return Results.fail(ErrorCode.NO_LEVELS);
            }

            // return success if files found
            return Results.success(levelFiles.length);
        } catch (IOException e) {
            // return fail with error code
            return Results.fail(ErrorCode.FILE_COUNT);
        }
    }

    /**
     * Gets the raw level data from the given file
     *
     * @param filePath name of level file
     * @return Results: ErrorCode if error, List of strings if success
     */
    public Results<List<String>> getLevelData(String filePath) {
        // return fail if missing context
        if (this.context == null) return Results.fail(ErrorCode.READER_UNINITIALIZED);
        AssetManager assetManager = this.context.getAssets();

        // read the contents of the file
        try (InputStreamReader inputStream = new InputStreamReader(assetManager.open(filePath), StandardCharsets.UTF_8);
             BufferedReader buffReader = new BufferedReader(inputStream);) {

            // add each line from file to list
            String line;
            List<String> levelLines = new ArrayList<>();
            while ((line = buffReader.readLine()) != null) {
                levelLines.add(line);
            }

            // return list
            return Results.success(levelLines);
        } catch (IOException e) {
            // return fail with error
            return Results.fail(ErrorCode.FILE_READ);
        }
    }
}
