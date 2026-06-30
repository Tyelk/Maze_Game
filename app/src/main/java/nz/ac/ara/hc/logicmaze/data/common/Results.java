package nz.ac.ara.hc.logicmaze.data.common;

/**
 * <h1>Results</h1>
 * The Results class is used
 * to hold data that and
 * errors to make it easier
 * to handle results from
 * calls.
 *
 * @author Hadley Clark
 * @version 1.0.0
 * @since 2026-06-02
 */
public class Results<T> {
    private final T data;
    private final ErrorCode error;

    // object to hold data or error to make it easier to handle the results from calls
    private Results(T data, ErrorCode error) {
        this.data = data;
        this.error = error;
    }

    // no error on success
    public static <T> Results<T> success(T data) {
        return new Results<T>(data, null);
    }

    // no data on error
    public static <T> Results<T> fail(ErrorCode error) {
        return new Results<T>(null, error);
    }

    public T getData() {
        return this.data;
    }
    public ErrorCode getError() {
        return this.error;
    }
}
