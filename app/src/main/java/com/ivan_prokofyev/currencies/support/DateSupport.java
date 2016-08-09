package com.ivan_prokofyev.currencies.support;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by Prokofyev Ivan on 19.07.16.
 */
public class DateSupport {

    public static String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c.getTime());
    }
    public static String getCurrentDateGMT(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR_OF_DAY, - 3);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c.getTime());
    }

    public static String getYesterdayDate(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        c.add(Calendar.DATE, -1);
        return df.format(c.getTime());
    }
    public static String getYesterdayDateGMT(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        c.add(Calendar.DATE, -1);
        c.add(Calendar.HOUR_OF_DAY, -3);
        return df.format(c.getTime());
    }
    public static Date getYesterdayWithHourAgo(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, - 1);
        c.add(Calendar.HOUR_OF_DAY, - 1);
        return c.getTime();
    }

    public static Date getTimeHourAgo(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, - 1);
        return c.getTime();
    }

    public static String formatTime(Date date){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(date);
    }
    public static String format(java.util.Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }
    public static String formatToHumanic(Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        return dateFormat.format(date);

    }
}
