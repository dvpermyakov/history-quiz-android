package dvpermyakov.historyquiz.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.Video;
import dvpermyakov.historyquiz.models.VideoChannel;

/**
 * Created by dvpermyakov on 10.12.2016.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {
    private Context context;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<Video> videos;
    private Map<String, VideoChannel> channelMap;

    public VideosAdapter(Context context, List<Video> videos, Map<String, VideoChannel> channelMap) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        this.videos = videos;
        this.channelMap = channelMap;
    }

    @Override
    public VideosAdapter.VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_video, parent, false);
        return new VideosAdapter.VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideosAdapter.VideoViewHolder holder, int position) {
        holder.videoTitle.setText(videos.get(position).getName());
        holder.videoDuration.setText(videos.get(position).getDuration());
        VideoChannel channel = channelMap.get(videos.get(position).getChannelId());
        if (channel != null) {
            holder.videoRemark.setText(channel.getTitle());
            ImageLoader.getInstance().loadImage(channel.getIcon(), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    RoundedBitmapDrawable remarkImageDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), loadedImage);
                    remarkImageDrawable.setCornerRadius(Math.max(loadedImage.getWidth(), loadedImage.getHeight()) / 2.0f);
                    remarkImageDrawable.setCircular(true);
                    holder.videoRemarkImage.setImageDrawable(remarkImageDrawable);
                }
            });
        } else {
            holder.videoRemark.setVisibility(View.GONE);
            holder.videoRemarkImage.setVisibility(View.GONE);
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
        CardView videoCardView;
        TextView videoTitle;
        TextView videoRemark;
        TextView videoDuration;
        ImageView videoImage;
        ImageView videoIconImage;
        ImageView videoRemarkImage;
        VideoViewHolder(View itemView) {
            super(itemView);
            videoCardView = (CardView)itemView.findViewById(R.id.videoCardView);
            videoTitle = (TextView)itemView.findViewById(R.id.videoTitle);
            videoRemark = (TextView)itemView.findViewById(R.id.videoRemark);
            videoDuration = (TextView) itemView.findViewById(R.id.videoDuration);
            videoImage = (ImageView) itemView.findViewById(R.id.videoImage);
            videoIconImage = (ImageView) itemView.findViewById(R.id.videoImageIcon);
            videoRemarkImage = (ImageView) itemView.findViewById(R.id.videoRemarkImage);
        }
    }
}
