package dvpermyakov.historyquiz.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.fragments.TestStartingFragment;
import dvpermyakov.historyquiz.fragments.VideoPlayerFragment;
import dvpermyakov.historyquiz.interfaces.OnYoutubePlayerListener;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.Video;
import dvpermyakov.historyquiz.models.VideoChannel;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 20.12.2016.
 */

public class VideoInfoTabsAdapter extends FragmentPagerAdapter {
    private Context context;
    private List<Fragment> fragments;
    private List<String> titles;
    private VideoPlayerFragment playerFragment;
    private Video video;
    private OnYoutubePlayerListener listener;

    public VideoInfoTabsAdapter(Context context, FragmentManager manager, OnYoutubePlayerListener listener) {
        super(manager);
        this.context = context;
        this.listener = listener;
        fragments = new ArrayList<>();
        titles = new ArrayList<>();
    }

    public void updateFragments(Video video, VideoChannel channel) {
        this.video = video;
        addFragment(createVideoPlayerFragment(video, channel), context.getResources().getText(R.string.player).toString());
        if (video.getTest() != null) {
            addFragment(createTestStartingFragment(video.getTest()), context.getResources().getText(R.string.test).toString());
        }
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    private void addFragment(Fragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);
    }

    public VideoPlayerFragment getPlayerFragment() {
        return playerFragment;
    }

    public int getTestStartingFragmentIndex() {
        return 1;
    }

    private VideoPlayerFragment createVideoPlayerFragment(Video video, VideoChannel channel) {
        Bundle args = new Bundle();
        args.putParcelable(IntentStrings.INTENT_VIDEO_PARAM, video);
        args.putParcelable(IntentStrings.INTENT_VIDEO_CHANNEL_PARAM, channel);
        playerFragment = new VideoPlayerFragment();
        playerFragment.setArguments(args);
        playerFragment.setListener(listener);
        return playerFragment;
    }

    private TestStartingFragment createTestStartingFragment(Test test) {
        Bundle args = new Bundle();
        args.putParcelable(IntentStrings.INTENT_TEST_PARAM, test);
        args.putParcelable(IntentStrings.INTENT_HISTORY_MARK_PARAM_TEST_START, video);
        args.putParcelableArrayList(IntentStrings.INTENT_HISTORY_MARK_CONDITIONS_PARAM, new ArrayList<Parcelable>());
        TestStartingFragment testStartingFragment = new TestStartingFragment();
        testStartingFragment.setArguments(args);
        return testStartingFragment;
    }
}
