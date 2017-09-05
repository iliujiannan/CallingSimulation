package com.ljn.callingsimulation.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 12390 on 2017/8/30.
 */
public class DateUtil {


    public static String[] weekDays = {"", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    public static String getDistanceTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (day != 0) {
            return day + "天" + hour + "小时" + min + "分";
        } else {
            if (day == 0 && hour != 0) {
                return hour + "小时" + min + "分";
            } else {
                return min + "分";
            }
        }
    }

    public static Integer getIndOfDay(String day){
        for (int i = 0; i <= 7; i++) {
            if(weekDays[i].equals(day))
                return i;
        }
        return -1;
    }

    public static String dateToString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public static String dateToString(Date date, String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date stringToDate(String date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getWeekOfDate(Date dt) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        if(w==0)
            w=7;
        return weekDays[w];
    }

    public static String getNextTHTime(){
        Date now = new Date();
        return dateToString(new Date(now.getTime() + 2*60*60*1000));
    }
}
