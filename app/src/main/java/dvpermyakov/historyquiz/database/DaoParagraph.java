package dvpermyakov.historyquiz.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.models.HistoryMark;
import dvpermyakov.historyquiz.models.Paragraph;

/**
 * Created by dvpermyakov on 08.06.2016.
 */
public class DaoParagraph extends BaseDaoImpl<Paragraph, Integer> {
    protected DaoParagraph(ConnectionSource connectionSource, Class<Paragraph> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public void saveParagraph(Paragraph paragraph) {
        try {
            createOrUpdate(paragraph);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeParagraphs(HistoryMark mark) {
        try {
            DeleteBuilder<Paragraph, Integer> query = deleteBuilder();
            query.setWhere(query.where().eq(DataBaseStrings.MARK_COLUMN, mark));
            query.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Paragraph> getParagraphs(HistoryMark mark) {
        try {
            QueryBuilder<Paragraph, Integer> query = queryBuilder();
            query.setWhere(query.where().eq(DataBaseStrings.MARK_COLUMN, mark));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
