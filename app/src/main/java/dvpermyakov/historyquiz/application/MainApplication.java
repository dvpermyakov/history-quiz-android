package dvpermyakov.historyquiz.application;

import android.content.Context;
import android.content.Intent;
import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.BuildConfig;
import com.crashlytics.android.core.CrashlyticsCore;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.managers.PlayServiceGameManagerFactory;
import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.preferences.UserPreferences;
import dvpermyakov.historyquiz.preferences.PreferencesStrings;
import dvpermyakov.historyquiz.services.UpdateUserInfoService;
import io.branch.referral.Branch;
import io.fabric.sdk.android.Fabric;


/**
 * Created by dvpermyakov on 05.06.2016.
 */
public class MainApplication extends MultiDexApplication {
    private Context context;
    private VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken != null) {
                newToken.saveTokenToSharedPreferences(context, PreferencesStrings.VK_LOGIN_TOKEN);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        DataBaseHelperFactory.setHelper(getApplicationContext());
        PlayServiceGameManagerFactory.setManager(context);
        RequestQueueFactory.setRequestQueue(this);
        UserPreferences.setContextPreferences(this);
        UserPreferences.setEnterAppCount(this, UserPreferences.getEnterAppCount(this) + 1);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        Analytics.setTracker(this);
        Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
        Branch.getAutoInstance(this);

        startService(new Intent(this, UpdateUserInfoService.class));
    }

    @Override
    public void onTerminate() {
        DataBaseHelperFactory.releaseHelper();
        PlayServiceGameManagerFactory.getManager().disconnect();
        super.onTerminate();
    }
}
