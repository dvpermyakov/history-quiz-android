package dvpermyakov.historyquiz.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import androidx.core.app.NotificationCompat;

import java.util.Date;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.preferences.UserPreferences;

import static dvpermyakov.historyquiz.specials.DateUtils.getDateFormat;

/**
 * Created by dvpermyakov on 18.11.2016.
 */

public class Notification {
    public enum NotificationCategory {
        NEW_MARKS, WITH_COINS, OPENED_MARK;

        public static final int DEFAULT = -1;
        public static final int NEW_MARKS_CONST = 0;
        public static final int WITH_COINS_CONST = 1;
        public static final int OPENED_MARK_CONST = 2;

        public int toInt() {
            switch(this) {
                case NEW_MARKS:
                    return NEW_MARKS_CONST;
                case WITH_COINS:
                    return WITH_COINS_CONST;
                case OPENED_MARK:
                    return OPENED_MARK_CONST;
            }
            return DEFAULT;
        }

        public static NotificationCategory fromInt(int x) {
            switch(x) {
                case NEW_MARKS_CONST:
                    return NEW_MARKS;
                case WITH_COINS_CONST:
                    return WITH_COINS;
                case OPENED_MARK_CONST:
                    return OPENED_MARK;
            }
            return null;
        }
    }

    public static void send(Context context, Intent intent, String title, String text) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        UserPreferences.setLastPush(context, getDateFormat().format(new Date()));

        Analytics.sendEvent(Analytics.CATEGORY_PUSH, title);
    }
}
