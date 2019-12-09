package dvpermyakov.historyquiz.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.models.Dependency;
import dvpermyakov.historyquiz.models.HistoryMark;

/**
 * Created by dvpermyakov on 08.06.2016.
 */
public class DaoDependency extends BaseDaoImpl<Dependency, Integer> {

    protected DaoDependency(ConnectionSource connectionSource, Class<Dependency> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public void saveDependency(Dependency dependency) {
        try {
            createIfNotExists(dependency);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeDependencies(HistoryMark mark) {
        try {
            DeleteBuilder<Dependency, Integer> query = deleteBuilder();
            query.setWhere(query.where().eq(DataBaseStrings.MARK_COLUMN, mark));
            query.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Dependency> getDependencies(HistoryMark mark) {
        try {
            QueryBuilder<Dependency, Integer> query = queryBuilder();
            query.setWhere(query.where().eq(DataBaseStrings.MARK_COLUMN, mark));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
