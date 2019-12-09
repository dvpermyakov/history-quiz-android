package dvpermyakov.historyquiz.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.model.VKWallPostResult;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;

import dvpermyakov.historyquiz.ExternalConstants;
import dvpermyakov.historyquiz.models.HistoryEntity;
import dvpermyakov.historyquiz.models.HistoryEntityCategory;
import dvpermyakov.historyquiz.models.User;
import dvpermyakov.historyquiz.preferences.SharePreferences;
import dvpermyakov.historyquiz.preferences.UserPreferences;

/**
 * Created by dvpermyakov on 19.10.2016.
 */
public class VKPostWallManager {
    public interface OnLoadingListener {
        void onLoadFail();
    }
    private Context context;
    private VKParameters parameters;
    private HistoryEntity historyEntity;
    private VKRequest.VKRequestListener listener;
    private OnLoadingListener loadingListener;

    public VKPostWallManager(Context context, HistoryEntity historyEntity, VKRequest.VKRequestListener listener, OnLoadingListener loadingListener) {
        this.context = context;
        this.historyEntity = historyEntity;
        this.listener = listener;
        this.loadingListener = loadingListener;

        parameters = new VKParameters();
        parameters.put(VKApiConst.OWNER_ID, String.valueOf(VKUserManager.getUserId(context)));
        loadImage();
    }

    private void loadImage() {
        ImageLoader.getInstance().loadImage(historyEntity.getImage(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                parameters.put(VKApiConst.ATTACHMENTS, new VKUploadImage[]{
                                new VKUploadImage(loadedImage, VKImageParameters.pngImage())}
                );
                loadPhotoToWall(loadedImage);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                loadingListener.onLoadFail();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                loadingListener.onLoadFail();
            }
        });
    }

    private void loadPhotoToWall(Bitmap photo) {
        VKRequest request = VKApi.uploadWallPhotoRequest(new VKUploadImage(photo, VKImageParameters.jpgImage(0.9f)), VKUserManager.getUserId(context), 0);
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiPhoto photoModel = ((VKPhotoArray) response.parsedModel).get(0);
                postToWall(new VKAttachments(photoModel));
            }
            @Override
            public void onError(VKError error) {
                loadingListener.onLoadFail();
            }
        });
    }

    private void postToWall(VKAttachments attachments) {
        parameters.put(VKApiConst.ATTACHMENTS, attachments);
        parameters.put(VKApiConst.MESSAGE, getMessage());
        VKRequest request = VKApi.wall().post(parameters);
        request.setModelClass(VKWallPostResult.class);
        request.executeWithListener(listener);
    }

    private String getMessage() {User user = UserPreferences.getUser(context);
        String doneWord = user.getGender() == User.Gender.FEMALE ? "Завершила" : "Завершил";
        String openWord = user.getGender() == User.Gender.FEMALE ? "Открыла" : "Открыл";
        String entityType = historyEntity.getCategory() == HistoryEntityCategory.VIDEO ? " видео “" : " статью “";
        String url = (SharePreferences.getSharedVKUrl(context) != null) ? SharePreferences.getSharedVKUrl(context) : ExternalConstants.VK_SHORT_URL;

        return doneWord + entityType + historyEntity.getName() + "” в мобильном приложении История России: Игра. " +
                openWord + " для себя новый способ изучать историю. " +
                "\nУстановить приложение: " + url;
    }
}
