package dvpermyakov.historyquiz.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.adapters.SocialNetworksAdapter;
import dvpermyakov.historyquiz.listeners.RecyclerItemClickListener;
import dvpermyakov.historyquiz.models.SocialNetwork;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 03.12.2016.
 */

public class SocialNetworksDialog extends DialogFragment {
    public interface SocialOnClickListener {
        void onClick(SocialNetwork.SocialType type);
    }

    private SocialOnClickListener listener;
    private List<SocialNetwork> socials;

    public void setListener(SocialOnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        socials = getArguments().getParcelableArrayList(IntentStrings.INTENT_SOCIAL_NETWORKS_PARAM);
        String title = getArguments().getString(IntentStrings.INTENT_SOCIAL_NETWORKS_TITLE);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_social_networks, null);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewSocialNetworks);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (listener != null) {
                            listener.onClick(socials.get(position).getType());
                        }
                        dismiss();
                    }
                }));
        recyclerView.setAdapter(new SocialNetworksAdapter(socials));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setCancelable(true);
        builder.setTitle(title);

        return builder.create();
    }
}
