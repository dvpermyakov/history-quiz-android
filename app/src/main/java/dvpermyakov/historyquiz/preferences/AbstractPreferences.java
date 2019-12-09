package dvpermyakov.historyquiz.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dvpermyakov on 26.11.2016.
 */

public abstract class AbstractPreferences {
    private static SharedPreferences preferences;

    protected static SharedPreferences getPreferences(Context context) {
        if (context != null) {
            setContextPreferences(context);
        }
        return preferences;
    }

    protected static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static void setContextPreferences(Context context) {
        preferences = context.getSharedPreferences(PreferencesStrings.LOCAL_PREFERENCES, Context.MODE_PRIVATE);
    }
}
