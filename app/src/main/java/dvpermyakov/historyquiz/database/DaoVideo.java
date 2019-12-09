package dvpermyakov.historyquiz.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.models.HistoryEntity;
import dvpermyakov.historyquiz.models.HistoryMark;
import dvpermyakov.historyquiz.models.Video;

/**
 * Created by dvpermyakov on 10.12.2016.
 */

public class DaoVideo extends BaseDaoImpl<Video, String> {
    protected DaoVideo(ConnectionSource connectionSource, Class<Video> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public Video getById(String id) {
        try {
            return queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Video> getVideos() {
        try {
            return queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Video> getVideos(HistoryEntity mark) {
        try {
            QueryBuilder<Video, String> query = queryBuilder();
            query.setWhere(query.where().eq(DataBaseStrings.MARK_PARENT, mark));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveVideo(Video video) {
        try {
            createOrUpdate(video);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveVideos(List<Video> videos) {
        for (Video video : videos) {
            saveVideo(video);
        }
    }
}
