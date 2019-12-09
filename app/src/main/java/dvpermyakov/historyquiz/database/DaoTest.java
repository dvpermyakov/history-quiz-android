package dvpermyakov.historyquiz.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.HistoryMark;
import dvpermyakov.historyquiz.models.Person;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.TestResult;

/**
 * Created by dvpermyakov on 05.06.2016.
 */
public class DaoTest extends BaseDaoImpl<Test, String> {
    protected DaoTest (ConnectionSource connectionSource, Class<Test> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public Test getById(String id) {
        try {
            return queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Test> getTests() {
        try {
            return queryBuilder().query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Test> getTestsWithNeedSendToServer() {
        QueryBuilder<Test, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.NEED_SEND_TO_SERVER, true));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Test> getTestsWithNeedSendToPlayService() {
        QueryBuilder<Test, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.NEED_SEND_TO_PLAY_SERVICE, true));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void save(Test test) {
        try {
            createOrUpdate(test);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
