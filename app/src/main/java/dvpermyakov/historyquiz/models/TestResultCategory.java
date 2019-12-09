package dvpermyakov.historyquiz.models;

import android.content.Context;

import dvpermyakov.historyquiz.R;

/**
 * Created by dvpermyakov on 04.06.2016.
 */
public enum TestResultCategory {
    DEFAULT, WIN, TIME_LIMIT, MISTAKE_LIMIT, CANCEL;

    public static final int DEFAULT_CONST = -1;
    public static final int WIN_CONST = 0;
    public static final int TIME_LIMIT_CONST = 1;
    public static final int MISTAKE_LIMIT_CONST = 2;
    public static final int CANCEL_CONST = 3;

    public static TestResultCategory fromInt(int x) {
        switch (x) {
            case WIN_CONST:
                return WIN;
            case TIME_LIMIT_CONST:
                return TIME_LIMIT;
            case MISTAKE_LIMIT_CONST:
                return MISTAKE_LIMIT;
            case CANCEL_CONST:
                return CANCEL;
        }
        return DEFAULT;
    }

    public static int toInt(TestResultCategory category) {
        switch (category) {
            case WIN:
                return WIN_CONST;
            case TIME_LIMIT:
                return TIME_LIMIT_CONST;
            case MISTAKE_LIMIT:
                return MISTAKE_LIMIT_CONST;
            case CANCEL:
                return CANCEL_CONST;
        }
        return DEFAULT_CONST;
    }

    public String toString(Context context) {
        switch (this) {
            case WIN:
                return context.getResources().getString(R.string.test_ending_result_win);
            case TIME_LIMIT:
                return context.getResources().getString(R.string.test_ending_result_time);
            case MISTAKE_LIMIT:
                return context.getResources().getString(R.string.test_ending_result_mistakes);
            case CANCEL:
                return context.getResources().getString(R.string.test_ending_result_cancel);
        }
        return context.getResources().getString(R.string.test_ending_result_default);
    }
}