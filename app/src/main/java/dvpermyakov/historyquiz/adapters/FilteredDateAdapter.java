package dvpermyakov.historyquiz.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.activities.HistoryMarkActivity;
import dvpermyakov.historyquiz.listeners.RecyclerItemClickListener;
import dvpermyakov.historyquiz.models.HistoryMark;
import dvpermyakov.historyquiz.specials.DateUtils;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 17.09.2016.
 */
public class FilteredDateAdapter extends RecyclerView.Adapter<FilteredDateAdapter.FilteredDateViewHolder> {
    private Context context;
    private Map<String, List<HistoryMark>> mapMarks;
    private List<String> listDates;
    private List<RecyclerItemClickListener> listListeners;

    public FilteredDateAdapter(Context context, Map<String, List<HistoryMark>> mapMarks, List<String> listDates) {
        this.context = context;
        this.mapMarks = mapMarks;
        this.listDates = listDates;
        listListeners = new ArrayList<>();
    }

    @Override
    public FilteredDateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_filtered_date, parent, false);
        return new FilteredDateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FilteredDateViewHolder holder, int position) {
        String date = listDates.get(position);
        final List<HistoryMark> marks = mapMarks.get(date);
        if (date.equals(DateUtils.defaultDateString)) date = DateUtils.defaultDateTitle;

        if (!listListeners.isEmpty()) {
            for (RecyclerItemClickListener listener : listListeners) {
                holder.marks.removeOnItemTouchListener(listener);
            }
        }

        holder.date.setText(date);
        holder.marks.setLayoutManager(new LinearLayoutManager(context));
        holder.marks.setAdapter(new FilteredHistoryMarksAdapter(context, marks));
        RecyclerItemClickListener listener = new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                context.startActivity(new Intent(context, HistoryMarkActivity.class)
                        .putExtra(IntentStrings.INTENT_HISTORY_MARK_PARAM, marks.get(position)));
            }
        });
        holder.marks.addOnItemTouchListener(listener);
        listListeners.add(listener);
    }

    @Override
    public int getItemCount() {
        return listDates.size();
    }

    public void refresh(Map<String, List<HistoryMark>> mapMarks, List<String> listDates) {
        this.mapMarks.clear();
        for (Map.Entry<String, List<HistoryMark>> entry : mapMarks.entrySet()) {
            this.mapMarks.put(entry.getKey(), entry.getValue());
        }
        this.listDates.clear();
        this.listDates.addAll(listDates);
        notifyDataSetChanged();
    }

    public static class FilteredDateViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        RecyclerView marks;

        public FilteredDateViewHolder(View itemView) {
            super(itemView);
            date = (TextView)itemView.findViewById(R.id.dateFilteredTextView);
            marks = (RecyclerView) itemView.findViewById(R.id.recyclerViewFilteredMarks);
        }
    }
}
