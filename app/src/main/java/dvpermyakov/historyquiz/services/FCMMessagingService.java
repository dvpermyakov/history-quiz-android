package dvpermyakov.historyquiz.services;

import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dvpermyakov.historyquiz.activities.FilteredHistoryMarksActivity;
import dvpermyakov.historyquiz.activities.HistoryMarkActivity;
import dvpermyakov.historyquiz.activities.HistoryPeriodsActivity;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.listeners.OnNavigationItemSelectedListener;
import dvpermyakov.historyquiz.models.HistoryMark;
import dvpermyakov.historyquiz.notifications.Notification;
import dvpermyakov.historyquiz.preferences.RewardPreferences;
import dvpermyakov.historyquiz.preferences.UserPreferences;
import dvpermyakov.historyquiz.specials.IntentStrings;

import static dvpermyakov.historyquiz.specials.DateUtils.isAfter;

/**
 * Created by dvpermyakov on 17.11.2016.
 */

public class FCMMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        if (data != null) {
            String title = data.get("title");
            String text = data.get("text");
            Notification.NotificationCategory category = Notification.NotificationCategory.fromInt(
                    Integer.parseInt(data.get("category")));

            Intent intent = null;
            if (category == Notification.NotificationCategory.NEW_MARKS) {
                intent = new Intent(this, FilteredHistoryMarksActivity.class)
                        .putExtra(IntentStrings.INTENT_FROM_PUSH_PARAM, true)
                        .putExtra(IntentStrings.INTENT_FILTERED_MARKS_PARAM,
                                OnNavigationItemSelectedListener.ActivityCategory.toInt(OnNavigationItemSelectedListener.ActivityCategory.NEW_MARKS));
            }
            else if (category == Notification.NotificationCategory.WITH_COINS) {
                int balance = DataBaseHelperFactory.getHelper().getCoinsTransactionDao().getCoinsBalance();
                if (balance >= 50) return;
                intent = new Intent(this, HistoryPeriodsActivity.class)
                        .putExtra(IntentStrings.INTENT_FROM_PUSH_PARAM, true)
                        .putExtra(IntentStrings.INTENT_PUSH_WITH_COINS_PARAM, true);
            }
            else if (category == Notification.NotificationCategory.OPENED_MARK) {
                List<HistoryMark> marks = new ArrayList<>();
                marks.addAll(DataBaseHelperFactory.getHelper().getEventDao().getOpenedEvents());
                marks.addAll(DataBaseHelperFactory.getHelper().getPersonDao().getOpenedPersons());
                HistoryMark suitableMark = null;
                for (HistoryMark mark : marks) {
                    boolean testClosed = DataBaseHelperFactory.getHelper().getTestResultDao().isTestClosed(mark.getTest());
                    if (!testClosed && mark.isReadDone()) {
                        intent = new Intent(this, HistoryMarkActivity.class)
                                .putExtra(IntentStrings.INTENT_FROM_PUSH_PARAM, true)
                                .putExtra(IntentStrings.INTENT_HISTORY_MARK_PARAM, mark);
                        suitableMark = mark;
                        break;
                    }
                }
                if (suitableMark == null) {
                    return;
                }
                else {
                    title = "Продолжить статью " + suitableMark.getName() + "?";
                    text = "Нажмите, чтобы начать с " + suitableMark.getName() + ".";
                }
            }
            else {
                intent = new Intent(this, HistoryPeriodsActivity.class);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            if (isAfter(RewardPreferences.getLastDailyCoinsReward(this), 3)
                    && isAfter(UserPreferences.getLastPush(this), 3)) {
                Notification.send(this, intent, title, text);
            }
        }
    }
}
