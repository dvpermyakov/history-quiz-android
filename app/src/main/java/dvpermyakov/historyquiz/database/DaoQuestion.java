package dvpermyakov.historyquiz.database;


import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.Question;
import dvpermyakov.historyquiz.models.Test;

/**
 * Created by dvpermyakov on 28.08.2016.
 */
public class DaoQuestion extends BaseDaoImpl<Question, String> {
    protected DaoQuestion(ConnectionSource connectionSource, Class<Question> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public void saveQuestion(Question question) {
        try {
            createOrUpdate(question);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean haveQuestions(Test test) {
        QueryBuilder<Question, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.TEST_COLUMN, test));
            return query.countOf() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Question> getQuestions(Test test) {
        QueryBuilder<Question, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.TEST_COLUMN, test));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void removeQuestions(Test test) {
        try {
            DeleteBuilder<Question, String> query = deleteBuilder();
            query.setWhere(query.where().eq(DataBaseStrings.TEST_COLUMN, test));
            query.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}