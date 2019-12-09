package dvpermyakov.historyquiz.network.responses;

import java.util.List;

import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.Person;

/**
 * Created by dvpermyakov on 07.10.2016.
 */
public class NewHistoryMarksResponse {
    private List<Event> events;
    private List<Person> persons;

    public List<Event> getEvents() {
        return events;
    }
    public List<Person> getPersons() {
        return persons;
    }
}