package com.krashidbuilt.api.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Ben Kauffman on 2/2/2016.
 */
public final class DateTime {

    private DateTime() {

    }

    public static String nowDateToString() {
        return nowToString().substring(0, 10);
    }

    public static String nowToString() {
        //Time in UTC
        return getFormat().format(new Date());
    }

    public static long stringToMillis(String date) throws ParseException {
        return stringToMillis("date", "yyyy-MM-dd HH:mm:ss.SSS");
    }

    public static long stringToMillis(String date, String format) throws ParseException {
        Date d = getFormat(format).parse(date);
        return d.getTime();
    }


    public static long getDiffDays(Date laterDate) {
        return getDiffDays(new Date(), laterDate);
    }

    public static long getDiffDays(Date earlierDate, Date laterDate) {
        return (int) (laterDate.getTime() - earlierDate.getTime()) / (1000 * 60 * 60 * 24);
    }

    public static long getDiffMinutes(Date laterDate) {
        return getDiffMinutes(new Date(), laterDate);
    }

    public static long getDiffMinutes(Date earlierDate, Date laterDate) {

        long result = laterDate.getTime() / 60000 - earlierDate.getTime() / 60000;
        return (int) result;
    }


    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public static String dateToString(Date date, DateFormat format) {
        return format.format(date);
    }

    public static String dateToString(Date date) {
        return getFormat().format(date);
    }

    public static Date stringToDate(String dateStr) throws ParseException {
        return getFormat().parse(dateStr);
    }

    public static Date stringToDate(String dateStr, DateFormat format) throws ParseException {
        return format.parse(dateStr);
    }

    public static DateFormat getFormat(String format) {
        SimpleDateFormat dateFormatUtc = new SimpleDateFormat(format);
        dateFormatUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatUtc;
    }

    public static DateFormat getFormat() {
        SimpleDateFormat dateFormatUtc = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormatUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatUtc;
    }

    public static long getEpochMillis() {
        return System.currentTimeMillis();
    }

    public static Date fromMillis(long millis) {
        return new Date(millis);
    }

}
