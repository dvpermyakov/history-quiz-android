package dvpermyakov.historyquiz.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.adapters.InstructionAdapter;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.specials.IntentStrings;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;

/**
 * Created by dvpermyakov on 14.09.2016.
 */
public class InstructionsActivity extends AppCompatActivity {
    public static int RC_LOGIN = 3;
    private boolean external;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        external = getIntent().getBooleanExtra(IntentStrings.INTENT_INSTRUCTIONS_EXTERNAL_PARAM, false);

        setContentView(R.layout.activity_instructions);
        ViewPager viewPager = findViewById(R.id.instructionsViewPager);
        viewPager.setAdapter(
                new InstructionAdapter(getSupportFragmentManager(), findViewById(R.id.textViewInstructionNext), external));

        Analytics.sendScreen(Analytics.SCREEN_INSTRUCTIONS + getResources().getString(R.string.app_name));
    }

    @Override
    public void onStart() {
        super.onStart();
        Branch.getInstance().initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
            }
        }, getIntent().getData(), this);

        // todo: amend it
//        if (!external && UserPreferences.getInstructionsDone(this)) {
        startPeriodActivity();
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOGIN) {
            startPeriodActivity();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    private void startPeriodActivity() {
        startActivity(new Intent(this, HistoryPeriodsActivity.class));
        finish();
    }
}
