package dvpermyakov.historyquiz.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import dvpermyakov.historyquiz.models.User;
import dvpermyakov.historyquiz.network.constants.Urls;

/**
 * Created by dvpermyakov on 27.05.2016.
 */
public class RegisterUserRequest extends BaseRequest<User> {
    public RegisterUserRequest(Response.Listener<User> listener, Response.ErrorListener errorListener) {
        super(Urls.REGISTER_USER_REQUEST_URL, listener, errorListener);
    }

    @Override
    protected Response<User> parseNetworkResponse(NetworkResponse response) {
        super.parseNetworkResponse(response);
        try {
            JSONObject json = new JSONObject(new String(response.data));
            return Response.success(gson.fromJson(json.toString(), User.class), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }
}
