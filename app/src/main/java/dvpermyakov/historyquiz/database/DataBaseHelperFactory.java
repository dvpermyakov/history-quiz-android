package dvpermyakov.historyquiz.database;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by dvpermyakov on 05.06.2016.
 */
public class DataBaseHelperFactory {
    private static DataBaseHelper dataBaseHelper;

    public static DataBaseHelper getHelper(){
        return dataBaseHelper;
    }
    public static void setHelper(Context context){
        dataBaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
    }
    public static void releaseHelper(){
        OpenHelperManager.releaseHelper();
        dataBaseHelper = null;
    }
}
