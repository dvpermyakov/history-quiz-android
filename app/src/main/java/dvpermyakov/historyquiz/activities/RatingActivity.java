package dvpermyakov.historyquiz.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import com.google.android.material.navigation.NavigationView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.adapters.RatingAdapter;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.appbar.DrawerToggle;
import dvpermyakov.historyquiz.dialogs.SocialNetworksDialog;
import dvpermyakov.historyquiz.listeners.OnNavigationItemSelectedListener;
import dvpermyakov.historyquiz.models.SocialNetwork;
import dvpermyakov.historyquiz.models.User;
import dvpermyakov.historyquiz.models.UserRatingItem;
import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.network.requests.RatingRequest;
import dvpermyakov.historyquiz.network.responses.RatingResponse;
import dvpermyakov.historyquiz.preferences.UserPreferences;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 05.01.2017.
 */

public class RatingActivity extends AppCompatActivity {
    private Context context;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_rating);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerBalance);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        if (navigationView != null) {
            navigationView.setCheckedItem(R.id.rating);
            navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener(this, OnNavigationItemSelectedListener.ActivityCategory.RATING, drawerLayout));
        }

        DrawerToggle toggle = new DrawerToggle(this, drawerLayout, toolbar, navigationView);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        progressBar = (ProgressBar) findViewById(R.id.loadContentProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        recyclerView = ((RecyclerView) findViewById(R.id.recyclerViewRating));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.enterButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });

        User user = UserPreferences.getUser(this);
        if (user.isAuthorized()) {
            downloadItems();
        } else {
            showLoginDialog();
        }

        Analytics.sendScreen(Analytics.SCREEN_RATING);
    }

    @Override
    protected void onResume() {
        super.onResume();
        User user = UserPreferences.getUser(this);
        if (user.isAuthorized() && !isLoaded) {
            downloadItems();
        }
    }

    private void createRecyclerView(List<UserRatingItem> items) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        RatingAdapter adapter = new RatingAdapter(this, items);
        recyclerView.setAdapter(adapter);
        isLoaded = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void downloadItems() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        findViewById(R.id.enterLayout).setVisibility(View.GONE);

        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        volleyQueue.cancelAll(this);
        Request request = new RatingRequest(new Response.Listener<RatingResponse>() {
            @Override
            public void onResponse(RatingResponse response) {
                createRecyclerView(response.getItems());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!isFinishing()) showNotNetworkDialog();
            }
        });
        request.setTag(this);
        volleyQueue.add(request);
    }

    private void showLoginDialog() {
        progressBar.setVisibility(View.GONE);
        findViewById(R.id.enterLayout).setVisibility(View.VISIBLE);

        Bundle args = new Bundle();
        args.putString(IntentStrings.INTENT_SOCIAL_NETWORKS_TITLE, getResources().getString(R.string.login_with));
        List<SocialNetwork> socials = new ArrayList<>();
        socials.add(new SocialNetwork(SocialNetwork.SocialType.VK, R.mipmap.ic_vk_dark, getResources().getString(R.string.vk)));
        socials.add(new SocialNetwork(SocialNetwork.SocialType.PLAY, R.mipmap.ic_play_game, getResources().getString(R.string.play_game)));
        socials.add(new SocialNetwork(SocialNetwork.SocialType.GOOGLE, R.mipmap.ic_google, getResources().getString(R.string.google)));
        socials.add(new SocialNetwork(SocialNetwork.SocialType.EMAIL, R.mipmap.ic_email, getResources().getString(R.string.mail)));
        args.putParcelableArrayList(IntentStrings.INTENT_SOCIAL_NETWORKS_PARAM, (ArrayList<? extends Parcelable>) socials);
        SocialNetworksDialog dialog = new SocialNetworksDialog();
        dialog.setArguments(args);
        dialog.setListener(new SocialNetworksDialog.SocialOnClickListener() {
            @Override
            public void onClick(SocialNetwork.SocialType type) {
                switch (type) {
                    case VK:
                        startActivityForResult(new Intent(context, VKLoginActivity.class), 0);
                        break;
                    case PLAY:
                        startActivity(new Intent(context, PlayServiceGameLoginActivity.class));
                        break;
                    case GOOGLE:
                        startActivity(new Intent(context, GoogleLoginActivity.class));
                        break;
                    case EMAIL:
                        startActivity(new Intent(context, UserInfoFormActivity.class));
                        break;
                }
            }
        });
        dialog.show(getSupportFragmentManager(), "tag");

        Analytics.sendEvent(Analytics.CATEGORY_RATING, Analytics.ACTION_LOGIN_DIALOG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            boolean success = data.getBooleanExtra(IntentStrings.INTENT_LOGIN_VK_SUCCESS, false);
            if (success) {
                downloadItems();
            }
        }
    }

    private void showNotNetworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.dialog_not_network_title));
        builder.setMessage(getResources().getString(R.string.dialog_not_network_text));
        builder.setPositiveButton(getString(R.string.dialog_not_network_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        downloadItems();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_not_network_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
