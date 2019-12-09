package dvpermyakov.historyquiz.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.database.DataBaseStrings;

/**
 * Created by dvpermyakov on 10.12.2016.
 */

@DatabaseTable(tableName = DataBaseStrings.VIDEO_TABLE)
public class Video extends HistoryEntity {
    @DatabaseField
    private String shortcut;
    @DatabaseField
    private String duration;
    @DatabaseField
    private String url;
    @DatabaseField
    @SerializedName("youtube_id")
    private String youtubeId;
    @DatabaseField
    @SerializedName("channel_id")
    private String channelId;
    @DatabaseField
    @SerializedName("view_count")
    private String viewCount;
    @DatabaseField
    @SerializedName("like_count")
    private String likeCount;
    @DatabaseField
    @SerializedName("comment_count")
    private String commentCount;
    @DatabaseField
    private boolean embeddable;

    public Video() {}

    protected Video(Parcel in) {
        test = in.readParcelable(Test.class.getClassLoader());
        parent = in.readParcelable(HistoryMark.class.getClassLoader());
        id = in.readString();
        name = in.readString();
        image = in.readString();
        shortcut = in.readString();
        duration = in.readString();
        url = in.readString();
        channelId = in.readString();
        youtubeId = in.readString();
        viewCount = in.readString();
        likeCount = in.readString();
        commentCount = in.readString();
        embeddable = in.readByte() != 0;
        category = (HistoryEntityCategory) in.readSerializable();
    }

    public void setAppFields() {
        Video video = DataBaseHelperFactory.getHelper().getVideoDao().getById(getId());
        if (video != null) {
            this.test = video.getTest();
            this.parent = video.getParent();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(test, flags);
        dest.writeParcelable(parent, flags);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeString(shortcut);
        dest.writeString(duration);
        dest.writeString(url);
        dest.writeString(channelId);
        dest.writeString(youtubeId);
        dest.writeString(viewCount);
        dest.writeString(likeCount);
        dest.writeString(commentCount);
        dest.writeByte((byte) (embeddable ? 1 : 0));
        dest.writeSerializable(category);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getShortcut() {
        return shortcut;
    }
    public String getDuration() {
        return duration;
    }
    public String getUrl() {
        return url;
    }
    public String getChannelId() {
        return channelId;
    }
    public String getViewCount() {
        return viewCount;
    }
    public String getLikeCount() {
        return likeCount;
    }
    public String getCommentCount() {
        return commentCount;
    }
    public String getYoutubeId() {
        return youtubeId;
    }
    public boolean isEmbeddable() {
        return embeddable;
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
