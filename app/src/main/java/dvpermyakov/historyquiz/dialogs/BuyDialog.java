package dvpermyakov.historyquiz.dialogs;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;

/**
 * Created by dvpermyakov on 12.10.2016.
 */
public class BuyDialog {
    public interface OnBuyClickListener {
        void onBuyClick(int position);
    }

    private Context context;
    private OnBuyClickListener listener;
    private int position;

    public BuyDialog(Context context, OnBuyClickListener listener, int position) {
        this.context = context;
        this.listener = listener;
        this.position = position;
    }

    public void show(String title, String message, int price) {
        final Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.ic_coins).getConstantState().newDrawable();

        LayoutInflater factory = LayoutInflater.from(context);
        View dialogView = factory.inflate(R.layout.dialog_buy, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog);

        builder.setView(dialogView);
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(message);

        final AlertDialog dialog = builder.create();

        ((ImageView)dialogView.findViewById(R.id.balanceBuyImageView)).setColorFilter(
                ContextCompat.getColor(context, R.color.colorBlackLight), PorterDuff.Mode.SRC_IN);
        ((TextView)dialogView.findViewById(R.id.coinsBalanceTextView)).setText(
                context.getResources().getString(R.string.coins_balance) + " " +
                        Integer.toString(DataBaseHelperFactory.getHelper().getCoinsTransactionDao().getCoinsBalance()));

        Button okButton = (Button) dialogView.findViewById(R.id.okButton);
        okButton.setText(context.getString(R.string.dialog_ok));
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawable.clearColorFilter();
                dialog.dismiss();
            }
        });
        Button buyButton = (Button) dialogView.findViewById(R.id.buyButton);
        CharSequence priceString = context.getString(R.string.dialog_buy) + " " + Integer.toString(price);
        buyButton.setText(priceString);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawable.clearColorFilter();
                dialog.dismiss();
                listener.onBuyClick(position);
            }
        });
        drawable.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
        drawable.setBounds(0, 0,
                (int) (drawable.getIntrinsicWidth() / 2.5),
                (int) (drawable.getIntrinsicHeight() / 2.5));
        buyButton.setCompoundDrawables(null, null, drawable, null);
        buyButton.setCompoundDrawablePadding(3);

        dialog.show();
    }
}
