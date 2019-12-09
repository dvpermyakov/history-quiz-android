package dvpermyakov.historyquiz.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.adapters.AnswersAdapter;
import dvpermyakov.historyquiz.decorations.DividerItemDecoration;
import dvpermyakov.historyquiz.listeners.RecyclerItemClickListener;
import dvpermyakov.historyquiz.models.Answer;
import dvpermyakov.historyquiz.models.Question;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 27.05.2016.
 */
public class QuestionFragment extends Fragment {
    public interface OnAnswerClickListener {
        void onAnswerClick(boolean correct);
    }
    private Context context;
    private OnAnswerClickListener listener;
    private Question question;
    private AnswersAdapter adapter;
    private List<Answer> answers;
    private RecyclerView recyclerView;
    private Set<Integer> clickedSet;
    private boolean blocked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        question = getArguments().getParcelable(IntentStrings.INTENT_QUESTION_PARAM);
        answers = getArguments().getParcelableArrayList(IntentStrings.INTENT_ANSWERS_PARAM);
        clickedSet = new HashSet<>();
        List<Integer> clickedArray = getArguments().getIntegerArrayList(IntentStrings.INTENT_CLICKED_SET_PARAM);
        if (clickedArray != null) {
            clickedSet.addAll(clickedArray);
        }
        listener = (OnAnswerClickListener) getActivity();
        blocked = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_question, container, false);
        ((TextView) view.findViewById(R.id.questionTextTextView)).setText(question.getText());
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewAnswers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider, (int) getResources().getDimension(R.dimen.decoration_answer_padding)));
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (clickedSet.contains(position) || blocked) return;
                        if (answers.get(position).getCorrect()) {
                            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorRightAnswer));
                            blockAnswers();
                        } else {
                            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWrongAnswer));
                            view.setClickable(false);
                        }
                        clickedSet.add(position);
                        listener.onAnswerClick(answers.get(position).getCorrect());
                        adapter.setTextColor(position, ContextCompat.getColor(context, R.color.colorWhite));
                    }
                }));
        adapter = new AnswersAdapter(getActivity(), answers, clickedSet);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void blockAnswers() {
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            recyclerView.getChildAt(i).setClickable(false);
        }
        blocked = true;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public Set<Integer> getClickedSet() {
        return clickedSet;
    }
}
