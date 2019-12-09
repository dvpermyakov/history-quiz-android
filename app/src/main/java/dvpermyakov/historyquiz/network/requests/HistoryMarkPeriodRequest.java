package dvpermyakov.historyquiz.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import dvpermyakov.historyquiz.network.constants.Urls;

/**
 * Created by dvpermyakov on 02.12.2016.
 */

public class HistoryMarkPeriodRequest extends BaseRequest<String> {
    private static final String PERIOD_ID_FIELD = "period_id";

    public HistoryMarkPeriodRequest(Response.Listener<String> listener, Response.ErrorListener errorListener, Map<String, String> getParams) {
        super(Urls.HISTORY_MARK_PERIOD_REQUEST_URL, listener, errorListener, getParams);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        super.parseNetworkResponse(response);
        try {
            JSONObject json = new JSONObject(new String(response.data));
            return Response.success(gson.fromJson(json.getString(PERIOD_ID_FIELD), String.class), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }
}
