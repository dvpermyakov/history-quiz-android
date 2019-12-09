package dvpermyakov.historyquiz.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.TestResult;
import dvpermyakov.historyquiz.models.TestResultCategory;

/**
 * Created by dvpermyakov on 05.06.2016.
 */
public class DaoTestResult extends BaseDaoImpl<TestResult, String> {

    protected DaoTestResult(ConnectionSource connectionSource, Class<TestResult> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public int getAttempts(Test test) {
        try {
            QueryBuilder<TestResult, String> query = queryBuilder();
            query.setWhere(query.where().eq(DataBaseStrings.TEST_COLUMN, test));
            return (int)query.countOf();
        }
        catch (SQLException e) {
            return 0;
        }
    }

    public int getMaxResult(Test test) {
        try {
            QueryBuilder<TestResult, String> query = queryBuilder();
            query.setWhere(query.where().eq(DataBaseStrings.TEST_COLUMN, test));
            List<TestResult> result = query.orderBy(DataBaseStrings.QUESTION_NUMBER, false).limit((long)1).query();
            if (result.size() > 0) return result.get(0).getQuestionNumber();
            else return 0;
        }
        catch (SQLException e) {
            return 0;
        }
    }

    public boolean isTestClosed(Test test) {
        if (test == null) return false;
        try {
            QueryBuilder<TestResult, String> query = queryBuilder();
            query.setWhere(query.where()
                    .eq(DataBaseStrings.TEST_COLUMN, test)
                    .and()
                    .eq(DataBaseStrings.TEST_RESULT, TestResultCategory.WIN));
            return query.countOf() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Date getFirstTestResultCreated(Test test) {
        if (test == null) return null;
        try {
            QueryBuilder<TestResult, String> query = queryBuilder();
            query.setWhere(query.where().eq(DataBaseStrings.TEST_COLUMN, test));
            return query.orderBy(DataBaseStrings.TEST_RESULT_CREATED_COLUMN, true).query().get(0).getCreatedDate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void save(TestResult result) {
        try {
            createIfNotExists(result);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
