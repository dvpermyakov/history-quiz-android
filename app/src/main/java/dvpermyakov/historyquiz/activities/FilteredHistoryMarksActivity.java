package dvpermyakov.historyquiz.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.vk.sdk.api.model.VKScopes;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dvpermyakov.historyquiz.ExternalConstants;
import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.adapters.FilteredDateAdapter;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.appbar.DrawerToggle;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.listeners.OnNavigationItemSelectedListener;
import dvpermyakov.historyquiz.models.Dignity;
import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.HistoryMark;
import dvpermyakov.historyquiz.models.HistoryEntityCategory;
import dvpermyakov.historyquiz.models.Person;
import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.network.constants.Params;
import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.requests.NewHistoryMarksRequest;
import dvpermyakov.historyquiz.network.responses.NewHistoryMarksResponse;
import dvpermyakov.historyquiz.preferences.UserPreferences;
import dvpermyakov.historyquiz.specials.DateUtils;
import dvpermyakov.historyquiz.specials.IntentStrings;
import dvpermyakov.historyquiz.specials.LogTag;

import static dvpermyakov.historyquiz.specials.DateUtils.getDateFormat;

/**
 * Created by dvpermyakov on 17.09.2016.
 */
public class FilteredHistoryMarksActivity extends AppCompatActivity {
    private static final int AMOUNT_NEW_MARKS = 5;
    private Context context;
    private OnNavigationItemSelectedListener.ActivityCategory category;
    private FilteredDateAdapter adapter;
    private TextView noDataTextView;
    private RecyclerView recyclerView;
    private int amountNewEvent = AMOUNT_NEW_MARKS;
    private int amountNewPerson = AMOUNT_NEW_MARKS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        boolean fromPush = getIntent().getBooleanExtra(IntentStrings.INTENT_FROM_PUSH_PARAM, false);
        if (fromPush) {
            Analytics.sendEvent(Analytics.CATEGORY_PUSH, Analytics.ACTION_ENTER_FROM_PUSH);
        }

        category =  OnNavigationItemSelectedListener.ActivityCategory.fromInt(
                getIntent().getIntExtra(IntentStrings.INTENT_FILTERED_MARKS_PARAM, 0));

