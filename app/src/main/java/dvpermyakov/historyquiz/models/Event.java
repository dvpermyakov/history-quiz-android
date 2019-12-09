package dvpermyakov.historyquiz.models;


import android.os.Parcel;

import com.j256.ormlite.table.DatabaseTable;

import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.database.DataBaseStrings;

/**
 * Created by dvpermyakov on 22.05.2016.
 */

@DatabaseTable(tableName = DataBaseStrings.EVENT_TABLE)
public class Event extends HistoryMark {
    public Event() {}

    public void setAppFields() {
        setAppFields(DataBaseHelperFactory.getHelper().getEventDao().getById(getId()));
    }

    public Event(HistoryMark mark) {
        super(mark);
    }

    protected Event(Parcel in) {
        super(in);
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
