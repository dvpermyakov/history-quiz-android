package dvpermyakov.historyquiz.models;

import android.content.Context;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.database.DataBaseStrings;

/**
 * Created by dvpermyakov on 19.09.2016.
 */
@DatabaseTable(tableName = DataBaseStrings.COINS_TRANSACTION_TABLE)
public class CoinsTransaction {
    public enum CoinsTransactionCategory {
        START_REWARD, DAILY_REWARD, OPEN_HISTORY_MARK, DONE_TEST, CONTINUE_TEST,
        BUY_HISTORY_MARK, BUY_TEST_CONDITIONS, INTERSTITIAL_AD, INTERSTITIAL_VIDEO_AD,
        SHARE_TEST_RESULT, READ_HISTORY_MARK, PUSH_REWARD, REFERRAL_REWARD, VK_GROUP_REWARD,
        RATE_REWARD;

        public int getValue () {
            switch(this) {
                case START_REWARD:
                    return 50;
                case DAILY_REWARD:
                    return 30;
                case OPEN_HISTORY_MARK:
                    return 5;
                case DONE_TEST:
                    return 30;
                case INTERSTITIAL_AD:
                    return 20;
                case INTERSTITIAL_VIDEO_AD:
                    return 90;
                case SHARE_TEST_RESULT:
                    return 90;
                case READ_HISTORY_MARK:
                    return 5;
                case BUY_HISTORY_MARK:
                    return -10;
                case BUY_TEST_CONDITIONS:
                    return -20;
                case CONTINUE_TEST:
                    return -30;
                case PUSH_REWARD:
                    return 100;
                case REFERRAL_REWARD:
                    return 200;
                case VK_GROUP_REWARD:
                    return 90;
                case RATE_REWARD:
                    return 30;
            }
            return 0;
        }

        public String getString(Context context) {
            switch(this) {
                case START_REWARD:
                    return context.getResources().getString(R.string.get_way_start);
                case DAILY_REWARD:
                    return context.getResources().getString(R.string.get_way_day);
                case OPEN_HISTORY_MARK:
                    return context.getResources().getString(R.string.get_way_open_mark);
                case DONE_TEST:
                    return context.getResources().getString(R.string.get_way_done_test);
                case INTERSTITIAL_AD:
                    return context.getResources().getString(R.string.get_way_interstitial_ad);
                case INTERSTITIAL_VIDEO_AD:
                    return context.getResources().getString(R.string.get_way_interstitial_video_ad);
                case BUY_HISTORY_MARK:
                    return context.getResources().getString(R.string.buy_way_open_mark);
                case BUY_TEST_CONDITIONS:
                    return context.getResources().getString(R.string.buy_way_open_test);
                case CONTINUE_TEST:
                    return context.getResources().getString(R.string.buy_way_continue_test);
                case SHARE_TEST_RESULT:
                    return context.getResources().getString(R.string.get_way_share_test_result);
                case READ_HISTORY_MARK:
                    return context.getResources().getString(R.string.get_way_read_mark);
                case PUSH_REWARD:
                    return context.getResources().getString(R.string.get_way_push);
                case REFERRAL_REWARD:
                    return context.getResources().getString(R.string.get_referral);
                case VK_GROUP_REWARD:
                    return context.getResources().getString(R.string.get_vk_group);
                case RATE_REWARD:
                    return context.getResources().getString(R.string.get_rate);
            }
            return "";
        }

    }

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private CoinsTransactionCategory category;
    @DatabaseField(columnName = DataBaseStrings.COINS_TRANSACTION_CREATED_COLUMN)
    private Date created;
    @DatabaseField(columnName = DataBaseStrings.COINS_TRANSACTION_VALUE_COLUMN)
    private int value;

    public CoinsTransaction() {}

    public CoinsTransaction(CoinsTransactionCategory category) {
        this.category = category;
        created = new Date();
        value = category.getValue();
    }

    public CoinsTransactionCategory getCategory() {
        return category;
    }

    public int getValue() {
        return value;
    }
}
