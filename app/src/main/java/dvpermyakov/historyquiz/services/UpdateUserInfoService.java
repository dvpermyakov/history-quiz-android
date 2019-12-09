package dvpermyakov.historyquiz.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.appbar.DrawerToggle;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.links.BranchLink;
import dvpermyakov.historyquiz.managers.PlayServiceGameManager;
import dvpermyakov.historyquiz.managers.PlayServiceGameManagerFactory;
import dvpermyakov.historyquiz.managers.VKUserManager;
import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.HistoryEntity;
import dvpermyakov.historyquiz.models.Period;
import dvpermyakov.historyquiz.models.Person;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.User;
import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.network.constants.Headers;
import dvpermyakov.historyquiz.network.requests.ApproveUserIdRequest;
import dvpermyakov.historyquiz.network.requests.RegisterUserRequest;
import dvpermyakov.historyquiz.network.responses.ApproveUserIdResponse;
import dvpermyakov.historyquiz.preferences.PreferencesStrings;
import dvpermyakov.historyquiz.preferences.SharePreferences;
import dvpermyakov.historyquiz.preferences.UserPreferences;
import dvpermyakov.historyquiz.specials.LogTag;
import io.branch.referral.Branch;

/**
 * Created by dvpermyakov on 16.01.2017.
 */

public class UpdateUserInfoService extends IntentService {
    public UpdateUserInfoService() {
        super(UpdateUserInfoService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LogTag.TAG_USER_SERVICE, "start service");

        setUser();
        if (SharePreferences.getSharedVKUrl(this) == null) {
            BranchLink.generateLink(this);
        }
        VKUserManager.setUserInfo(this);
        VKUserManager.setGroupMember(this);
        DrawerToggle.loadImage(this);
        if (!UserPreferences.isPeriodsCountUpdated(this)) {  // it is need for backward compatibility
            setPeriodsCounts();
            sendUserInfoToServer();
            UserPreferences.setPeriodsCountUpdated(this, true);
        }
        if (!UserPreferences.getInitPlayGameService(this)) {
            setNeedSendToPlayService();
            UserPreferences.setInitPlayGameService(this, true);
        }
        sendTestDoneToServer();
        PlayServiceGameManager manager = PlayServiceGameManagerFactory.getManager();
        if (manager.isConnected()) {
            manager.updateTestDone();
            manager.updateReadArticle();
        }
    }

    private void setNeedSendToPlayService() {
        List<Test> tests = DataBaseHelperFactory.getHelper().getTestDao().getTests();
        for (Test test : tests) {
            if (DataBaseHelperFactory.getHelper().getTestResultDao().isTestClosed(test)) {
                test.saveNeedSendToPlayService(true);
            }
        }
        List<Period> periods = DataBaseHelperFactory.getHelper().getPeriodDao().getReadPeriods();
        for (Period period : periods) {
            period.setNeedSendToPlayService(true);
            DataBaseHelperFactory.getHelper().getPeriodDao().savePeriod(period);
        }
        List<Event> events = DataBaseHelperFactory.getHelper().getEventDao().getReadEvents();
        for (Event event : events) {
            event.setNeedSendToPlayService(true);
            DataBaseHelperFactory.getHelper().getEventDao().saveEvent(event);
        }
        List<Person> persons = DataBaseHelperFactory.getHelper().getPersonDao().getReadPerson();
        for (Person person : persons) {
            person.setNeedSendToPlayService(true);
            DataBaseHelperFactory.getHelper().getPersonDao().savePerson(person);
        }
    }

    private void sendTestDoneToServer() {
        List<Test> tests = DataBaseHelperFactory.getHelper().getTestDao().getTestsWithNeedSendToServer();
        for (Test test : tests) {
            test.sendDoneToServer();
        }
    }

    private void setPeriodsCounts() {
        Map<String, Integer> periodMap = new HashMap<>();
        for (Period period : DataBaseHelperFactory.getHelper().getPeriodDao().getPeriods()) {
            periodMap.put(period.getId(), 0);
        }
        int marksDoneCount = 0;
        List<HistoryEntity> entities = new ArrayList<>();
        entities.addAll(DataBaseHelperFactory.getHelper().getEventDao().getOpenedEvents());
        entities.addAll(DataBaseHelperFactory.getHelper().getPersonDao().getOpenedPersons());
        entities.addAll(DataBaseHelperFactory.getHelper().getPeriodDao().getPeriods());
        entities.addAll(DataBaseHelperFactory.getHelper().getVideoDao().getVideos());
        List<HistoryEntity> entitiesWithoutPeriod = new ArrayList<>();
        for (HistoryEntity entity : entities) {
            boolean testClosed = DataBaseHelperFactory.getHelper().getTestResultDao().isTestClosed(entity.getTest());
            if (testClosed) {
                marksDoneCount++;
                Period period = entity.getPeriod();
                if (period != null) {
                    periodMap.put(period.getId(), periodMap.get(period.getId()) + 1);
                } else {
                    entitiesWithoutPeriod.add(entity);
                }
                entity.getTest().sendDoneToServer();
            }
        }
        for (Map.Entry<String, Integer> entry : periodMap.entrySet()) {
            Period period = DataBaseHelperFactory.getHelper().getPeriodDao().getById(entry.getKey());
            period.setCountDone(entry.getValue());
            DataBaseHelperFactory.getHelper().getPeriodDao().savePeriod(period);
        }
        for (HistoryEntity entity : entitiesWithoutPeriod) {
            Period.updatePeriodCountByServer(entity);
        }

        User user = UserPreferences.getUser(this);
        user.setMarksDoneCount(marksDoneCount);
        UserPreferences.setUser(this, user);

        if (marksDoneCount > 0) {
            Analytics.sendEvent(Analytics.CATEGORY_MARKS_DONE_COUNT, Analytics.ACTION_MARKS_DONE_COUNT_UPDATED);
        }
    }

    private void sendUserInfoToServer() {
        User user = UserPreferences.getUser(this);
        if (user.getId() != null && !user.getId().equals(PreferencesStrings.NOT_ID)) {
            user.sendUserInfoToServer();
        }
    }

    private void setUser() {
        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        Request request = new ApproveUserIdRequest(new Response.Listener<ApproveUserIdResponse>() {
            @Override
            public void onResponse(ApproveUserIdResponse response) {
                if (!response.getApproved()) {
                    registerUser();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        request.setTag(this);
        volleyQueue.add(request);
    }

    private void registerUser() {
        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        volleyQueue.cancelAll(this);
        Request request = new RegisterUserRequest(new Response.Listener<User>() {
            @Override
            public void onResponse(User response) {
                UserPreferences.setUserCredentials(UpdateUserInfoService.this, response.getId(), response.getSecret());
                Headers.setUserPreferences();
                Branch.getInstance().setIdentity(response.getId());
                FirebaseMessaging.getInstance().subscribeToTopic("user" + response.getId());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        request.setTag(this);
        volleyQueue.add(request);
    }
}
