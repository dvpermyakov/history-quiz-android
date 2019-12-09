package dvpermyakov.historyquiz.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Date;
import java.util.List;

import dvpermyakov.historyquiz.ExternalConstants;
import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.adapters.HistoryPeriodsAdapter;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.appbar.DrawerToggle;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.dialogs.AwardDialog;
import dvpermyakov.historyquiz.listeners.OnNavigationItemSelectedListener;
import dvpermyakov.historyquiz.listeners.RecyclerItemClickListener;
import dvpermyakov.historyquiz.models.CoinsTransaction;
import dvpermyakov.historyquiz.models.HistoryEntityCategory;
import dvpermyakov.historyquiz.models.Period;
import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.network.requests.HistoryPeriodsRequest;
import dvpermyakov.historyquiz.network.requests.TimestampHistoryPeriodsRequest;
import dvpermyakov.historyquiz.network.responses.PeriodsResponse;
import dvpermyakov.historyquiz.preferences.UserPreferences;
import dvpermyakov.historyquiz.preferences.RewardPreferences;
import dvpermyakov.historyquiz.specials.IntentStrings;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;

import static dvpermyakov.historyquiz.specials.DateUtils.getDateFormat;
import static dvpermyakov.historyquiz.specials.DateUtils.isAfter;

/**
 * Created by dvpermyakov on 20.05.2016.
 */
public class HistoryPeriodsActivity extends CoinsActivity {
    private Context context;
    private HistoryPeriodsAdapter adapter;
    private List<Period> periods;
    private boolean fromPushWithCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        fromPushWithCoins = getIntent().getBooleanExtra(IntentStrings.INTENT_PUSH_WITH_COINS_PARAM, false);
        boolean fromPush = getIntent().getBooleanExtra(IntentStrings.INTENT_FROM_PUSH_PARAM, false);
        if (fromPush) {
            Analytics.sendEvent(Analytics.CATEGORY_PUSH, Analytics.ACTION_ENTER_FROM_PUSH);
        }

