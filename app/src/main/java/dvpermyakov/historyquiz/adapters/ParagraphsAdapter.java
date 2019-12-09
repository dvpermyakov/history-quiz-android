package dvpermyakov.historyquiz.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.activities.FullScreenImageActivity;
import dvpermyakov.historyquiz.models.Paragraph;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 23.05.2016.
 */
public class ParagraphsAdapter extends RecyclerView.Adapter<ParagraphsAdapter.ParagraphViewHolder> {
    private Context context;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<Paragraph> paragraphs;

    public ParagraphsAdapter(Context context, List<Paragraph> paragraphs) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        this.paragraphs = paragraphs;
    }

    @Override
    public ParagraphViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_paragraph, parent, false);
        return new ParagraphViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ParagraphViewHolder holder, final int position) {
        holder.headerTextView.setText(paragraphs.get(position).getHeader());
        holder.textTextView.setText(Html.fromHtml(paragraphs.get(position).getText()));
        holder.textTextView.setMovementMethod(LinkMovementMethod.getInstance());
        if (paragraphs.get(position).getImage() != null && !paragraphs.get(position).getImage().isEmpty()) {
            holder.paragraphImage.setVisibility(View.VISIBLE);
            holder.paragraphImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, FullScreenImageActivity.class)
                            .putExtra(IntentStrings.INTENT_PARAGRAPHS_IMAGE_PARAM, paragraphs.get(position).getImage()));
                }
            });
            imageLoader.displayImage(paragraphs.get(position).getImage(), holder.paragraphImage, options);
        } else {
            holder.paragraphImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return paragraphs.size();
    }


    public static class ParagraphViewHolder extends RecyclerView.ViewHolder {
        CardView paragraphCardView;
        TextView headerTextView;
        TextView textTextView;
        ImageView paragraphImage;
        ParagraphViewHolder(View itemView) {
            super(itemView);
            paragraphCardView = (CardView)itemView.findViewById(R.id.eventCardView);
            headerTextView = (TextView)itemView.findViewById(R.id.textViewParagraphHeader);
            textTextView = (TextView)itemView.findViewById(R.id.textViewParagraphText);
            paragraphImage = (ImageView)itemView.findViewById(R.id.paragraphImage);
        }
    }
}
