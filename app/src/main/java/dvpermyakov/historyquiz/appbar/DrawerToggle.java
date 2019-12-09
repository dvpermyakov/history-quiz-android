package dvpermyakov.historyquiz.appbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.activities.GoogleLoginActivity;
import dvpermyakov.historyquiz.activities.PlayServiceGameLoginActivity;
import dvpermyakov.historyquiz.activities.UserInfoFormActivity;
import dvpermyakov.historyquiz.activities.VKLoginActivity;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.dialogs.SocialNetworksDialog;
import dvpermyakov.historyquiz.managers.VKUserManager;
import dvpermyakov.historyquiz.models.SocialNetwork;
import dvpermyakov.historyquiz.preferences.UserPreferences;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 21.09.2016.
 */
public class DrawerToggle extends ActionBarDrawerToggle {
    private static Bitmap loadedImage;

    private AppCompatActivity activity;
    private RoundedBitmapDrawable userImageDrawable;
    private ImageView userImage;
    private TextView username;
    private TextView marks;
    private TextView balance;

    public DrawerToggle(final AppCompatActivity activity, DrawerLayout drawerLayout, Toolbar toolbar, NavigationView navigationView) {
        super(activity, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        this.activity = activity;
        userImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.userImageView);
        username = (TextView)navigationView.getHeaderView(0).findViewById(R.id.usernameTextView);
        marks = (TextView)navigationView.getHeaderView(0).findViewById(R.id.marksTextView);
        balance = (TextView)navigationView.getHeaderView(0).findViewById(R.id.balanceTextView);
        navigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VKUserManager.getToken(activity) == null) {
                    showLoginDialog();
                }
                Analytics.sendEvent(Analytics.CATEGORY_HEADER, Analytics.ACTION_HEADER_CLICK);
            }
        });
        updateHeaderText();
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        super.onDrawerSlide(drawerView, 0);
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        super.onDrawerStateChanged(newState);
        updateImage();
        updateHeaderText();
    }

    private void updateHeaderText() {
        String marksString = activity.getResources().getString(R.string.appbar_header_test) + UserPreferences.getUser(activity).getMarksDoneCount();
        String balanceString = activity.getResources().getString(R.string.appbar_header_balance) + DataBaseHelperFactory.getHelper().getCoinsTransactionDao().getCoinsBalance();
        username.setText(UserPreferences.getUser(activity).getName());
        marks.setText(marksString);
        balance.setText(balanceString);
    }

    private void updateImage() {
        if (userImageDrawable == null) {
            if (loadedImage != null) {
                setImage(loadedImage);
            } else if (UserPreferences.getUser(activity).getImage() != null) {
                String imageUrl = UserPreferences.getUser(activity).getImage();
                ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        setImage(loadedImage);
                    }
                });
            }
        }
    }

    private void setImage(Bitmap image) {
        userImageDrawable = RoundedBitmapDrawableFactory.create(activity.getResources(), cropBitmap(image));
        userImageDrawable.setCircular(true);
        userImage.setImageDrawable(userImageDrawable);
    }

    public static void loadImage(Context context) {
        if (UserPreferences.getUser(context).getImage() != null) {
            String imageUrl = UserPreferences.getUser(context).getImage();
            ImageLoader.getInstance().loadImage(imageUrl, new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build(),
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            DrawerToggle.loadedImage = loadedImage;
                        }
            });
        }
    }

    private Bitmap cropBitmap(Bitmap bitmap) {
        if (bitmap.getWidth() >= bitmap.getHeight()){
            return Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth()/2 - bitmap.getHeight()/2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );
        } else {
            return Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight()/2 - bitmap.getWidth()/2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }
    }

    private void showLoginDialog() {
        Bundle args = new Bundle();
        args.putString(IntentStrings.INTENT_SOCIAL_NETWORKS_TITLE, activity.getResources().getString(R.string.login_with));
        List<SocialNetwork> socials = new ArrayList<>();
        socials.add(new SocialNetwork(SocialNetwork.SocialType.VK, R.mipmap.ic_vk_dark, activity.getResources().getString(R.string.vk)));
        socials.add(new SocialNetwork(SocialNetwork.SocialType.PLAY, R.mipmap.ic_play_game, activity.getResources().getString(R.string.play_game)));
        socials.add(new SocialNetwork(SocialNetwork.SocialType.GOOGLE, R.mipmap.ic_google, activity.getResources().getString(R.string.google)));
        socials.add(new SocialNetwork(SocialNetwork.SocialType.EMAIL, R.mipmap.ic_email, activity.getResources().getString(R.string.mail)));
        args.putParcelableArrayList(IntentStrings.INTENT_SOCIAL_NETWORKS_PARAM, (ArrayList<? extends Parcelable>) socials);
        SocialNetworksDialog dialog = new SocialNetworksDialog();
        dialog.setArguments(args);
        dialog.setListener(new SocialNetworksDialog.SocialOnClickListener() {
            @Override
            public void onClick(SocialNetwork.SocialType type) {
                switch (type) {
                    case VK:
                        activity.startActivity(new Intent(activity, VKLoginActivity.class));
                        break;
                    case PLAY:
                        activity.startActivity(new Intent(activity, PlayServiceGameLoginActivity.class));
                        break;
                    case GOOGLE:
                        activity.startActivity(new Intent(activity, GoogleLoginActivity.class));
                        break;
                    case EMAIL:
                        activity.startActivity(new Intent(activity, UserInfoFormActivity.class));
                        break;
                }
            }
        });
        dialog.show(activity.getSupportFragmentManager(), "tag");
    }
}
