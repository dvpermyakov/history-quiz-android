package dvpermyakov.historyquiz.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.widget.Button;

import dvpermyakov.historyquiz.R;

/**
 * Created by dvpermyakov on 12.10.2016.
 */
public class AwardDialog {
    public interface OnAwardClickListener {
        void onAwardClick();
    }

    private Context context;
    private OnAwardClickListener listener;

    public AwardDialog(Context context, OnAwardClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void show(String title, String message, final int award) {
        final Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.ic_coins).getConstantState().newDrawable();

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog);
        builder.setCancelable(false);
        builder.setTitle(title);
        if (message != null) {
            builder.setMessage(message);
        }
        builder.setPositiveButton(context.getString(R.string.dialog_balance_ok) + " " + Integer.toString(award),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drawable.clearColorFilter();
                        listener.onAwardClick();
                        dialog.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                drawable.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
                drawable.setBounds(0, 0,
                        (int) (drawable.getIntrinsicWidth() / 2.5),
                        (int) (drawable.getIntrinsicHeight() / 2.5));
                button.setCompoundDrawables(null, null, drawable, null);
                button.setCompoundDrawablePadding(3);
            }
        });

        dialog.show();
    }
}
