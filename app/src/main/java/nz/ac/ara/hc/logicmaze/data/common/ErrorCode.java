package nz.ac.ara.hc.logicmaze.data.common;

import nz.ac.ara.hc.logicmaze.R;

/**
 * Defines errors used to communicate to the user
 * what went wrong.
 */
public enum ErrorCode {
    NO_LEVELS(R.string.error_no_levels),
    NO_LEVEL_LOADED(R.string.error_no_level_loaded),
    UNKNOWN_ERROR(R.string.error_unknown),
    READER_UNINITIALIZED(R.string.error_reader_uninitialized),
    FILE_COUNT(R.string.error_file_count),
    FILE_READ(R.string.error_file_read),
    INVALID_LEVEL_NUMBER(R.string.error_invalid_level_number),
    INVALID_LEVEL(R.string.error_invalid_level),
    INVALID_SQUARE(R.string.error_invalid_square),
    INVALID_DIRECTION(R.string.error_invalid_direction),
    INVALID_POSITION_OR_LEVEL(R.string.error_invalid_position_or_level),
    INVALID_EYEBALL_OR_LEVEL(R.string.error_invalid_eyeball_or_level),
    LEVEL_EXISTS(R.string.error_level_exists),
    MALFORMED_LEVEL_DATA(R.string.error_malformed_level_data),
    NO_REPO(R.string.error_missing_repo),
    NO_EYEBALL(R.string.error_no_player);

    private final int resourceId;

    ErrorCode(int stringCode) {
        this.resourceId = stringCode;
    }

    public int getResourceId() {
        return this.resourceId;
    }
}
