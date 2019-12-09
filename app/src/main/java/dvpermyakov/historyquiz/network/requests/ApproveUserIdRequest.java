package dvpermyakov.historyquiz.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.responses.ApproveUserIdResponse;

/**
 * Created by dvpermyakov on 27.05.2016.
 */
public class ApproveUserIdRequest extends BaseRequest<ApproveUserIdResponse> {
    public ApproveUserIdRequest(Response.Listener<ApproveUserIdResponse> listener, Response.ErrorListener errorListener) {
        super(Urls.USER_ID_APPROVE_REQUEST_URL, listener, errorListener);
    }

    @Override
    protected Response<ApproveUserIdResponse> parseNetworkResponse(NetworkResponse response) {
        super.parseNetworkResponse(response);
        try {
            JSONObject json = new JSONObject(new String(response.data));
            return Response.success(gson.fromJson(json.toString(), ApproveUserIdResponse.class), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }
}
