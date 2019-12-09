package dvpermyakov.historyquiz.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import dvpermyakov.historyquiz.database.DataBaseStrings;

/**
 * Created by dvpermyakov on 27.05.2016.
 */
@DatabaseTable(tableName = DataBaseStrings.QUESTION_TABLE)
public class Question implements Parcelable {
    @DatabaseField(canBeNull = false, id = true)
    private String id;
    @DatabaseField
    private String text;
    @ForeignCollectionField(eager = false)
    private Collection<Answer> answers;
    @DatabaseField(foreign = true, columnName = DataBaseStrings.TEST_COLUMN)
    private Test test;

    public String getId() {
        return id;
    }
    public String getText() {
        return text;
    }
    public List<Answer> getAnswers() {
        return new ArrayList<>(answers);
    }
    public void setTest(Test test) {
        this.test = test;
    }

    public List<Answer> getRandomAnswer(int count) {
        Answer correct = null;
        List<Answer> answers = new ArrayList<>(this.answers);
        for (Answer answer : answers) {
            if (answer.getCorrect()) {
                correct = answer;
                break;
            }
        }
        answers.remove(correct);
        Collections.shuffle(answers, new Random(System.nanoTime()));
        answers = answers.subList(0, count - 1);
        if (correct != null) {
            answers.add(correct);
        }
        Collections.shuffle(answers, new Random(System.nanoTime()));
        return answers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(text);
        dest.writeTypedList(new ArrayList<>(answers));
    }

    public Question() {}

    protected Question(Parcel in) {
        id = in.readString();
        text = in.readString();
        answers = in.createTypedArrayList(Answer.CREATOR);
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}
