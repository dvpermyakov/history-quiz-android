package dvpermyakov.historyquiz.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.responses.RatingResponse;

/**
 * Created by dvpermyakov on 05.01.2017.
 */

public class RatingRequest extends BaseRequest<RatingResponse> {
    public RatingRequest(Response.Listener<RatingResponse> listener, Response.ErrorListener errorListener) {
        super(Urls.RATING_REQUEST_URL, listener, errorListener);
    }

    @Override
    protected Response<RatingResponse> parseNetworkResponse(NetworkResponse response) {
        super.parseNetworkResponse(response);
        try {
            JSONObject json = new JSONObject(new String(response.data));
            return Response.success(gson.fromJson(json.toString(), RatingResponse.class), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }
}
