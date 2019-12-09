package dvpermyakov.historyquiz.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import dvpermyakov.historyquiz.models.Period;
import dvpermyakov.historyquiz.network.constants.Params;
import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.responses.PeriodsResponse;

/**
 * Created by dvpermyakov on 22.05.2016.
 */
public class HistoryPeriodsRequest extends BaseRequest<PeriodsResponse> {
    public HistoryPeriodsRequest(Response.Listener<PeriodsResponse> listener, Response.ErrorListener errorListener) {
        super(Urls.HISTORY_PERIODS_REQUEST_URL, listener, errorListener, Params.getParams(Urls.HISTORY_PERIODS_REQUEST_URL));
    }

    @Override
    protected Response<PeriodsResponse> parseNetworkResponse(NetworkResponse response) {
        super.parseNetworkResponse(response);
        try {
            JSONObject json = new JSONObject(new String(response.data));
            return Response.success(gson.fromJson(json.toString(), PeriodsResponse.class), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }
}
