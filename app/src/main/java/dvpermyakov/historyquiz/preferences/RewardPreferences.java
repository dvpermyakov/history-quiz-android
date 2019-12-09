package dvpermyakov.historyquiz.preferences;

import android.content.Context;

import dvpermyakov.historyquiz.specials.DateUtils;

/**
 * Created by dvpermyakov on 26.11.2016.
 */

public class RewardPreferences extends AbstractPreferences {
    public static boolean getFirstCoinsReward(Context context) {
        return getPreferences(context).getBoolean(PreferencesStrings.FIRST_COINS_REWARD, PreferencesStrings.FIRST_COINS_REWARD_DEFAULT);
    }

    public static boolean setFirstCoinsReward(Context context, boolean firstCoinsReward) {
        return getEditor(context).putBoolean(PreferencesStrings.FIRST_COINS_REWARD, firstCoinsReward).commit();
    }

    public static String getLastDailyCoinsReward(Context context) {
        return getPreferences(context).getString(PreferencesStrings.LAST_DAILY_COINS_REWARD, DateUtils.defaultDateString);
    }

    public static boolean setLastDailyCoinsReward(Context context, String lastDay) {
        return getEditor(context).putString(PreferencesStrings.LAST_DAILY_COINS_REWARD, lastDay).commit();
    }

    public static boolean getFirstDoneTestCoinsReward(Context context) {
        return getPreferences(context).getBoolean(PreferencesStrings.FIRST_DONE_TEST_COINS_REWARD, PreferencesStrings.FIRST_DONE_TEST_COINS_REWARD_DEFAULT);
    }

    public static boolean setFirstDoneTestCoinsReward(Context context, boolean firstCoinsReward) {
        return getEditor(context).putBoolean(PreferencesStrings.FIRST_DONE_TEST_COINS_REWARD, firstCoinsReward).commit();
    }

    public static boolean getFirstOpenCoinsReward(Context context) {
        return getPreferences(context).getBoolean(PreferencesStrings.FIRST_OPEN_COINS_REWARD, PreferencesStrings.FIRST_OPEN_COINS_REWARD_DEFAULT);
    }

    public static boolean setFirstOpenCoinsReward(Context context, boolean firstCoinsReward) {
        return getEditor(context).putBoolean(PreferencesStrings.FIRST_OPEN_COINS_REWARD, firstCoinsReward).commit();
    }
}
