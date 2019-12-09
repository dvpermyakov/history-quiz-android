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

import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.models.Period;

/**
 * Created by dvpermyakov on 20.05.2016.
 */

public class HistoryPeriodsAdapter extends RecyclerView.Adapter<HistoryPeriodsAdapter.PeriodViewHolder> {
    private Context context;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<Period> periods;

    public HistoryPeriodsAdapter(Context context, List<Period> periods) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        this.periods = periods;
    }

    public void updateList(List<Period> periods) {
        this.periods = periods;
        notifyDataSetChanged();
    }

    @Override
    public PeriodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_period, parent, false);
        return new PeriodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PeriodViewHolder holder, int position) {
        Period period = periods.get(position);
        holder.periodName.setText(period.getName());
        holder.periodYear.setText(period.getYear());
        if (!period.isDeveloping()) {
            holder.periodCount.setText(period.getCountDone() + " / " + period.getCount());
        } else {
            holder.periodCount.setText("");
        }
        imageLoader.displayImage(period.getImage(), holder.periodImage, options);

        holder.periodIconImage.setVisibility(View.GONE);
        holder.periodImage.clearColorFilter();
        if (periods.get(position).isDeveloping()) {
            holder.periodIconImage.setVisibility(View.VISIBLE);
            holder.periodIconImage.setImageResource(R.drawable.ic_build);
            holder.periodIconImage.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryLight), PorterDuff.Mode.SRC_IN);
            holder.periodIconImage.setAlpha(0.4f);
        } else {
            if (period.getCountDone() == period.getCount()) {
                holder.periodIconImage.setVisibility(View.VISIBLE);
                holder.periodIconImage.setImageResource(R.drawable.ic_check_circle);
                holder.periodIconImage.setColorFilter(ContextCompat.getColor(context, R.color.colorRightAnswer), PorterDuff.Mode.SRC_IN);
                holder.periodIconImage.setAlpha(0.5f);
            }
        }
    }

    @Override
    public int getItemCount() {
        return periods.size();
    }

    public static class PeriodViewHolder extends RecyclerView.ViewHolder {
        CardView periodCardView;
        TextView periodName;
        TextView periodYear;
        TextView periodCount;
        ImageView periodImage;
        ImageView periodIconImage;

        PeriodViewHolder(View itemView) {
            super(itemView);
            periodCardView = (CardView)itemView.findViewById(R.id.periodCardView);
            periodName = (TextView)itemView.findViewById(R.id.periodName);
            periodYear = (TextView)itemView.findViewById(R.id.periodYear);
            periodCount = (TextView)itemView.findViewById(R.id.periodCount);
            periodImage = (ImageView) itemView.findViewById(R.id.periodImage);
            periodIconImage = (ImageView) itemView.findViewById(R.id.periodImageIcon);
        }
    }
}
