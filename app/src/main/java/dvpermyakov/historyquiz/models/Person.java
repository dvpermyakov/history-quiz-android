package dvpermyakov.historyquiz.models;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.database.DataBaseStrings;

/**
 * Created by dvpermyakov on 26.05.2016.
 */
@DatabaseTable(tableName = DataBaseStrings.PERSON_TABLE)
public class Person extends HistoryMark {
    @ForeignCollectionField
    @SerializedName("person_titles")
    private Collection<Dignity> dignities;

    public List<Dignity> getDignities() {
        return new ArrayList<>(dignities);
    }

    public void setAppFields() {
        setAppFields(DataBaseHelperFactory.getHelper().getPersonDao().getById(getId()));
    }

    public Person() {}

    public Person(HistoryMark mark) {
        super(mark);
    }

    protected Person(Parcel in) {
        super(in);
        dignities = new ArrayList<>();
        in.readTypedList((List<Dignity>)dignities, Dignity.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(new ArrayList<>(dignities));
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}
