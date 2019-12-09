package dvpermyakov.historyquiz.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.models.User;
import dvpermyakov.historyquiz.preferences.UserPreferences;

/**
 * Created by dvpermyakov on 04.01.2017.
 */

public class GoogleLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Analytics.sendScreen(Analytics.SCREEN_GOOGLE_LOGIN);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {
                saveUserInfo(account);
                finish();
            } else {
                showFailDialog();
            }
        } else {
            showFailDialog();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showFailDialog();
    }

    private void saveUserInfo(GoogleSignInAccount account) {
        User user = UserPreferences.getUser(this);
        user.setName(account.getDisplayName());
        user.setEmail(account.getEmail());
        if (account.getPhotoUrl() != null) {
            user.setImage(account.getPhotoUrl().toString());
        }
        UserPreferences.setUser(this, user);
        user.sendUserInfoToServer();

        Analytics.sendEvent(Analytics.CATEGORY_GOOGLE_LOGIN, Analytics.ACTION_GOOGLE_LOGIN);
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
                        finish();
                    }
                });
        builder.create().show();

        Analytics.sendEvent(Analytics.CATEGORY_GOOGLE_LOGIN, Analytics.ACTION_GOOGLE_CANCEL_LOGIN);
    }
}
