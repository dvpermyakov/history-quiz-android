package dvpermyakov.historyquiz.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import dvpermyakov.historyquiz.database.DataBaseStrings;

/**
 * Created by dvpermyakov on 03.06.2016.
 */
@DatabaseTable(tableName = DataBaseStrings.DIGNITY_TABLE)
public class Dignity implements Parcelable {
    @DatabaseField(generatedId = true)
    private Integer dependencyId;
    @DatabaseField
    private String name;
    @DatabaseField
    @SerializedName("year_title")
    private String year;
    @DatabaseField(foreign = true, columnName = DataBaseStrings.PERSON_COLUMN)
    private Person person;

    public String getName() {
        return name;
    }
    public String getYear() {
        return year;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(year);
    }

    public Dignity() {}

    protected Dignity(Parcel in) {
        name = in.readString();
        year = in.readString();
    }

    public static final Creator<Dignity> CREATOR = new Creator<Dignity>() {
        @Override
        public Dignity createFromParcel(Parcel in) {
            return new Dignity(in);
        }

        @Override
        public Dignity[] newArray(int size) {
            return new Dignity[size];
        }
    };
}
