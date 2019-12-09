package dvpermyakov.historyquiz.models;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.database.DataBaseStrings;

/**
 * Created by dvpermyakov on 26.05.2016.
 */

public class HistoryMark extends HistoryEntity {
    @DatabaseField(columnName = DataBaseStrings.CREATED_COLUMN)
    protected String created;
    @DatabaseField
    @SerializedName("group_title")
    protected String groupTitle;
    @DatabaseField
    protected String description;
    @DatabaseField
    @SerializedName("year_title")
    protected String year;
    @DatabaseField(columnName = DataBaseStrings.IS_OPENED_COLUMN)
    protected boolean isOpened = false;
    @DatabaseField
    protected Date openedDate;
    @DatabaseField(columnName = DataBaseStrings.HISTORY_MARK_CONDITIONS_HACK)
    protected boolean hackConditions = false;
    @DatabaseField(columnName = DataBaseStrings.HISTORY_MARK_SHARED)
    protected boolean shared = false;
    @DatabaseField(columnName = DataBaseStrings.HISTORY_MARK_READ_DONE)
    protected boolean readDone = false;
    @DatabaseField(columnName = DataBaseStrings.NEED_SEND_TO_PLAY_SERVICE)
    protected boolean needSendToPlayService = false;

    public HistoryMark() {}

    public HistoryMark(HistoryMark mark) {
        super(mark);
        year = mark.getYear();
        created = mark.getCreated();
        groupTitle = mark.getGroupTitle();
        description = mark.getDescription();
        isOpened = mark.isOpened();
        openedDate = mark.openedDate;
    }

    public String getDescription() {
        return description;
    }

    public String getYear() {
        return year;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setIsOpened(boolean isOpened) {
        this.isOpened = isOpened;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public Date getOpenedDate() {
        return openedDate;
    }

    public void setOpenedDate(Date openedDate) {
        this.openedDate = openedDate;
    }

    @Override
    public void setHacked() {
        hackConditions = true;
    }
    @Override
    public boolean isHacked() {
        return hackConditions;
    }

    public String getCreated() {
        return created;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared() {
        shared = true;
    }

    public boolean isReadDone() {
        return readDone;
    }

    public void setReadDone() {
        readDone = true;
    }

    public void setNeedSendToPlayService(boolean needSendToPlayService) {
        this.needSendToPlayService = needSendToPlayService;
    }

    public boolean isNeedSendToPlayService() {
        return needSendToPlayService;
    }

    private List<Dependency> getDependencies() {
        return DataBaseHelperFactory.getHelper().getDependencyDao().getDependencies(this);
    }

    protected void setAppFields(HistoryMark mark) {
        if (mark != null) {
            this.isOpened = mark.isOpened();
            this.openedDate = mark.getOpenedDate();
            this.test = mark.getTest();
            this.hackConditions = mark.isHacked();
            this.shared = mark.isShared();
            this.readDone = mark.isReadDone();
            this.parent = mark.getParent();
            this.needSendToPlayService = mark.isNeedSendToPlayService();
        }
    }

    @Override
    public List<Boolean> getConditionBooleans() {
        List<Boolean> result = new ArrayList<>();
        for (HistoryMark mark : getConditionMarks()) {
            if (mark.getCategory() == HistoryEntityCategory.PERIOD) {
                mark = DataBaseHelperFactory.getHelper().getPeriodDao().getById(mark.getId());
            }
            if (mark.getCategory() == HistoryEntityCategory.EVENT) {
                mark = DataBaseHelperFactory.getHelper().getEventDao().getById(mark.getId());
            }
            if (mark.getCategory() == HistoryEntityCategory.PERSON) {
                mark = DataBaseHelperFactory.getHelper().getPersonDao().getById(mark.getId());
            }
            result.add(DataBaseHelperFactory.getHelper().getTestResultDao().isTestClosed(mark.getTest()));
        }
        return result;
    }

    public List<HistoryMark> getConditionMarks() {
        List<HistoryMark> conditionMarks = new ArrayList<>();
        for (Dependency dependency : getDependencies()) {
            HistoryMark conditionMark = null;
            if (dependency.getConditionMark() != null) {
                if (dependency.getConditionCategory() == HistoryEntityCategory.PERIOD)
                    conditionMark = DataBaseHelperFactory.getHelper().getPeriodDao().getById(dependency.getConditionMark().getId());
                if (dependency.getConditionCategory() == HistoryEntityCategory.EVENT)
                    conditionMark = DataBaseHelperFactory.getHelper().getEventDao().getById(dependency.getConditionMark().getId());
                if (dependency.getConditionCategory() == HistoryEntityCategory.PERSON)
                    conditionMark = DataBaseHelperFactory.getHelper().getPersonDao().getById(dependency.getConditionMark().getId());
                if (conditionMark != null) {
                    conditionMarks.add(conditionMark);
                }
            }
        }
        Collections.sort(conditionMarks);
        return conditionMarks;
    }

    @Override
    public boolean canStartTest() {
        if (isHacked()) return true;

        for (HistoryMark mark : getConditionMarks()) {
            if (mark.getCategory() == HistoryEntityCategory.PERIOD) {
                mark = DataBaseHelperFactory.getHelper().getPeriodDao().getById(mark.getId());
            }
            if (mark.getCategory() == HistoryEntityCategory.EVENT) {
                mark = DataBaseHelperFactory.getHelper().getEventDao().getById(mark.getId());
            }
            if (mark.getCategory() == HistoryEntityCategory.PERSON) {
                mark = DataBaseHelperFactory.getHelper().getPersonDao().getById(mark.getId());
            }
            if (!DataBaseHelperFactory.getHelper().getTestResultDao().isTestClosed(mark.getTest())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected HistoryMark(Parcel in) {
        super(in);
        year = in.readString();
        shared = in.readByte() != 0;
        readDone = in.readByte() != 0;
        category = (HistoryEntityCategory) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(year);
        dest.writeByte((byte) (shared ? 1 : 0));
        dest.writeByte((byte) (readDone ? 1 : 0));
        dest.writeSerializable(category);
    }

    public static final Creator<HistoryMark> CREATOR = new Creator<HistoryMark>() {
        @Override
        public HistoryMark createFromParcel(Parcel in) {
            return new HistoryMark(in);
        }

        @Override
        public HistoryMark[] newArray(int size) {
            return new HistoryMark[size];
        }
    };
}
