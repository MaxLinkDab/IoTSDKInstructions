package com.td.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description: 日期工具类
 * @Author: chenqp
 * @CreateDate: 2019/3/27 15:22
 * @Version: 1.0
 */
public class DateHelper {
    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        if (date == null)
            return null;
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @param calendar
     * @return
     */
    public static String format(Calendar calendar) {
        if (calendar == null)
            return null;
        return DateFormatUtils.format(calendar, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * yyyyMMddHHmmss
     *
     * @param date
     * @return
     */
    public static String formatNoDelimiters(Date date) {
        if (date == null)
            return null;
        return DateFormatUtils.format(date, "yyyyMMddHHmmss");
    }

    public static String formatDate(Date date, String format) {
        if (date == null)
            return null;
        return DateFormatUtils.format(date, format);
    }

    public static String formatMillis(Long millis, String format) {
        if (millis == null)
            return null;
        return DateFormatUtils.format(millis, format);
    }

    /**
     * yyyyMMddHHmmss
     *
     * @param calendar
     * @return
     */
    public static String formatNoDelimiters(Calendar calendar) {
        if (calendar == null)
            return null;
        return DateFormatUtils.format(calendar, "yyyyMMddHHmmss");
    }

    /**
     * yyyyMMddHHmmss
     *
     * @param millis
     * @return
     */
    public static String formatNoDelimiters(Long millis) {
        if (millis == null)
            return null;
        return DateFormatUtils.format(millis, "yyyyMMddHHmmss");
    }

    /**
     * 昨天开始时间
     *
     * @return
     */
    public static Date yesterdayBegin() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -1);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    /**
     * 今天开始时间
     *
     * @return
     */
    public static Date todayBegin() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    /**
     * 今天结束时间
     *
     * @return
     */
    public static Date todayEnd() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, 1);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    /**
     * 一周开始时间
     *
     * @return
     */
    public static Date weekBegin() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setFirstDayOfWeek(Calendar.MONDAY);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date time = currentDate.getTime();
        return time;
    }

    /**
     * 一周结束时间
     *
     * @return
     */
    public static Date weekEnd() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setFirstDayOfWeek(Calendar.MONDAY);
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        Date time = currentDate.getTime();
        return time;
    }

    public static Date oneMonthBegin(){
        Calendar ins = Calendar.getInstance();
        ins.add(Calendar.MONTH, 0);
        ins.set(Calendar.DAY_OF_MONTH,1);
        return ins.getTime();
    }

    /**
     * @param days
     * @return
     */
    public static Date dateIntervalToNow(int days) {
        Calendar ins = Calendar.getInstance();
        ins.set(Calendar.HOUR_OF_DAY, 0);
        ins.set(Calendar.MINUTE, 0);
        ins.set(Calendar.SECOND, 0);
        ins.add(Calendar.DAY_OF_MONTH, days);
        Date time = ins.getTime();
        return time;
    }

    /**
     * 一月内  当前时间的上个月时间开始
     *
     * @return
     */
    public static Date monthBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, -1);
        Date time = calendar.getTime();
        return time;
    }

    /**
     * 三个月开始时间
     *
     * @return
     */
    public static Date threeMonthBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, -3);
        Date time = calendar.getTime();
        return time;
    }

    public static Date getAddDayDate(Integer dayCount) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, dayCount);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    public static Date getAddDayDate(Date date, Integer dayCount) {
        Calendar now = Calendar.getInstance();
        if (date != null) now.setTime(date);
        now.add(Calendar.DAY_OF_MONTH, dayCount);
        return now.getTime();
    }

    public static Date getAddWeekDate(Integer weekCount) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.WEEK_OF_YEAR, weekCount);
        return now.getTime();
    }

    public static Date getAddWeekDate(Date date, Integer weekCount) {
        Calendar now = Calendar.getInstance();
        if (date != null) now.setTime(date);
        now.add(Calendar.WEEK_OF_YEAR, weekCount);
        return now.getTime();
    }

    /**
     * <pre>
     * 获取日期距离当天相差的天数
     * </pre>
     *
     * @param date
     * @return
     */
    public static int getDateDifferDayToNow(String date) {
        try {
            Date expireDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            return (int) ((new Date().getTime() - expireDate.getTime()) / 1000 / 60 / 60 / 24);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1000;
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static String todayBeginStr() {
        return DateHelper.format(DateHelper.todayBegin());
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static String todayEndStr() {
        return DateHelper.format(DateHelper.todayEnd());
    }

    public static Date getNowAddDate(int days) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, days);
        return now.getTime();
    }

    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    public static Date nowAddMinutes(int minutes) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minutes);
        return now.getTime();
    }

    public static Date getShortTime(Date time) {
        Calendar now = Calendar.getInstance();
        now.setTime(time);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }

    /***
     * 	时间差
     */
    public static Long getDatePoor(Date endDate, Date nowDate) {

        // 获得两个时间的毫秒时间差异
        long between = endDate.getTime() - nowDate.getTime();
        long minute = between / 60 / 1000;

        return minute;
    }

}
