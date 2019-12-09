package dvpermyakov.historyquiz.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import dvpermyakov.historyquiz.database.DataBaseStrings;

/**
 * Created by dvpermyakov on 08.06.2016.
 */

@DatabaseTable(tableName = DataBaseStrings.DEPENDENCY_TABLE)
public class Dependency {
    @DatabaseField(generatedId = true)
    private Integer dependencyId;
    @DatabaseField(foreign = true, columnName = DataBaseStrings.MARK_COLUMN)
    private HistoryMark mark;
    @DatabaseField(foreign = true, columnName = DataBaseStrings.CONDITION_MARK_COLUMN)
    private HistoryMark conditionMark;
    @DatabaseField
    private HistoryEntityCategory conditionCategory;

    public Dependency() {}

    public Dependency(HistoryMark conditionMark, HistoryEntityCategory conditionCategory) {
        this.conditionMark = conditionMark;
        this.conditionCategory = conditionCategory;
    }

    public HistoryMark getMark() {
        return mark;
    }
    public void setMark(HistoryMark mark) {
        this.mark = mark;
    }

    public HistoryMark getConditionMark() {
        return conditionMark;
    }

    public HistoryEntityCategory getConditionCategory() {
        return conditionCategory;
    }
}
