package dvpermyakov.historyquiz.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import dvpermyakov.historyquiz.files.InternalStorage;
import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.network.requests.FileRequest;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 24.01.2017.
 */

public class FileService extends IntentService {
    private Context context;

    public FileService() {
        super(FileService.class.getName());
        context = this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra(IntentStrings.INTENT_FILE_URL);
        if (url != null) {
            download(url);
        }
    }

    private void download(String url) {
        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        Request request = new FileRequest(url, new Response.Listener<byte[]>() {
            @Override
            public void onResponse(byte[] response) {
                InternalStorage.writeFile(context, "byUrl", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        request.setTag(this);
        volleyQueue.add(request);
    }
}
