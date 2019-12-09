package dvpermyakov.historyquiz.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import dvpermyakov.historyquiz.models.Dignity;
import dvpermyakov.historyquiz.models.Person;

/**
 * Created by dvpermyakov on 24.08.2016.
 */
public class DaoDignity extends BaseDaoImpl<Dignity, Integer> {

    protected DaoDignity(ConnectionSource connectionSource, Class<Dignity> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public void saveDignity(Dignity dignity) {
        try {
            createIfNotExists(dignity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeDignities(Person person) {
        try {
            DeleteBuilder<Dignity, Integer> query = deleteBuilder();
            query.setWhere(query.where().eq(DataBaseStrings.PERSON_COLUMN, person));
            query.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}