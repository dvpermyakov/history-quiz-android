package dvpermyakov.historyquiz.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.models.Person;

/**
 * Created by dvpermyakov on 26.05.2016.
 */
public class HistoryPersonsAdapter extends RecyclerView.Adapter<HistoryPersonsAdapter.PersonViewHolder> {
    private Context context;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<Person> persons;

    public HistoryPersonsAdapter(Context context, List<Person> persons) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        this.persons = persons;
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_person, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PersonViewHolder holder, int position) {
        holder.name.setText(persons.get(position).getName());
        holder.year.setText(persons.get(position).getYear());
        holder.dignities.setHasFixedSize(true);
        holder.dignities.setLayoutManager(new LinearLayoutManager(context));
        holder.dignities.setAdapter(new HistoryDignityAdapter(persons.get(position).getDignities()));
        imageLoader.displayImage(persons.get(position).getImage(), holder.image, options);

        holder.iconImage.setVisibility(View.GONE);
        holder.image.clearColorFilter();
        if (!persons.get(position).isOpened()) {
            holder.iconImage.setVisibility(View.VISIBLE);
            holder.image.setColorFilter(ContextCompat.getColor(context, R.color.colorBlackDarkLight), PorterDuff.Mode.LIGHTEN);
            holder.iconImage.setImageResource(R.drawable.ic_lock);
            holder.iconImage.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryLight), PorterDuff.Mode.SRC_IN);
            holder.iconImage.setAlpha(0.6f);
        }
        else {
            boolean testClosed = DataBaseHelperFactory.getHelper().getTestResultDao().isTestClosed(persons.get(position).getTest());
            if (testClosed) {
                holder.iconImage.setVisibility(View.VISIBLE);
                holder.image.setColorFilter(ContextCompat.getColor(context, R.color.colorBlackDarkLight), PorterDuff.Mode.LIGHTEN);
                holder.iconImage.setImageResource(R.drawable.ic_check_circle);
                holder.iconImage.setColorFilter(ContextCompat.getColor(context, R.color.colorRightAnswer), PorterDuff.Mode.SRC_IN);
                holder.iconImage.setAlpha(0.6f);
            }
        }

        holder.dignities.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                float x = holder.cardView.getWidth() - holder.dignities.getWidth();
                float y = holder.cardView.getHeight() - holder.dignities.getHeight();
                e.offsetLocation(x, y);
                holder.cardView.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
        });
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    public void refresh() {
        List<Person> refreshedPersons = new ArrayList<>();
        for (Person person : persons) {
            refreshedPersons.add(DataBaseHelperFactory.getHelper().getPersonDao().getById(person.getId()));
        }
        persons.clear();
        persons.addAll(refreshedPersons);
        notifyDataSetChanged();
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView name;
        TextView year;
        ImageView image;
        ImageView iconImage;
        RecyclerView dignities;
        PersonViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.personCardView);
            name = (TextView)itemView.findViewById(R.id.personName);
            year = (TextView)itemView.findViewById(R.id.personYear);
            image = (ImageView) itemView.findViewById(R.id.personImage);
            iconImage = (ImageView) itemView.findViewById(R.id.personImageIcon);
            dignities = (RecyclerView) itemView.findViewById(R.id.recyclerViewDignities);
        }
    }
}
