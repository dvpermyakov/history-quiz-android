package dvpermyakov.historyquiz.preferences;

import android.content.Context;

import dvpermyakov.historyquiz.models.User;
import dvpermyakov.historyquiz.specials.DateUtils;

/**
 * Created by dvpermyakov on 27.05.2016.
 */
public class UserPreferences extends AbstractPreferences {
    public static User getUser(Context context) {
        String id = getPreferences(context).getString(PreferencesStrings.USER_ID, PreferencesStrings.NOT_ID);
        String secret = getPreferences(context).getString(PreferencesStrings.USER_SECRET, "");
        String name = getPreferences(context).getString(PreferencesStrings.USER_NAME, PreferencesStrings.USER_NAME_DEFAULT);
        User.Gender gender = User.Gender.fromInt(getPreferences(context).getInt(PreferencesStrings.USER_GENDER, PreferencesStrings.USER_GENDER_DEFAULT));
        int marksDoneCount = getPreferences(context).getInt(PreferencesStrings.USER_MARKS_COUNT, PreferencesStrings.USER_MARKS_COUNT_DEFAULT);
        String image = getPreferences(context).getString(PreferencesStrings.USER_IMAGE, PreferencesStrings.USER_IMAGE_DEFAULT);
        String email = getPreferences(context).getString(PreferencesStrings.USER_EMAIL, PreferencesStrings.USER_EMAIL_DEFAULT);
        boolean isMember = getPreferences(context).getBoolean(PreferencesStrings.USER_IS_MEMBER, PreferencesStrings.USER_IS_MEMBER_DEFAULT);
        return new User(id, secret, name, gender, marksDoneCount, image, email, isMember);
    }

    public static boolean setUser(Context context, User user) {
        boolean result = true;
        if (user.getName() != null) result = getEditor(context).putString(PreferencesStrings.USER_NAME, user.getName()).commit();
        if (user.getGender() != null) result &= getEditor(context).putInt(PreferencesStrings.USER_GENDER, User.Gender.toInt(user.getGender())).commit();
        result &= getEditor(context).putInt(PreferencesStrings.USER_MARKS_COUNT, user.getMarksDoneCount()).commit();
        if (user.getImage() != null) result &= getEditor(context).putString(PreferencesStrings.USER_IMAGE, user.getImage()).commit();
        if (user.getEmail() != null) result &= getEditor(context).putString(PreferencesStrings.USER_EMAIL, user.getEmail()).commit();
        result &= getEditor(context).putBoolean(PreferencesStrings.USER_IS_MEMBER, user.isVkGroupMember()).commit();
        return result;
    }

    public static boolean setUserCredentials(Context context, String id, String secret) {
        boolean result = true;
        if (id != null) result = getEditor(context).putString(PreferencesStrings.USER_ID, id).commit();
        if (secret != null) result &= getEditor(context).putString(PreferencesStrings.USER_SECRET, secret).commit();
        return result;
    }

    public static int getEnterAppCount(Context context) {
        return getPreferences(context).getInt(PreferencesStrings.ENTER_APP_COUNT, PreferencesStrings.ENTER_APP_COUNT_DEFAULT);
    }

    public static boolean setEnterAppCount(Context context, int count) {
        return getEditor(context).putInt(PreferencesStrings.ENTER_APP_COUNT, count).commit();
    }

    public static boolean getShowRateApp(Context context) {
        return getPreferences(context).getBoolean(PreferencesStrings.SHOW_RATE_APP, PreferencesStrings.SHOW_RATE_APP_DEFAULT);
    }

    public static boolean setShowRateApp(Context context, boolean rateApp) {
        return getEditor(context).putBoolean(PreferencesStrings.SHOW_RATE_APP, rateApp).commit();
    }

    public static String getPeriodLastUpdate(Context context) {
        return getPreferences(context).getString(PreferencesStrings.PERIOD_LAST_UPDATE, PreferencesStrings.NOT_UPDATE);
    }

    public static boolean setPeriodLastUpdate(Context context, String lastUpdate) {
        return getEditor(context).putString(PreferencesStrings.PERIOD_LAST_UPDATE, lastUpdate).commit();
    }

    public static boolean isPeriodsCountUpdated(Context context) {
        return getPreferences(context).getBoolean(PreferencesStrings.PERIODS_COUNT_UPDATE, PreferencesStrings.PERIODS_COUNT_UPDATE_DEFAULT);
    }

    public static boolean setPeriodsCountUpdated(Context context, boolean countUpdated) {
        return getEditor(context).putBoolean(PreferencesStrings.PERIODS_COUNT_UPDATE, countUpdated).commit();
    }

    public static boolean getInstructionsDone(Context context) {
        return getPreferences(context).getBoolean(PreferencesStrings.INSTRUCTIONS_DONE, PreferencesStrings.INSTRUCTIONS_DONE_DEFAULT);
    }

    public static boolean setInstructionsDone(Context context, boolean instructionDone) {
        return getEditor(context).putBoolean(PreferencesStrings.INSTRUCTIONS_DONE, instructionDone).commit();
    }

    public static boolean getCopyrightDialogDone(Context context) {
        return getPreferences(context).getBoolean(PreferencesStrings.COPYRIGHT_DONE, PreferencesStrings.COPYRIGHT_DONE_DEFAULT);
    }

    public static boolean setCopyrightDialogDone(Context context, boolean copyrightDone) {
        return getEditor(context).putBoolean(PreferencesStrings.COPYRIGHT_DONE, copyrightDone).commit();
    }

    public static int getContinueTestNumber(Context context) {
        return getPreferences(context).getInt(PreferencesStrings.CONTINUE_TEST, PreferencesStrings.CONTINUE_TEST_DEFAULT);
    }

    public static boolean setContinueTestNumber(Context context, int continueTestNumber) {
        return getEditor(context).putInt(PreferencesStrings.CONTINUE_TEST, continueTestNumber).commit();
    }

    public static String getLastPush(Context context) {
        return getPreferences(context).getString(PreferencesStrings.LAST_PUSH, DateUtils.defaultDateString);
    }

    public static boolean setLastPush(Context context, String lastDay) {
        return getEditor(context).putString(PreferencesStrings.LAST_PUSH, lastDay).commit();
    }

    public static boolean getShareAdvice(Context context) {
        return getPreferences(context).getBoolean(PreferencesStrings.SHARE_ADVICE, PreferencesStrings.SHARE_ADVICE_DEFAULT);
    }

    public static boolean setShareAdvice(Context context, boolean shareAdvice) {
        return getEditor(context).putBoolean(PreferencesStrings.SHARE_ADVICE, shareAdvice).commit();
    }

    public static boolean getInitPlayGameService(Context context) {
        return getPreferences(context).getBoolean(PreferencesStrings.INIT_PLAY_GAME_SERVICE, PreferencesStrings.INIT_PLAY_GAME_SERVICE_DEFAULT);
    }

    public static boolean setInitPlayGameService(Context context, boolean shareAdvice) {
        return getEditor(context).putBoolean(PreferencesStrings.INIT_PLAY_GAME_SERVICE, shareAdvice).commit();
    }
}
