package dvpermyakov.historyquiz.activities;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.models.User;
import dvpermyakov.historyquiz.preferences.UserPreferences;

/**
 * Created by dvpermyakov on 04.01.2017.
 */

public class UserInfoFormActivity extends AppCompatActivity {
    private ImageView doneImageView;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info_form);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        doneImageView = (ImageView) inflater.inflate(R.layout.imageview_done, null);
        doneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveUserInfo()) {
                    finish();
                }
            }
        });

        view = findViewById(R.id.userFormLayout);
        ImageView nameImageView = (ImageView) findViewById(R.id.nameImageView);
        nameImageView.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        ImageView surnameImageView = (ImageView) findViewById(R.id.surnameImageView);
        surnameImageView.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        ImageView emailImageView = (ImageView) findViewById(R.id.emailImageView);
        emailImageView.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);

        User user = UserPreferences.getUser(this);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        nameEditText.setText(user.getFirstName());
        nameEditText.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorBlack), PorterDuff.Mode.SRC_IN);
        surnameEditText = (EditText) findViewById(R.id.surnameEditText);
        surnameEditText.setText(user.getSecondName());
        surnameEditText.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorBlack), PorterDuff.Mode.SRC_IN);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        emailEditText.setText(user.getEmail());
        emailEditText.getBackground().setColorFilter(ContextCompat.getColor(this, R.color.colorBlack), PorterDuff.Mode.SRC_IN);

        Analytics.sendScreen(Analytics.SCREEN_USER_INFO_FORM);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done_appbar, menu);
        menu.findItem(R.id.done).setActionView(doneImageView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        saveUserInfo();
        super.onBackPressed();
    }

    private boolean saveUserInfo() {
        String name = nameEditText.getText().toString();
        if (name.length() == 0) {
            showSnackBar("Необходимо ввести имя");
            return false;
        }
        String surname = surnameEditText.getText().toString();
        if (surname.length() == 0) {
            showSnackBar("Необходимо ввести фамилию");
            return false;
        }
        String email = emailEditText.getText().toString();
        if (email.length() == 0) {
            showSnackBar("Необходимо ввести почту");
            return false;
        }

        User user = UserPreferences.getUser(this);
        user.setName(name + " " + surname);
        user.setEmail(email);
        UserPreferences.setUser(this, user);
        user.sendUserInfoToServer();
        return true;
    }

    private void showSnackBar(String message) {
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAction(getResources().getString(R.string.dialog_rules_ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        snackbar.setActionTextColor(ContextCompat.getColor(this, R.color.colorRightAnswer));
        snackbar.show();
    }
}
