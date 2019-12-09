package dvpermyakov.historyquiz.managers;

import android.content.Context;

/**
 * Created by dvpermyakov on 04.02.17.
 */

public class PlayServiceGameManagerFactory {
    private static PlayServiceGameManager manager;

    public static PlayServiceGameManager getManager() {
        return manager;
    }
    public static void setManager(Context context) {
        manager = new PlayServiceGameManager(context);
    }
}
