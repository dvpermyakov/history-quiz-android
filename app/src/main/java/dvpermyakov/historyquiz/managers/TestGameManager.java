package dvpermyakov.historyquiz.managers;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import dvpermyakov.historyquiz.database.DataBaseHelperFactory;
import dvpermyakov.historyquiz.models.Question;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.TestResult;
import dvpermyakov.historyquiz.models.TestResultCategory;

/**
 * Created by dvpermyakov on 28.05.2016.
 */
public class TestGameManager {
    public interface OnSecondUpListener {
        void onSecondUp();
    }
    private OnSecondUpListener listener;
    private int currentMistakeNumber;
    private int currentQuestionNumber;
    private int currentSecondNumber;
    private Test test;
    private List<Question> questions;
    private boolean isUserWin;
    private boolean isUserFail;
    private Timer timer;
    private TestResult testResult;

    public TestGameManager(OnSecondUpListener listener, Test test) {
        this.listener = listener;
        this.test = test;
        currentQuestionNumber = 0;
        currentMistakeNumber = 0;
        currentSecondNumber = 0;
        isUserWin = false;
        isUserFail = false;

        questions = DataBaseHelperFactory.getHelper().getQuestionDao().getQuestions(test);
        Collections.shuffle(questions, new Random(System.nanoTime()));
        questions = questions.subList(0, test.getQuestionAmount());

        schedule();
    }

    public void schedule() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isUserWin && !isUserFail) updateSecondRest();
            }
        }, 1000, 1000);
    }

    public Question getCurrentQuestion() {
        return questions.get(currentQuestionNumber);
    }

    public String getQuestionAmountString() {
        return String.format("%s из %s", currentQuestionNumber + 1, questions.size());
    }

    public int getCurrentQuestionNumber() {
        return currentQuestionNumber;
    }

    public String getMistakeRest() {
        return String.valueOf(test.getMaxMistakes() - currentMistakeNumber);
    }

    public String getSecondRestString() {
        return String.valueOf(test.getMaxSeconds() - currentSecondNumber);
    }

    public int getCurrentSecondRest() {
        return currentSecondNumber;
    }

    public void updateSecondRest() {
        if (currentSecondNumber < test.getMaxSeconds()) {
            currentSecondNumber += 1;
        } else {
            isUserFail = true;
        }
        listener.onSecondUp();
    }

    public void putAnswerUpdate(boolean correct) {
        if (correct) {
            currentQuestionNumber += 1;
        } else {
            currentMistakeNumber += 1;
        }
        if (currentQuestionNumber == questions.size()) {
            isUserWin = true;
        }
        if (currentMistakeNumber == test.getMaxMistakes()) {
            isUserFail = true;
        }
    }

    public double getQuestionProgress() {
        return (currentQuestionNumber + 1) / (double)questions.size();
    }

    public boolean isUserWin() {
        return isUserWin;
    }

    public boolean isUserFail() {
        return isUserFail;
    }

    public boolean isClosed() {
        return testResult != null;
    }

    public TestResult closeGame() {
        TestResultCategory resultCategory = TestResultCategory.DEFAULT;
        if (isUserWin) resultCategory = TestResultCategory.WIN;
        else if(isUserFail) {
            if (currentMistakeNumber >= test.getMaxMistakes()) resultCategory = TestResultCategory.MISTAKE_LIMIT;
            if (currentSecondNumber >= test.getMaxSeconds()) resultCategory = TestResultCategory.TIME_LIMIT;
        } else {
            resultCategory = TestResultCategory.CANCEL;
        }
        testResult = new TestResult(test, resultCategory, currentQuestionNumber, currentMistakeNumber, currentSecondNumber);
        timer.cancel();
        return testResult;
    }

    public boolean reviveGame() {
        if (testResult == null) return false;
        switch (testResult.getResultCategory()) {
            case WIN:
                return false;
            case TIME_LIMIT:
                currentSecondNumber = Math.max(0, currentSecondNumber - 10);
                break;
            case MISTAKE_LIMIT:
                currentMistakeNumber -= 1;
                break;
            case CANCEL:
                break;
        }
        isUserFail = false;
        isUserWin = false;
        testResult = null;
        schedule();
        return true;
    }
}
