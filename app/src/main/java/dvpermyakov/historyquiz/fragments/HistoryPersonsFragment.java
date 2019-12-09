package dvpermyakov.historyquiz.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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
import dvpermyakov.historyquiz.adapters.HistoryPersonsAdapter;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.dialogs.BuyDialog;
import dvpermyakov.historyquiz.listeners.RecyclerItemClickListener;
import dvpermyakov.historyquiz.models.CoinsTransaction;
import dvpermyakov.historyquiz.models.Person;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 21.05.2016.
 */
public class HistoryPersonsFragment extends Fragment implements BuyDialog.OnBuyClickListener {
    private Context context;
    private List<Person> persons;
    private HistoryPersonsAdapter personsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        persons = getArguments().getParcelableArrayList(IntentStrings.INTENT_HISTORY_PERSONS_PARAM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_persons, container, false);
        RecyclerView recyclerView = ((RecyclerView) view.findViewById(R.id.recyclerViewPersons));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        try {
                            if (DataBaseHelperFactory.getHelper().getPersonDao().queryForId(persons.get(position).getId()).isOpened()) {
                                startActivity(new Intent(context, HistoryMarkActivity.class)
                                        .putExtra(IntentStrings.INTENT_HISTORY_MARK_PARAM, persons.get(position)));
                            } else {
                                showHowToOpenDialog(position);
                                Analytics.sendEvent(Analytics.SCREEN_MARK_INFO + persons.get(position).getName(), Analytics.ACTION_CLOSED_MARK);
                            }
                        } catch (SQLException | ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                }));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        personsAdapter = new HistoryPersonsAdapter(context, persons);
        recyclerView.setAdapter(personsAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        if (personsAdapter != null) {
            personsAdapter.refresh();
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
        Person person = persons.get(position);
        int balance = DataBaseHelperFactory.getHelper().getCoinsTransactionDao().getCoinsBalance();
        if (balance + CoinsTransaction.CoinsTransactionCategory.BUY_HISTORY_MARK.getValue() >= 0) {
            person.setIsOpened(true);
            person.setOpenedDate(new Date());
            DataBaseHelperFactory.getHelper().getPersonDao().savePerson(person);

            CoinsTransaction transaction = new CoinsTransaction(CoinsTransaction.CoinsTransactionCategory.BUY_HISTORY_MARK);
            DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(context, transaction);
            ((CoinsActivity) context).startShrinkAnimation();

            refresh();
        } else {
            startActivity(new Intent(context, BalanceActivity.class));
        }
    }
}
