package dvpermyakov.historyquiz.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.models.Dignity;

/**
 * Created by dvpermyakov on 03.06.2016.
 */
public class HistoryDignityAdapter extends RecyclerView.Adapter<HistoryDignityAdapter.HistoryDignityViewHolder> {
    private List<Dignity> dignities;

    public HistoryDignityAdapter(List<Dignity> dignities) {
        this.dignities = dignities;
    }

    @Override
    public HistoryDignityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dignity, parent, false);
        return new HistoryDignityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryDignityViewHolder holder, int position) {
        holder.name.setText(dignities.get(position).getName());
        holder.year.setText(dignities.get(position).getYear());
    }

    @Override
    public int getItemCount() {
        return dignities.size();
    }

    public static class HistoryDignityViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView year;
        HistoryDignityViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.dignityName);
            year = (TextView)itemView.findViewById(R.id.dignityYear);
        }
    }
}
