package dvpermyakov.historyquiz.network.constants;

import android.os.Build;

import java.util.HashMap;
import java.util.Map;

import dvpermyakov.historyquiz.BuildConfig;
import dvpermyakov.historyquiz.preferences.UserPreferences;

/**
 * Created by dvpermyakov on 22.05.2016.
 */
public class Headers {
    public static final Map<String, String> headers = new HashMap<String, String>() {{
        String debug = BuildConfig.DEBUG ? "test" : "release";

        put("App-Name", "HistoryQuiz");
        put("Platform", "Android/" + Build.VERSION.SDK_INT);
        put("Version", BuildConfig.VERSION_CODE + "/" + BuildConfig.VERSION_NAME + "/" + debug);
        put("User-Id", UserPreferences.getUser(null).getId());
        put("Secret", UserPreferences.getUser(null).getSecret());
    }};

    public static void setUserPreferences() {
        headers.put("User-Id", UserPreferences.getUser(null).getId());
        headers.put("Secret", UserPreferences.getUser(null).getSecret());
    }
}
