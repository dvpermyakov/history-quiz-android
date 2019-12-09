package dvpermyakov.historyquiz.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.activities.VideoInfoActivity;
import dvpermyakov.historyquiz.adapters.VideosAdapter;
import dvpermyakov.historyquiz.listeners.RecyclerItemClickListener;
import dvpermyakov.historyquiz.models.Video;
import dvpermyakov.historyquiz.models.VideoChannel;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 10.12.2016.
 */

public class VideosFragment extends Fragment {
    private Context context;
    private List<Video> videos;
    private Map<String, VideoChannel> channelMap;
    private VideosAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        videos = getArguments().getParcelableArrayList(IntentStrings.INTENT_VIDEOS_PARAM);
        Collections.sort(videos);
        List<VideoChannel> channels = getArguments().getParcelableArrayList(IntentStrings.INTENT_VIDEOS_CHANNELS_PARAM);
        channelMap = new HashMap<>();
        if (channels != null) {
            for (VideoChannel channel : channels) {
                channelMap.put(channel.getId(), channel);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        RecyclerView recyclerView = ((RecyclerView) view.findViewById(R.id.recyclerViewVideos));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        startActivity(new Intent(context, VideoInfoActivity.class)
                                .putExtra(IntentStrings.INTENT_VIDEO_PARAM, videos.get(position))
                                .putExtra(IntentStrings.INTENT_VIDEO_CHANNEL_PARAM, channelMap.get(videos.get(position).getChannelId())));
                    }
                }));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new VideosAdapter(context, videos, channelMap);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.refresh();
        }
    }
}
