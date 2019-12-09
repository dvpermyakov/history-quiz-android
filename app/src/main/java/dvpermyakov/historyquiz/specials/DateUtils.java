package dvpermyakov.historyquiz.specials;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dvpermyakov on 18.09.2016.
 */
public class DateUtils {
    public static final String dateFormatString = "dd.MM.yyyy";
    public static final String defaultDateString = "01.01.2000";
    public static final String defaultDateTitle = "Ранее";

    public static DateFormat getDateFormat() {
       return new SimpleDateFormat(DateUtils.dateFormatString);
    }

    public static boolean isAfter(String date, int days) {
        Date lastDate;
        try {
            lastDate = getDateFormat().parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            lastDate = new Date();
        }
        Calendar suitableCalendar = Calendar.getInstance();  // now
        suitableCalendar.add(Calendar.DAY_OF_MONTH, -days);  // days before now

        return suitableCalendar.after(getCalendar(lastDate));
    }

    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
