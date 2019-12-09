package dvpermyakov.historyquiz.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import dvpermyakov.historyquiz.database.DataBaseStrings;

/**
 * Created by dvpermyakov on 27.05.2016.
 */
@DatabaseTable(tableName = DataBaseStrings.ANSWER_TABLE)
public class Answer implements Parcelable {
    @DatabaseField(canBeNull = false, id = true)
    private String id;
    @DatabaseField
    private String text;
    @DatabaseField
    private Boolean correct;
    @DatabaseField(foreign = true, columnName = DataBaseStrings.QUESTION_COLUMN)
    private Question question;

    public String getId() {
        return id;
    }
    public String getText() {
        return text;
    }
    public Boolean getCorrect() {
        return correct;
    }
    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(text);
    }

    public Answer() {}

    protected Answer(Parcel in) {
        id = in.readString();
        text = in.readString();
    }

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };
}
