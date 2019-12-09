package dvpermyakov.historyquiz.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import android.util.Log;

import com.j256.ormlite.field.DatabaseField;

import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.database.DataBaseStrings;

/**
 * Created by dvpermyakov on 25.12.2016.
 */

public class HistoryEntity implements Comparable<HistoryEntity>, Parcelable {
    @DatabaseField(canBeNull = false, id = true)
    protected String id;
    @DatabaseField
    protected HistoryEntityCategory category;
    @DatabaseField
    protected String name;
    @DatabaseField
    protected String image;
    @DatabaseField
    protected int order;
    @DatabaseField(foreign = true, columnName = DataBaseStrings.MARK_PARENT)
    protected HistoryMark parent;
    @DatabaseField(foreign = true, columnName = DataBaseStrings.TEST_COLUMN)
    protected Test test;

    public String getName() {
        return name;
    }
    public String getImage() {
        return image;
    }
    public HistoryMark getParent() {
        return parent;
    }
    public String getId() {
        return id;
    }
    public Test getTest() {
        return test;
    }
    public HistoryEntityCategory getCategory() {
        return category;
    }
    public boolean isHacked() {
        return false;
    }

    public void setParent(HistoryMark parent) {
        this.parent = parent;
    }
    public void setOrder(int order) {
        this.order = order;
    }
    public void setCategory(HistoryEntityCategory category) {
        this.category = category;
    }
    public void setTest(Test test) {
        this.test = test;
    }
    public void setHacked() {}

    public List<Boolean> getConditionBooleans() {
        return new ArrayList<>();
    }
    public boolean canStartTest() {
        return true;
    }

    public Period getPeriod() {
        HistoryEntity entity = this;
        int deep = 10;
        while (entity != null && deep > 0) {
            String id = entity.getId();
            entity = DataBaseHelperFactory.getHelper().getPeriodDao().getById(id);
            if (entity == null) entity = DataBaseHelperFactory.getHelper().getEventDao().getById(id);
            if (entity == null) entity = DataBaseHelperFactory.getHelper().getPersonDao().getById(id);
            if (entity == null) entity = DataBaseHelperFactory.getHelper().getVideoDao().getById(id);

            if (entity != null) {
                if (entity.getCategory() == HistoryEntityCategory.PERIOD) break;
                else {
                    entity = entity.parent;
                }
            }
            deep--;
        }
        try {
            return (Period) entity;
        } catch(ClassCastException exception) {
            Log.e("exception", exception.getMessage());
            return null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public HistoryEntity() {}

    public HistoryEntity(HistoryEntity entity) {
        id = entity.getId();
        category = entity.getCategory();
        parent = entity.getParent();
        name = entity.getName();
        image = entity.getImage();
    }

    protected HistoryEntity(Parcel in) {
        test = in.readParcelable(Test.class.getClassLoader());
        parent = in.readParcelable(HistoryMark.class.getClassLoader());
        id = in.readString();
        order = in.readInt();
        name = in.readString();
        image = in.readString();
        category = (HistoryEntityCategory) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(test, flags);
        dest.writeParcelable(parent, flags);
        dest.writeString(id);
        dest.writeInt(order);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeSerializable(category);
    }

    @Override
    public int compareTo(@NonNull HistoryEntity another) {
        return this.order - another.order;
    }

    public static final Creator<HistoryEntity> CREATOR = new Creator<HistoryEntity>() {
        @Override
        public HistoryEntity createFromParcel(Parcel in) {
            return new HistoryEntity(in);
        }

        @Override
        public HistoryEntity[] newArray(int size) {
            return new HistoryEntity[size];
        }
    };
}
