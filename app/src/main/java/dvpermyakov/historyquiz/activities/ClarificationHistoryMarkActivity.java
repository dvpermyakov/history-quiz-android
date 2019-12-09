package dvpermyakov.historyquiz.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.Date;
import java.util.Map;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.dialogs.AwardDialog;
import dvpermyakov.historyquiz.models.CoinsTransaction;
import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.HistoryMark;
import dvpermyakov.historyquiz.models.HistoryEntityCategory;
import dvpermyakov.historyquiz.models.Person;
import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.network.constants.Params;
import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.requests.ShortHistoryMarkInfoRequest;
import dvpermyakov.historyquiz.preferences.RewardPreferences;
import dvpermyakov.historyquiz.specials.QueryStrings;

/**
 * Created by dvpermyakov on 07.06.2016.
 */
public class ClarificationHistoryMarkActivity extends CoinsActivity {
    private Context context;
    private View view;
    private HistoryMark mark;
    private HistoryEntityCategory category;
    private String markId;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_clarification_history_mark);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        view = findViewById(R.id.openedLinearLayout);

        progressBar = (ProgressBar) findViewById(R.id.loadContentProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        category = HistoryEntityCategory.fromInt(Integer.valueOf(getIntent().getData().getQueryParameter(QueryStrings.CATEGORY_QUERY)));
        markId = getIntent().getData().getQueryParameter(QueryStrings.MARK_ID_QUERY);

        if (category == HistoryEntityCategory.EVENT) mark = DataBaseHelperFactory.getHelper().getEventDao().getById(markId);
        if (category == HistoryEntityCategory.PERSON) mark = DataBaseHelperFactory.getHelper().getPersonDao().getById(markId);
        if (mark == null) {
            downloadShortMarkInfo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mark != null) {
            showDescription();
        }
    }

    private void downloadShortMarkInfo() {
        progressBar.setVisibility(View.VISIBLE);

        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        volleyQueue.cancelAll(this);
        Map<String, String> getParams = Params.getParams(Urls.HISTORY_MARK_INFO_REQUEST_URL);
        getParams.put("mark_id", markId);
        getParams.put("category", String.valueOf(HistoryEntityCategory.toInt(category)));
        Request request = new ShortHistoryMarkInfoRequest(new Response.Listener<HistoryMark>() {
            @Override
            public void onResponse(HistoryMark response) {
                mark = response;
                mark.setCategory(category);
                showDescription();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showNotNetworkDialog();
            }
        }, getParams);
        request.setTag(this);
        volleyQueue.add(request);
    }

    private void showDescription() {
        TextView text = (TextView) findViewById(R.id.textViewMarkInfoDescription);
        text.setText(mark.getDescription());
        progressBar.setVisibility(View.GONE);
        openMarkInfo();

        Analytics.sendScreen(Analytics.SCREEN_OPEN_MARK + mark.getName());
    }

    private void openMarkInfo() {
        if (mark != null) {
            if (!mark.isOpened()) {
                mark.setIsOpened(true);
                mark.setOpenedDate(new Date());
                if (mark.getCategory() == HistoryEntityCategory.EVENT)
                    DataBaseHelperFactory.getHelper().getEventDao().saveEvent(new Event(mark));
                if (mark.getCategory() == HistoryEntityCategory.PERSON)
                    DataBaseHelperFactory.getHelper().getPersonDao().savePerson(new Person(mark));
                showNewOpenedSnackBar();

                if (!RewardPreferences.getFirstOpenCoinsReward(this)) {
                    showOpenRewardDialog();
                } else {
                    CoinsTransaction transaction = new CoinsTransaction(CoinsTransaction.CoinsTransactionCategory.OPEN_HISTORY_MARK);
                    DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(this, transaction);
                    startEnlargeAnimation();
                }

                Analytics.sendEvent(Analytics.SCREEN_OPEN_MARK + mark.getName(), Analytics.ACTION_OPEN_MARK);
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void showNewOpenedSnackBar() {
        String title = getResources().getString(R.string.dialog_open_new_mark) + " " + '"' + mark.getName() + '"';
        final Snackbar snackbar = Snackbar.make(view, title, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getResources().getString(R.string.dialog_rules_ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorRightAnswer));
        snackbar.show();
    }

    private void showOpenRewardDialog() {
        AwardDialog dialog = new AwardDialog(this, new AwardDialog.OnAwardClickListener() {
            @Override
            public void onAwardClick() {
                CoinsTransaction transaction = new CoinsTransaction(CoinsTransaction.CoinsTransactionCategory.OPEN_HISTORY_MARK);
                DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(context, transaction);
                startEnlargeAnimation();
                RewardPreferences.setFirstOpenCoinsReward(context, true);
            }
        });
        dialog.show(
                getResources().getString(R.string.dialog_open_reward_title),
                getResources().getString(R.string.dialog_open_reward_text),
                CoinsTransaction.CoinsTransactionCategory.OPEN_HISTORY_MARK.getValue());
    }

    private void showNotNetworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.dialog_not_network_title));
        builder.setCancelable(false);
        builder.setMessage(getResources().getString(R.string.dialog_not_network_text));
        builder.setPositiveButton(getString(R.string.dialog_not_network_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadShortMarkInfo();
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_not_network_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.create().show();
    }
}
