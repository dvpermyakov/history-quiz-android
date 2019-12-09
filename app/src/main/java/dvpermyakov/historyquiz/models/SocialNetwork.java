package dvpermyakov.historyquiz.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dvpermyakov on 04.12.2016.
 */

public class SocialNetwork implements Parcelable {
    public enum SocialType{ VK, GOOGLE, EMAIL, PLAY }

    private SocialType type;
    private int icon;
    private String title;

    public SocialNetwork(SocialType type, int icon, String title) {
        this.type = type;
        this.icon = icon;
        this.title = title;
    }

    public SocialType getType() {
        return type;
    }
    public int getIcon() {
        return icon;
    }
    public String getTitle() {
        return title;
    }

    protected SocialNetwork(Parcel in) {
        icon = in.readInt();
        title = in.readString();
        type = (SocialType) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(icon);
        dest.writeString(title);
        dest.writeSerializable(type);
    }

    public static final Parcelable.Creator<SocialNetwork> CREATOR = new Parcelable.Creator<SocialNetwork>() {
        @Override
        public SocialNetwork createFromParcel(Parcel in) {
            return new SocialNetwork(in);
        }

        @Override
        public SocialNetwork[] newArray(int size) {
            return new SocialNetwork[size];
        }
    };
}
