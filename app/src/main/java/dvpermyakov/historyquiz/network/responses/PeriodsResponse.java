package dvpermyakov.historyquiz.network.responses;

import java.util.List;

import dvpermyakov.historyquiz.models.Period;

/**
 * Created by dvpermyakov on 08.06.2016.
 */
public class PeriodsResponse {
    private List<Period> periods;
    private String timestamp;

    public List<Period> getPeriods() {
        return periods;
    }
    public String getTimestamp() {
        return timestamp;
    }
}
