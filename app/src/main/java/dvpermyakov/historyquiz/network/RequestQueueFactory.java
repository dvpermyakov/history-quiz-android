package dvpermyakov.historyquiz.network;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by dvpermyakov on 18.01.2017.
 */

public class RequestQueueFactory {
    private static RequestQueue queue;

    public static RequestQueue getRequestQueue() {
        return queue;
    }

    public static void setRequestQueue(Context context) {
        queue = Volley.newRequestQueue(context.getApplicationContext());
    }
}
