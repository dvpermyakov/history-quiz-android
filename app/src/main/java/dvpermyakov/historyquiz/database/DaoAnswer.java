package dvpermyakov.historyquiz.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import dvpermyakov.historyquiz.models.Answer;
import dvpermyakov.historyquiz.models.Dependency;
import dvpermyakov.historyquiz.models.Question;
import dvpermyakov.historyquiz.models.Test;

/**
 * Created by dvpermyakov on 28.08.2016.
 */
public class DaoAnswer extends BaseDaoImpl<Answer, String> {
    protected DaoAnswer(ConnectionSource connectionSource, Class<Answer> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public void saveAnswer(Answer answer) {
        try {
            createOrUpdate(answer);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeAnswer(Question question) {
        try {
            DeleteBuilder<Answer, String> query = deleteBuilder();
            query.setWhere(query.where().eq(DataBaseStrings.QUESTION_COLUMN, question));
            query.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
