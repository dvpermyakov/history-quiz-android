package dvpermyakov.historyquiz.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Date;
import java.util.List;
import java.util.Map;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.adapters.HistoryMarkTabsAdapter;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.fragments.HistoryInfoFragment;
import dvpermyakov.historyquiz.fragments.TestStartingFragment;
import dvpermyakov.historyquiz.managers.PlayServiceGameManager;
import dvpermyakov.historyquiz.managers.PlayServiceGameManagerFactory;
import dvpermyakov.historyquiz.models.CoinsTransaction;
import dvpermyakov.historyquiz.models.Dependency;
import dvpermyakov.historyquiz.models.Dignity;
import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.HistoryEntity;
import dvpermyakov.historyquiz.models.HistoryMark;
import dvpermyakov.historyquiz.models.HistoryEntityCategory;
import dvpermyakov.historyquiz.models.Paragraph;
import dvpermyakov.historyquiz.models.Period;
import dvpermyakov.historyquiz.models.Person;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.Video;
import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.network.constants.Params;
import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.requests.HistoryMarkInfoRequest;
import dvpermyakov.historyquiz.network.responses.MarkInfoResponse;
import dvpermyakov.historyquiz.specials.IntentStrings;
import dvpermyakov.historyquiz.specials.LogTag;

/**
 * Created by dvpermyakov on 21.05.2016.
 */
public class HistoryMarkActivity extends CoinsActivity implements HistoryInfoFragment.OnReadClickListener {
    private Context context;
    private HistoryMark historyMark;
    private boolean fromPush;
    private ViewPager viewPager;
    private TabLayout tabs;
    private HistoryMarkTabsAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        historyMark = getIntent().getParcelableExtra(IntentStrings.INTENT_HISTORY_MARK_PARAM);
        fromPush = getIntent().getBooleanExtra(IntentStrings.INTENT_FROM_PUSH_PARAM, false);
        if (fromPush) {
            Analytics.sendEvent(Analytics.CATEGORY_PUSH, Analytics.ACTION_ENTER_FROM_PUSH);
        }

