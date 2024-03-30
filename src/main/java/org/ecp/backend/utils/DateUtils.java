package org.ecp.backend.utils;


import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtils {
    public static Date add(Date date, int field, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(field, amount);
        return c.getTime();
    }

    public static Date getStartOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getEndOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public static String convertDateToString(Date date, String formatOut) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(formatOut);
            return sdf.format(date);
        } catch (Exception ex) {
            log.error("Convert date to string has some errors: ('{}', '{}') -> {}", date, formatOut, ex);
            return null;
        }
    }

    public static Date convertStringToDate(String str, String formatIn) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(formatIn);
            return sdf.parse(str);
        } catch (Exception ex) {
            log.error("Convert string to date has some errors: ('{}', '{}') -> {}", str, formatIn, ex);
            return null;
        }
    }

    public static String formatDateString(String str, String formatIn, String formatOut) {
        Date date = convertStringToDate(str, formatIn);
        return convertDateToString(date, formatOut);
    }

    public static Date shiftDate(Date date, int shiftDays) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, shiftDays);
            return calendar.getTime();
        } catch (Exception ex) {
            log.error("Shift date has some errors: ('{}', '{}') -> {}", date, shiftDays, ex);
            return null;
        }
    }

    public static Date shiftMinute(Date date, int shiftMinutes) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, shiftMinutes);
            return calendar.getTime();
        } catch (Exception ex) {
            log.error("Shift minute has some errors: ('{}', '{}') -> {}", date, shiftMinutes, ex);
            return null;
        }
    }
}
