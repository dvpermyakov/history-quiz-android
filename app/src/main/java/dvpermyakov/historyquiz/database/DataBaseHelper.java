package dvpermyakov.historyquiz.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import dvpermyakov.historyquiz.models.Answer;
import dvpermyakov.historyquiz.models.CoinsTransaction;
import dvpermyakov.historyquiz.models.Dependency;
import dvpermyakov.historyquiz.models.Dignity;
import dvpermyakov.historyquiz.models.Event;
import dvpermyakov.historyquiz.models.Paragraph;
import dvpermyakov.historyquiz.models.Period;
import dvpermyakov.historyquiz.models.Person;
import dvpermyakov.historyquiz.models.Question;
import dvpermyakov.historyquiz.models.Test;
import dvpermyakov.historyquiz.models.TestResult;
import dvpermyakov.historyquiz.models.Video;
import dvpermyakov.historyquiz.models.VideoChannel;
import dvpermyakov.historyquiz.specials.LogTag;


/**
 * Created by dvpermyakov on 05.06.2016.
 */

public class DataBaseHelper extends OrmLiteSqliteOpenHelper {
    private DaoCoinsTransaction daoCoinsTransaction;
    private DaoTestResult daoTestResult;
    private DaoTest daoTest;
    private DaoQuestion daoQuestion;
    private DaoAnswer daoAnswer;
    private DaoPeriod daoPeriod;
    private DaoEvent daoEvent;
    private DaoPerson daoPerson;
    private DaoDependency daoDependency;
    private DaoParagraph daoParagraph;
    private DaoDignity daoDignity;
    private DaoVideo daoVideo;
    private DaoVideoChannel daoVideoChannel;

    public DataBaseHelper(Context context) {
        super(context, DataBaseStrings.DATABASE_NAME, null, DataBaseStrings.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Test.class);
            TableUtils.createTable(connectionSource, Question.class);
            TableUtils.createTable(connectionSource, Answer.class);
            TableUtils.createTable(connectionSource, Dependency.class);
            TableUtils.createTable(connectionSource, Paragraph.class);
            TableUtils.createTable(connectionSource, Period.class);
            TableUtils.createTable(connectionSource, Event.class);
            TableUtils.createTable(connectionSource, Person.class);
            TableUtils.createTable(connectionSource, Dignity.class);
            TableUtils.createTable(connectionSource, TestResult.class);
            TableUtils.createTable(connectionSource, CoinsTransaction.class);
            TableUtils.createTable(connectionSource, Video.class);
            TableUtils.createTable(connectionSource, VideoChannel.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            switch (oldVersion) {
                case 1:
                    updateFromVersion1(database, connectionSource, oldVersion, newVersion);
                    break;
                case 2:
                    updateFromVersion2(database, connectionSource, oldVersion, newVersion);
                    break;
                case 3:
                    updateFromVersion3(database, connectionSource, oldVersion, newVersion);
                    break;
                case 4:
                    updateFromVersion4(database, connectionSource, oldVersion, newVersion);
                    break;
                case 5:
                    updateFromVersion5(database, connectionSource, oldVersion, newVersion);
                    break;
                case 6:
                    updateFromVersion6(database, connectionSource, oldVersion, newVersion);
                    break;
                case 7:
                    updateFromVersion7(database, connectionSource, oldVersion, newVersion);
                    break;
                case 8:
                    updateFromVersion8(database, connectionSource, oldVersion, newVersion);
                    break;
                case 9:
                    updateFromVersion9(database, connectionSource, oldVersion, newVersion);
                    break;
                case 10:
                    updateFromVersion10(database, connectionSource, oldVersion, newVersion);
                    break;
                case 11:
                    updateFromVersion11(database, connectionSource, oldVersion, newVersion);
                    break;
                case 12:
                    updateFromVersion12(database, connectionSource, oldVersion, newVersion);
                    break;
                case 13:
                    updateFromVersion13(database, connectionSource, oldVersion, newVersion);
                    break;
                case 14:
                    updateFromVersion14(database, connectionSource, oldVersion, newVersion);
                    break;
                case 15:
                    updateFromVersion15(database, connectionSource, oldVersion, newVersion);
                    break;
                case 16:
                    updateFromVersion16(database, connectionSource, oldVersion, newVersion);
                    break;
            }
        } catch (SQLException e) {
            Log.e(LogTag.TAG_DATABASE, "onUpgrade");
            e.printStackTrace();
        }
    }

