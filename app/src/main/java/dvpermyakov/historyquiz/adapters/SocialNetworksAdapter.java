package dvpermyakov.historyquiz.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.models.SocialNetwork;

/**
 * Created by dvpermyakov on 04.12.2016.
 */

public class SocialNetworksAdapter extends RecyclerView.Adapter<SocialNetworksAdapter.SocialNetworkViewHolder> {
    private List<SocialNetwork> socials;

    public SocialNetworksAdapter(List<SocialNetwork> socials) {
        this.socials = socials;
    }

    @Override
    public SocialNetworksAdapter.SocialNetworkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_social_networks, parent, false);
        return new SocialNetworksAdapter.SocialNetworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SocialNetworksAdapter.SocialNetworkViewHolder holder, int position) {
        SocialNetwork social = socials.get(position);
        holder.icon.setImageResource(social.getIcon());
        holder.title.setText(social.getTitle());
    }

    @Override
    public int getItemCount() {
        return socials.size();
    }

    public static class SocialNetworkViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;

        SocialNetworkViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.socialImageView);
            title = (TextView)itemView.findViewById(R.id.socialTextView);
        }
    }
}
