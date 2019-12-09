package dvpermyakov.historyquiz.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import dvpermyakov.historyquiz.database.DataBaseStrings;

/**
 * Created by dvpermyakov on 23.05.2016.
 */
@DatabaseTable(tableName = DataBaseStrings.PARAGRAPH_TABLE)
public class Paragraph implements Parcelable {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String header;
    @DatabaseField
    private String text;
    @DatabaseField
    private String image;
    @DatabaseField
    @SerializedName("image_description")
    private String imageDescription;
    @DatabaseField(foreign = true, columnName = DataBaseStrings.MARK_COLUMN)
    private HistoryMark mark;

    public Paragraph() {}

    public String getHeader() {
        return header;
    }
    public String getText() {
        return text;
    }
    public HistoryMark getMark() {
        return mark;
    }
    public void setMark(HistoryMark mark) {
        this.mark = mark;
    }
    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(header);
        dest.writeString(text);
    }

    private Paragraph(Parcel parcel) {
        header = parcel.readString();
        text = parcel.readString();
    }

    public static final Creator<Paragraph> CREATOR = new Creator<Paragraph>() {
        @Override
        public Paragraph createFromParcel(Parcel in) {
            return new Paragraph(in);
        }

        @Override
        public Paragraph[] newArray(int size) {
            return new Paragraph[size];
        }
    };
}