        setContentView(R.layout.activity_history_mark);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(historyMark.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.loadContentProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        viewPager = (ViewPager) findViewById(R.id.markViewPager);
        adapter = new HistoryMarkTabsAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabs = (TabLayout) findViewById(R.id.markTabs);
        tabs.setupWithViewPager(viewPager);

        downloadMarkInfo();

        Analytics.sendScreen(Analytics.SCREEN_MARK_INFO + historyMark.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        openMark();
    }

    private void openMark() {
        if (historyMark.isOpened() || historyMark.getCategory() == HistoryEntityCategory.PERIOD) return;

        if (historyMark.getCategory() == HistoryEntityCategory.EVENT)
            historyMark = DataBaseHelperFactory.getHelper().getEventDao().getById(historyMark.getId());
        if (historyMark.getCategory() == HistoryEntityCategory.PERSON)
            historyMark = DataBaseHelperFactory.getHelper().getPersonDao().getById(historyMark.getId());

        if (!historyMark.isOpened()) {
            historyMark.setIsOpened(true);
            historyMark.setOpenedDate(new Date());
            if (historyMark.getCategory() == HistoryEntityCategory.EVENT)
                DataBaseHelperFactory.getHelper().getEventDao().saveEvent((Event) historyMark);
            if (historyMark.getCategory() == HistoryEntityCategory.PERSON)
                DataBaseHelperFactory.getHelper().getPersonDao().savePerson((Person) historyMark);
            showNewOpenedSnackBar();

            Analytics.sendEvent(Analytics.SCREEN_MARK_INFO + historyMark.getName(), Analytics.ACTION_OPEN_MARK);
        }
    }

    private void downloadMarkInfo() {
        progressBar.setVisibility(View.VISIBLE);

        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        volleyQueue.cancelAll(this);
        Map<String, String> getParams = Params.getParams(Urls.HISTORY_MARK_INFO_REQUEST_URL);
        getParams.put("mark_id", historyMark.getId());
        getParams.put("category", String.valueOf(HistoryEntityCategory.toInt(historyMark.getCategory())));
        Request request = new HistoryMarkInfoRequest(new Response.Listener<MarkInfoResponse>() {
            @Override
            public void onResponse(MarkInfoResponse response) {
                Test test = response.getTest();
                test.setAppFields();
                DataBaseHelperFactory.getHelper().getTestDao().save(test);
                setTest(test);

                DataBaseHelperFactory.getHelper().getParagraphDao().removeParagraphs(historyMark);
                for (Paragraph paragraph : response.getText().getParagraphs()) {
                    paragraph.setMark(historyMark);
                    DataBaseHelperFactory.getHelper().getParagraphDao().saveParagraph(paragraph);
                }

                int order = 0;

                List<Video> videos = response.getVideos();
                for (Video video : videos) {
                    video.setOrder(order);
                    video.setCategory(HistoryEntityCategory.VIDEO);
                    video.setAppFields();
                }
                setParent(videos);
                DataBaseHelperFactory.getHelper().getVideoDao().saveVideos(videos);
                DataBaseHelperFactory.getHelper().getVideoChannelDao().saveVideoChannels(response.getChannels());

                List<Event> events = response.getEvents();
                for(Event event : events) {
                    event.setOrder(order++);
                    event.setCategory(HistoryEntityCategory.EVENT);
                    event.setAppFields();
                }
                setParent(events);
                DataBaseHelperFactory.getHelper().getEventDao().saveEvents(events);

                List<Person> persons = response.getPersons();
                for(Person person : persons) {
                    person.setOrder(order++);
                    person.setCategory(HistoryEntityCategory.PERSON);
                    person.setAppFields();
                    DataBaseHelperFactory.getHelper().getDignityDao().removeDignities(person);
                    for (Dignity dignity : person.getDignities()) {
                        dignity.setPerson(person);
                        DataBaseHelperFactory.getHelper().getDignityDao().saveDignity(dignity);
                    }
                }
                setParent(persons);
                DataBaseHelperFactory.getHelper().getPersonDao().savePersons(persons);

                DataBaseHelperFactory.getHelper().getDependencyDao().removeDependencies(historyMark);
                for (Dependency dependency : response.getDependencies()) {
                    dependency.setMark(historyMark);
                    DataBaseHelperFactory.getHelper().getDependencyDao().saveDependency(dependency);
                }
                updateAdapter();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LogTag.TAG_NETWORK, "Fail!");
                if (historyMark.getTest() != null) {
                    setTest(DataBaseHelperFactory.getHelper().getTestDao().getById(historyMark.getTest().getId()));
                    updateAdapter();
                } else {
                    if (!isFinishing()) showNotNetworkDialog();
                }
            }
        }, getParams);
        request.setTag(this);
        volleyQueue.add(request);
    }

    private void setParent(List<? extends HistoryEntity> children) {
        for (HistoryEntity child : children) {
            child.setParent(historyMark);
        }
    }

    private void setTest(Test test) {
        if (historyMark.getCategory() == HistoryEntityCategory.PERIOD) {
            historyMark = DataBaseHelperFactory.getHelper().getPeriodDao().getById(historyMark.getId());
            historyMark.setTest(test);
            DataBaseHelperFactory.getHelper().getPeriodDao().savePeriod((Period) historyMark);
        }
        if (historyMark.getCategory() == HistoryEntityCategory.EVENT) {
            historyMark = DataBaseHelperFactory.getHelper().getEventDao().getById(historyMark.getId());
            historyMark.setTest(test);
            DataBaseHelperFactory.getHelper().getEventDao().saveEvent((Event) historyMark);
        }
        if (historyMark.getCategory() == HistoryEntityCategory.PERSON) {
            historyMark = DataBaseHelperFactory.getHelper().getPersonDao().getById(historyMark.getId());
            historyMark.setTest(test);
            DataBaseHelperFactory.getHelper().getPersonDao().savePerson((Person) historyMark);
        }
    }

