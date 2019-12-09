package dvpermyakov.historyquiz.database;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import dvpermyakov.historyquiz.models.VideoChannel;

/**
 * Created by dvpermyakov on 10.12.2016.
 */

public class DaoVideoChannel extends BaseDaoImpl<VideoChannel, String> {
    protected DaoVideoChannel(ConnectionSource connectionSource, Class<VideoChannel> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public VideoChannel getById(String id) {
        try {
            return queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveVideoChannel(VideoChannel videoChannel) {
        try {
            createOrUpdate(videoChannel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveVideoChannels(List<VideoChannel> videoChannels) {
        for (VideoChannel videoChannel : videoChannels) {
            saveVideoChannel(videoChannel);
        }
    }

}