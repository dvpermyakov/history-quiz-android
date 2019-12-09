package dvpermyakov.historyquiz.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;


import dvpermyakov.historyquiz.network.constants.Params;
import dvpermyakov.historyquiz.network.constants.Urls;

/**
 * Created by dvpermyakov on 08.06.2016.
 */
public class TimestampHistoryPeriodsRequest extends BaseRequest<String> {
    private static final String TIMESTAMP_FIELD = "timestamp";

    public TimestampHistoryPeriodsRequest(Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Urls.TIMESTAMP_HISTORY_PERIODS_REQUEST_URL, listener, errorListener, Params.getParams(Urls.HISTORY_PERIODS_REQUEST_URL));
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        super.parseNetworkResponse(response);
        try {
            JSONObject json = new JSONObject(new String(response.data));
            return Response.success(gson.fromJson(json.getString(TIMESTAMP_FIELD), String.class), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }
}
