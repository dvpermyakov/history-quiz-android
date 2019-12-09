package dvpermyakov.historyquiz.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import dvpermyakov.historyquiz.database.DataBaseStrings;

/**
 * Created by dvpermyakov on 10.12.2016.
 */

@DatabaseTable(tableName = DataBaseStrings.VIDEO_CHANNEL_TABLE)
public class VideoChannel implements Parcelable{
    @DatabaseField(canBeNull = false, id = true)
    private String id;
    @DatabaseField
    private String title;
    @DatabaseField
    private String icon;

    @DatabaseField
    @SerializedName("video_count")
    private String videoCount;
    @DatabaseField
    @SerializedName("subscriber_count")
    private String subscriberCount;


    public VideoChannel() {}

    protected VideoChannel(Parcel in) {
        id = in.readString();
        title = in.readString();
        icon = in.readString();
        videoCount = in.readString();
        subscriberCount = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(icon);
        dest.writeString(videoCount);
        dest.writeString(subscriberCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getIcon() {
        return icon;
    }
    public String getVideoCount() {
        return videoCount;
    }
    public String getSubscriberCount() {
        return subscriberCount;
    }

    public static final Parcelable.Creator<VideoChannel> CREATOR = new Parcelable.Creator<VideoChannel>() {
        @Override
        public VideoChannel createFromParcel(Parcel in) {
            return new VideoChannel(in);
        }

        @Override
        public VideoChannel[] newArray(int size) {
            return new VideoChannel[size];
        }
    };

}
