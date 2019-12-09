package dvpermyakov.historyquiz.managers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;

import java.util.List;

import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.Period;
import dvpermyakov.historyquiz.models.Person;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.specials.LogTag;

/**
 * Created by dvpermyakov on 04.02.17.
 */

public class PlayServiceGameManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String READ_TEXT_EVENT_ID = "CgkI0ba65_4REAIQAg";
    private static final String READ_TEXT_5_ACHIEVEMENT_ID = "CgkI0ba65_4REAIQBQ";
    private static final String READ_TEXT_10_ACHIEVEMENT_ID = "CgkI0ba65_4REAIQBg";
    private static final String READ_TEXT_20_ACHIEVEMENT_ID = "CgkI0ba65_4REAIQBw";
    private static final String READ_TEXT_50_ACHIEVEMENT_ID = "CgkI0ba65_4REAIQCA";

    private static final String TEST_DONE_EVENT_ID = "CgkI0ba65_4REAIQDQ";
    private static final String TEST_DONE_5_ACHIEVEMENT_ID = "CgkI0ba65_4REAIQCQ";
    private static final String TEST_DONE_10_ACHIEVEMENT_ID = "CgkI0ba65_4REAIQCg";
    private static final String TEST_DONE_20_ACHIEVEMENT_ID = "CgkI0ba65_4REAIQCw";
    private static final String TEST_DONE_50_ACHIEVEMENT_ID = "CgkI0ba65_4REAIQDA";
    private static final String TEST_DONE_100_ACHIEVEMENT_ID = "CgkI0ba65_4REAIQDg";

    private GoogleApiClient googleApiClient;
    private ConnectionResult connectionResult;

    public PlayServiceGameManager(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        connect();
    }

    public void connect() {
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    public boolean isConnected() {
        return googleApiClient.isConnected();
    }

    public void sendReadEvent() {
        if (googleApiClient.isConnected()) {
            Log.i(LogTag.TAG_PLAY_SERVICE, "send reading event");
            Games.Events.increment(googleApiClient, READ_TEXT_EVENT_ID, 1);
            Games.Achievements.increment(googleApiClient, READ_TEXT_5_ACHIEVEMENT_ID, 1);
            Games.Achievements.increment(googleApiClient, READ_TEXT_10_ACHIEVEMENT_ID, 1);
            Games.Achievements.increment(googleApiClient, READ_TEXT_20_ACHIEVEMENT_ID, 1);
            Games.Achievements.increment(googleApiClient, READ_TEXT_50_ACHIEVEMENT_ID, 1);
        }
    }

    public void sendTestDoneEvent() {
        if (googleApiClient.isConnected()) {
            Log.i(LogTag.TAG_PLAY_SERVICE, "send test done event");
            Games.Events.increment(googleApiClient, TEST_DONE_EVENT_ID, 1);
            Games.Achievements.increment(googleApiClient, TEST_DONE_5_ACHIEVEMENT_ID, 1);
            Games.Achievements.increment(googleApiClient, TEST_DONE_10_ACHIEVEMENT_ID, 1);
            Games.Achievements.increment(googleApiClient, TEST_DONE_20_ACHIEVEMENT_ID, 1);
            Games.Achievements.increment(googleApiClient, TEST_DONE_50_ACHIEVEMENT_ID, 1);
            Games.Achievements.increment(googleApiClient, TEST_DONE_100_ACHIEVEMENT_ID, 1);
        }
    }

    public Intent getAchievementsActivityIntent() {
        return Games.Achievements.getAchievementsIntent(googleApiClient);
    }

    public Player getPlayer() {
        if (googleApiClient.isConnected()) {
            return Games.Players.getCurrentPlayer(googleApiClient);
        } else {
            return null;
        }
    }

    public void updateTestDone() {
        if (isConnected()) {
            List<Test> tests = DataBaseHelperFactory.getHelper().getTestDao().getTestsWithNeedSendToPlayService();
            for (Test test : tests) {
                sendTestDoneEvent();
                test.saveNeedSendToPlayService(false);
            }
        }
    }

    public void updateReadArticle() {
        if (isConnected()) {
            List<Period> periods = DataBaseHelperFactory.getHelper().getPeriodDao().getPeriodsToSendPlayService();
            for (Period period : periods) {
                sendReadEvent();
                period.setNeedSendToPlayService(false);
                DataBaseHelperFactory.getHelper().getPeriodDao().savePeriod(period);
            }
            List<Event> events = DataBaseHelperFactory.getHelper().getEventDao().getEventsToSendPlayService();
            for (Event event : events) {
                sendReadEvent();
                event.setNeedSendToPlayService(false);
                DataBaseHelperFactory.getHelper().getEventDao().saveEvent(event);
            }
            List<Person> persons = DataBaseHelperFactory.getHelper().getPersonDao().getPersonsToSendPlayService();
            for (Person person : persons) {
                sendReadEvent();
                person.setNeedSendToPlayService(false);
                DataBaseHelperFactory.getHelper().getPersonDao().savePerson(person);
            }
        }
    }

    public ConnectionResult getConnectionResult() {
        return connectionResult;
    }

    public void disconnect() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LogTag.TAG_PLAY_SERVICE, "connection of api client is " + googleApiClient.isConnected());
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LogTag.TAG_PLAY_SERVICE, "connection of api client is " + googleApiClient.isConnected() + ". " +
                "Error = " + connectionResult.toString());
        this.connectionResult = connectionResult;
    }
}