        setContentView(R.layout.activity_history_periods);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerPeriods);
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigationView);
        if (navigationView != null) {
            navigationView.setCheckedItem(R.id.periods);
            navigationView.setNavigationItemSelectedListener(
                    new OnNavigationItemSelectedListener(this, OnNavigationItemSelectedListener.ActivityCategory.PERIODS, drawerLayout));
        }

        DrawerToggle toggle = new DrawerToggle(this, drawerLayout, toolbar, navigationView);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        createPeriodRecyclerView();
        checkDownloadPeriods();

        Analytics.sendScreen(Analytics.SCREEN_MAIN + getResources().getString(R.string.app_name));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePeriodRecyclerView();
        invalidateOptionsMenu();
        setEnterCoinsReward();
        setReferralCoinsReward();
    }

    private void checkDownloadPeriods() {
        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        volleyQueue.cancelAll(this);
        Request request = new TimestampHistoryPeriodsRequest(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!UserPreferences.getPeriodLastUpdate(context).equals(response)) {
                    downloadPeriods();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!isFinishing()) showNotNetworkDialog();
            }
        });
        request.setTag(this);
        volleyQueue.add(request);
    }

    private void downloadPeriods() {
        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        Request request = new HistoryPeriodsRequest(new Response.Listener<PeriodsResponse>() {
            @Override
            public void onResponse(PeriodsResponse response) {
                UserPreferences.setPeriodLastUpdate(context, response.getTimestamp());
                periods = response.getPeriods();
                for (Period period : periods) {
                    period.setCategory(HistoryEntityCategory.PERIOD);
                    period.setAppFields();
                }
                DataBaseHelperFactory.getHelper().getPeriodDao().savePeriods(periods);
                adapter.updateList(periods);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!isFinishing()) showNotNetworkDialog();
            }
        });
        request.setTag(this);
        volleyQueue.add(request);
    }

    private void createPeriodRecyclerView() {
        periods = DataBaseHelperFactory.getHelper().getPeriodDao().getPeriods();
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerViewPeriods);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (!periods.get(position).isDeveloping()) {
                            startActivity(new Intent(context, HistoryMarkActivity.class)
                                    .putExtra(IntentStrings.INTENT_HISTORY_MARK_PARAM, periods.get(position)));
                        } else {
                            showDevelopingDialog(periods.get(position));
                            Analytics.sendEvent(Analytics.CATEGORY_PERIOD + periods.get(position).getName(), Analytics.ACTION_DEVELOPING);
                        }
                    }
                }));
        adapter = new HistoryPeriodsAdapter(this, periods);
        recyclerView.setAdapter(adapter);
    }

    private void updatePeriodRecyclerView() {
        periods = DataBaseHelperFactory.getHelper().getPeriodDao().getPeriods();
        adapter.updateList(periods);
    }

    private void setReferralCoinsReward() {
        Branch.getInstance(getApplicationContext()).loadRewards(new Branch.BranchReferralStateChangedListener() {
            @Override
            public void onStateChanged(boolean changed, BranchError error) {
                int amount = Branch.getInstance().getCredits();
                if (amount > 0) {
                    showReferralRewardDialog(amount);
                }
            }
        });
    }

    private void setEnterCoinsReward() {
        if (!RewardPreferences.getFirstCoinsReward(this)) {
            showFirstRewardDialog();
        } else {
            if (isAfter(RewardPreferences.getLastDailyCoinsReward(this), 1)) {
                if (isRunning) {
                    showDailyRewardDialog();
                }
            } else {
                boolean allow = UserPreferences.getShowRateApp(this);
                int count = UserPreferences.getEnterAppCount(this);
                if (allow && count % 5 == 0) {
                    showRateAppDialog();
                }
            }
        }
        if (fromPushWithCoins) showPushRewardDialog();
    }

    private void showReferralRewardDialog(final int amount) {
        AwardDialog dialog = new AwardDialog(this, new AwardDialog.OnAwardClickListener() {
            @Override
            public void onAwardClick() {
                CoinsTransaction transaction = new CoinsTransaction(CoinsTransaction.CoinsTransactionCategory.REFERRAL_REWARD);
                for (int i = 0; i < amount; i++) {
                    DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(context, transaction);
                }
                startEnlargeAnimation();
                Branch.getInstance().redeemRewards(amount);
            }
        });
        dialog.show(
                getResources().getString(R.string.dialog_referral_title),
                getResources().getString(R.string.dialog_referral_text),
                CoinsTransaction.CoinsTransactionCategory.REFERRAL_REWARD.getValue() * amount);
    }

    private void showFirstRewardDialog() {
        AwardDialog dialog = new AwardDialog(this, new AwardDialog.OnAwardClickListener() {
            @Override
            public void onAwardClick() {
                RewardPreferences.setFirstCoinsReward(context, true);
                RewardPreferences.setLastDailyCoinsReward(context, getDateFormat().format(new Date()));
                CoinsTransaction transaction = new CoinsTransaction(CoinsTransaction.CoinsTransactionCategory.START_REWARD);
                DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(context, transaction);
                startEnlargeAnimation();
            }
        });
        dialog.show(
                getResources().getString(R.string.dialog_balance_first_title),
                getResources().getString(R.string.dialog_balance_first_text),
                CoinsTransaction.CoinsTransactionCategory.START_REWARD.getValue());
    }

    private void showDailyRewardDialog() {
        AwardDialog dialog = new AwardDialog(this, new AwardDialog.OnAwardClickListener() {
            @Override
            public void onAwardClick() {
                CoinsTransaction transaction = new CoinsTransaction(CoinsTransaction.CoinsTransactionCategory.DAILY_REWARD);
                DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(context, transaction);
                RewardPreferences.setLastDailyCoinsReward(context, getDateFormat().format(new Date()));
                startEnlargeAnimation();
            }
        });
        dialog.show(
                getResources().getString(R.string.dialog_daily_reward_title),
                getResources().getString(R.string.dialog_daily_reward_text),
                CoinsTransaction.CoinsTransactionCategory.DAILY_REWARD.getValue());
    }

    private void showPushRewardDialog() {
        AwardDialog dialog = new AwardDialog(this, new AwardDialog.OnAwardClickListener() {
            @Override
            public void onAwardClick() {
                CoinsTransaction transaction = new CoinsTransaction(CoinsTransaction.CoinsTransactionCategory.PUSH_REWARD);
                DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(context, transaction);
                RewardPreferences.setLastDailyCoinsReward(context, getDateFormat().format(new Date()));
                startEnlargeAnimation();
            }
        });
        dialog.show(
                getResources().getString(R.string.dialog_push_reward_title),
                getResources().getString(R.string.dialog_push_reward_text),
                CoinsTransaction.CoinsTransactionCategory.PUSH_REWARD.getValue());
        fromPushWithCoins = false;
    }

    private void showNotNetworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.dialog_not_network_title));
        builder.setMessage(getResources().getString(R.string.dialog_not_network_text));
        builder.setPositiveButton(getString(R.string.dialog_not_network_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadPeriods();
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_not_network_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void showDevelopingDialog(final Period period) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.dialog_developing_title));
        builder.setMessage(getResources().getString(R.string.dialog_developing_text));
        builder.setPositiveButton(getString(R.string.dialog_developing_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserPreferences.setShowRateApp(context, false);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ExternalConstants.ANDROID_MARKET_URL)));
                        dialog.dismiss();
                        Analytics.sendEvent(Analytics.CATEGORY_PERIOD + period.getName(), Analytics.ACTION_RATE);
                    }
                });
        builder.create().show();
    }

    private void showRateAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.dialog_rate_title));
        builder.setMessage(getResources().getString(R.string.dialog_rate_text));
        builder.setPositiveButton(getString(R.string.dialog_rate_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserPreferences.setShowRateApp(context, false);
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ExternalConstants.ANDROID_MARKET_URL)));
                        dialog.dismiss();
                        Analytics.sendEvent(Analytics.CATEGORY_RATE_APP, Analytics.ACTION_GO_TO_MARKET);
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_rate_later),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Analytics.sendEvent(Analytics.CATEGORY_RATE_APP, Analytics.ACTION_LATER);
                    }
                });
        builder.setNeutralButton(getString(R.string.dialog_rate_forbid),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserPreferences.setShowRateApp(context, false);
                        dialog.dismiss();
                        Analytics.sendEvent(Analytics.CATEGORY_RATE_APP, Analytics.ACTION_NEVER);
                    }
                });
        builder.create().show();
    }
}
