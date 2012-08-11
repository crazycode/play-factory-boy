package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date & DateTime Helper.
 * @author crazycode@gmail.com
 *
 */
public final class DateHelper {

    /**
     * get Date from String.
     * @param dateSource format as 'yyyy-MM-dd HH:mm' or 'yyyy-MM-dd'
     * @return
     */
    public static Date t(final String dateSource) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            return sdf.parse(dateSource);
        } catch (ParseException e) {
            sdf.applyPattern("yyyy-MM-dd");
            try {
                return sdf.parse(dateSource);
            } catch (ParseException e1) {
                throw new RuntimeException("the dateSource(" + dateSource
                        + ") MUST format as 'yyyy-MM-dd HH:mm' or 'yyyy-MM-dd'.", e1);
            }
        }
    }

    public static Date afterMinuts(String dateSource, int minuts) {
        return afterMinuts(t(dateSource), minuts);
    }

    public static Date afterMinuts(Date date, int minuts) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minuts);
        return cal.getTime();
    }


    public static Date beforeMinuts(String dateSource, int minuts) {
        return beforeMinuts(t(dateSource), minuts);
    }
    public static Date beforeMinuts(Date date, int minuts) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.roll(Calendar.MINUTE, minuts);
        return cal.getTime();
    }

    public static Date afterHours(String dateSource, int hours) {
    	return afterHours(t(dateSource), hours);
    }
    public static Date afterHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hours);
        return cal.getTime();
    }

    public static Date beforeHours(String dateSource, int hours) {
    	return beforeHours(t(dateSource), hours);
    }
    
    public static Date beforeHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.roll(Calendar.HOUR, hours);
        return cal.getTime();
    }


    public static Date afterDays(String dateSource, int days) {
    	return afterDays(t(dateSource), days);
    }
    
    public static Date afterDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }

    public static Date beforeDays(String dateSource, int days) {
    	return beforeDays(t(dateSource), days);
    }
    
    public static Date beforeDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.roll(Calendar.DAY_OF_YEAR, days);
        return cal.getTime();
    }
}
