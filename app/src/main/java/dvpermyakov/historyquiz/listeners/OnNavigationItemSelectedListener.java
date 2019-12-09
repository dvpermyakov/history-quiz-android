package dvpermyakov.historyquiz.listeners;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.MenuItem;

import dvpermyakov.historyquiz.ExternalConstants;
import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.activities.AboutActivity;
import dvpermyakov.historyquiz.activities.BalanceActivity;
import dvpermyakov.historyquiz.activities.HistoryPeriodsActivity;
import dvpermyakov.historyquiz.activities.FilteredHistoryMarksActivity;
import dvpermyakov.historyquiz.activities.PlayServiceGameLoginActivity;
import dvpermyakov.historyquiz.activities.RatingActivity;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.managers.PlayServiceGameManager;
import dvpermyakov.historyquiz.managers.PlayServiceGameManagerFactory;
import dvpermyakov.historyquiz.preferences.UserPreferences;
import dvpermyakov.historyquiz.specials.IntentStrings;

import static dvpermyakov.historyquiz.activities.InstructionsActivity.RC_LOGIN;

/**
 * Created by dvpermyakov on 17.09.2016.
 */
public class OnNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {
    public enum ActivityCategory {
        PERIODS, OPENED_MARKS, DONE_MARKS, BALANCE, NEW_MARKS, ABOUT, RATING, ACHIEVEMENT;

        public static final int DEFAULT = -1;
        public static final int PERIODS_CONST = 0;
        public static final int OPENED_MARKS_CONST = 1;
        public static final int DONE_MARKS_CONST = 2;
        public static final int BALANCE_CONST = 3;
        public static final int NEW_MARKS_CONST = 4;
        public static final int ABOUT_CONST = 5;
        public static final int RATING_CONST = 6;
        public static final int ACHIEVEMENT_CONST = 7;

        public static ActivityCategory fromInt (int x) {
            switch(x) {
                case PERIODS_CONST:
                    return PERIODS;
                case OPENED_MARKS_CONST:
                    return OPENED_MARKS;
                case DONE_MARKS_CONST:
                    return DONE_MARKS;
                case BALANCE_CONST:
                    return BALANCE;
                case NEW_MARKS_CONST:
                    return NEW_MARKS;
                case ABOUT_CONST:
                    return ABOUT;
                case RATING_CONST:
                    return RATING;
                case ACHIEVEMENT_CONST:
                    return ACHIEVEMENT;
            }
            return null;
        }

        public static int toInt (ActivityCategory category) {
            switch(category) {
                case PERIODS:
                    return PERIODS_CONST;
                case OPENED_MARKS:
                    return OPENED_MARKS_CONST;
                case DONE_MARKS:
                    return DONE_MARKS_CONST;
                case NEW_MARKS:
                    return NEW_MARKS_CONST;
                case ABOUT:
                    return ABOUT_CONST;
                case RATING:
                    return RATING_CONST;
                case ACHIEVEMENT:
                    return ACHIEVEMENT_CONST;
            }
            return DEFAULT;
        }

        public String toString(Context context) {
            switch (this) {
                case PERIODS:
                    return context.getResources().getString(R.string.menu_navigation_periods);
                case OPENED_MARKS:
                    return context.getResources().getString(R.string.menu_navigation_opened);
                case DONE_MARKS:
                    return context.getResources().getString(R.string.menu_navigation_done);
                case BALANCE:
                    return context.getResources().getString(R.string.menu_navigation_balance);
                case NEW_MARKS:
                    return context.getResources().getString(R.string.menu_navigation_new);
                case ABOUT:
                    return context.getResources().getString(R.string.menu_navigation_instructions);
                case RATING:
                    return context.getResources().getString(R.string.menu_navigation_rating);
                case ACHIEVEMENT:
                    return context.getResources().getString(R.string.menu_navigation_achievement);
            }
            return context.getResources().getString(R.string.title_filtered_history_marks);
        }
    }

    private Activity activity;
    private ActivityCategory category;
    private DrawerLayout drawerLayout;

