package dvpermyakov.historyquiz.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.util.Map;

import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.managers.VKUserManager;
import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.network.constants.Params;
import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.requests.SetVKRequest;
import dvpermyakov.historyquiz.network.responses.SuccessResponse;
import dvpermyakov.historyquiz.preferences.PreferencesStrings;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 19.10.2016.
 */
public class VKLoginActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        String[] scopes = getIntent().getStringArrayExtra(IntentStrings.INTENT_VK_SCOPE);
        if (scopes != null) {
            VKSdk.login(this, scopes);
        } else {
            VKSdk.login(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data,
                new VKCallback<VKAccessToken>() {
                    @Override
                    public void onResult(VKAccessToken res) {
                        Analytics.sendEvent(Analytics.CATEGORY_SOCIAL_NETWORKS, Analytics.ACTION_VK_LOGIN);

                        res.saveTokenToSharedPreferences(context, PreferencesStrings.VK_LOGIN_TOKEN);
                        VKUserManager.setUserInfo(context);
                        VKUserManager.setGroupMember(context);
                        setResult(RESULT_OK, new Intent().putExtra(IntentStrings.INTENT_LOGIN_VK_SUCCESS, true));

                        setVKLogin();
                        finish();
                    }
                    @Override
                    public void onError(VKError error) {
                        Analytics.sendEvent(Analytics.CATEGORY_SOCIAL_NETWORKS, Analytics.ACTION_VK_CANCEL_LOGIN);

                        setResult(RESULT_OK, new Intent().putExtra(IntentStrings.INTENT_LOGIN_VK_SUCCESS, false));
                        finish();
                    }
                })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setVKLogin() {
        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        Map<String, String> getParams = Params.getParams(Urls.SET_VK_REQUEST_URL);
        getParams.put("vk", String.valueOf(VKUserManager.getUserId(this)));
        Request request = new SetVKRequest(new Response.Listener<SuccessResponse>() {
            @Override
            public void onResponse(SuccessResponse response) {}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        }, getParams);
        volleyQueue.add(request);
    }
}
