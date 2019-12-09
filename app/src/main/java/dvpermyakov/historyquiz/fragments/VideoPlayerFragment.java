package dvpermyakov.historyquiz.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.adapter_models.CardViewInformationModel;
import dvpermyakov.historyquiz.adapters.CardViewInformationAdapter;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.decorations.DividerItemDecoration;
import dvpermyakov.historyquiz.interfaces.OnYoutubePlayerListener;
import dvpermyakov.historyquiz.models.Video;
import dvpermyakov.historyquiz.models.VideoChannel;
import dvpermyakov.historyquiz.specials.IntentStrings;
import dvpermyakov.historyquiz.managers.YoutubePlayerWebViewManager;

/**
 * Created by dvpermyakov on 20.12.2016.
 */

public class VideoPlayerFragment extends Fragment implements YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener, YouTubePlayer.PlayerStateChangeListener {
    public static final int RECOVERY_REQUEST = 1;

    private Video video;
    private VideoChannel channel;
    private YouTubePlayer player;
    private YouTubePlayerFragment youTubePlayerFragment;
    private YoutubePlayerWebViewManager youtubeWebViewManager;
    private boolean isFullscreen = false;
    private boolean adviceStartTest = false;
    private boolean isEnded = false;
    private OnYoutubePlayerListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        video = getArguments().getParcelable(IntentStrings.INTENT_VIDEO_PARAM);
        channel = getArguments().getParcelable(IntentStrings.INTENT_VIDEO_CHANNEL_PARAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        addCardViewVideoInfo(view);
        addCardViewVideoStatistic(view);

        if (video.isEmbeddable()) {
            view.findViewById(R.id.youtube_player_container).setVisibility(View.VISIBLE);
        } else {
            youtubeWebViewManager = new YoutubePlayerWebViewManager(getActivity(), (WebView) view.findViewById(R.id.webPlayer), video.getUrl(), listener);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (video.isEmbeddable()) {
            youTubePlayerFragment = (YouTubePlayerFragment)
                    getActivity().getFragmentManager().findFragmentById(R.id.youtube_video_fragment);
            initYoutubeFragment();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (youtubeWebViewManager != null) {
            youtubeWebViewManager.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (youtubeWebViewManager != null) {
            youtubeWebViewManager.start();
        }
    }

    public void setListener(OnYoutubePlayerListener listener) {
        this.listener = listener;
    }

    public boolean isAdviceStartTest() {
        if (!video.isEmbeddable() && youtubeWebViewManager != null) {
            return youtubeWebViewManager.isAdviceStartTest();
        }
        return adviceStartTest;
    }

    public boolean isFullscreen() {
        return isFullscreen;
    }

    public void setFullscreen(boolean isFullscreen) {
        player.setFullscreen(isFullscreen);
    }

    public void initYoutubeFragment() {
        if (youTubePlayerFragment != null) {
            youTubePlayerFragment.initialize(getResources().getString(R.string.youtube_api_key), this);
        }
    }

    public void pauseVideo() {
        if (youTubePlayerFragment != null) {
            player.pause();
        }
        if (youtubeWebViewManager != null) {
            youtubeWebViewManager.pause();
        }
    }

    private void addCardViewVideoInfo(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewVideoInformation);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider, (int) getResources().getDimension(R.dimen.decoration_normal_padding)));
        List<CardViewInformationModel> list = new ArrayList<>();
        list.add(new CardViewInformationModel(getResources().getString(R.string.video_info_source), "Youtube", null, null));
        list.add(new CardViewInformationModel(getResources().getString(R.string.video_info_author), channel.getTitle(), null, null));
        list.add(new CardViewInformationModel(getResources().getString(R.string.video_info_duration), video.getDuration(), null, null));
        CardViewInformationAdapter adapter = new CardViewInformationAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
    }

    private void addCardViewVideoStatistic(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewVideoStatistic);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider, (int) getResources().getDimension(R.dimen.decoration_normal_padding)));
        List<CardViewInformationModel> list = new ArrayList<>();
        list.add(new CardViewInformationModel(getResources().getString(R.string.video_info_views), video.getViewCount(), null, null));
        list.add(new CardViewInformationModel(getResources().getString(R.string.video_info_likes), video.getLikeCount(), null, null));
        list.add(new CardViewInformationModel(getResources().getString(R.string.video_info_comments), video.getCommentCount(), null, null));
        CardViewInformationAdapter adapter = new CardViewInformationAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            player = youTubePlayer;
            player.setOnFullscreenListener(this);
            player.setPlayerStateChangeListener(this);
            player.cueVideo(video.getYoutubeId());
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(getActivity(), RECOVERY_REQUEST).show();
        } else {
            Analytics.sendEvent(Analytics.CATEGORY_VIDEO + video.getName(), Analytics.ACTION_YOUTUBE_INIT_FAILURE);
        }
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        if (!player.isPlaying() && isEnded && !isFullscreen && listener != null) {
            listener.onWatchDone();
        }
    }

    @Override
    public void onLoading() {}

    @Override
    public void onLoaded(String s) {
        listener.onUIComplete();
    }

    @Override
    public void onAdStarted() {
        Analytics.sendEvent(Analytics.CATEGORY_VIDEO + video.getName(), Analytics.ACTION_YOUTUBE_AD);
    }

    @Override
    public void onVideoStarted() {
        Analytics.sendEvent(Analytics.CATEGORY_VIDEO + video.getName(), Analytics.ACTION_YOUTUBE_START);
    }

    @Override
    public void onVideoEnded() {
        isEnded = true;
        adviceStartTest = true;
        if (!isFullscreen && listener != null) {
            listener.onWatchDone();
        }
        Analytics.sendEvent(Analytics.CATEGORY_VIDEO + video.getName(), Analytics.ACTION_YOUTUBE_END);
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        Analytics.sendEvent(Analytics.CATEGORY_VIDEO + video.getName(), Analytics.ACTION_YOUTUBE_ERROR + errorReason.name());
    }
}
