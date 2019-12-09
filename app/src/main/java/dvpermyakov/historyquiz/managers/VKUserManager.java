package dvpermyakov.historyquiz.managers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import dvpermyakov.historyquiz.ExternalConstants;
import dvpermyakov.historyquiz.appbar.DrawerToggle;
import dvpermyakov.historyquiz.models.User;
import dvpermyakov.historyquiz.network.constants.Params;
import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.requests.SetUserInfoRequest;
import dvpermyakov.historyquiz.network.requests.SetVKRequest;
import dvpermyakov.historyquiz.network.responses.SuccessResponse;
import dvpermyakov.historyquiz.preferences.PreferencesStrings;
import dvpermyakov.historyquiz.preferences.UserPreferences;

/**
 * Created by dvpermyakov on 26.11.2016.
 */

public class VKUserManager {
    private static String getUserToken(Context context) {
        VKAccessToken vkAccessToken = getToken(context);
        return vkAccessToken != null ? vkAccessToken.accessToken : "";
    }

    public static VKAccessToken getToken(Context context) {
        return VKAccessToken.tokenFromSharedPreferences(context, PreferencesStrings.VK_LOGIN_TOKEN);
    }

    public static int getUserId(Context context) {
        VKAccessToken vkAccessToken = getToken(context);
        return vkAccessToken != null ? Integer.parseInt(vkAccessToken.userId) : 0;
    }


    public static boolean hasScopes(Context context, String[] scopes) {
        VKAccessToken vkAccessToken = getToken(context);
        return vkAccessToken.hasScope(scopes);
    }

    public static void setGroupMember(final Context context) {
        int userId = getUserId(context);
        if (userId != 0) {
            VKParameters parameters = new VKParameters();
            parameters.put(VKApiConst.USER_ID, userId);
            parameters.put(VKApiConst.ACCESS_TOKEN, getUserToken(context));
            parameters.put(VKApiConst.GROUP_ID, ExternalConstants.VK_GROUP_ID);
            VKRequest request = VKApi.groups().isMember(parameters);
            request.executeWithListener(new VKRequest.VKRequestListener() {
                public void onComplete(VKResponse response) {
                    try {
                        boolean isMember = response.json.getInt("response") == 1;
                        User user = UserPreferences.getUser(context);
                        user.setVkGroupMember(isMember);
                        UserPreferences.setUser(context, user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public static void subscribeGroup(final Context context) {
        int userId = getUserId(context);
        if (userId != 0) {
            VKParameters parameters = new VKParameters();
            parameters.put(VKApiConst.ACCESS_TOKEN, getUserToken(context));
            parameters.put(VKApiConst.GROUP_ID, ExternalConstants.VK_GROUP_ID);
            VKRequest request = VKApi.groups().join(parameters);
            request.executeWithListener(new VKRequest.VKRequestListener() {
                public void onComplete(VKResponse response) {
                    VKUserManager.setGroupMember(context);
                }
            });
        }
    }

    public static void setUserInfo(final Context context) {
        int userId = getUserId(context);
        if (userId != 0) {
            VKParameters parameters = new VKParameters();
            parameters.put(VKApiConst.USER_IDS, userId);
            parameters.put(VKApiConst.ACCESS_TOKEN, getUserToken(context));
            parameters.put(VKApiConst.FIELDS, "sex, photo_400_orig");
            VKRequest request = VKApi.users().get(parameters);
            request.executeWithListener(new VKRequest.VKRequestListener() {
                public void onComplete(VKResponse response) {
                    try {
                        JSONObject json = response.json.getJSONArray("response").getJSONObject(0);
                        User user = UserPreferences.getUser(context);
                        String previousName = user.getName();
                        String previousImage = user.getImage();

                        user.setGender(User.Gender.fromInt(json.getInt("sex")));
                        String firstName = json.getString("first_name");
                        String lastName = json.getString("last_name");
                        if (firstName != null && firstName.length() > 0 && lastName != null && lastName.length() >0) {
                            user.setName(firstName + " " + lastName);
                        }
                        String image =json.getString("photo_400_orig");
                        if (image != null && image.length() > 0 && !image.equals("https://vk.com/images/camera_400.png")) {
                            user.setImage(image);
                        }
                        UserPreferences.setUser(context, user);
                        DrawerToggle.loadImage(context);

                        if (previousName == null || previousImage == null || !user.getName().equals(previousName) || !user.getImage().equals(previousImage)) {
                            user.sendUserInfoToServer();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
