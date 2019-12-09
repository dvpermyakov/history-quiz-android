package dvpermyakov.historyquiz.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.responses.MarkInfoResponse;

/**
 * Created by dvpermyakov on 25.05.2016.
 */
public class HistoryMarkInfoRequest extends BaseRequest<MarkInfoResponse> {
    public HistoryMarkInfoRequest(Response.Listener<MarkInfoResponse> listener, Response.ErrorListener errorListener, Map<String, String> getParams) {
        super(Urls.HISTORY_MARK_INFO_REQUEST_URL, listener, errorListener, getParams);
    }

    @Override
    protected Response<MarkInfoResponse> parseNetworkResponse(NetworkResponse response) {
        super.parseNetworkResponse(response);
        try {
            JSONObject json = new JSONObject(new String(response.data));
            return Response.success(gson.fromJson(json.toString(), MarkInfoResponse.class), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }
}
