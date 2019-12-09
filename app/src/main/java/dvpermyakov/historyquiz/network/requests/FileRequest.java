package dvpermyakov.historyquiz.network.requests;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * Created by dvpermyakov on 24.01.2017.
 */

public class FileRequest extends BaseRequest<byte[]> {
    public FileRequest(String url, Response.Listener<byte[]> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, url, listener, errorListener);
        setShouldCache(false);
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        super.parseNetworkResponse(response);
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }
}
