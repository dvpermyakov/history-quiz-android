package dvpermyakov.historyquiz.network.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import dvpermyakov.historyquiz.models.UserRatingItem;

/**
 * Created by dvpermyakov on 05.01.2017.
 */

public class RatingResponse {
    @SerializedName("users")
    private List<UserRatingItem> items;

    public List<UserRatingItem> getItems() {
        return items;
    }
}
