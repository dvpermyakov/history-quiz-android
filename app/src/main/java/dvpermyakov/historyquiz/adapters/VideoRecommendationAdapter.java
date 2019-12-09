package dvpermyakov.historyquiz.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.models.Video;
import dvpermyakov.historyquiz.models.VideoChannel;

/**
 * Created by dvpermyakov on 18.12.2016.
 */

public class VideoRecommendationAdapter extends RecyclerView.Adapter<VideoRecommendationAdapter.VideoViewHolder> {
    private Context context;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<Video> videos;
    private Map<String, VideoChannel> channelMap;

    public VideoRecommendationAdapter(Context context, List<Video> videos, Map<String, VideoChannel> channelMap) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        this.videos = videos;
        this.channelMap = channelMap;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_video_recommendation, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.videoTitle.setText(videos.get(position).getShortcut());
        VideoChannel channel = channelMap.get(videos.get(position).getChannelId());
        if (channel != null) {
            holder.videoRemark.setText(channel.getTitle());
        } else {
            holder.videoRemark.setVisibility(View.GONE);
        }
        imageLoader.displayImage(videos.get(position).getImage(), holder.videoImage, options);

        holder.videoIconImage.setVisibility(View.GONE);
        boolean testClosed = DataBaseHelperFactory.getHelper().getTestResultDao().isTestClosed(videos.get(position).getTest());
        if (testClosed) {
            holder.videoIconImage.setVisibility(View.VISIBLE);
            holder.videoImage.setColorFilter(ContextCompat.getColor(context, R.color.colorBlackDarkLight), PorterDuff.Mode.LIGHTEN);
            holder.videoIconImage.setImageResource(R.drawable.ic_check_circle);
            holder.videoIconImage.setColorFilter(ContextCompat.getColor(context, R.color.colorRightAnswer), PorterDuff.Mode.SRC_IN);
            holder.videoIconImage.setAlpha(0.6f);
        }
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public void refresh() {
        List<Video> refreshed = new ArrayList<>();
        for (Video video : videos) {
            refreshed.add(DataBaseHelperFactory.getHelper().getVideoDao().getById(video.getId()));
        }
        videos.clear();
        videos.addAll(refreshed);
        notifyDataSetChanged();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView videoTitle;
        TextView videoRemark;
        ImageView videoImage;
        ImageView videoIconImage;
        VideoViewHolder(View itemView) {
            super(itemView);
            videoTitle = (TextView)itemView.findViewById(R.id.videoTitle);
            videoRemark = (TextView)itemView.findViewById(R.id.videoRemark);
            videoImage = (ImageView) itemView.findViewById(R.id.videoImage);
            videoIconImage = (ImageView) itemView.findViewById(R.id.videoImageIcon);
        }
    }
}
