package dvpermyakov.historyquiz.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.Map;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.adapters.VideoInfoTabsAdapter;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.fragments.VideoPlayerFragment;
import dvpermyakov.historyquiz.interfaces.OnYoutubePlayerListener;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.Video;
import dvpermyakov.historyquiz.models.VideoChannel;
import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.network.constants.Params;
import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.requests.VideoInfoRequest;
import dvpermyakov.historyquiz.preferences.UserPreferences;
import dvpermyakov.historyquiz.specials.IntentStrings;
import dvpermyakov.historyquiz.specials.LogTag;

/**
 * Created by dvpermyakov on 11.12.2016.
 */

public class VideoInfoActivity extends CoinsActivity {
    private Context context;
    private Video video;
    private VideoChannel channel;
    private ViewPager viewPager;
    private TabLayout tabs;
    private VideoInfoTabsAdapter adapter;
    private ProgressBar progressBar;
    private boolean showDialog = true;
    private boolean goneToYoutube = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        video = getIntent().getParcelableExtra(IntentStrings.INTENT_VIDEO_PARAM);
        channel = getIntent().getParcelableExtra(IntentStrings.INTENT_VIDEO_CHANNEL_PARAM);

        setContentView(R.layout.activity_video_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(video.getShortcut());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.loadContentProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        viewPager = (ViewPager) findViewById(R.id.videoViewPager);
        adapter = new VideoInfoTabsAdapter(this, getSupportFragmentManager(), new OnYoutubePlayerListener() {
            @Override
            public void onUIComplete() {
                viewPager.setVisibility(View.VISIBLE);
                tabs.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onWatchDone() {
                if (DataBaseHelperFactory.getHelper().getTestResultDao().getAttempts(video.getTest()) == 0 && showDialog) {
                    showStartTestDialog(false);
                }
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setVisibility(View.INVISIBLE);
        tabs = (TabLayout) findViewById(R.id.videoTabs);
        tabs.setupWithViewPager(viewPager);

        if (!UserPreferences.getCopyrightDialogDone(this)) {
            showCopyRightDialog();
            UserPreferences.setCopyrightDialogDone(this, true);
        }

        downloadVideoInfo();

        Analytics.sendScreen(Analytics.SCREEN_VIDEO_INFO + video.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (goneToYoutube) {
            showStartTestDialog(false);
            goneToYoutube = false;
        }
    }

    private void updateAdapter() {
        adapter.updateFragments(video, channel);
        adapter.notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(adapter.getTestStartingFragmentIndex());
    }

    private void downloadVideoInfo() {
        progressBar.setVisibility(View.VISIBLE);

        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        volleyQueue.cancelAll(this);
        Map<String, String> getParams = Params.getParams(Urls.SHORT_HISTORY_MARK_INFO_REQUEST_URL);
        getParams.put("video_id", video.getId());
        Request request = new VideoInfoRequest(new Response.Listener<Test>() {
            @Override
            public void onResponse(Test test) {
                test.setAppFields();
                DataBaseHelperFactory.getHelper().getTestDao().save(test);

                video.setTest(test);
                DataBaseHelperFactory.getHelper().getVideoDao().saveVideo(video);
                updateAdapter();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LogTag.TAG_NETWORK, "Fail!");
                if (video.getTest() == null) {
                    if (!isFinishing()) showNotNetworkDialog();
                }
            }
        }, getParams);
        request.setTag(this);
        volleyQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_video_appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (item.getItemId() == R.id.youtube_link) {
            goneToYoutube = true;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(video.getUrl())));
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        boolean backPressedHandled = false;
        VideoPlayerFragment playerFragment = adapter.getPlayerFragment();
        if (playerFragment != null) {
            if (video.isEmbeddable() && playerFragment.isFullscreen()) {
                playerFragment.setFullscreen(false);
                backPressedHandled = true;
            } else if (playerFragment.isAdviceStartTest() && DataBaseHelperFactory.getHelper().getTestResultDao().getAttempts(video.getTest()) == 0 && showDialog) {
                playerFragment.pauseVideo();
                showStartTestDialog(true);
                backPressedHandled = true;
            }
        }
        if (!backPressedHandled) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VideoPlayerFragment.RECOVERY_REQUEST) {
            adapter.getPlayerFragment().initYoutubeFragment();
        }
    }

    private void showCopyRightDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setCancelable(true);
        builder.setTitle(getResources().getString(R.string.dialog_copyright_title));
        builder.setMessage(getResources().getString(R.string.dialog_copyright_text));
        builder.setPositiveButton(getString(R.string.dialog_copyright_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setNeutralButton(getString(R.string.dialog_copyright_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Urls.EMAIL_URL});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Видеоконтент");
                        context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.email_via)));

                        dialog.dismiss();
                        Analytics.sendEvent(Analytics.CATEGORY_VIDEO, Analytics.ACTION_EMAIL);
                    }
                });
        builder.create().show();
    }

    private void showStartTestDialog(final boolean closeActivityOnCancel) {
        showDialog = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.dialog_start_test_title));
        builder.setMessage(getResources().getString(R.string.dialog_start_test_short_text));
        builder.setPositiveButton(getString(R.string.dialog_start_test_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewPager.setCurrentItem(adapter.getTestStartingFragmentIndex());
                        startActivity(new Intent(context, TestGameActivity.class)
                                .putExtra(IntentStrings.INTENT_HISTORY_ENTITY_PARAM, video)
                                .putExtra(IntentStrings.INTENT_TEST_PARAM, video.getTest()));
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_start_test_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (closeActivityOnCancel) {
                            finish();
                        }
                    }
                });
        builder.create().show();
    }

    private void showNotNetworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.dialog_not_network_title));
        builder.setMessage(getResources().getString(R.string.dialog_not_network_text));
        builder.setPositiveButton(getString(R.string.dialog_not_network_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadVideoInfo();
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_not_network_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.create().show();
    }
}
