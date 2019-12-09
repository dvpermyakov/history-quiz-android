package dvpermyakov.historyquiz.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKScopes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.dialogs.AwardDialog;
import dvpermyakov.historyquiz.dialogs.BuyDialog;
import dvpermyakov.historyquiz.dialogs.SocialNetworksDialog;
import dvpermyakov.historyquiz.fragments.QuestionFragment;
import dvpermyakov.historyquiz.fragments.TestEndingFragment;
import dvpermyakov.historyquiz.managers.PlayServiceGameManager;
import dvpermyakov.historyquiz.managers.PlayServiceGameManagerFactory;
import dvpermyakov.historyquiz.managers.TestGameManager;
import dvpermyakov.historyquiz.managers.VKPostWallManager;
import dvpermyakov.historyquiz.managers.VKUserManager;
import dvpermyakov.historyquiz.models.Answer;
import dvpermyakov.historyquiz.models.CoinsTransaction;
import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.HistoryEntity;
import dvpermyakov.historyquiz.models.HistoryEntityCategory;
import dvpermyakov.historyquiz.models.Period;
import dvpermyakov.historyquiz.models.Person;
import dvpermyakov.historyquiz.models.Question;
import dvpermyakov.historyquiz.models.SocialNetwork;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.TestResult;
import dvpermyakov.historyquiz.models.TestResultCategory;
import dvpermyakov.historyquiz.models.User;
import dvpermyakov.historyquiz.network.RequestQueueFactory;
import dvpermyakov.historyquiz.network.constants.Params;
import dvpermyakov.historyquiz.network.constants.Urls;
import dvpermyakov.historyquiz.network.requests.TestQuestionsRequest;
import dvpermyakov.historyquiz.network.responses.TestQuestionsResponse;
import dvpermyakov.historyquiz.preferences.RewardPreferences;
import dvpermyakov.historyquiz.preferences.UserPreferences;
import dvpermyakov.historyquiz.preferences.PreferencesStrings;
import dvpermyakov.historyquiz.specials.IntentStrings;
import dvpermyakov.historyquiz.specials.LogTag;

/**
 * Created by dvpermyakov on 27.05.2016.
 */
