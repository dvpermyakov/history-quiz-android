package dvpermyakov.historyquiz.models;

/**
 * Created by dvpermyakov on 26.05.2016.
 */
public enum HistoryEntityCategory {
    PERIOD, EVENT, PERSON, VIDEO;

    public static final int DEFAULT = -1;
    public static final int PERIOD_CONST = 0;
    public static final int EVENT_CONST = 1;
    public static final int PERSON_CONST = 2;
    public static final int VIDEO_CONST = 3;

    public static HistoryEntityCategory fromInt (int x) {
        switch(x) {
            case PERIOD_CONST:
                return PERIOD;
            case EVENT_CONST:
                return EVENT;
            case PERSON_CONST:
                return PERSON;
            case VIDEO_CONST:
                return VIDEO;
        }
        return null;
    }

    public static int toInt (HistoryEntityCategory category) {
        switch(category) {
            case PERIOD:
                return PERIOD_CONST;
            case EVENT:
                return EVENT_CONST;
            case PERSON:
                return PERSON_CONST;
            case VIDEO:
                return VIDEO_CONST;
        }
        return DEFAULT;
    }

    @Override
    public String toString() {
        return String.valueOf(toInt(this));
    }
}
