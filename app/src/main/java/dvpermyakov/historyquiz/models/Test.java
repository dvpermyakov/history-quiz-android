package dvpermyakov.historyquiz.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Map;

import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.database.DataBaseStrings;
import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.network.constants.Params;
import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.requests.TestDoneRequest;
import dvpermyakov.historyquiz.network.responses.SuccessResponse;

/**
 * Created by dvpermyakov on 27.05.2016.
 */

@DatabaseTable(tableName = DataBaseStrings.TEST_TABLE)
public class Test implements Parcelable {
    @DatabaseField(canBeNull = false, id = true)
    private String id;
    @DatabaseField
    @SerializedName("max_questions")
    private int questionAmount;
    @DatabaseField
    @SerializedName("max_mistakes")
    private int maxMistakes;
    @DatabaseField
    @SerializedName("max_seconds")
    private int maxSeconds;
    @DatabaseField(columnName = DataBaseStrings.NEED_SEND_TO_SERVER)
    private boolean needSendToServer = false;
    @DatabaseField(columnName = DataBaseStrings.NEED_SEND_TO_PLAY_SERVICE)
    private boolean needSendToPlayService = false;

    public Test() {}

    public String getId() {
        return id;
    }
    public int getQuestionAmount() {
        return questionAmount;
    }
    public int getMaxMistakes() {
        return maxMistakes;
    }
    public int getMaxSeconds() {
        return maxSeconds;
    }

    public void setAppFields() {
        Test test = DataBaseHelperFactory.getHelper().getTestDao().getById(getId());
        if (test != null) {
            needSendToServer = test.needSendToServer;
            needSendToPlayService = test.needSendToPlayService;
        }
    }

    public void sendDoneToServer() {
        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        Map<String, String> getParams = Params.getParams(Urls.TEST_DONE_URL);
        getParams.put("test_id", getId());
        getParams.put("attempts", String.valueOf(DataBaseHelperFactory.getHelper().getTestResultDao().getAttempts(this)));
        Request request = new TestDoneRequest(new Response.Listener<SuccessResponse>() {
            @Override
            public void onResponse(SuccessResponse response) {
                if (needSendToServer) {
                    needSendToServer = false;
                    DataBaseHelperFactory.getHelper().getTestDao().save(Test.this);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                needSendToServer = true;
                DataBaseHelperFactory.getHelper().getTestDao().save(Test.this);
            }
        }, getParams);
        volleyQueue.add(request);
    }

    public void saveNeedSendToPlayService(boolean need) {
        needSendToPlayService = need;
        DataBaseHelperFactory.getHelper().getTestDao().save(Test.this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(questionAmount);
        dest.writeInt(maxMistakes);
        dest.writeInt(maxSeconds);
    }

    protected Test(Parcel in) {
        id = in.readString();
        questionAmount = in.readInt();
        maxMistakes = in.readInt();
        maxSeconds = in.readInt();
    }

    public static final Creator<Test> CREATOR = new Creator<Test>() {
        @Override
        public Test createFromParcel(Parcel in) {
            return new Test(in);
        }

        @Override
        public Test[] newArray(int size) {
            return new Test[size];
        }
    };
}
