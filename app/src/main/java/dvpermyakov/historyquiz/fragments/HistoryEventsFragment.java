package dvpermyakov.historyquiz.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.activities.BalanceActivity;
import dvpermyakov.historyquiz.activities.CoinsActivity;
import dvpermyakov.historyquiz.activities.HistoryMarkActivity;
import dvpermyakov.historyquiz.adapters.HistoryEventsAdapter;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.dialogs.BuyDialog;
import dvpermyakov.historyquiz.listeners.RecyclerItemClickListener;
import dvpermyakov.historyquiz.models.CoinsTransaction;
import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 21.05.2016.
 */
public class HistoryEventsFragment extends Fragment implements BuyDialog.OnBuyClickListener {
    private Context context;
    private List<Event> events;
    private HistoryEventsAdapter eventsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        events = getArguments().getParcelableArrayList(IntentStrings.INTENT_HISTORY_EVENTS_PARAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_events, container, false);
        RecyclerView recyclerView = ((RecyclerView) view.findViewById(R.id.recyclerViewEvents));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {
                            if (DataBaseHelperFactory.getHelper().getEventDao().queryForId(events.get(position).getId()).isOpened()) {
                                startActivity(new Intent(context, HistoryMarkActivity.class)
                                        .putExtra(IntentStrings.INTENT_HISTORY_MARK_PARAM, events.get(position)));
                            } else {
                                showHowToOpenDialog(position);
                                Analytics.sendEvent(Analytics.SCREEN_MARK_INFO + events.get(position).getName(), Analytics.ACTION_CLOSED_MARK);
                            }
                        } catch (SQLException | ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                }));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        eventsAdapter = new HistoryEventsAdapter(context, events);
        recyclerView.setAdapter(eventsAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        if (eventsAdapter != null) {
            eventsAdapter.refresh();
        }
    }

    private void showHowToOpenDialog(int position) {
        BuyDialog dialog = new BuyDialog(getActivity(), this, position);
        dialog.show(
                getResources().getString(R.string.dialog_how_to_open_title),
                getResources().getString(R.string.dialog_how_to_open_text),
                Math.abs(CoinsTransaction.CoinsTransactionCategory.BUY_HISTORY_MARK.getValue()));
    }

    @Override
    public void onBuyClick(int position) {
        Event event = events.get(position);
        int balance = DataBaseHelperFactory.getHelper().getCoinsTransactionDao().getCoinsBalance();
        if (balance + CoinsTransaction.CoinsTransactionCategory.BUY_HISTORY_MARK.getValue() >= 0) {
            event.setIsOpened(true);
            event.setOpenedDate(new Date());
            DataBaseHelperFactory.getHelper().getEventDao().saveEvent(event);

            CoinsTransaction transaction = new CoinsTransaction(CoinsTransaction.CoinsTransactionCategory.BUY_HISTORY_MARK);
            DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(context, transaction);
            ((CoinsActivity) getActivity()).startShrinkAnimation();

            refresh();
        } else {
            startActivity(new Intent(context, BalanceActivity.class));
        }
    }
}
