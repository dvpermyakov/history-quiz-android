package dvpermyakov.historyquiz.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.Period;
import dvpermyakov.historyquiz.models.Person;

/**
 * Created by dvpermyakov on 08.06.2016.
 */
public class DaoPeriod extends BaseDaoImpl<Period, String> {
    protected DaoPeriod(ConnectionSource connectionSource, Class<Period> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public Period getById(String id) {
        try {
            return queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Period> getPeriods() {
        try {
            return queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Period> getOpenedPeriods() {
        QueryBuilder<Period, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.IS_OPENED_COLUMN, true));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Period> getReadPeriods() {
        QueryBuilder<Period, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.HISTORY_MARK_READ_DONE, true));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Period> getPeriodsToSendPlayService() {
        QueryBuilder<Period, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.NEED_SEND_TO_PLAY_SERVICE, true));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void savePeriod(Period period) {
        try {
            createOrUpdate(period);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePeriods(List<Period> periods) {
        for (Period period : periods) {
            savePeriod(period);
        }
    }
}
