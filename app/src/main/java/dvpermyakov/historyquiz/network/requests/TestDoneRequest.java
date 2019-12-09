package dvpermyakov.historyquiz.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.responses.SuccessResponse;

/**
 * Created by dvpermyakov on 04.01.2017.
 */

public class TestDoneRequest extends BaseRequest<SuccessResponse> {

    public TestDoneRequest(Response.Listener<SuccessResponse> listener, Response.ErrorListener errorListener, Map<String, String> getParams) {
        super(Urls.TEST_DONE_URL, listener, errorListener, getParams);
    }

    @Override
    protected Response<SuccessResponse> parseNetworkResponse(NetworkResponse response) {
        super.parseNetworkResponse(response);
        try {
            JSONObject json = new JSONObject(new String(response.data));
            return Response.success(gson.fromJson(json.toString(), SuccessResponse.class), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }
}
