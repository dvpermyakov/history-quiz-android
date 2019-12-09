package dvpermyakov.historyquiz.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.models.Answer;

/**
 * Created by dvpermyakov on 27.05.2016.
 */
public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder> {
    private Context context;
    private List<Answer> answers;
    private Set<Integer> clickedSet;
    private List<AnswerViewHolder> holders;

    public AnswersAdapter(Context context, List<Answer> answers, Set<Integer> clickedSet) {
        this.context = context;
        this.answers = answers;
        this.clickedSet = clickedSet;
        holders = new LinkedList<>();
    }

    @Override
    public AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_answer, parent, false);

        int height = parent.getMeasuredHeight() / 4;
        int width = parent.getMeasuredWidth();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, height));

        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AnswerViewHolder holder, int position) {
        holder.text.setText(answers.get(position).getText());

        if (clickedSet.contains(position)) {
            holder.view.setClickable(false);
            holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWrongAnswer));
            holder.text.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        }

        holders.add(position, holder);
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public void setTextColor(int position, int color) {
        holders.get(position).text.setTextColor(color);
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView text;
        AnswerViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            text = (TextView)itemView.findViewById(R.id.answerTextView);
        }
    }
}
