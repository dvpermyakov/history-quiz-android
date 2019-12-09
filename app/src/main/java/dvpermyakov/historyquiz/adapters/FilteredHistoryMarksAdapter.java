package dvpermyakov.historyquiz.adapters;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.models.HistoryMark;
import dvpermyakov.historyquiz.models.HistoryEntityCategory;
import dvpermyakov.historyquiz.models.Person;

/**
 * Created by dvpermyakov on 17.09.2016.
 */
public class FilteredHistoryMarksAdapter extends RecyclerView.Adapter<FilteredHistoryMarksAdapter.FilteredHistoryMarkViewHolder> {
    private Context context;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<HistoryMark> marks;

    public FilteredHistoryMarksAdapter(Context context, List<HistoryMark> marks) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        this.marks = marks;
    }

    @Override
    public FilteredHistoryMarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_filtered_history_mark, parent, false);
        return new FilteredHistoryMarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FilteredHistoryMarkViewHolder holder, int position) {
        holder.name.setText(marks.get(position).getName());
        holder.year.setText(marks.get(position).getYear());
        imageLoader.displayImage(marks.get(position).getImage(), holder.image, options);

        if (marks.get(position).getCategory() == HistoryEntityCategory.PERSON) {
            Person person = DataBaseHelperFactory.getHelper().getPersonDao().getById(marks.get(position).getId());
            holder.dignities.setHasFixedSize(true);
            holder.dignities.setLayoutManager(new LinearLayoutManager(context));
            holder.dignities.setAdapter(new HistoryDignityAdapter(person.getDignities()));

            holder.dignities.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                    float x = holder.cardView.getWidth() - holder.dignities.getWidth();
                    float y = holder.cardView.getHeight() - holder.dignities.getHeight();
                    e.offsetLocation(x, y);
                    holder.cardView.onTouchEvent(e);
                    return false;
                }

                @Override
                public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return marks.size();
    }

    public static class FilteredHistoryMarkViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView name;
        TextView year;
        ImageView image;
        RecyclerView dignities;

        public FilteredHistoryMarkViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.filteredCardView);
            name = (TextView)itemView.findViewById(R.id.filteredName);
            year = (TextView)itemView.findViewById(R.id.filteredYear);
            image = (ImageView) itemView.findViewById(R.id.filteredImage);
            dignities = (RecyclerView) itemView.findViewById(R.id.filteredDignities);
        }
    }
}
