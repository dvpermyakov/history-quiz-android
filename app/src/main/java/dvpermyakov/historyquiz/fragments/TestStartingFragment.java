package dvpermyakov.historyquiz.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.activities.BalanceActivity;
import dvpermyakov.historyquiz.activities.CoinsActivity;
import dvpermyakov.historyquiz.activities.TestGameActivity;
import dvpermyakov.historyquiz.adapter_models.CardViewInformationModel;
import dvpermyakov.historyquiz.adapters.CardViewInformationAdapter;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.decorations.DividerItemDecoration;
import dvpermyakov.historyquiz.dialogs.BuyDialog;
import dvpermyakov.historyquiz.models.CoinsTransaction;
import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.HistoryEntity;
import dvpermyakov.historyquiz.models.HistoryMark;
import dvpermyakov.historyquiz.models.HistoryEntityCategory;
import dvpermyakov.historyquiz.models.Period;
import dvpermyakov.historyquiz.models.Person;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.specials.IntentStrings;

/**
 * Created by dvpermyakov on 26.05.2016.
 */
public class TestStartingFragment extends Fragment implements BuyDialog.OnBuyClickListener {
    private Context context;
    private Test test;
    private HistoryEntity historyEntity;
    private List<HistoryMark> conditionMarks;
    private CardViewInformationAdapter userStatisticsAdapter;
    private CardViewInformationAdapter dependenciesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        test = getArguments().getParcelable(IntentStrings.INTENT_TEST_PARAM);
        historyEntity = getArguments().getParcelable(IntentStrings.INTENT_HISTORY_MARK_PARAM_TEST_START);
        conditionMarks = getArguments().getParcelableArrayList(IntentStrings.INTENT_HISTORY_MARK_CONDITIONS_PARAM);
        List<HistoryMark> conditionMarks = new ArrayList<>();
        for (HistoryMark mark : this.conditionMarks) {
            if (mark.getCategory() == HistoryEntityCategory.PERIOD)
                conditionMarks.add(DataBaseHelperFactory.getHelper().getPeriodDao().getById(mark.getId()));
            if (mark.getCategory() == HistoryEntityCategory.EVENT)
                conditionMarks.add(DataBaseHelperFactory.getHelper().getEventDao().getById(mark.getId()));
            if (mark.getCategory() == HistoryEntityCategory.PERSON)
                conditionMarks.add(DataBaseHelperFactory.getHelper().getPersonDao().getById(mark.getId()));
        }
        this.conditionMarks.clear();
        this.conditionMarks.addAll(conditionMarks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_starting, container, false);
        if (conditionMarks.size() > 0) addCardViewConditions(view);
        addCardViewTestInfo(view);
        addCardViewUserStatistics(view);
        view.findViewById(R.id.startTestButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTest();
            }
        });
        view.findViewById(R.id.rulesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRulesDialog();
                Analytics.sendEvent(Analytics.SCREEN_MARK_INFO + historyEntity.getName(), Analytics.ACTION_RULES);
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUserStatistic();
    }

    public void refreshUserStatistic() {
        if (userStatisticsAdapter != null) {
            userStatisticsAdapter.setNewCardViewInformationModelList(getUserStatisticList());
            userStatisticsAdapter.notifyDataSetChanged();
        }
        if (dependenciesAdapter != null) {
            dependenciesAdapter.setNewCardViewInformationModelList(getDependenciesList());
            dependenciesAdapter.notifyDataSetChanged();
        }
    }

    private void addCardViewConditions(View view) {
        view.findViewById(R.id.conditionsTextView).setVisibility(View.VISIBLE);
        view.findViewById(R.id.conditionsCardView).setVisibility(View.VISIBLE);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewConditions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(context, R.drawable.divider, (int) context.getResources().getDimension(R.dimen.decoration_normal_padding)));
        dependenciesAdapter = new CardViewInformationAdapter(context, getDependenciesList());
        recyclerView.setAdapter(dependenciesAdapter);
    }

    private void addCardViewTestInfo(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewTestInfo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), R.drawable.divider, (int) getResources().getDimension(R.dimen.decoration_normal_padding)));
        List<CardViewInformationModel> list = new ArrayList<>();
        list.add(new CardViewInformationModel(context.getResources().getString(R.string.question_amount_test), String.valueOf(test.getQuestionAmount()), null, null));
        list.add(new CardViewInformationModel(context.getResources().getString(R.string.time_amount_test), String.valueOf(test.getMaxSeconds()), null, null));
        list.add(new CardViewInformationModel(context.getResources().getString(R.string.mistake_amount_test), String.valueOf(test.getMaxMistakes()), null, null));
        CardViewInformationAdapter adapter = new CardViewInformationAdapter(context, list);
        recyclerView.setAdapter(adapter);
    }

    private void addCardViewUserStatistics(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewUserStatistics);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(context, R.drawable.divider, (int) context.getResources().getDimension(R.dimen.decoration_normal_padding)));
        userStatisticsAdapter = new CardViewInformationAdapter(context, getUserStatisticList());
        recyclerView.setAdapter(userStatisticsAdapter);
    }

    private List<CardViewInformationModel> getDependenciesList() {
        List<CardViewInformationModel> list = new ArrayList<>();
        if (historyEntity.getCategory() == HistoryEntityCategory.VIDEO) return list;
        List<Boolean> conditionBooleans = historyEntity.getConditionBooleans();
        int index = 0;
        for (HistoryMark mark : conditionMarks) {
            if (conditionBooleans.get(index++)) {
                list.add(new CardViewInformationModel(mark.getName(), null, R.drawable.ic_done, R.color.colorRightAnswer));
            } else {
                if (this.historyEntity.isHacked()) {
                    list.add(new CardViewInformationModel(mark.getName(), null, R.mipmap.ic_coins, R.color.colorBlackLight));
                } else {
                    list.add(new CardViewInformationModel(mark.getName(), null, null, null));
                }
            }
        }
        return list;
    }

    private List<CardViewInformationModel> getUserStatisticList() {
        List<CardViewInformationModel> list = new ArrayList<>();
        int attemptsAmount = DataBaseHelperFactory.getHelper().getTestResultDao().getAttempts(test);
        int maxResult = DataBaseHelperFactory.getHelper().getTestResultDao().getMaxResult(test);
        list.add(new CardViewInformationModel(context.getResources().getString(R.string.test_attempts), String.valueOf(attemptsAmount), null, null));
        list.add(new CardViewInformationModel(context.getResources().getString(R.string.test_max_result), String.valueOf(maxResult), null, null));
        return list;
    }

    public void startTest() {
        if (historyEntity.canStartTest()) {
            startActivity(new Intent(context, TestGameActivity.class)
                    .putExtra(IntentStrings.INTENT_HISTORY_ENTITY_PARAM, historyEntity)
                    .putExtra(IntentStrings.INTENT_TEST_PARAM, test));
        } else {
            showFailConditionsDialog();
            Analytics.sendEvent(Analytics.SCREEN_MARK_INFO + historyEntity.getName(), Analytics.ACTION_CONDITIONS_FAIL);
        }
    }

    private void showRulesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Dialog);
        builder.setTitle(context.getResources().getString(R.string.dialog_rules_title));
        builder.setMessage(context.getResources().getString(R.string.dialog_rules_text));
        builder.setPositiveButton(context.getString(R.string.dialog_rules_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void showFailConditionsDialog() {
        BuyDialog dialog = new BuyDialog(getActivity(), this, 0);
        dialog.show(
                getResources().getString(R.string.dialog_conditions_title),
                getResources().getString(R.string.dialog_conditions_text),
                Math.abs(CoinsTransaction.CoinsTransactionCategory.BUY_TEST_CONDITIONS.getValue()));
    }

    @Override
    public void onBuyClick(int position) {
        int balance = DataBaseHelperFactory.getHelper().getCoinsTransactionDao().getCoinsBalance();
        if (balance + CoinsTransaction.CoinsTransactionCategory.BUY_TEST_CONDITIONS.getValue() >= 0) {
            historyEntity.setHacked();
            if (historyEntity.getCategory() == HistoryEntityCategory.EVENT) {
                Event event = DataBaseHelperFactory.getHelper().getEventDao().getById(historyEntity.getId());
                event.setHacked();
                DataBaseHelperFactory.getHelper().getEventDao().saveEvent(event);
            }
            if (historyEntity.getCategory() == HistoryEntityCategory.PERSON) {
                Person person = DataBaseHelperFactory.getHelper().getPersonDao().getById(historyEntity.getId());
                person.setHacked();
                DataBaseHelperFactory.getHelper().getPersonDao().savePerson(person);
            }
            if (historyEntity.getCategory() == HistoryEntityCategory.PERIOD) {
                Period period = DataBaseHelperFactory.getHelper().getPeriodDao().getById(historyEntity.getId());
                period.setHacked();
                DataBaseHelperFactory.getHelper().getPeriodDao().savePeriod(period);
            }

            CoinsTransaction transaction = new CoinsTransaction(CoinsTransaction.CoinsTransactionCategory.BUY_TEST_CONDITIONS);
            DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(context, transaction);
            ((CoinsActivity) getActivity()).startShrinkAnimation();

            startTest();
        } else {
            startActivity(new Intent(context, BalanceActivity.class));
        }
    }
}
