package dvpermyakov.historyquiz.preferences;

import android.content.Context;

/**
 * Created by dvpermyakov on 26.11.2016.
 */

public class SharePreferences extends AbstractPreferences {
    public static String getSharedVKUrl(Context context) {
        return getPreferences(context).getString(PreferencesStrings.SHARED_VK_URL, null);
    }
    public static boolean setSharedVKUrl(Context context, String url) {
        return getEditor(context).putString(PreferencesStrings.SHARED_VK_URL, url).commit();
    }
}
