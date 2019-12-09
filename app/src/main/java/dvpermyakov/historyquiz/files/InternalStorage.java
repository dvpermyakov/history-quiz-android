package dvpermyakov.historyquiz.files;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by dvpermyakov on 24.01.2017.
 */

public class InternalStorage{
    public static void writeFile(Context context, String filename, byte[] data) {
        try {
            FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFile(Context context, String filename) {
        String result = "";
        try {
            FileInputStream inputStream = context.openFileInput(filename);
            int current;
            while((current = inputStream.read()) != -1){
                result = result + Character.toString((char)current);
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
