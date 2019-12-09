package dvpermyakov.historyquiz.adapters;

import android.content.Context;
import android.graphics.PorterDuff;

import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
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

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.models.Event;

/**
 * Created by dvpermyakov on 22.05.2016.
 */
public class HistoryEventsAdapter extends RecyclerView.Adapter<HistoryEventsAdapter.EventViewHolder> {
    private Context context;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<Event> events;

    public HistoryEventsAdapter(Context context, List<Event> events) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        this.events = events;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        holder.eventName.setText(events.get(position).getName());
        holder.eventYear.setText(events.get(position).getYear());
        imageLoader.displayImage(events.get(position).getImage(), holder.eventImage, options);

        holder.eventIconImage.setVisibility(View.GONE);
        holder.eventImage.clearColorFilter();
        if (!events.get(position).isOpened()) {
            holder.eventIconImage.setVisibility(View.VISIBLE);
            holder.eventImage.setColorFilter(ContextCompat.getColor(context, R.color.colorBlackDarkLight), PorterDuff.Mode.LIGHTEN);
            holder.eventIconImage.setImageResource(R.drawable.ic_lock);
            holder.eventIconImage.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryLight), PorterDuff.Mode.SRC_IN);
            holder.eventIconImage.setAlpha(0.6f);
        }
        else {
            boolean testClosed = DataBaseHelperFactory.getHelper().getTestResultDao().isTestClosed(events.get(position).getTest());
            if (testClosed) {
                holder.eventIconImage.setVisibility(View.VISIBLE);
                holder.eventImage.setColorFilter(ContextCompat.getColor(context, R.color.colorBlackDarkLight), PorterDuff.Mode.LIGHTEN);
                holder.eventIconImage.setImageResource(R.drawable.ic_check_circle);
                holder.eventIconImage.setColorFilter(ContextCompat.getColor(context, R.color.colorRightAnswer), PorterDuff.Mode.SRC_IN);
                holder.eventIconImage.setAlpha(0.6f);
            }
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void refresh() {
        List<Event> refreshedEvents = new ArrayList<>();
        for (Event event : events) {
            refreshedEvents.add(DataBaseHelperFactory.getHelper().getEventDao().getById(event.getId()));
        }
        events.clear();
        events.addAll(refreshedEvents);
        notifyDataSetChanged();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        CardView eventCardView;
        TextView eventName;
        TextView eventYear;
        ImageView eventImage;
        ImageView eventIconImage;
        EventViewHolder(View itemView) {
            super(itemView);
            eventCardView = (CardView)itemView.findViewById(R.id.eventCardView);
            eventName = (TextView)itemView.findViewById(R.id.eventName);
            eventYear = (TextView)itemView.findViewById(R.id.eventYear);
            eventImage = (ImageView) itemView.findViewById(R.id.eventImage);
            eventIconImage = (ImageView) itemView.findViewById(R.id.eventImageIcon);
        }
    }
}
