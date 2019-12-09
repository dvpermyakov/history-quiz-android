package dvpermyakov.historyquiz.network.requests;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import dvpermyakov.historyquiz.network.constants.Headers;
import dvpermyakov.historyquiz.network.constants.MethodTypes;
import dvpermyakov.historyquiz.specials.LogTag;

/**
 * Created by dvpermyakov on 22.05.2016.
 */
public abstract class BaseRequest<T> extends Request<T> {
    protected Context context;
    protected String url;
    protected int method;
    protected Map<String, String> postParams;
    protected Response.Listener<T> listener;
    protected final Gson gson = new Gson();

    public BaseRequest(int method, String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.url = url;
        this.method = method;
        this.listener = listener;
    }

    public BaseRequest(String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(MethodTypes.getMethodType(url), url, errorListener);
        this.url = url;
        this.method = MethodTypes.getMethodType(url);
        this.listener = listener;
    }

    public BaseRequest(String url,
                       Response.Listener<T> listener,
                       Response.ErrorListener errorListener,
                       Map<String, String> getParams) {
        this(url, listener, errorListener);
        url += "?";
        for (Map.Entry<String, String> entry : getParams.entrySet()) {
            try {
                url += entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "utf-8") + "&";
            } catch (UnsupportedEncodingException e) {
                Log.e(LogTag.TAG_NETWORK, "encoding params");
                e.printStackTrace();
            }
        }
        this.url = url;
    }

    public BaseRequest(String url,
                       Response.Listener<T> listener,
                       Response.ErrorListener errorListener,
                       Map<String, String> getParams,
                       Map<String, String> postParams) {
        this(url, listener, errorListener, getParams);
        this.postParams = postParams;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        Log.i(LogTag.TAG_NETWORK, "url = " + url);
        Log.i(LogTag.TAG_NETWORK, "code = " + response.statusCode);
        return null;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return Headers.headers;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if (postParams == null) {
            return new HashMap<>();
        }
        else {
            return postParams;
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    public String getUrl() {
        return url;
    }
}

