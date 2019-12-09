package dvpermyakov.historyquiz.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import dvpermyakov.historyquiz.models.HistoryMark;
import dvpermyakov.historyquiz.network.constants.Urls;

/**
 * Created by dvpermyakov on 15.11.2016.
 */

public class ShortHistoryMarkInfoRequest extends BaseRequest<HistoryMark> {
    public ShortHistoryMarkInfoRequest(Response.Listener<HistoryMark> listener, Response.ErrorListener errorListener, Map<String, String> getParams) {
        super(Urls.SHORT_HISTORY_MARK_INFO_REQUEST_URL, listener, errorListener, getParams);
    }

    @Override
    protected Response<HistoryMark> parseNetworkResponse(NetworkResponse response) {
        super.parseNetworkResponse(response);
        try {
            JSONObject json = new JSONObject(new String(response.data));
            return Response.success(gson.fromJson(json.toString(), HistoryMark.class), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }
}
