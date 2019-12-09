package dvpermyakov.historyquiz.network.constants;

import java.util.HashMap;
import java.util.Map;

import dvpermyakov.historyquiz.ExternalConstants;
import dvpermyakov.historyquiz.models.HistoryEntityCategory;

/**
 * Created by dvpermyakov on 22.05.2016.
 */
public class Params {
    public static Map<String, String> getParams(String url) {
        if (url.equals(Urls.SET_USER_INFO_REQUEST_URL)) {
            return new HashMap<String, String>() {{
                put("name", "");
                put("image", "");
                put("email", "");
            }};
        }
        if (url.equals(Urls.SET_VK_REQUEST_URL)) {
            return new HashMap<String, String>() {{
                put("vk", "");
            }};
        }
        if (url.equals(Urls.HISTORY_PERIODS_REQUEST_URL)) {
            return new HashMap<String, String>() {{
                put("country_id", ExternalConstants.COUNTRY_ID);
            }};
        }
        if (url.equals(Urls.HISTORY_MARK_INFO_REQUEST_URL)) {
            return new HashMap<String, String>() {{
                put("mark_id", "");
                put("category", String.valueOf(HistoryEntityCategory.DEFAULT));
            }};
        }
        if (url.equals(Urls.HISTORY_MARK_PERIOD_REQUEST_URL)) {
            return new HashMap<String, String>() {{
                put("mark_id", "");
                put("category", String.valueOf(HistoryEntityCategory.DEFAULT));
            }};
        }
        if (url.equals(Urls.SHORT_HISTORY_MARK_INFO_REQUEST_URL)) {
            return new HashMap<String, String>() {{
                put("mark_id", "");
                put("category", String.valueOf(HistoryEntityCategory.DEFAULT));
            }};
        }
        if (url.equals(Urls.TEST_QUESTIONS_REQUEST_URL)) {
            return new HashMap<String, String>() {{
                put("test_id", "");
            }};
        }
        if (url.equals(Urls.TEST_DONE_URL)) {
            return new HashMap<String, String>() {{
                put("test_id", "");
                put("attempts", "");
            }};
        }
        if (url.equals(Urls.NEW_MARKS_REQUEST_URL)) {
            return new HashMap<String, String>() {{
                put("country_id", ExternalConstants.COUNTRY_ID);
                put("amount", "5");
                put("timestamp", "0");
            }};
        }
        if (url.equals(Urls.VIDEO_INFO_REQUEST_URL)) {
            return new HashMap<String, String>() {{
                put("video_id", "");
            }};
        }
        return new HashMap<>();
    }
}
