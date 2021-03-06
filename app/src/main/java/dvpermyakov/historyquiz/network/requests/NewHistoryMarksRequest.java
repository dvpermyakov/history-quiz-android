package dvpermyakov.historyquiz.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.responses.NewHistoryMarksResponse;

/**
 * Created by dvpermyakov on 07.10.2016.
 */
public class NewHistoryMarksRequest extends BaseRequest<NewHistoryMarksResponse>  {

    public NewHistoryMarksRequest(Response.Listener<NewHistoryMarksResponse> listener, Response.ErrorListener errorListener, Map<String, String> getParams) {
        super(Urls.NEW_MARKS_REQUEST_URL, listener, errorListener, getParams);
    }

    @Override
    protected Response<NewHistoryMarksResponse> parseNetworkResponse(NetworkResponse response) {
        super.parseNetworkResponse(response);
        try {
            JSONObject json = new JSONObject(new String(response.data));
            return Response.success(gson.fromJson(json.toString(), NewHistoryMarksResponse.class), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }
}
