package dvpermyakov.historyquiz.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.specials.IntentStrings;
import dvpermyakov.historyquiz.views.TouchImageView;

/**
 * Created by dvpermyakov on 09.11.2016.
 */
public class FullScreenImageActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String imageUrl = getIntent().getStringExtra(IntentStrings.INTENT_PARAGRAPHS_IMAGE_PARAM);

        setContentView(R.layout.activity_fullscreen_image);
        TouchImageView imageView = (TouchImageView) findViewById(R.id.touchImageView);

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).build();
        imageLoader.displayImage(imageUrl, imageView, options);
    }
}