    public OnNavigationItemSelectedListener(Activity activity, ActivityCategory category, DrawerLayout drawerLayout) {
        this.activity = activity;
        this.category = category;
        this.drawerLayout = drawerLayout;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.periods:
                if (category != ActivityCategory.PERIODS) {
                    activity.startActivity(new Intent(activity, HistoryPeriodsActivity.class));
                    activity.finish();
                } else {
                    drawerLayout.closeDrawers();
                }
                Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + activity.getResources().getString(R.string.app_name), Analytics.ACTION_PERIODS);
                break;
            case R.id.news:
                if (category != ActivityCategory.NEW_MARKS) {
                    activity.startActivity(new Intent(activity, FilteredHistoryMarksActivity.class)
                            .putExtra(IntentStrings.INTENT_FILTERED_MARKS_PARAM, ActivityCategory.toInt(ActivityCategory.NEW_MARKS)));
                    activity.finish();
                } else {
                    drawerLayout.closeDrawers();
                }
                Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + activity.getResources().getString(R.string.app_name), Analytics.ACTION_NEW_HISTORY_MARKS);
                break;
            case R.id.opened:
                if (category != ActivityCategory.OPENED_MARKS) {
                    activity.startActivity(new Intent(activity, FilteredHistoryMarksActivity.class)
                            .putExtra(IntentStrings.INTENT_FILTERED_MARKS_PARAM, ActivityCategory.toInt(ActivityCategory.OPENED_MARKS)));
                    activity.finish();
                } else {
                    drawerLayout.closeDrawers();
                }
                Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + activity.getResources().getString(R.string.app_name), Analytics.ACTION_OPENED_HISTORY_MARKS);
                break;
            case R.id.done:
                if (category != ActivityCategory.DONE_MARKS) {
                    activity.startActivity(new Intent(activity, FilteredHistoryMarksActivity.class)
                            .putExtra(IntentStrings.INTENT_FILTERED_MARKS_PARAM, ActivityCategory.toInt(ActivityCategory.DONE_MARKS)));
                    activity.finish();
                } else {
                    drawerLayout.closeDrawers();
                }
                Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + activity.getResources().getString(R.string.app_name), Analytics.ACTION_DONE_HISTORY_MARKS);
                break;
            case R.id.rating:
                if (category != ActivityCategory.RATING) {
                    activity.startActivity(new Intent(activity, RatingActivity.class));
                    activity.finish();
                } else {
                    drawerLayout.closeDrawers();
                }
                Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + activity.getResources().getString(R.string.app_name), Analytics.ACTION_RATING);
                break;
            case R.id.achievement:
                if (category != ActivityCategory.ACHIEVEMENT) {
                    PlayServiceGameManager manager = PlayServiceGameManagerFactory.getManager();
                    if (manager.isConnected()) {
                        activity.startActivityForResult(manager.getAchievementsActivityIntent(), 1);
                    } else {
                        activity.startActivityForResult(new Intent(activity, PlayServiceGameLoginActivity.class), RC_LOGIN);
                    }
                } else {
                    drawerLayout.closeDrawers();
                }
                Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + activity.getResources().getString(R.string.app_name), Analytics.ACTION_ACHIEVEMENT);
                break;
            case R.id.balance:
                if (category != ActivityCategory.BALANCE) {
                    activity.startActivity(new Intent(activity, BalanceActivity.class)
                            .putExtra(IntentStrings.INTENT_WITH_NAVIGATION_VIEW_PARAM, true));
                    activity.finish();
                } else {
                    drawerLayout.closeDrawers();
                }
                Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + activity.getResources().getString(R.string.app_name), Analytics.ACTION_BALANCE);
                break;
            case R.id.rate:
                if (!ExternalConstants.ANDROID_MARKET_URL.isEmpty()) {
                    UserPreferences.setShowRateApp(activity, false);
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ExternalConstants.ANDROID_MARKET_URL)));
                }
                Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + activity.getResources().getString(R.string.app_name), Analytics.ACTION_RATE);
                break;
            case R.id.instructions:
                activity.startActivity(new Intent(activity, AboutActivity.class));
                Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + activity.getResources().getString(R.string.app_name), Analytics.ACTION_INSTRUCTION);
                break;
        }
        return true;
    }
}
