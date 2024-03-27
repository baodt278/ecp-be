package org.ecp.backend.utils;


import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtils {

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
