package dvpermyakov.historyquiz.network.constants;

/**
 * Created by dvpermyakov on 22.05.2016.
 */
public class Urls {
    public final static String EMAIL_URL = "developing.apps.mobile@gmail.com";

    private final static String BASE_URL = "http://history-quiz-app.appspot.com";
    public final static String TIMESTAMP_HISTORY_PERIODS_REQUEST_URL = BASE_URL + "/api/history/timestamp/periods";
    public final static String HISTORY_PERIODS_REQUEST_URL = BASE_URL + "/api/history/periods";
    public final static String SHORT_HISTORY_MARK_INFO_REQUEST_URL = BASE_URL + "/api/history/short_mark_info";
    public final static String HISTORY_MARK_INFO_REQUEST_URL = BASE_URL + "/api/history/mark_info";
    public final static String HISTORY_MARK_PERIOD_REQUEST_URL = BASE_URL + "/api/history/period_of_mark_info";
    public final static String USER_ID_APPROVE_REQUEST_URL = BASE_URL + "/api/user/approve";
    public final static String REGISTER_USER_REQUEST_URL = BASE_URL + "/api/user/register";
    public final static String SET_USER_INFO_REQUEST_URL = BASE_URL + "/api/user/set_info";
    public final static String SET_VK_REQUEST_URL = BASE_URL + "/api/user/set_vk";
    public final static String TEST_QUESTIONS_REQUEST_URL = BASE_URL + "/api/test/questions";
    public final static String TEST_DONE_URL = BASE_URL + "/api/test/done";
    public final static String NEW_MARKS_REQUEST_URL = BASE_URL + "/api/history/new";
    public final static String VIDEO_INFO_REQUEST_URL = BASE_URL + "/api/video/info";
    public final static String RATING_REQUEST_URL = BASE_URL + "/api/rating/list";
}