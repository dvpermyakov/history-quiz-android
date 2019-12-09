package dvpermyakov.historyquiz.models;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Map;

import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.network.constants.Params;
import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.requests.SetUserInfoRequest;
import dvpermyakov.historyquiz.network.responses.SuccessResponse;
import dvpermyakov.historyquiz.preferences.PreferencesStrings;

/**
 * Created by dvpermyakov on 27.05.2016.
 */
public class User {
    public enum Gender {
        UNKNOWN, FEMALE, MALE;
        public static Gender fromInt(int value) {
            switch (value) {
                case 0:
                    return UNKNOWN;
                case 1:
                    return FEMALE;
                case 2:
                    return MALE;
            }
            return UNKNOWN;
        }
        public static int toInt(Gender gender) {
            switch (gender) {
                case UNKNOWN:
                    return 0;
                case FEMALE:
                    return 1;
                case MALE:
                    return 2;
            }
            return 0;
        }
    }
    private String id;
    private String secret;
    private String name;
    private Gender gender;
    private int marksDoneCount;
    private String image;
    private String email;
    private boolean vkGroupMember = false;

    public User(String id, String secret, String name, Gender gender, int marksDoneCount, String image, String email, boolean vkGroupMember) {
        this.id = id;
        this.secret = secret;
        this.name = name;
        this.gender = gender;
        this.marksDoneCount = marksDoneCount;
        this.image = image;
        this.email = email;
        this.vkGroupMember = vkGroupMember;
    }

    public String getId() {
        return id;
    }
    public String getSecret() {
        return secret;
    }
    public String getName() {
        return name;
    }
    public Gender getGender() {
        return gender;
    }
    public int getMarksDoneCount() {
        return marksDoneCount;
    }
    public String getImage() {
        return image;
    }
    public String getEmail() {
        return email;
    }
    public boolean isVkGroupMember() {
        return vkGroupMember;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    public void setMarksDoneCount(int count) {
        marksDoneCount = count;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setVkGroupMember(boolean vkGroupMember) {
        this.vkGroupMember = vkGroupMember;
    }

    public String getFirstName() {
        if (!isAuthorized()) return "";
        String[] splits = name.split(" ");
        if (splits.length > 0) {
            return splits[0];
        } else {
            return "";
        }
    }

    public String getSecondName() {
        if (!isAuthorized()) return "";
        String[] splits = name.split(" ");
        if (splits.length > 1) {
            return splits[1];
        } else {
            return "";
        }
    }

    public boolean isAuthorized() {
        return name != null && !name.equals(PreferencesStrings.USER_NAME_DEFAULT);
    }

    public void sendUserInfoToServer() {
        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        Map<String, String> getParams = Params.getParams(Urls.SET_USER_INFO_REQUEST_URL);
        if (name != null) getParams.put("name", name);
        if (image != null) getParams.put("image", image);
        if (email != null) getParams.put("email", email);
        Request request = new SetUserInfoRequest(new Response.Listener<SuccessResponse>() {
            @Override
            public void onResponse(SuccessResponse response) {}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        }, getParams);
        volleyQueue.add(request);
    }
}
