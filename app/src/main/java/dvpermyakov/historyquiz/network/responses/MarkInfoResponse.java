package dvpermyakov.historyquiz.network.responses;

import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.models.Dependency;
import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.HistoryEntityCategory;
import dvpermyakov.historyquiz.models.Person;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.Text;
import dvpermyakov.historyquiz.models.Video;
import dvpermyakov.historyquiz.models.VideoChannel;

/**
 * Created by dvpermyakov on 25.05.2016.
 */
public class MarkInfoResponse {
    private Text text;
    private Test test;
    private List<Event> events;
    private List<Person> persons;
    private List<Video>  videos;
    private List<VideoChannel> channels;
    private List<ServerDependency> dependencies;

    public Text getText() {
        return text;
    }
    public Test getTest() {
        return test;
    }
    public List<Event> getEvents() {
        return events;
    }
    public List<Person> getPersons() {
        return persons;
    }
    public List<Video> getVideos() {
        if (videos != null) {
            return videos;
        } else {
            return new ArrayList<>();
        }
    }
    public List<VideoChannel> getChannels() {
        if (channels != null) {
            return channels;
        } else {
            return new ArrayList<>();
        }
    }

    public List<Dependency> getDependencies() {
        List<Dependency> result = new ArrayList<>();
        for (ServerDependency dependency : dependencies) {
            HistoryEntityCategory category = HistoryEntityCategory.fromInt(Integer.parseInt(dependency.getCategory()));
            if (category == HistoryEntityCategory.PERIOD) {
                result.add(new Dependency(DataBaseHelperFactory.getHelper().getPeriodDao().getById(dependency.getId()), category));
            }
            if (category == HistoryEntityCategory.EVENT) {
                result.add(new Dependency(DataBaseHelperFactory.getHelper().getEventDao().getById(dependency.getId()), category));
            }
            if (category == HistoryEntityCategory.PERSON) {
                result.add(new Dependency(DataBaseHelperFactory.getHelper().getPersonDao().getById(dependency.getId()), category));
            }
        }
        return result;
    }

    private class ServerDependency {
        private String id;
        private String category;

        public String getId() {
            return id;
        }
        public String getCategory() {
            return category;
        }
    }
}
