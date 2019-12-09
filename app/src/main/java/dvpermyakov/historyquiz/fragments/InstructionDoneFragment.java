package dvpermyakov.historyquiz.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.activities.HistoryPeriodsActivity;
import dvpermyakov.historyquiz.activities.PlayServiceGameLoginActivity;
import dvpermyakov.historyquiz.managers.PlayServiceGameManager;
import dvpermyakov.historyquiz.managers.PlayServiceGameManagerFactory;
import dvpermyakov.historyquiz.preferences.UserPreferences;
import dvpermyakov.historyquiz.specials.IntentStrings;

import static dvpermyakov.historyquiz.activities.InstructionsActivity.RC_LOGIN;

/**
 * Created by dvpermyakov on 15.09.2016.
 */
public class InstructionDoneFragment extends Fragment {
    private boolean external;
    private View nextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        external = getArguments().getBoolean(IntentStrings.INTENT_INSTRUCTIONS_EXTERNAL_PARAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_done_instructions, container, false);
        view.findViewById(R.id.doneInstructionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPreferences.setInstructionsDone(getActivity(), true);
                if (external) {
                    getActivity().finish();
                } else {
                    PlayServiceGameManager manager = PlayServiceGameManagerFactory.getManager();
                    if (manager.isConnected()) {
                        startActivity(new Intent(getActivity(), HistoryPeriodsActivity.class));
                        getActivity().finish();
                    } else {
                        startActivityForResult(new Intent(getActivity(), PlayServiceGameLoginActivity.class), RC_LOGIN);
                    }
                }
            }
        });
        ((ImageView)view.findViewById(R.id.doneInstructionIcon)).setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorRightAnswer), PorterDuff.Mode.SRC_IN);
        return view;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            if (nextView != null) {
                nextView.setVisibility(View.GONE);
            }
        }
    }

    public void setNextView(View view) {
        nextView = view;
    }
}
