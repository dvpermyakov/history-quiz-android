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

/**
 * Created by dvpermyakov on 06.06.2016.
 */
public class DaoPerson extends BaseDaoImpl<Person, String> {
    protected DaoPerson(ConnectionSource connectionSource, Class<Person> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public Person getById(String id) {
        try {
            return queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void savePerson(Person person) {
        try {
            createOrUpdate(person);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePersons(List<Person> persons) {
        for (Person person : persons) {
            savePerson(person);
        }
    }

    public List<Person> getPersons(HistoryMark parent) {
        QueryBuilder<Person, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.MARK_PARENT, parent));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Person> getOpenedPersons() {
        QueryBuilder<Person, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.IS_OPENED_COLUMN, true));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Person> getNewPersons(int amount) {
        QueryBuilder<Person, String> query = queryBuilder();
        try {
            query.orderBy(DataBaseStrings.CREATED_COLUMN, false).limit((long) amount);
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Person> getReadPerson() {
        QueryBuilder<Person, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.HISTORY_MARK_READ_DONE, true));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Person> getPersonsToSendPlayService() {
        QueryBuilder<Person, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq(DataBaseStrings.NEED_SEND_TO_PLAY_SERVICE, true));
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean hasInDataBase(Person person) {
        QueryBuilder<Person, String> query = queryBuilder();
        try {
            query.setWhere(query.where().eq("id", person.getId()));
            return query.countOf() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
}