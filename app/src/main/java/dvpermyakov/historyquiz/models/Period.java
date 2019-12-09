package dvpermyakov.historyquiz.models;

import android.os.Parcel;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Map;

import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.database.DataBaseStrings;
import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.network.constants.Params;
import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.requests.HistoryMarkPeriodRequest;

/**
 * Created by dvpermyakov on 20.05.2016.
 */
@DatabaseTable(tableName = DataBaseStrings.PERIOD_TABLE)
public class Period extends HistoryMark {
    @DatabaseField
    private boolean developing;
    @DatabaseField
    private int count;
    @DatabaseField
    private int countDone;

    public Period() {}

    public Period(HistoryMark mark) {
        super(mark);
    }

    public void setAppFields() {
        Period period = DataBaseHelperFactory.getHelper().getPeriodDao().getById(getId());
        setAppFields(period);
        if (period != null) {
            countDone = period.countDone;
        }
    }

    public boolean isDeveloping() {
        return developing;
    }

    public void setCountDone(int count) {
        countDone = count;
    }

    public int getCount() {
        return count;
    }

    public int getCountDone() {
        return countDone;
    }

    public void increaseCountDone() {
        countDone++;
    }

    public static void updatePeriodCountByServer(HistoryEntity mark) {
        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        Map<String, String> getParams = Params.getParams(Urls.HISTORY_MARK_PERIOD_REQUEST_URL);
        getParams.put("mark_id", mark.getId());
        getParams.put("category", String.valueOf(HistoryEntityCategory.toInt(mark.getCategory())));
        Request request = new HistoryMarkPeriodRequest(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Period period = DataBaseHelperFactory.getHelper().getPeriodDao().getById(response);
                period.increaseCountDone();
                DataBaseHelperFactory.getHelper().getPeriodDao().savePeriod(period);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Analytics.sendEvent(Analytics.CATEGORY_USER, Analytics.ACTION_USER_COUNT_FAIL);
            }
        }, getParams);
        volleyQueue.add(request);
    }

    protected Period(Parcel in) {
        super(in);
    }

    public static final Creator<Period> CREATOR = new Creator<Period>() {
        @Override
        public Period createFromParcel(Parcel in) {
            return new Period(in);
        }

        @Override
        public Period[] newArray(int size) {
            return new Period[size];
        }
    };
}
