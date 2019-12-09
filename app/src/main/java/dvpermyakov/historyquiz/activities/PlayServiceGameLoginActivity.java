package dvpermyakov.historyquiz.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.games.Player;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.managers.PlayServiceGameManager;
import dvpermyakov.historyquiz.managers.PlayServiceGameManagerFactory;
import dvpermyakov.historyquiz.models.User;
import dvpermyakov.historyquiz.preferences.UserPreferences;

/**
 * Created by dvpermyakov on 05.02.17.
 */

public class PlayServiceGameLoginActivity extends AppCompatActivity {
    private static int RC_SIGN_IN = 9001;

    private PlayServiceGameManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = PlayServiceGameManagerFactory.getManager();
        manager.connect();

        ConnectionResult connectionResult = manager.getConnectionResult();
        if (!manager.isConnected() && connectionResult != null) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
                showFailDialog();
            }
        } else {
            finishActivity();
        }
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == RC_SIGN_IN) {
            manager.connect();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (manager.isConnected()) {
                        manager.updateTestDone();
                        manager.updateReadArticle();
                        saveUserInfo(PlayServiceGameManagerFactory.getManager().getPlayer());
                    }
                }
            }, 1000);
            finishActivity();
        }
    }

    private void finishActivity() {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    private void saveUserInfo(Player player) {
        if (player != null) {
            User user = UserPreferences.getUser(this);
            user.setName(player.getDisplayName());
            UserPreferences.setUser(this, user);
            user.sendUserInfoToServer();
        }

        Analytics.sendEvent(Analytics.CATEGORY_PLAY_LOGIN, Analytics.ACTION_PLAY_LOGIN);
    }

    private void showFailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.dialog_not_login_title));
        builder.setMessage(getResources().getString(R.string.dialog_not_login_text));
        builder.setPositiveButton(getString(R.string.dialog_not_login_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishActivity();
                    }
                });
        builder.create().show();

        Analytics.sendEvent(Analytics.CATEGORY_PLAY_LOGIN, Analytics.ACTION_PLAY_CANCEL_LOGIN);
    }
}