    private void updateFromVersion1(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        getParagraphDao().executeRaw("ALTER TABLE `paragraphTable` ADD COLUMN image String;");
        getParagraphDao().executeRaw("ALTER TABLE `paragraphTable` ADD COLUMN imageDescription String;");
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion2(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        getPersonDao().executeRaw("ALTER TABLE `personTable` ADD COLUMN openedDate Date;");
        getEventDao().executeRaw("ALTER TABLE `eventTable` ADD COLUMN openedDate Date;");
        getPeriodDao().executeRaw("ALTER TABLE `periodTable` ADD COLUMN openedDate Date;");
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion3(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        getTestResultDao().executeRaw("ALTER TABLE `testResultTable` ADD COLUMN createdDate Date;");
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion4(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        TableUtils.createTable(connectionSource, CoinsTransaction.class);
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion5(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        getCoinsTransactionDao().executeRaw("ALTER TABLE `coinsTransactionTable` ADD COLUMN coinsTransactionValue Integer;");
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion6(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        getPersonDao().executeRaw("ALTER TABLE `personTable` ADD COLUMN hackConditions Boolean;");
        getEventDao().executeRaw("ALTER TABLE `eventTable` ADD COLUMN hackConditions Boolean;");
        getPeriodDao().executeRaw("ALTER TABLE `periodTable` ADD COLUMN hackConditions Boolean;");
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion7(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        getPersonDao().executeRaw("ALTER TABLE `personTable` ADD COLUMN createdColumn String;");
        getEventDao().executeRaw("ALTER TABLE `eventTable` ADD COLUMN createdColumn String;");
        getPeriodDao().executeRaw("ALTER TABLE `periodTable` ADD COLUMN createdColumn String;");
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion8(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        getPersonDao().executeRaw("ALTER TABLE `personTable` ADD COLUMN shared Boolean;");
        getEventDao().executeRaw("ALTER TABLE `eventTable` ADD COLUMN shared Boolean;");
        getPeriodDao().executeRaw("ALTER TABLE `periodTable` ADD COLUMN shared Boolean;");
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion9(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        getPersonDao().executeRaw("ALTER TABLE `personTable` ADD COLUMN readDone Boolean;");
        getEventDao().executeRaw("ALTER TABLE `eventTable` ADD COLUMN readDone Boolean;");
        getPeriodDao().executeRaw("ALTER TABLE `periodTable` ADD COLUMN readDone Boolean;");
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion10(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        getPeriodDao().executeRaw("ALTER TABLE `periodTable` ADD COLUMN count Integer;");
        getPeriodDao().executeRaw("ALTER TABLE `periodTable` ADD COLUMN countDone Integer;");
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion11(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        TableUtils.createTable(connectionSource, Video.class);
        TableUtils.createTable(connectionSource, VideoChannel.class);
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion12(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        getVideoDao().executeRaw("ALTER TABLE `videoTable` ADD COLUMN youtubeId String;");
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion13(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        TableUtils.dropTable(connectionSource, Video.class, true);
        TableUtils.createTable(connectionSource, Video.class);
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion14(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        getTestDao().executeRaw("ALTER TABLE `testTable` ADD COLUMN needSendToServer Boolean;");
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion15(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        getTestDao().executeRaw("ALTER TABLE `testTable` ADD COLUMN needSendToPlayService Boolean;");
        getPersonDao().executeRaw("ALTER TABLE `personTable` ADD COLUMN needSendToPlayService Boolean;");
        getEventDao().executeRaw("ALTER TABLE `eventTable` ADD COLUMN needSendToPlayService Boolean;");
        getPeriodDao().executeRaw("ALTER TABLE `periodTable` ADD COLUMN needSendToPlayService Boolean;");
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    private void updateFromVersion16(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) throws SQLException {
        getVideoDao().executeRaw("ALTER TABLE `videoTable` ADD COLUMN embeddable Boolean;");
        onUpgrade(database, connectionSource, oldVersion + 1, newVersion);
    }

    public DaoCoinsTransaction getCoinsTransactionDao() {
        if (daoCoinsTransaction == null) {
            try {
                daoCoinsTransaction = new DaoCoinsTransaction(getConnectionSource(), CoinsTransaction.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoCoinsTransaction;
    }

    public DaoTestResult getTestResultDao() {
        if (daoTestResult == null) {
            try {
                daoTestResult = new DaoTestResult(getConnectionSource(), TestResult.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoTestResult;
    }

    public DaoTest getTestDao() {
        if (daoTest == null) {
            try {
                daoTest = new DaoTest(getConnectionSource(), Test.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoTest;
    }

    public DaoEvent getEventDao() {
        if (daoEvent == null) {
            try {
                daoEvent = new DaoEvent(getConnectionSource(), Event.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoEvent;
    }

    public DaoPerson getPersonDao() {
        if (daoPerson == null) {
            try {
                daoPerson = new DaoPerson(getConnectionSource(), Person.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoPerson;
    }

    public DaoPeriod getPeriodDao() {
        if (daoPeriod == null) {
            try {
                daoPeriod = new DaoPeriod(getConnectionSource(), Period.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoPeriod;
    }

    public DaoDependency getDependencyDao() {
        if (daoDependency == null) {
            try {
                daoDependency = new DaoDependency(getConnectionSource(), Dependency.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoDependency;
    }

    public DaoDignity getDignityDao() {
        if (daoDignity == null) {
            try {
                daoDignity = new DaoDignity(getConnectionSource(), Dignity.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoDignity;
    }

    public DaoParagraph getParagraphDao() {
        if (daoParagraph == null) {
            try {
                daoParagraph = new DaoParagraph(getConnectionSource(), Paragraph.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoParagraph;
    }

    public DaoQuestion getQuestionDao() {
        if (daoQuestion == null) {
            try {
                daoQuestion = new DaoQuestion(getConnectionSource(), Question.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoQuestion;
    }

    public DaoAnswer getAnswerDao() {
        if (daoAnswer == null) {
            try {
                daoAnswer = new DaoAnswer(getConnectionSource(), Answer.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoAnswer;
    }

    public DaoVideo getVideoDao() {
        if (daoVideo == null) {
            try {
                daoVideo = new DaoVideo(getConnectionSource(), Video.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoVideo;
    }

    public DaoVideoChannel getVideoChannelDao() {
        if (daoVideoChannel == null) {
            try {
                daoVideoChannel = new DaoVideoChannel(getConnectionSource(), VideoChannel.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoVideoChannel;
    }

    @Override
    public void close(){
        super.close();
        daoTestResult = null;
        daoTest = null;
        daoEvent = null;
    }
}