    private void setReadDone(boolean needSendToPlayService) {
        if (historyMark.getCategory() == HistoryEntityCategory.PERIOD) {
            historyMark = DataBaseHelperFactory.getHelper().getPeriodDao().getById(historyMark.getId());
            historyMark.setReadDone();
            historyMark.setNeedSendToPlayService(needSendToPlayService);
            DataBaseHelperFactory.getHelper().getPeriodDao().savePeriod((Period) historyMark);
        }
        if (historyMark.getCategory() == HistoryEntityCategory.EVENT) {
            historyMark = DataBaseHelperFactory.getHelper().getEventDao().getById(historyMark.getId());
            historyMark.setReadDone();
            historyMark.setNeedSendToPlayService(needSendToPlayService);
            DataBaseHelperFactory.getHelper().getEventDao().saveEvent((Event) historyMark);
        }
        if (historyMark.getCategory() == HistoryEntityCategory.PERSON) {
            historyMark = DataBaseHelperFactory.getHelper().getPersonDao().getById(historyMark.getId());
            historyMark.setReadDone();
            historyMark.setNeedSendToPlayService(needSendToPlayService);
            DataBaseHelperFactory.getHelper().getPersonDao().savePerson((Person) historyMark);
        }
    }

    private void updateAdapter() {
        adapter.updateFragments(historyMark);
        adapter.notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(adapter.getTestStartingFragmentIndex());
        tabs.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (fromPush) {
            startActivity(new Intent(context, HistoryPeriodsActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        } else {
            finish();
        }
    }

    @Override
    public void onReadClick() {
        TestStartingFragment testStartingFragment = adapter.getTestStartingFragment();
        if (testStartingFragment != null) {
            if (!historyMark.isReadDone()) {
                PlayServiceGameManager manager = PlayServiceGameManagerFactory.getManager();
                boolean needSendToPlayService;
                if (manager.isConnected()) {
                    needSendToPlayService = false;
                    manager.sendReadEvent();
                } else {
                    needSendToPlayService = true;
                }
                CoinsTransaction transaction = new CoinsTransaction(CoinsTransaction.CoinsTransactionCategory.READ_HISTORY_MARK);
                DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(this, transaction);
                setReadDone(needSendToPlayService);
                startEnlargeAnimation();
            }
            if (historyMark.canStartTest()) {
                showStartTestDialog();
            } else {
                int index = 0;
                for (Boolean dependencyBool : historyMark.getConditionBooleans()) {
                    if (!dependencyBool) {
                        showStartOtherMarkDialog(historyMark.getConditionMarks().get(index));
                        break;
                    }
                    index++;
                }
            }
        }
    }

    private void showStartOtherMarkDialog(final HistoryMark mark) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.dialog_start_other_title));
        builder.setMessage(getResources().getString(R.string.dialog_start_other_text).replace("123", mark.getName()));
        builder.setPositiveButton(getString(R.string.dialog_start_other_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewPager.setCurrentItem(adapter.getTestStartingFragmentIndex());
                        startActivity(new Intent(context, HistoryMarkActivity.class)
                                .putExtra(IntentStrings.INTENT_HISTORY_MARK_PARAM, mark));
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_start_other_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void showStartTestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.dialog_start_test_title));
        builder.setMessage(getResources().getString(R.string.dialog_start_test_text));
        builder.setPositiveButton(getString(R.string.dialog_start_test_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewPager.setCurrentItem(adapter.getTestStartingFragmentIndex());
                        startActivity(new Intent(context, TestGameActivity.class)
                                .putExtra(IntentStrings.INTENT_HISTORY_ENTITY_PARAM, historyMark)
                                .putExtra(IntentStrings.INTENT_TEST_PARAM,
                                        DataBaseHelperFactory.getHelper().getTestDao().getById(historyMark.getTest().getId())));
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_start_test_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void showNotNetworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.dialog_not_network_title));
        builder.setMessage(getResources().getString(R.string.dialog_not_network_text));
        builder.setPositiveButton(getString(R.string.dialog_not_network_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadMarkInfo();
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

    private void showNewOpenedSnackBar() {
        String title = getResources().getString(R.string.dialog_open_new_mark) + " " + '"' + historyMark.getName() + '"';
        final Snackbar snackbar = Snackbar.make(viewPager, title, Snackbar.LENGTH_INDEFINITE);
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

}
