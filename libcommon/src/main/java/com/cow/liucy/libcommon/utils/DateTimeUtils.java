package com.cow.liucy.libcommon.utils;

import androidx.annotation.NonNull;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateTimeUtils {

    static public final String FORMAT_DATE_UI = "yyyy-MM-dd";
    static public final String FORMAT_DATETIME_UI = "yyyy-MM-dd HH:mm:ss";
    static public final String FORMAT_SMALLDATETIME_UI = "yyyy-MM-dd HH:mm";
    static public final String FORMAT_TIME_UI = "HH:mm:ss";
    static public final String FORMAT_HOUR_SECOND = "mm:ss";
    static public final String FORMAT_DATE_DETAIL_UI = "yyyy/MM/dd HH:mm";
    static public final String FORMAT_CH_DATE_DETAIL_UI = "yyyy年MM月dd日 HH:mm:ss";

    static public final String FORMAT_DATE_YM = "yyyy-MM";

    static public final String FORMAT_DATE_DATETIME_LOG = "yyyyMMddHHmmss";
    static public final String FORMAT_DATE_DATETIME_YYYYMMDD = "yyyyMMdd";
    static public final String FORMAT_DATE_HOUR_MINUTE = "MM月dd日 HH:mm";

    private static final int SECONDS_PER_DAY = 24 * 3600;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int SECONDS_PER_MINUTE = 60;
    public static final long MILSECONDS_PER_DAY = 24 * 60 * 60 * 1000;

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param dbDate
     * @return
     */
    public static String getDatetime(Date dbDate) {
        if (dbDate == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(FORMAT_DATETIME_UI, Locale.CHINA);
        return df.format(dbDate.getTime());
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss格式的字符串转化日期
     *
     * @param date
     * @return
     */
    public static Date parse(String date) {
        DateFormat df = new SimpleDateFormat(FORMAT_DATETIME_UI, Locale.CHINA);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static Date parse(String date, String format) {
        DateFormat df = new SimpleDateFormat(format, Locale.CHINA);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 将yyyy-MM-dd格式的字符串转化日期
     *
     * @param date
     * @return
     */
    public static Date parse2(String date) {
        DateFormat df = new SimpleDateFormat(FORMAT_DATE_UI, Locale.CHINA);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 将yyyy-MM-dd格式的字符串转化日期
     *
     * @param date
     * @return
     */
    public static Date parseyyyyMMdd(String date) {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            return df.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 将yyyy-MM-dd HH:mm格式的字符串转化日期
     *
     * @param date
     * @return
     */
    public static Date parse3(String date) {
        DateFormat df = new SimpleDateFormat(FORMAT_SMALLDATETIME_UI, Locale.CHINA);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }
    /**
     * 时间戳转换成Date
     * @param ts
     * @return
     */
    public static Date parseByTimestamps(Long ts){
        //时间戳转化为Sting或Date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(ts);
        try {
            Date date=format.parse(d);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static String getDatetime(long time) {
        if (time == 0) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(FORMAT_DATETIME_UI, Locale.CHINA);
        return df.format(time);
    }

    /**
     * yyyy-MM-dd
     *
     * @param dbDate
     * @return
     */
    public static String getDate(Date dbDate) {
        if (dbDate == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(FORMAT_DATE_UI, Locale.CHINA);
        return df.format(dbDate.getTime());
    }

    public static String getIpcDate(Date date) {
        if (date == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        return df.format(date.getTime());
    }

    /**
     * yyyy-MM-dd
     *
     * @param milliseconds
     * @return
     */
    public static String getDate(long milliseconds) {
        if (milliseconds == 0) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(FORMAT_DATE_UI, Locale.CHINA);
        return df.format(milliseconds);
    }

    /**
     * yyyy-MM
     *
     * @param milliseconds
     * @return
     */
    public static String getDateYM(long milliseconds) {
        if (milliseconds == 0) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(FORMAT_DATE_YM, Locale.CHINA);
        return df.format(milliseconds);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param datetime
     * @return
     */
    public static String getDatetime(Timestamp datetime) {
        if (datetime == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(FORMAT_DATETIME_UI, Locale.CHINA);
        return df.format(datetime);
    }

    /**
     * yyyy-MM-dd HH:mm
     *
     * @param time
     * @return
     */
    public static String getDatetime(Long time) {
        if (time == null || time == 0) {
            return "";
        }
        Date n_date = new Date(time);
        DateFormat df = new SimpleDateFormat(FORMAT_SMALLDATETIME_UI, Locale.CHINA);
        return df.format(n_date.getTime());
    }


    /**
     * @param dbDate
     * @param customizedFormat
     * @return
     */
    public static String getCustomizedDateTime(Date dbDate, String customizedFormat) {
        if (dbDate == null || customizedFormat == null || customizedFormat.equals("")) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(customizedFormat, Locale.CHINA);
        return df.format(dbDate.getTime());
    }

    /**
     * @param datetime
     * @param customizedFormat
     * @return
     */
    public static String getCustomizedDateTime(Timestamp datetime, String customizedFormat) {
        if (datetime == null || customizedFormat == null || customizedFormat.equals("")) {
            return "";
        }
        DateFormat df = new SimpleDateFormat(customizedFormat, Locale.CHINA);
        return df.format(datetime);
    }

    /**
     * mm:ss
     *
     * @param time
     * @return
     */
    public static String getVideotime(long time) {
        if (time <= 0) {
            return "";
        }
        Date n_date = new Date(time);
        DateFormat df = new SimpleDateFormat(FORMAT_HOUR_SECOND, Locale.CHINA);
        return df.format(n_date.getTime());
    }

    /**
     *
     */
    public static int getDateSecond(long time) {
        if (time <= 0) {
            return 0;
        }
        Date n_date = new Date(time);
        return n_date.getSeconds();

    }

    /**
     * yyyy/MM/dd HH:mm
     */
    public static String getDateDetailTime(String time) {
        if (time == null || time.trim().equals("")) {
            return "";
        }
        Long t = Long.valueOf(time);
        Date n_date = new Date(t);
        DateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE_DETAIL_UI, Locale.CHINA);
        return dateFormat.format(n_date);
    }

    /**
     * yyyy年MM月dd日 HH:mm:ss
     */
    public static String getCHDateDetailTime(long time) {
        if (time <= 0) {
            return "";
        }
        Date n_date = new Date(time);
        DateFormat dateFormat = new SimpleDateFormat(FORMAT_CH_DATE_DETAIL_UI, Locale.CHINA);
        return dateFormat.format(n_date);

    }

    /**
     * yyyyMMddHHmmss
     *
     * @param time
     * @return
     */
    public static String getDateTimeForLog(long time) {
        if (time <= 0) {
            return "";
        }
        Date n_date = new Date(time);
        DateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE_DATETIME_LOG, Locale.CHINA);
        return dateFormat.format(n_date);
    }

    /**
     * yyyyMMdd
     *
     * @param time
     * @return
     */
    public static String getDateTimeYYYYMMDD(long time) {
        if (time <= 0) {
            return "";
        }
        Date n_date = new Date(time);
        DateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE_DATETIME_YYYYMMDD, Locale.CHINA);
        return dateFormat.format(n_date);
    }

    public static long diffSecondCurrentTime(String endTime) {
        DateFormat df = new SimpleDateFormat(FORMAT_DATETIME_UI, Locale.CHINA);
        Date current = new Date();
        Date end;
        try {
            end = df.parse(endTime);
            long between = (end.getTime() - current.getTime()) / 1000;// 除以1000是为了转换成秒
            return between;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //需要增加的天数例如 1
    public static String getDateByAddDays(int addDays) {
        //如果需要向后计算日期 -改为+
        Date newDate2 = new Date(System.currentTimeMillis() + (long)addDays * 24 * 60 * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_DATETIME_UI);
        String dateOk = simpleDateFormat.format(newDate2);
        return dateOk;
    }

    public static boolean isTimeInRange(Long time,String startDateStr,String endDateStr,
                                               String startTimeStr,String endTimeStr){
        boolean flag = false;
        Date curDate = new Date(time);
        Date startDate = parse(startDateStr + " " + "00:00:00");
        Date endDate = parse(endDateStr + " " + "23:59:59");
        if(startDate.before(curDate) && endDate.after(curDate)) {
            String currentDateStr = getDate(new Date(time));
            Date startTime = DateTimeUtils.parse(currentDateStr + " " + startTimeStr);
            Date endTime = DateTimeUtils.parse(currentDateStr + " " + endTimeStr);
            if(startTime.before(curDate) && endTime.after(curDate)) {
                flag = true;
            }
//            Timber.e("currentTime="+time+",startTime="+startTime.getTime()+",endTime="+endTime.getTime());
        }

        return flag;
    }

    /**
     * 获取20180101 00:00:00的时间戳值，用于判断机器时间是否复位
     * @return
     */
    public static long getDefaultTimeTS(){
//            String time = "2018/01/01 00:00:00";
//            Date date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(time);
            long unixTimestamp = 1514736000000l;//date.getTime();
//            Timber.e(">>>>>>>>unixTimestamp"+unixTimestamp);
            return unixTimestamp;
    }

    public static String getDateTimeMMddHHmm(long time) {
        if (time <= 0) {
            return "";
        }
        Date n_date = new Date(time);
        DateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE_HOUR_MINUTE, Locale.CHINA);
        return dateFormat.format(n_date);
    }

    /**
     * 时间戳转换成格式化Date
     * @param ts
     * @return  yyyy-MM-dd HH:mm:ss
     */
    public static Date getFormatedData(Long ts){
        //时间戳转化为Sting或Date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(ts);
        try {
            Date date=format.parse(d);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 时间戳转换成格式化Date
     * @param ts
     * @return  yyyy-MM-dd HH:mm
     */
    public static Date getFormatedData2(Long ts){
        //时间戳转化为Sting或Date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String d = format.format(ts);
        try {
            Date date=format.parse(d);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 时间戳转换成格式化Date
     * @return  yyyy-MM-dd HH:mm:ss:SSS
     */
    public static String getFormatedStringData(){
        //时间戳转化为Sting或Date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String d = format.format(System.currentTimeMillis());
        return d;
    }


    /**
     * 时间戳转换成格式化Date
     * @return  yyyy-MM-dd HH:mm:ss
     */
    public static String getFormatedDataString(){
        //时间戳转化为Sting或Date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(System.currentTimeMillis());
        return d;
    }

    /**
     * 时间戳转换成格式化Date
     * @return  yyyy-MM-dd HH:mm
     */
    public static String getFormatedDataString2(){
        //时间戳转化为Sting或Date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String d = format.format(System.currentTimeMillis());
        return d;
    }

    /**
     * 获取当前格式化Date
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static Date getFormatedData(){
        return getFormatedData(System.currentTimeMillis());
    }

    /**
     * 获取当前格式化Date
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static Date getFormatedData2(){
        return getFormatedData2(System.currentTimeMillis());
    }

    /**
     * @param seconds 时间，秒数
     * @return 格式化时长字符串，如3666 -> 1小时1分
     */
    public static String getFormatedDateTimeString(int seconds){
//        seconds = seconds + SECONDS_PER_MINUTE - 1;
//        int days = seconds / SECONDS_PER_DAY;
        int hours = (seconds / SECONDS_PER_HOUR);
        int minutes = (seconds % SECONDS_PER_HOUR) / 60;

        String resultString = "";

//        if (days > 0) {
//            resultString = resultString + days + "天";
//        }
        if (hours > 0) {
            resultString = resultString + hours + "小时";
        }
        if (minutes >= 0) {
            resultString = resultString + minutes + "分钟";
        }
        return resultString;
    }

    public static void resetDate2EndDatetime(@NonNull Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        endDate.setTime(calendar.getTimeInMillis());
    }

    public static Date getEndDateTime(@NonNull Date endDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        return calendar.getTime();
    }

    /**
     * 得到yyyy-mm-dd 00:00:00日期
     * @param endDate
     */
    public static void resetDate2StartDatetime(@NonNull Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND));
        endDate.setTime(calendar.getTimeInMillis());
    }

    /**
     * 得到几天前的时间
     * @param d
     * @param day
     * @return
     */
    public static Date getDateBefore(Date d,int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)-day);
        return now.getTime();
    }

    /**
     * 得到几天后的时间
     * @param d
     * @param day
     * @return
     */
    public static Date getDateAfter(Date d,int day){
        Calendar now =Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE,now.get(Calendar.DATE)+day);
        return now.getTime();
    }

    /**
     * 日期差多少天
     * @param date1
     * @param date2
     * @return
     */
    public static int getDateDiffDays(Date date1,Date date2){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)   //同一年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        }
        else    //不同年
        {
            return day2-day1;
        }
    }

    /**
     * 判断当前时间是否在凌晨0~1时
     * @return
     */
    public static boolean isBefore1clock(){

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        try{
            if(now.getHours()>=format.parse("1:00:00").getHours()){
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;

    }

    public static void main(String[] args) {

//        Date now=DateTimeUtils.getFormatedData();
//        System.out.println(DateTimeUtils.getFormatedData(now.getTime()));
//        resetDate2StartDatetime(now);
//        System.out.println(DateTimeUtils.getFormatedData(now.getTime()));
//        resetDate2EndDatetime(now);
//        System.out.println(DateTimeUtils.getFormatedData(now.getTime()));
        String dateStr = "2018-6-1 1:21:28";
        String dateStr2 = "2018-5-30 0:0:0";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try
//        {
//            Date date2 = format.parse(dateStr2);
//            Date date = format.parse(dateStr);
//
//            System.out.println("两个日期的差距：" + getDateDiffDays(date,date2));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        System.out.print(isBefore1clock());
    }

    /**
     * 将时间戳转化为年月日、时分格式
     *
     * @param timestamp 时间戳
     * @return
     */
    public static String timestamp2String(long timestamp, String format) {
        if (timestamp == 0) return "";

        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String str = "";
        try {
            str = sdf.format(new Date(timestamp));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static boolean isValidDate(String str, String formatType) {
        boolean convertSuccess=true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat(formatType);
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess=false;
        }
        return convertSuccess;
    }

    /**
     * 日期转时间戳
     * @param timeStr 日期
     * @param format 格式
     * @return
     */
    public static long getTimeStamp(String timeStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date = sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date.getTime();
    }


    /**
     * 判断时间格式 格式必须为“yyyy-MM-dd HH:mm:ss”
     * 2004-2-30 是无效的
     * 2003-2-29 是无效的
     * @param sDate
     * @return
     */
    public static boolean isLegalDate(String sDate) {
        int legalLen = 19;
        if ((sDate == null) || (sDate.length() != legalLen)) {
            return false;
        }

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(sDate);
            return sDate.equals(formatter.format(date));
        } catch (Exception e) {
            return false;
        }
    }

    /*获取星期几*/
    public static String getWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        switch (i) {
            case 1:
                return "0";
            case 2:
                return "1";
            case 3:
                return "2";
            case 4:
                return "3";
            case 5:
                return "4";
            case 6:
                return "5";
            case 7:
                return "6";
            default:
                return "";
        }
    }

    /*获取一个月的几号*/
    public static String getDay() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(day);
    }
}