        setContentView(R.layout.activity_filtered_hisory_marks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(category.toString(this));

        noDataTextView = (TextView) findViewById(R.id.noDataTextView);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerFilteredMarks);
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigationView);
        if (navigationView != null) {
            if (category == OnNavigationItemSelectedListener.ActivityCategory.OPENED_MARKS) {
                navigationView.setCheckedItem(R.id.opened);
            }
            if (category == OnNavigationItemSelectedListener.ActivityCategory.DONE_MARKS) {
                navigationView.setCheckedItem(R.id.done);
            }
            if (category == OnNavigationItemSelectedListener.ActivityCategory.NEW_MARKS) {
                navigationView.setCheckedItem(R.id.news);
            }
            navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener(this, category, drawerLayout));
        }

        DrawerToggle toggle = new DrawerToggle(this, drawerLayout, toolbar, navigationView);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (category == OnNavigationItemSelectedListener.ActivityCategory.NEW_MARKS) {
            findViewById(R.id.vkInviteLinearLayout).setVisibility(View.VISIBLE);
            Button vkPublicButton = (Button)findViewById(R.id.vkPublicButton);
            vkPublicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (UserPreferences.getUser(context).isVkGroupMember()) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ExternalConstants.VK_GROUP_URL)));
                    } else {
                        showInviteVkGroupDialog();
                    }
                    Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + context.getResources().getString(R.string.app_name), Analytics.ACTION_VK_PUBLIC);
                }
            });
        }

        createHistoryMarksRecyclerView();

        Analytics.sendScreen(Analytics.SCREEN_FILTERED_HISTORY_MARKS + category.toString(this) + " " + getResources().getString(R.string.app_name));
    }

    private void createHistoryMarksRecyclerView() {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewFilteredDate);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Map<String, List<HistoryMark>> mapMarks = getMapMarks(getMarks());
        List<String> listDates = getListDates(mapMarks);
        adapter = new FilteredDateAdapter(this, mapMarks, listDates);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAdapter();
        if (category == OnNavigationItemSelectedListener.ActivityCategory.NEW_MARKS) {
            downloadNewMarks();
        }
    }

    private void updateAdapter() {
        Map<String, List<HistoryMark>> mapMarks = getMapMarks(getMarks());
        List<String> listDates = getListDates(mapMarks);
        adapter.refresh(mapMarks, listDates);
        if (listDates.size() == 0) {
            noDataTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noDataTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private List<HistoryMark> getMarks() {
        List<HistoryMark> suitableMarks = new ArrayList<>();
        if (category == OnNavigationItemSelectedListener.ActivityCategory.OPENED_MARKS || category == OnNavigationItemSelectedListener.ActivityCategory.DONE_MARKS) {
            List<HistoryMark> marks = new ArrayList<>();
            marks.addAll(DataBaseHelperFactory.getHelper().getEventDao().getOpenedEvents());
            marks.addAll(DataBaseHelperFactory.getHelper().getPersonDao().getOpenedPersons());
            for (HistoryMark mark : marks) {
                boolean testClosed = DataBaseHelperFactory.getHelper().getTestResultDao().isTestClosed(mark.getTest());
                if (!testClosed && category == OnNavigationItemSelectedListener.ActivityCategory.OPENED_MARKS) {
                    suitableMarks.add(mark);
                }
                if (testClosed && category == OnNavigationItemSelectedListener.ActivityCategory.DONE_MARKS) {
                    suitableMarks.add(mark);
                }
            }
        } else if (category == OnNavigationItemSelectedListener.ActivityCategory.NEW_MARKS) {
            suitableMarks.addAll(DataBaseHelperFactory.getHelper().getEventDao().getNewEvents(amountNewEvent));
            suitableMarks.addAll(DataBaseHelperFactory.getHelper().getPersonDao().getNewPersons(amountNewPerson));
        }
        return suitableMarks;
    }

    private Map<String, List<HistoryMark>> getMapMarks(List<HistoryMark> marks) {
        Map<String, List<HistoryMark>> mapMarks = new HashMap<>();
        for (HistoryMark mark : marks) {
            String dateString = DateUtils.defaultDateString;
            Date date = getMarkDate(mark);
            if (date != null) {
                dateString = getDateFormat().format(date);
            }
            if (!mapMarks.containsKey(dateString)) {
                mapMarks.put(dateString, new ArrayList<HistoryMark>());
            }
            mapMarks.get(dateString).add(mark);
        }
        return mapMarks;
    }

    private List<String> getListDates(Map<String, List<HistoryMark>> mapMarks) {
        List<String> listDates = new ArrayList<>();

        for (Map.Entry<String, List<HistoryMark>> entry : mapMarks.entrySet()) {
            listDates.add(entry.getKey());
            Collections.sort(entry.getValue(), new Comparator<HistoryMark>() {
                public int compare(HistoryMark historyMark1, HistoryMark historyMark2) {
                    if (getMarkDate(historyMark1) == null || getMarkDate(historyMark2) == null)
                        return 0;
                    return -getMarkDate(historyMark1).compareTo(getMarkDate(historyMark2));
                }
            });
        }
        Collections.sort(listDates, new Comparator<String>() {
            @Override
            public int compare(String date1, String date2) {
                try {
                    return -getDateFormat().parse(date1).compareTo(getDateFormat().parse(date2));
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        return listDates;
    }

    private Date getMarkDate(HistoryMark mark) {
        if (category == OnNavigationItemSelectedListener.ActivityCategory.OPENED_MARKS) {
            return mark.getOpenedDate();
        }
        if (category == OnNavigationItemSelectedListener.ActivityCategory.DONE_MARKS) {
            return DataBaseHelperFactory.getHelper().getTestResultDao().getFirstTestResultCreated(mark.getTest());
        }
        if (category == OnNavigationItemSelectedListener.ActivityCategory.NEW_MARKS) {
            return new Date(Long.parseLong(mark.getCreated()) * 1000);
        }
        return new Date();
    }

    private void downloadNewMarks() {
        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        volleyQueue.cancelAll(this);
        Map<String, String> getParams = Params.getParams(Urls.NEW_MARKS_REQUEST_URL);
        getParams.put("amount", String.valueOf(AMOUNT_NEW_MARKS));
        Request request = new NewHistoryMarksRequest(new Response.Listener<NewHistoryMarksResponse>() {
            @Override
            public void onResponse(NewHistoryMarksResponse response) {
                amountNewEvent = response.getEvents().size();
                for (Event event : response.getEvents()) {
                    event.setCategory(HistoryEntityCategory.EVENT);
                    if (!DataBaseHelperFactory.getHelper().getEventDao().hasInDataBase(event)) {
                        DataBaseHelperFactory.getHelper().getEventDao().saveEvent(event);
                    }
                }

                amountNewPerson = response.getPersons().size();
                for(Person person : response.getPersons()) {
                    person.setCategory(HistoryEntityCategory.PERSON);
                    DataBaseHelperFactory.getHelper().getDignityDao().removeDignities(person);
                    for (Dignity dignity : person.getDignities()) {
                        dignity.setPerson(person);
                        DataBaseHelperFactory.getHelper().getDignityDao().saveDignity(dignity);
                    }
                    if (!DataBaseHelperFactory.getHelper().getPersonDao().hasInDataBase(person)) {
                        DataBaseHelperFactory.getHelper().getPersonDao().savePerson(person);
                    }
                }

                updateAdapter();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LogTag.TAG_NETWORK, "Fail!");
            }
        }, getParams);
        request.setTag(this);
        volleyQueue.add(request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ExternalConstants.VK_GROUP_URL)));
        }
    }

    private void showInviteVkGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.dialog_vk_group_invite_title));
        builder.setMessage(getResources().getString(R.string.dialog_vk_group_invite_text_opt));
        builder.setPositiveButton(getString(R.string.dialog_vk_group_invite_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] scopes = new String[] {VKScopes.GROUPS};
                        startActivityForResult(new Intent(context, VKLoginActivity.class)
                                .putExtra(IntentStrings.INTENT_VK_SCOPE, scopes), 0);
                        dialog.dismiss();

                        Analytics.sendEvent(Analytics.CATEGORY_GROUP_INVITE, Analytics.ACTION_GROUP_VK);
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_vk_group_invite_no_opt),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ExternalConstants.VK_GROUP_URL)));
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
