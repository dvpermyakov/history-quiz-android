package dvpermyakov.historyquiz.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.adapters.ParagraphsAdapter;
import dvpermyakov.historyquiz.models.Paragraph;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 21.05.2016.
 */
public class HistoryInfoFragment extends Fragment {
    public interface OnReadClickListener {
        void onReadClick();
    }
    private OnReadClickListener listener;
    private List<Paragraph> paragraphs;
    private boolean readDone;
    private Button readButton;
    private Button readDoneButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (OnReadClickListener) context;
        readDone = getArguments().getBoolean(IntentStrings.INTENT_HISTORY_MARK_READ_DONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paragraphs = getArguments().getParcelableArrayList(IntentStrings.INTENT_PARAGRAPHS_PARAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_history_info, container, false);

        readButton = (Button)view.findViewById(R.id.readButton);
        readDoneButton = (Button)view.findViewById(R.id.readDoneButton);
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapButton();
                listener.onReadClick();
            }
        });
        readDoneButton.setClickable(false);
        if (readDone) {
            swapButton();
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewInfo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ParagraphsAdapter adapter = new ParagraphsAdapter(getActivity(), paragraphs);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void swapButton() {
        if (readButton != null && readDoneButton != null) {
            readButton.setVisibility(View.GONE);
            readDoneButton.setVisibility(View.VISIBLE);
        }
    }
}
