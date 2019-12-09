package dvpermyakov.historyquiz.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import dvpermyakov.historyquiz.ExternalConstants;
import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.appbar.DrawerToggle;
import dvpermyakov.historyquiz.listeners.OnNavigationItemSelectedListener;
import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 18.11.2016.
 */

public class AboutActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerBalance);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigationView);
        if (navigationView != null) {
            navigationView.setCheckedItem(R.id.instructions);
            navigationView.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener(this, OnNavigationItemSelectedListener.ActivityCategory.ABOUT, drawerLayout));
        }

        DrawerToggle toggle = new DrawerToggle(this, drawerLayout, toolbar, navigationView);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Button instructionButton = (Button)findViewById(R.id.instructionButton);
        instructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, InstructionsActivity.class)
                        .putExtra(IntentStrings.INTENT_INSTRUCTIONS_EXTERNAL_PARAM, true));
                Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + context.getResources().getString(R.string.app_name), Analytics.ACTION_INSTRUCTION);
            }
        });

        Button videoButton = (Button)findViewById(R.id.videoButton);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ExternalConstants.YOUTUBE_URL)));
                Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + context.getResources().getString(R.string.app_name), Analytics.ACTION_VIDEO);
            }
        });

        Button emailButton = (Button)findViewById(R.id.emailButton);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Urls.EMAIL_URL});
                intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.app_name));
                context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.email_via)));
                Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + context.getResources().getString(R.string.app_name), Analytics.ACTION_EMAIL);
            }
        });

        Button vkPublicButton = (Button)findViewById(R.id.vkPublicButton);
        vkPublicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ExternalConstants.VK_GROUP_URL)));
                Analytics.sendEvent(Analytics.FRAGMENT_NAVIGATION_DRAWER + context.getResources().getString(R.string.app_name), Analytics.ACTION_VK_PUBLIC);
            }
        });

        Analytics.sendScreen(Analytics.SCREEN_ABOUT + getResources().getString(R.string.app_name));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}
