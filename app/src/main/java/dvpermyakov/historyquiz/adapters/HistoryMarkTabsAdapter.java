package dvpermyakov.historyquiz.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.fragments.HistoryEventsFragment;
import dvpermyakov.historyquiz.fragments.HistoryInfoFragment;
import dvpermyakov.historyquiz.fragments.HistoryPersonsFragment;
import dvpermyakov.historyquiz.fragments.TestStartingFragment;
import dvpermyakov.historyquiz.fragments.VideosFragment;
import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.HistoryMark;
import dvpermyakov.historyquiz.models.Paragraph;
import dvpermyakov.historyquiz.models.Person;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.Video;
import dvpermyakov.historyquiz.models.VideoChannel;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 21.05.2016.
 */
public class HistoryMarkTabsAdapter extends FragmentPagerAdapter {
    private Context context;
    private HistoryMark mark;
    private List<Fragment> fragments;
    private List<String> titles;
    private TestStartingFragment testStartingFragment;
    private VideosFragment videoFragment;
    private List<HistoryEventsFragment> eventsFragments;
    private List<HistoryPersonsFragment> personsFragments;

    public HistoryMarkTabsAdapter(Context context, FragmentManager manager) {
        super(manager);
        this.context = context;
        fragments = new ArrayList<>();
        titles = new ArrayList<>();
        eventsFragments = new ArrayList<>();
        personsFragments = new ArrayList<>();
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

    public void updateFragments(HistoryMark mark) {
        this.mark = mark;
        List<Paragraph> paragraphs = DataBaseHelperFactory.getHelper().getParagraphDao().getParagraphs(mark);
        addFragment(createInfoFragment(paragraphs), String.valueOf(context.getResources().getText(R.string.info)));

        List<Person> persons = DataBaseHelperFactory.getHelper().getPersonDao().getPersons(mark);
        Collections.sort(persons);
        Map<String, List<Person>> personMap = new HashMap<>();
        for (Person person : persons) {
            if (!personMap.containsKey(person.getGroupTitle())) personMap.put(person.getGroupTitle(), new ArrayList<Person>());
            personMap.get(person.getGroupTitle()).add(person);
        }
        for (Map.Entry<String, List<Person>> entry : personMap.entrySet()) {
            HistoryPersonsFragment fragment = createPersonsFragment(entry.getValue());
            personsFragments.add(fragment);
            addFragment(fragment, entry.getKey());
        }

        List<Event> events = DataBaseHelperFactory.getHelper().getEventDao().getEvents(mark);
        Collections.sort(events);
        Map<String, List<Event>> eventMap = new HashMap<>();
        for (Event event : events) {
            if (!eventMap.containsKey(event.getGroupTitle())) eventMap.put(event.getGroupTitle(), new ArrayList<Event>());
            eventMap.get(event.getGroupTitle()).add(event);
        }
        for (Map.Entry<String, List<Event>> entry : eventMap.entrySet()) {
            HistoryEventsFragment fragment = createEventsFragment(entry.getValue());
            eventsFragments.add(fragment);
            addFragment(fragment, entry.getKey());
        }

        List<Video> videos = DataBaseHelperFactory.getHelper().getVideoDao().getVideos(mark);
        if (videos.size() > 0) {
            Set<String> channelIds = new HashSet<>();
            for (Video video : videos) {
                channelIds.add(video.getChannelId());
            }
            List<VideoChannel> channels = new ArrayList<>();
            for (String id : channelIds) {
                if (id != null) {
                    VideoChannel channel = DataBaseHelperFactory.getHelper().getVideoChannelDao().getById(id);
                    if (channel != null) {
                        channels.add(channel);
                    }
                }
            }
            videoFragment = createVideosFragment(videos, channels);
            addFragment(videoFragment, context.getResources().getText(R.string.videos).toString());
        }

        testStartingFragment = createTestStartingFragment(mark.getTest());
        addFragment(testStartingFragment, String.valueOf(context.getResources().getText(R.string.test)));
    }

    public TestStartingFragment getTestStartingFragment() {
        return testStartingFragment;
    }

    public int getTestStartingFragmentIndex() {
        return 1 + personsFragments.size() + eventsFragments.size() + (videoFragment != null ? 1 : 0);
    }

    private HistoryInfoFragment createInfoFragment(List<Paragraph> paragraphs) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(IntentStrings.INTENT_PARAGRAPHS_PARAM, (ArrayList<? extends Parcelable>) paragraphs);
        args.putBoolean(IntentStrings.INTENT_HISTORY_MARK_READ_DONE, mark.isReadDone());
        HistoryInfoFragment historyInfoFragment = new HistoryInfoFragment();
        historyInfoFragment.setArguments(args);
        return historyInfoFragment;
    }

    private HistoryEventsFragment createEventsFragment(List<Event> events) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(IntentStrings.INTENT_HISTORY_EVENTS_PARAM, (ArrayList<? extends Parcelable>) events);
        HistoryEventsFragment historyEventsFragment = new HistoryEventsFragment();
        historyEventsFragment.setArguments(args);
        return historyEventsFragment;
    }

    private HistoryPersonsFragment createPersonsFragment(List<Person> persons) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(IntentStrings.INTENT_HISTORY_PERSONS_PARAM, (ArrayList<? extends Parcelable>) persons);
        HistoryPersonsFragment historyPersonsFragment = new HistoryPersonsFragment();
        historyPersonsFragment.setArguments(args);
        return historyPersonsFragment;
    }

    private TestStartingFragment createTestStartingFragment(Test test) {
        Bundle args = new Bundle();
        args.putParcelable(IntentStrings.INTENT_TEST_PARAM, test);
        args.putParcelable(IntentStrings.INTENT_HISTORY_MARK_PARAM_TEST_START, mark);
        args.putParcelableArrayList(IntentStrings.INTENT_HISTORY_MARK_CONDITIONS_PARAM, (ArrayList<? extends Parcelable>) mark.getConditionMarks());
        TestStartingFragment testStartingFragment = new TestStartingFragment();
        testStartingFragment.setArguments(args);
        return testStartingFragment;
    }

    private VideosFragment createVideosFragment(List<Video> videos, List<VideoChannel> channels) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(IntentStrings.INTENT_VIDEOS_PARAM, (ArrayList<? extends Parcelable>) videos);
        args.putParcelableArrayList(IntentStrings.INTENT_VIDEOS_CHANNELS_PARAM, (ArrayList<? extends Parcelable>) channels);
        VideosFragment videosFragment = new VideosFragment();
        videosFragment.setArguments(args);
        return videosFragment;
    }
}
