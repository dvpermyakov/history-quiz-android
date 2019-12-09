package dvpermyakov.historyquiz.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.responses.TestQuestionsResponse;

/**
 * Created by dvpermyakov on 27.05.2016.
 */
public class TestQuestionsRequest extends BaseRequest<TestQuestionsResponse> {
    public TestQuestionsRequest(Response.Listener<TestQuestionsResponse> listener, Response.ErrorListener errorListener, Map<String, String> getParams) {
        super(Urls.TEST_QUESTIONS_REQUEST_URL, listener, errorListener, getParams);
    }

    @Override
    protected Response<TestQuestionsResponse> parseNetworkResponse(NetworkResponse response) {
        super.parseNetworkResponse(response);
        try {
            JSONObject json = new JSONObject(new String(response.data));
            return Response.success(gson.fromJson(json.toString(), TestQuestionsResponse.class), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }
}
