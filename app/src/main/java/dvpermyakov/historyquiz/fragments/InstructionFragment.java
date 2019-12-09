package dvpermyakov.historyquiz.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 14.09.2016.
 */
public class InstructionFragment extends Fragment {
    private int imageResource;
    private String header;
    private View nextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageResource = getArguments().getInt(IntentStrings.INTENT_INSTRUCTION_IMAGE_ID_PARAM);
        header = getArguments().getString(IntentStrings.INTENT_INSTRUCTION_HEADER_PARAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_instruction, container, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.instructionImage);
        imageView.setImageResource(imageResource);

        TextView textView = (TextView) view.findViewById(R.id.instructionHeader);
        textView.setText(header);

        return view;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            if (nextView != null) {
                nextView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setNextView(View view) {
        nextView = view;
    }
}
