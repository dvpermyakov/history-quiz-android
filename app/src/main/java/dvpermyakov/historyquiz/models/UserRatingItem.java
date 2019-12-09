package dvpermyakov.historyquiz.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dvpermyakov on 05.01.2017.
 */

public class UserRatingItem {
    @SerializedName("id")
    private String userId;
    @SerializedName("image")
    private String userImage;
    @SerializedName("name")
    private String userName;
    @SerializedName("attempts_amount")
    private String attemptsAmount;
    @SerializedName("test_done_amount")
    private String doneAmount;

    public UserRatingItem() {}

    public String getUserId() {
        return userId;
    }
    public String getUserImage() {
        return userImage;
    }
    public String getUserName() {
        return userName;
    }
    public String getAttemptsAmount() {
        return attemptsAmount;
    }
    public String getDoneAmount() {
        return doneAmount;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setAttemptsAmount(String attemptsAmount) {
        this.attemptsAmount = attemptsAmount;
    }
    public void setDoneAmount(String doneAmount) {
        this.doneAmount = doneAmount;
    }
}
