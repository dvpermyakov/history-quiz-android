package dvpermyakov.historyquiz.analytics;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import dvpermyakov.historyquiz.BuildConfig;
import dvpermyakov.historyquiz.R;

/**
 * Created by dvpermyakov on 12.09.2016.
 */
public class Analytics {
    private static Tracker tracker;

    public static final String SCREEN_INSTRUCTIONS = "Инструкции: ";
    public static final String SCREEN_MAIN = "Главный экран: ";
    public static final String SCREEN_MARK_INFO = "Информация: ";
    public static final String SCREEN_OPEN_MARK = "Пояснение: ";
    public static final String SCREEN_TEST = "Тестирование: ";
    public static final String SCREEN_FILTERED_HISTORY_MARKS = "Cтатьи по фильтру: ";
    public static final String SCREEN_BALANCE = "Баланс: ";
    public static final String SCREEN_ABOUT = "Как пользоваться?: ";
    public static final String SCREEN_VIDEO_INFO = "Видео инфо: ";
    public static final String SCREEN_GOOGLE_LOGIN = "Авторизация через гугл";
    public static final String SCREEN_USER_INFO_FORM = "Заполнение пользовательских данных";
    public static final String SCREEN_RATING = "Рейтинг";
    public static final String FRAGMENT_NAVIGATION_DRAWER = "Боковое меню: ";

    public static final String CATEGORY_PERIOD = "Период: ";
    public static final String CATEGORY_BALANCE = "Изменение баланса";
    public static final String CATEGORY_FAIL_LOADING = "Неудачная загрузка";
    public static final String CATEGORY_SOCIAL_NETWORKS = "Соц.сети";
    public static final String CATEGORY_SHARE = "Рассказать друзьям";
    public static final String CATEGORY_GROUP_INVITE = "Вступить в группу";
    public static final String CATEGORY_PUSH = "Пуш";
    public static final String CATEGORY_ADVICE = "Совет";
    public static final String CATEGORY_MARKS_DONE_COUNT = "Количество завершенных статей";
    public static final String CATEGORY_HEADER = "Заголовок";
    public static final String CATEGORY_USER = "Пользователь";
    public static final String CATEGORY_VIDEO = "Видео: ";
    public static final String CATEGORY_RATE_APP = "Оценить";
    public static final String CATEGORY_GOOGLE_LOGIN = "Логин через google";
    public static final String CATEGORY_PLAY_LOGIN = "Логин через play";
    public static final String CATEGORY_RATING = "Рейтинг";


    public static final String ACTION_PERIODS = "Периоды";
    public static final String ACTION_NEW_HISTORY_MARKS = "Новые статьи";
    public static final String ACTION_OPENED_HISTORY_MARKS = "Открытые статьи";
    public static final String ACTION_DONE_HISTORY_MARKS = "Выполненные статьи";
    public static final String ACTION_RATING = "Рейтинг";
    public static final String ACTION_ACHIEVEMENT = "Достижения";
    public static final String ACTION_BALANCE = "Баланс";
    public static final String ACTION_INSTRUCTION = "Инструкции";
    public static final String ACTION_VIDEO = "Видео";
    public static final String ACTION_RATE = "Оценить";
    public static final String ACTION_EMAIL = "Написать нам";
    public static final String ACTION_VK_PUBLIC = "Паблик вк";
    public static final String ACTION_VK_SHARE = "Рассказать через вк";
    public static final String ACTION_FAIL_VK_SHARE = "Неудача в рассказать через вк";
    public static final String ACTION_ENTER_FROM_PUSH = "Переход по пушу: ";
    public static final String ACTION_MARKS_DONE_COUNT_UPDATED = "Обновление количества";
    public static final String ACTION_HEADER_CLICK = "Клик по заголовку";
    public static final String ACTION_USER_COUNT_FAIL = "Неудача обновить счетчик";
    public static final String ACTION_YOUTUBE_START = "Начало просмотра";
    public static final String ACTION_YOUTUBE_END = "Конец просмотра";
    public static final String ACTION_YOUTUBE_AD = "Реклама перед просмотром";
    public static final String ACTION_YOUTUBE_ERROR = "Ошибка перед просмотром: ";
    public static final String ACTION_YOUTUBE_INIT_FAILURE = "Ошибка инициализации";

    public static final String ACTION_LOGIN_DIALOG = "Диалог для логина";

    public static final String ACTION_VK_LOGIN = "Успешный вход в вк";
    public static final String ACTION_VK_CANCEL_LOGIN = "Отмена входа в вк";

    public static final String ACTION_GOOGLE_LOGIN = "Успешный вход в google";
    public static final String ACTION_GOOGLE_CANCEL_LOGIN = "Отмена входа в google";

    public static final String ACTION_PLAY_LOGIN = "Успешный вход в play";
    public static final String ACTION_PLAY_CANCEL_LOGIN = "Отмена входа в play";

    public static final String ACTION_DEVELOPING = "В разработке";

    public static final String ACTION_GO_TO_MARKET = "Переход в маркет";
    public static final String ACTION_LATER = "Позже";
    public static final String ACTION_NEVER = "Не показывать";

    public static final String ACTION_RULES = "Правила";
    public static final String ACTION_CONDITIONS_FAIL = "Условия не выполнены";

    public static final String ACTION_CONTINUE_ADVICE = "Продолжить";
    public static final String ACTION_SHARE_ADVICE = "Поделиться";

    public static final String ACTION_OPEN_MARK = "Откыта статья";
    public static final String ACTION_CLOSED_MARK = "Закрытая статья";

    public static final String ACTION_AD_VIDEO = "Рекламное видео";
    public static final String ACTION_AD_BANNER = "Рекламное баннер";

    public static final String ACTION_GROUP_VK = "Группа вк";

    public static final String ACTION_WIN = "Победа";
    public static final String ACTION_TIME_LIMIT = "Вышло время";
    public static final String ACTION_MISTAKE_LIMIT = "Ошибки";
    public static final String ACTION_CANCEL = "Отмена";

    public static void setTracker(Application app) {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(app);
            analytics.setAppOptOut(BuildConfig.DEBUG);
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
    }

    public static void sendScreen(String screen) {
        tracker.setScreenName(screen);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void sendEvent(String category, String action) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }
}
