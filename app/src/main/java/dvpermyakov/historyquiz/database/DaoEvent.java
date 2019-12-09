package dvpermyakov.historyquiz.database;


import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.HistoryMark;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.TestResultCategory;

/**
 * Created by dvpermyakov on 06.06.2016.
 */
public class DaoEvent extends BaseDaoImpl<Event, String> {
    protected DaoEvent(ConnectionSource connectionSource, Class<Event> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public Event getById(String id) {
        try {
            return queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveEvent(Event event) {
        try {
            createOrUpdate(event);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveEvents(List<Event> events) {
        for (Event event : events) {
            saveEvent(event);
        }
    }

    public List<Event> getEvents(HistoryMark parent) {
        QueryBuilder<Event, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.MARK_PARENT, parent));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Event> getOpenedEvents() {
        QueryBuilder<Event, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.IS_OPENED_COLUMN, true));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Event> getNewEvents(int amount) {
        QueryBuilder<Event, String> query = queryBuilder();
        try {
            query.orderBy(DataBaseStrings.CREATED_COLUMN, false).limit((long) amount);
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Event> getReadEvents() {
        QueryBuilder<Event, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.HISTORY_MARK_READ_DONE, true));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Event> getEventsToSendPlayService() {
        QueryBuilder<Event, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.NEED_SEND_TO_PLAY_SERVICE, true));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean hasInDataBase(Event event) {
        QueryBuilder<Event, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq("id", event.getId()));
            return query.countOf() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
}