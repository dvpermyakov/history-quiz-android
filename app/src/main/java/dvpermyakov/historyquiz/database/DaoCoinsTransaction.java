package dvpermyakov.historyquiz.database;

import android.content.Context;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dvpermyakov.historyquiz.R;
import dvpermyakov.historyquiz.analytics.Analytics;
import dvpermyakov.historyquiz.models.CoinsTransaction;
import dvpermyakov.historyquiz.models.Event;

/**
 * Created by dvpermyakov on 20.09.2016.
 */
public class DaoCoinsTransaction extends BaseDaoImpl<CoinsTransaction, Integer> {
    protected DaoCoinsTransaction(ConnectionSource connectionSource, Class<CoinsTransaction> dataClass) throws SQLException {
        super(connectionSource, dataClass);
    }

    public void saveTransaction(Context context, CoinsTransaction transaction) {
        try {
            create(transaction);
            Analytics.sendEvent(Analytics.CATEGORY_BALANCE, transaction.getCategory().getString(context));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCoinsBalance() {
        QueryBuilder<CoinsTransaction, Integer> query = queryBuilder();
        try {
            query.selectRaw("SUM(" + DataBaseStrings.COINS_TRANSACTION_VALUE_COLUMN + ")");
            return (int)queryRawValue(query.prepareStatementString());
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<CoinsTransaction> getTransactions() {
        QueryBuilder<CoinsTransaction, Integer> query = queryBuilder();
        try {
            query.orderBy(DataBaseStrings.COINS_TRANSACTION_CREATED_COLUMN, false);
            return query.query();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}