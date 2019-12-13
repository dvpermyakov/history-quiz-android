package dvpermyakov.historyquiz.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.vk.sdk.api.model.VKScopes;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.adapter_models.CardViewInformationModel;
import dvpermyakov.historyquiz.adapters.CardViewInformationAdapter;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.appbar.DrawerToggle;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.decorations.DividerItemDecoration;
import dvpermyakov.historyquiz.listeners.OnNavigationItemSelectedListener;
import dvpermyakov.historyquiz.managers.VKUserManager;
import dvpermyakov.historyquiz.models.CoinsTransaction;
import dvpermyakov.historyquiz.models.User;
import dvpermyakov.historyquiz.preferences.UserPreferences;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 21.09.2016.
 */
public class BalanceActivity extends AppCompatActivity {
    private Activity context;
    private boolean withNavigationView;
    private TextView balance;
    private View.OnClickListener adClickListener;
    private View.OnClickListener videoClickListener;
    private Button interstitialVideoButton;
    private Button interstitialButton;
    private CardViewInformationAdapter historyAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        withNavigationView = getIntent().getBooleanExtra(IntentStrings.INTENT_WITH_NAVIGATION_VIEW_PARAM, false);

        setContentView(R.layout.activity_balance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadAdsButtons();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerBalance);
        if (withNavigationView) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
            if (navigationView != null) {
                navigationView.setCheckedItem(R.id.balance);
                navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener(this, OnNavigationItemSelectedListener.ActivityCategory.BALANCE, drawerLayout));
            }

            DrawerToggle toggle = new DrawerToggle(this, drawerLayout, toolbar, navigationView);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        balance = (TextView) findViewById(R.id.coinsBalanceTextView);
        updateBalanceTextView();

        ImageView coinsIcon = (ImageView) findViewById(R.id.coinsImageView);
        if (coinsIcon != null) {
            coinsIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorBlackLight), PorterDuff.Mode.SRC_IN);
        }

        addCardViewCoinsGetWays();
        addCardViewCoinsBuyWays();
        addCardViewCoinsHistory();

        Analytics.sendScreen(Analytics.SCREEN_BALANCE + getResources().getString(R.string.app_name));
    }

    private void updateBalanceTextView() {
        if (balance != null) {
            String text = getResources().getString(R.string.coins_balance) +
                    Integer.toString(DataBaseHelperFactory.getHelper().getCoinsTransactionDao().getCoinsBalance());
            balance.setText(text);
        }
    }

    private void loadAdsButtons() {
        final Drawable drawable = ContextCompat.getDrawable(this, R.mipmap.ic_coins).getConstantState().newDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorBlackLight), PorterDuff.Mode.SRC_IN);
        drawable.setBounds(0, 0,
                (int) (drawable.getIntrinsicWidth() / 2.5),
                (int) (drawable.getIntrinsicHeight() / 2.5));

        interstitialVideoButton = (Button) findViewById(R.id.vkButton);
        if (interstitialVideoButton != null) {
            interstitialVideoButton.setOnClickListener(videoClickListener);
            interstitialVideoButton.setText(interstitialVideoButton.getText() + "  +" + CoinsTransaction.CoinsTransactionCategory.INTERSTITIAL_VIDEO_AD.getValue());
            interstitialVideoButton.setCompoundDrawables(null, null, drawable, null);
            interstitialVideoButton.setCompoundDrawablePadding(3);

            User user = UserPreferences.getUser(this);
            if (!user.isVkGroupMember()) {
                String title = "Группа ВК  +" + CoinsTransaction.CoinsTransactionCategory.VK_GROUP_REWARD.getValue();
                interstitialVideoButton.setText(title);
                interstitialVideoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showInviteVkGroupDialog();
                    }
                });
            }
        }
    }

    private void setAward(CoinsTransaction.CoinsTransactionCategory category) {
        CoinsTransaction transaction = new CoinsTransaction(category);
        DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(this, transaction);
        updateBalanceTextView();
        historyAdapter.setNewCardViewInformationModelList(getCoinsTransactions());
        historyAdapter.notifyDataSetChanged();
    }

    private void addCardViewCoinsGetWays() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCoinsGetWays);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider, (int) getResources().getDimension(R.dimen.decoration_normal_padding)));
        List<CardViewInformationModel> list = new ArrayList<>();
        list.add(new CardViewInformationModel(CoinsTransaction.CoinsTransactionCategory.DAILY_REWARD.getString(this),
                String.valueOf(CoinsTransaction.CoinsTransactionCategory.DAILY_REWARD.getValue()), null, null));
        list.add(new CardViewInformationModel(CoinsTransaction.CoinsTransactionCategory.OPEN_HISTORY_MARK.getString(this),
                String.valueOf(CoinsTransaction.CoinsTransactionCategory.OPEN_HISTORY_MARK.getValue()), null, null));
        list.add(new CardViewInformationModel(CoinsTransaction.CoinsTransactionCategory.READ_HISTORY_MARK.getString(this),
                String.valueOf(CoinsTransaction.CoinsTransactionCategory.READ_HISTORY_MARK.getValue()), null, null));
        list.add(new CardViewInformationModel(CoinsTransaction.CoinsTransactionCategory.DONE_TEST.getString(this),
                String.valueOf(CoinsTransaction.CoinsTransactionCategory.DONE_TEST.getValue()), null, null));
        CardViewInformationAdapter adapter = new CardViewInformationAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    private void addCardViewCoinsBuyWays() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCoinsBuyWays);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider, (int) getResources().getDimension(R.dimen.decoration_normal_padding)));
        List<CardViewInformationModel> list = new ArrayList<>();
        list.add(new CardViewInformationModel(CoinsTransaction.CoinsTransactionCategory.BUY_HISTORY_MARK.getString(this),
                String.valueOf(Math.abs(CoinsTransaction.CoinsTransactionCategory.BUY_HISTORY_MARK.getValue())), null, null));
        list.add(new CardViewInformationModel(CoinsTransaction.CoinsTransactionCategory.BUY_TEST_CONDITIONS.getString(this),
                String.valueOf(Math.abs(CoinsTransaction.CoinsTransactionCategory.BUY_TEST_CONDITIONS.getValue())), null, null));
        list.add(new CardViewInformationModel(CoinsTransaction.CoinsTransactionCategory.CONTINUE_TEST.getString(this),
                String.valueOf(Math.abs(CoinsTransaction.CoinsTransactionCategory.CONTINUE_TEST.getValue())), null, null));
        CardViewInformationAdapter adapter = new CardViewInformationAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    private void addCardViewCoinsHistory() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCoinsHistory);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(this, R.drawable.divider, (int) getResources().getDimension(R.dimen.decoration_normal_padding)));

        historyAdapter = new CardViewInformationAdapter(this, getCoinsTransactions());
        recyclerView.setAdapter(historyAdapter);
    }

    private List<CardViewInformationModel> getCoinsTransactions() {
        List<CardViewInformationModel> list = new ArrayList<>();
        for (CoinsTransaction transaction : DataBaseHelperFactory.getHelper().getCoinsTransactionDao().getTransactions()) {
            String sign = Math.signum(transaction.getCategory().getValue()) > 0.0 ? "+" : "";
            list.add(new CardViewInformationModel(transaction.getCategory().getString(this),
                    sign + String.valueOf(transaction.getValue()), null, null));
        }
        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!withNavigationView) {
                finish();
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            boolean success = data.getBooleanExtra(IntentStrings.INTENT_LOGIN_VK_SUCCESS, false);
            if (success) {
                VKUserManager.subscribeGroup(this);
                setAward(CoinsTransaction.CoinsTransactionCategory.VK_GROUP_REWARD);
                String title = "Видеоролик  +" + CoinsTransaction.CoinsTransactionCategory.INTERSTITIAL_VIDEO_AD.getValue();
                interstitialVideoButton.setText(title);
                interstitialVideoButton.setOnClickListener(videoClickListener);
            }
        }
    }

    private void showInviteVkGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.dialog_vk_group_invite_title));
        builder.setMessage(getResources().getString(R.string.dialog_vk_group_invite_text));
        builder.setPositiveButton(getString(R.string.dialog_vk_group_invite_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] scopes = new String[]{VKScopes.GROUPS};
                        startActivityForResult(new Intent(context, VKLoginActivity.class)
                                .putExtra(IntentStrings.INTENT_VK_SCOPE, scopes), 0);
                        dialog.dismiss();

                        Analytics.sendEvent(Analytics.CATEGORY_GROUP_INVITE, Analytics.ACTION_GROUP_VK);
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_vk_group_invite_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
