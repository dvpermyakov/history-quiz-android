package dvpermyakov.historyquiz.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.models.UserRatingItem;

/**
 * Created by dvpermyakov on 05.01.2017.
 */

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingItemViewHolder> {
    private Context context;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<UserRatingItem> items;

    public RatingAdapter(Context context, List<UserRatingItem> items) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_user)
                .showImageForEmptyUri(R.mipmap.ic_user)
                .showImageOnFail(R.mipmap.ic_user)
                .cacheInMemory(true).cacheOnDisk(true).build();
        this.items = items;
    }

    @Override
    public RatingAdapter.RatingItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_rating_item, parent, false);
        return new RatingAdapter.RatingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RatingAdapter.RatingItemViewHolder holder, final int position) {
        holder.userPosition.setText("" + (position + 1) + " место");
        holder.userName.setText(items.get(position).getUserName());
        holder.userDoneAmount.setText("Завершено тестов: " + items.get(position).getDoneAmount());

        ImageLoader.getInstance().loadImage(items.get(position).getUserImage(), options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage != null) {
                    setImage(holder.userImage, loadedImage);
                } else {
                    imageLoader.displayImage(items.get(position).getUserImage(), holder.userImage, options);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void setImage(ImageView imageView, Bitmap image) {
        RoundedBitmapDrawable userImageDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), cropBitmap(image));
        userImageDrawable.setCircular(true);
        imageView.setImageDrawable(userImageDrawable);
    }

    private Bitmap cropBitmap(Bitmap bitmap) {
        if (bitmap.getWidth() >= bitmap.getHeight()){
            return Bitmap.createBitmap(
                    bitmap,
                    bitmap.getWidth()/2 - bitmap.getHeight()/2,
                    0,
                    bitmap.getHeight(),
                    bitmap.getHeight()
            );
        } else {
            return Bitmap.createBitmap(
                    bitmap,
                    0,
                    bitmap.getHeight()/2 - bitmap.getWidth()/2,
                    bitmap.getWidth(),
                    bitmap.getWidth()
            );
        }
    }

    public static class RatingItemViewHolder extends RecyclerView.ViewHolder {
        TextView userPosition;
        TextView userName;
        TextView userDoneAmount;
        ImageView userImage;
        RatingItemViewHolder(View itemView) {
            super(itemView);
            userPosition = (TextView)itemView.findViewById(R.id.userPosition);
            userName = (TextView)itemView.findViewById(R.id.userName);
            userDoneAmount = (TextView)itemView.findViewById(R.id.userDoneAmount);
            userImage = (ImageView) itemView.findViewById(R.id.userImage);
        }
    }
}
