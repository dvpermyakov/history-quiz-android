package dvpermyakov.historyquiz.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import dvpermyakov.historyquiz.R;

/**
 * Created by dvpermyakov on 21.09.2016.
 */
public abstract class CoinsActivity extends AppCompatActivity {
    private Context context;
    protected boolean isRunning;
    private ImageView balanceImageView;
    private Animation enlarge;
    private Animation shrink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        balanceImageView = (ImageView) inflater.inflate(R.layout.imageview_balance, null);
        balanceImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, BalanceActivity.class));
            }
        });

        enlarge = AnimationUtils.loadAnimation(this, R.anim.enlarge);
        shrink = AnimationUtils.loadAnimation(this, R.anim.shrink);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_coins_appbar, menu);
        menu.findItem(R.id.coins_balance).setActionView(balanceImageView);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        balanceImageView.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        if (balanceImageView != null) {
            balanceImageView.clearAnimation();
        }
    }

    public void startEnlargeAnimation() {
        balanceImageView.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_IN);
        balanceImageView.startAnimation(enlarge);
    }

    public void startShrinkAnimation() {
        balanceImageView.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_IN);
        balanceImageView.startAnimation(shrink);
    }
}
