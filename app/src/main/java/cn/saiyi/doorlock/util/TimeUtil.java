package cn.saiyi.doorlock.util;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述：时间处理器
 * 创建作者：黎丝军
 * 创建时间：2016/10/15 15:39
 */

public class TimeUtil {

    /**
     * 获取当前时间
     * @param format 时间格式
     * @return 时间字符串
     */
    public static String getCurrentTime(String format){
        return DateFormat.format(format,System.currentTimeMillis()).toString();
    }

    /**
     * 获取当前时间,返回时间格式为yyyy-MM-dd
     * @return 时间字符串
     */
    public static String getCurrentTime(){
        return getCurrentTime("yyyy-MM-dd");
    }

    /**
     * 获取当前全时间，返回格式为yyyy-MM-dd HH:mm:ss
     * @return 全时间
     */
    public static String getCurrentAllTime() {
        return getCurrentTime("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取时间，根据时间制定时间格式
     * @param dateStr 时间字符串
     * @param originTimeFormat 原始时间格式
     * @param timeFormat 时间格式
     * @return 现在的时间格式
     */
    public static String getTime(String dateStr,String originTimeFormat,String timeFormat) {
        SimpleDateFormat dateFormatSimple = new SimpleDateFormat(originTimeFormat);
        try {
            final Date date = dateFormatSimple.parse(dateStr);
            dateFormatSimple = new SimpleDateFormat(timeFormat);
            return dateFormatSimple.format(date);
        } catch (Exception e) {
        }
        return getCurrentTime(timeFormat);
    }

    /**
     * 获取时间，根据时间制定时间格式
     * @param dateStr 时间字符串
     * @param timeFormat 时间格式
     * @return 现在的时间格式 "date" -> "2016-12-14 09:58:57.0"
     */
    public static String getTime(String dateStr,String timeFormat) {
        return getTime(dateStr,"yyyy-MM-dd HH:mm:ss",timeFormat);
    }
}
