package dvpermyakov.historyquiz.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import dvpermyakov.historyquiz.database.DataBaseStrings;

/**
 * Created by dvpermyakov on 04.06.2016.
 */

@DatabaseTable(tableName = DataBaseStrings.TEST_RESULT_TABLE)
public class TestResult implements Parcelable {
    @DatabaseField(generatedId = true)
    private Integer dependencyId;
    @DatabaseField(foreign = true, columnName = DataBaseStrings.TEST_COLUMN)
    private Test test;
    @DatabaseField(dataType = DataType.ENUM_INTEGER, columnName = DataBaseStrings.TEST_RESULT)
    private TestResultCategory result;
    @DatabaseField(canBeNull = false, columnName = DataBaseStrings.QUESTION_NUMBER)
    private int questionNumber;
    private int mistakeNumber;
    private int secondNumber;
    @DatabaseField(columnName = DataBaseStrings.TEST_RESULT_CREATED_COLUMN)
    private Date createdDate;

    public TestResult() {}

    public TestResult(Test test, TestResultCategory result, int questionNumber, int mistakeNumber, int secondNumber) {
        this.test = test;
        this.result = result;
        this.questionNumber = questionNumber;
        this.mistakeNumber = mistakeNumber;
        this.secondNumber = secondNumber;
        createdDate = new Date();
    }

    public TestResultCategory getResultCategory() {
        return result;
    }
    public int getQuestionNumber() {
        return questionNumber;
    }
    public int getMistakeNumber() {
        return mistakeNumber;
    }
    public int getSecondNumber() {
        return secondNumber;
    }

    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(questionNumber);
        dest.writeInt(mistakeNumber);
        dest.writeInt(secondNumber);
        dest.writeInt(TestResultCategory.toInt(result));
    }
    protected TestResult(Parcel in) {
        questionNumber = in.readInt();
        mistakeNumber = in.readInt();
        secondNumber = in.readInt();
        result = TestResultCategory.fromInt(in.readInt());
    }

    public static final Creator<TestResult> CREATOR = new Creator<TestResult>() {
        @Override
        public TestResult createFromParcel(Parcel in) {
            return new TestResult(in);
        }

        @Override
        public TestResult[] newArray(int size) {
            return new TestResult[size];
        }
    };
}