public class TestGameActivity extends CoinsActivity implements
        QuestionFragment.OnAnswerClickListener, TestGameManager.OnSecondUpListener, TestEndingFragment.OnRetryClickListener {
    private TestGameManager.OnSecondUpListener listener;
    private Context context;
    private Test test;
    private HistoryEntity historyEntity;
    private TestGameManager gameManager;
    private boolean continueTestDialogShow = false;
    private QuestionFragment questionFragment;
    private TestEndingFragment testEndingFragment;
    private boolean paused;
    private boolean postponedNextQuestion;
    private boolean postponedEnding;

    private ProgressBar progressBar;
    private ProgressBar questionProgressBar;
    private ProgressBar timerProgressBar;
    private TextView questionNumber;
    private TextView secondRest;
    private TextView mistakeRest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        listener = this;
        paused = false;
        postponedNextQuestion = false;
        postponedEnding = false;

        test = getIntent().getParcelableExtra(IntentStrings.INTENT_TEST_PARAM);
        historyEntity = getIntent().getParcelableExtra(IntentStrings.INTENT_HISTORY_ENTITY_PARAM);

        setContentView(R.layout.activity_test_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.loadContentProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        questionProgressBar = (ProgressBar) findViewById(R.id.questionAmountProgressBar);
        questionProgressBar.setMax(test.getQuestionAmount());
        questionProgressBar.getProgressDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorRightAnswer), PorterDuff.Mode.SRC_IN);
        timerProgressBar = (ProgressBar) findViewById(R.id.timerProgressBar);
        timerProgressBar.setMax(test.getMaxSeconds());
        timerProgressBar.getProgressDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorWrongAnswer), PorterDuff.Mode.SRC_IN);
        questionNumber = (TextView) findViewById(R.id.questionAmountTextView);
        secondRest = (TextView) findViewById(R.id.timerTextView);
        mistakeRest = (TextView) findViewById(R.id.mistakeRestAmountTextView);

        Drawable drawable;
        drawable = ContextCompat.getDrawable(this, R.drawable.ic_question_answer);
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryLight), PorterDuff.Mode.SRC_IN);
        questionNumber.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        questionNumber.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.test_icon_text_padding));
        drawable = ContextCompat.getDrawable(this, R.drawable.ic_timer);
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryLight), PorterDuff.Mode.SRC_IN);
        secondRest.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        secondRest.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.test_icon_text_padding));
        drawable = ContextCompat.getDrawable(this, R.drawable.ic_favorite);
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryLight), PorterDuff.Mode.SRC_IN);
        mistakeRest.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        mistakeRest.setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.test_icon_text_padding));

        setVisibleGameElements(false);

        downloadQuestions();

        Analytics.sendScreen(Analytics.SCREEN_TEST + historyEntity.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
        if (postponedEnding) createTestEndingFragment();
        else if (postponedNextQuestion)
            createQuestionFragment(gameManager.getCurrentQuestion().getRandomAnswer(4), new ArrayList<Integer>());
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    private void downloadQuestions() {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue volleyQueue = RequestQueueFactory.getRequestQueue();
        volleyQueue.cancelAll(this);
        Map<String, String> getParams = Params.getParams(Urls.TEST_QUESTIONS_REQUEST_URL);
        getParams.put("test_id", test.getId());
        Request request = new TestQuestionsRequest(new Response.Listener<TestQuestionsResponse>() {
            @Override
            public void onResponse(TestQuestionsResponse response) {
                DataBaseHelperFactory.getHelper().getQuestionDao().removeQuestions(test);
                for (Question question : response.getQuestions()) {
                    question.setTest(test);
                    DataBaseHelperFactory.getHelper().getQuestionDao().saveQuestion(question);
                    DataBaseHelperFactory.getHelper().getAnswerDao().removeAnswer(question);
                    for (Answer answer : question.getAnswers()) {
                        answer.setQuestion(question);
                        DataBaseHelperFactory.getHelper().getAnswerDao().saveAnswer(answer);
                    }
                }
                startTestGame();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LogTag.TAG_NETWORK, "Fail!");
                if (DataBaseHelperFactory.getHelper().getQuestionDao().haveQuestions(test)) {
                    startTestGame();
                } else {
                    progressBar.setVisibility(View.GONE);
                    if (!isFinishing()) showNotNetworkDialog();
                }
            }
        }, getParams);
        request.setTag(this);
        volleyQueue.add(request);
    }

    private void startTestGame() {
        if (DataBaseHelperFactory.getHelper().getQuestionDao().haveQuestions(test) && test.getQuestionAmount() > 0) {
            gameManager = new TestGameManager(listener, test);
            createQuestionFragment(gameManager.getCurrentQuestion().getRandomAnswer(4), new ArrayList<Integer>());
            secondRest.setText(gameManager.getSecondRestString());
            mistakeRest.setText(gameManager.getMistakeRest());
            setVisibleGameElements(true);
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            showNotQuestionsDialog();
        }
    }

    private void createQuestionFragment(List<Answer> answers, List<Integer> clickedSet) {
        if (paused) {
            postponedNextQuestion = true;
            return;
        }

        Bundle args = new Bundle();
        args.putParcelable(IntentStrings.INTENT_QUESTION_PARAM, gameManager.getCurrentQuestion());
        args.putParcelableArrayList(IntentStrings.INTENT_ANSWERS_PARAM, new ArrayList<>(answers));
        args.putIntegerArrayList(IntentStrings.INTENT_CLICKED_SET_PARAM, new ArrayList<>(clickedSet));
        questionFragment = new QuestionFragment();
        questionFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.questionContainer, questionFragment)
                .commit();
        questionNumber.setText(gameManager.getQuestionAmountString());
        questionProgressBar.setProgress(gameManager.getCurrentQuestionNumber() + 1);
        timerProgressBar.setProgress(gameManager.getCurrentSecondRest());
    }

    private void createTestEndingFragment() {
        if (paused) {
            postponedEnding = true;
            return;
        }
        boolean isTestClosed = DataBaseHelperFactory.getHelper().getTestResultDao().isTestClosed(test);

        TestResult result = gameManager.closeGame();
        DataBaseHelperFactory.getHelper().getTestResultDao().save(result);
        Bundle args = new Bundle();
        args.putParcelable(IntentStrings.INTENT_TEST_PARAM, test);
        args.putParcelable(IntentStrings.INTENT_TEST_RESULT_PARAM, result);
        args.putParcelable(IntentStrings.INTENT_HISTORY_ENTITY_PARAM, historyEntity);
        testEndingFragment = new TestEndingFragment();
        testEndingFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.questionContainer, testEndingFragment)
                .addToBackStack(null)
                .commit();
        setVisibleGameElements(false);

        if (result.getResultCategory() == TestResultCategory.WIN && !isTestClosed) {
            Period period = historyEntity.getPeriod();
            if (period != null) {
                period.increaseCountDone();
                DataBaseHelperFactory.getHelper().getPeriodDao().savePeriod(period);
            } else {
                Period.updatePeriodCountByServer(historyEntity);
            }
            test.sendDoneToServer();
            User user = UserPreferences.getUser(this);
            user.setMarksDoneCount(user.getMarksDoneCount() + 1);
            UserPreferences.setUser(this, user);

            if (!RewardPreferences.getFirstDoneTestCoinsReward(this)) {
                showDoneTestRewardDialog();
            } else {
                int balance = DataBaseHelperFactory.getHelper().getCoinsTransactionDao().getCoinsBalance();
                if (UserPreferences.getContinueTestNumber(this) >= 2 && !UserPreferences.getShareAdvice(this) && balance <= 70 &&
                        VKAccessToken.tokenFromSharedPreferences(context, PreferencesStrings.VK_LOGIN_TOKEN) == null) {
                    showAdviceShareDialog();
                }
                setTestDoneAward();
            }
        }

        if (!continueTestDialogShow) {
            if ((result.getResultCategory() == TestResultCategory.MISTAKE_LIMIT && gameManager.getQuestionProgress() > 0.5)
                    || result.getResultCategory() == TestResultCategory.TIME_LIMIT) {
                int balance = DataBaseHelperFactory.getHelper().getCoinsTransactionDao().getCoinsBalance();
                if (balance + 3 * CoinsTransaction.CoinsTransactionCategory.CONTINUE_TEST.getValue() >= 0) {
                    showContinueTestDialog();
                }
            }
        }

        switch (result.getResultCategory()) {
            case WIN:
                Analytics.sendEvent(Analytics.SCREEN_TEST + historyEntity.getName(), Analytics.ACTION_WIN);
                break;
            case TIME_LIMIT:
                Analytics.sendEvent(Analytics.SCREEN_TEST + historyEntity.getName(), Analytics.ACTION_TIME_LIMIT);
                break;
            case MISTAKE_LIMIT:
                Analytics.sendEvent(Analytics.SCREEN_TEST + historyEntity.getName(), Analytics.ACTION_MISTAKE_LIMIT);
                break;
            case CANCEL:
                Analytics.sendEvent(Analytics.SCREEN_TEST + historyEntity.getName(), Analytics.ACTION_CANCEL);
                break;
        }
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
        if (gameManager == null || gameManager.isClosed()) {
            finish();
        } else {
            showCancelDialog();
        }
    }

    public void setVisibleGameElements(boolean visible) {
        int visibility = View.GONE;
        if (visible) visibility = View.VISIBLE;

        questionProgressBar.setVisibility(visibility);
        timerProgressBar.setVisibility(visibility);
        questionNumber.setVisibility(visibility);
        secondRest.setVisibility(visibility);
        mistakeRest.setVisibility(visibility);
    }

    @Override
    public void onAnswerClick(final boolean correct) {
        gameManager.putAnswerUpdate(correct);
        if (gameManager.isUserFail()) {
            questionFragment.blockAnswers();
        }
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!gameManager.isUserFail() && !gameManager.isUserWin()) {
                    if (correct) {
                        createQuestionFragment(gameManager.getCurrentQuestion().getRandomAnswer(4), new ArrayList<Integer>());
                    }
                } else {
                    createTestEndingFragment();
                }
            }
        }, 1000);
        mistakeRest.setText(gameManager.getMistakeRest());
    }

    @Override
    public void onSecondUp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                secondRest.setText(gameManager.getSecondRestString());
                timerProgressBar.setProgress(gameManager.getCurrentSecondRest());
                if (!gameManager.isClosed() && gameManager.isUserFail()) {
                    createTestEndingFragment();
                }
            }
        });
    }

    @Override
    public void onRetryClick() {
        if (testEndingFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.questionContainer, testEndingFragment)
                    .commit();
        }
        startTestGame();
    }

    @Override
    public void onContinueClick() {
        if (gameManager.isUserWin()) {
            String[] scopes = new String[] {VKScopes.WALL, VKScopes.PHOTOS};
            if (VKUserManager.getToken(this) == null || !VKUserManager.hasScopes(this, scopes)) {
                showSocialNetworksDialog(scopes);
            } else {
                makePost();
            }
        } else {
            int balance = DataBaseHelperFactory.getHelper().getCoinsTransactionDao().getCoinsBalance();
            if (balance + CoinsTransaction.CoinsTransactionCategory.CONTINUE_TEST.getValue() >= 0) {
                buyContinueTest();
            } else {
                startActivity(new Intent(this, BalanceActivity.class));
            }
        }
    }

    private void buyContinueTest() {
        UserPreferences.setContinueTestNumber(this, UserPreferences.getContinueTestNumber(this) + 1);
        continueTestDialogShow = true;

        if (questionFragment != null && gameManager.reviveGame()) {
            CoinsTransaction transaction = new CoinsTransaction(CoinsTransaction.CoinsTransactionCategory.CONTINUE_TEST);
            DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(this, transaction);
            startShrinkAnimation();

            createQuestionFragment(questionFragment.getAnswers(), new ArrayList<>(questionFragment.getClickedSet()));
            secondRest.setText(gameManager.getSecondRestString());
            mistakeRest.setText(gameManager.getMistakeRest());
            setVisibleGameElements(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            boolean success = data.getBooleanExtra(IntentStrings.INTENT_LOGIN_VK_SUCCESS, false);
            if (success) {
                makePost();
            } else {
                showShareFailDialog();
            }
        }
    }

    private void makePost() {
        if (!isShared(false)) {
            final ProgressDialog progressDialog = ProgressDialog.show(this, "", getResources().getString(R.string.loading), true);
            new VKPostWallManager(this, historyEntity, new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    isShared(true);
                    progressDialog.dismiss();
                    showShareRewardDialog();
                }
                @Override
                public void onError(VKError error) {
                    progressDialog.dismiss();
                    showShareFailDialog();
                }
            }, new VKPostWallManager.OnLoadingListener() {
                @Override
                public void onLoadFail() {
                    progressDialog.dismiss();
                    showShareFailDialog();
                }
            });
        } else {
            showSharedAlreadyDoneDialog();
        }
    }

    private boolean isShared(boolean setShared) {
        if (historyEntity.getCategory() == HistoryEntityCategory.EVENT) {
            Event event = DataBaseHelperFactory.getHelper().getEventDao().getById(historyEntity.getId());
            if (setShared) {
                event.setShared();
                DataBaseHelperFactory.getHelper().getEventDao().saveEvent(event);
            }
            return event.isShared();
        }
        if (historyEntity.getCategory() == HistoryEntityCategory.PERSON) {
            Person person = DataBaseHelperFactory.getHelper().getPersonDao().getById(historyEntity.getId());
            if (setShared) {
                person.setShared();
                DataBaseHelperFactory.getHelper().getPersonDao().savePerson(person);
            }
            return person.isShared();
        }
        if (historyEntity.getCategory() == HistoryEntityCategory.PERIOD) {
            Period period = DataBaseHelperFactory.getHelper().getPeriodDao().getById(historyEntity.getId());
            if (setShared) {
                period.setShared();
                DataBaseHelperFactory.getHelper().getPeriodDao().savePeriod(period);
            }
            return period.isShared();
        }
        return false;
    }

    private void setTestDoneAward() {
        CoinsTransaction transaction = new CoinsTransaction(CoinsTransaction.CoinsTransactionCategory.DONE_TEST);
        DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(context, transaction);
        startEnlargeAnimation();
        PlayServiceGameManager manager = PlayServiceGameManagerFactory.getManager();
        if (manager.isConnected()) {
            manager.sendTestDoneEvent();
        } else {
            test.saveNeedSendToPlayService(true);
        }
    }

    private void showSocialNetworksDialog(final String[] scopesVK) {
        Bundle args = new Bundle();
        args.putString(IntentStrings.INTENT_SOCIAL_NETWORKS_TITLE, "Поделиться с помощью:");
        List<SocialNetwork> socials = new ArrayList<>();
        socials.add(new SocialNetwork(SocialNetwork.SocialType.VK, R.mipmap.ic_vk_dark, "Вконтакте"));
        args.putParcelableArrayList(IntentStrings.INTENT_SOCIAL_NETWORKS_PARAM, (ArrayList<? extends Parcelable>) socials);
        SocialNetworksDialog dialog = new SocialNetworksDialog();
        dialog.setArguments(args);
        dialog.setListener(new SocialNetworksDialog.SocialOnClickListener() {
            @Override
            public void onClick(SocialNetwork.SocialType type) {
                switch (type) {
                    case VK:
                        startActivityForResult(new Intent(context, VKLoginActivity.class)
                                .putExtra(IntentStrings.INTENT_VK_SCOPE, scopesVK), 0);
                }
            }
        });
        dialog.show(getSupportFragmentManager(), "tag");
    }

    private void showContinueTestDialog() {
        continueTestDialogShow = true;
        BuyDialog dialog = new BuyDialog(this, new BuyDialog.OnBuyClickListener() {
            @Override
            public void onBuyClick(int position) {
                buyContinueTest();
            }
        }, 0);
        dialog.show(
                getResources().getString(R.string.dialog_continue_test_title),
                getResources().getString(R.string.dialog_continue_test_text),
                Math.abs(CoinsTransaction.CoinsTransactionCategory.CONTINUE_TEST.getValue()));

        Analytics.sendEvent(Analytics.CATEGORY_ADVICE, Analytics.ACTION_CONTINUE_ADVICE);
    }

    private void showShareFailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.dialog_share_fail_title));
        builder.setMessage(getResources().getString(R.string.dialog_share_fail_message));
        builder.setPositiveButton(getString(R.string.dialog_share_fail_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();

        Analytics.sendEvent(Analytics.CATEGORY_SHARE, Analytics.ACTION_FAIL_VK_SHARE);
    }

    private void showSharedAlreadyDoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.dialog_share_already_title));
        builder.setMessage(getResources().getString(R.string.dialog_share_already_message));
        builder.setPositiveButton(getString(R.string.dialog_share_already_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void showShareRewardDialog() {
        AwardDialog dialog = new AwardDialog(this, new AwardDialog.OnAwardClickListener() {
            @Override
            public void onAwardClick() {
                CoinsTransaction transaction = new CoinsTransaction(CoinsTransaction.CoinsTransactionCategory.SHARE_TEST_RESULT);
                DataBaseHelperFactory.getHelper().getCoinsTransactionDao().saveTransaction(context, transaction);
                startEnlargeAnimation();
            }
        });
        dialog.show(
                getResources().getString(R.string.dialog_share_title),
                getResources().getString(R.string.dialog_share_text),
                CoinsTransaction.CoinsTransactionCategory.SHARE_TEST_RESULT.getValue());
        Analytics.sendEvent(Analytics.CATEGORY_SHARE, Analytics.ACTION_VK_SHARE);
    }

    private void showDoneTestRewardDialog() {
        AwardDialog dialog = new AwardDialog(this, new AwardDialog.OnAwardClickListener() {
            @Override
            public void onAwardClick() {
                setTestDoneAward();
                RewardPreferences.setFirstDoneTestCoinsReward(context, true);
            }
        });
        dialog.show(
                getResources().getString(R.string.dialog_done_test_reward_title),
                getResources().getString(R.string.dialog_done_test_reward_text),
                CoinsTransaction.CoinsTransactionCategory.DONE_TEST.getValue());
    }

    private void showAdviceShareDialog() {
        UserPreferences.setShareAdvice(this, true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.dialog_share_advice_title));
        builder.setMessage(getResources().getString(R.string.dialog_share_advice_text));
        builder.setPositiveButton(getString(R.string.dialog_share_advice_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(context, VKLoginActivity.class), 0);
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_share_advice_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();

        Analytics.sendEvent(Analytics.CATEGORY_ADVICE, Analytics.ACTION_SHARE_ADVICE);
    }

    private void showNotNetworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.dialog_not_network_title));
        builder.setMessage(getResources().getString(R.string.dialog_not_network_text));
        builder.setPositiveButton(getString(R.string.dialog_not_network_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadQuestions();
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_not_network_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.create().show();
    }

    private void showNotQuestionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.dialog_not_questions_title));
        builder.setMessage(getResources().getString(R.string.dialog_not_questions_text));
        builder.setPositiveButton(getString(R.string.dialog_not_questions_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.create().show();
    }

    private void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.dialog_test_title));
        builder.setPositiveButton(getString(R.string.dialog_test_yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (!gameManager.isClosed()) createTestEndingFragment();
                    }
                });
        builder.setNegativeButton(getString(R.string.dialog_test_no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
