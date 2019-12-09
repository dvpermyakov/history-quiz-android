package dvpermyakov.historyquiz.network.constants;

import com.android.volley.Request;

/**
 * Created by dvpermyakov on 22.05.2016.
 */
public class MethodTypes {
    public final static int NOT_FOUND = -2;

    public static int getMethodType(String url) {
        if (url.equals(Urls.TIMESTAMP_HISTORY_PERIODS_REQUEST_URL)) return Request.Method.GET;
        if (url.equals(Urls.HISTORY_PERIODS_REQUEST_URL)) return Request.Method.GET;
        if (url.equals(Urls.HISTORY_MARK_INFO_REQUEST_URL)) return Request.Method.GET;
        if (url.equals(Urls.HISTORY_MARK_PERIOD_REQUEST_URL)) return Request.Method.GET;
        if (url.equals(Urls.SHORT_HISTORY_MARK_INFO_REQUEST_URL)) return Request.Method.GET;
        if (url.equals(Urls.USER_ID_APPROVE_REQUEST_URL)) return Request.Method.POST;
        if (url.equals(Urls.REGISTER_USER_REQUEST_URL)) return Request.Method.POST;
        if (url.equals(Urls.SET_USER_INFO_REQUEST_URL)) return Request.Method.POST;
        if (url.equals(Urls.SET_VK_REQUEST_URL)) return Request.Method.POST;
        if (url.equals(Urls.TEST_QUESTIONS_REQUEST_URL)) return Request.Method.POST;
        if (url.equals(Urls.TEST_DONE_URL)) return Request.Method.POST;
        if (url.equals(Urls.NEW_MARKS_REQUEST_URL)) return Request.Method.GET;
        if (url.equals(Urls.VIDEO_INFO_REQUEST_URL)) return Request.Method.GET;
        if (url.equals(Urls.RATING_REQUEST_URL)) return Request.Method.GET;
        return NOT_FOUND;
    }
}
