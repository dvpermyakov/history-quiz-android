package dvpermyakov.historyquiz.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.adapter_models.CardViewInformationModel;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.models.Event;

/**
 * Created by dvpermyakov on 02.06.2016.
 */

public class CardViewInformationAdapter extends RecyclerView.Adapter<CardViewInformationAdapter.InformationViewHolder> {
    private Context context;
    private List<CardViewInformationModel> list;

    public CardViewInformationAdapter(Context context, List<CardViewInformationModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public InformationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_cardview_information_item, parent, false);
        return new InformationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InformationViewHolder holder, int position) {
        if (list.get(position).getDrawable() != null) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageResource(list.get(position).getDrawable());
            holder.imageView.setColorFilter(ContextCompat.getColor(context, list.get(position).getColor()), PorterDuff.Mode.SRC_IN);

        }
        if (list.get(position) != null) {
            holder.title.setText(list.get(position).getTitle());
        }
        if (list.get(position).getValue() != null) {
            holder.value.setText(list.get(position).getValue());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setNewCardViewInformationModelList(List<CardViewInformationModel> list) {
        List<CardViewInformationModel> refreshedList = new ArrayList<>();
        for (CardViewInformationModel model : list) {
            refreshedList.add(model);
        }
        this.list.clear();
        this.list.addAll(refreshedList);
    }

    public static class InformationViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView value;
        ImageView imageView;
        InformationViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.cvInfoTitle);
            value = (TextView)itemView.findViewById(R.id.cvInfoValue);
            imageView = (ImageView) itemView.findViewById(R.id.cvIcon);
        }
    }
}
