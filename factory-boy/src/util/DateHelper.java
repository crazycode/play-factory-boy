package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Date & DateTime Helper.
 * 
 * @author crazycode@gmail.com
 * 
 */
public final class DateHelper {

    /**
     * get Date from String.
     * 
     * @param dateSource
     *            format as 'yyyy-MM-dd HH:mm' or 'yyyy-MM-dd'
     * @return
     */
    public static Date t(final String dateSource) {
        String[] dateFormats = new String[] { "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd" };

        for (String dateFormat : dateFormats) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            try {
                return sdf.parse(dateSource);
            } catch (ParseException e) {
                // do nothing.
            }
        }
        throw new RuntimeException(
                "the dateSource("
                        + dateSource
                        + ") MUST format as 'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd HH:mm' or 'yyyy-MM-dd'.");

    }

    
    public static Date afterMinuts(int minuts) {
    	return afterMinuts(new Date(), minuts);
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

    public static Date beforeMinuts(int minuts) {
    	return beforeMinuts(new Date(), minuts);
    }
    
    public static Date beforeMinuts(String dateSource, int minuts) {
        return beforeMinuts(t(dateSource), minuts);
    }

    public static Date beforeMinuts(Date date, int minuts) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, -minuts);
        return cal.getTime();
    }

    public static Date afterHours(int hours) {
    	return afterHours(new Date(), hours);
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

    public static Date beforeHours(int hours) {
    	return beforeHours(new Date(), hours);
    }
    
    public static Date beforeHours(String dateSource, int hours) {
        return beforeHours(t(dateSource), hours);
    }

    public static Date beforeHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, -hours);
        return cal.getTime();
    }

    public static Date afterDays(int days) {
    	return afterDays(new Date(), days);
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

    public static Date beforeDays(int days) {
    	return beforeDays(new Date(), days);
    }
    
    public static Date beforeDays(String dateSource, int days) {
        return beforeDays(t(dateSource), days);
    }

    public static Date beforeDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, -days);
        return cal.getTime();
    }
}
