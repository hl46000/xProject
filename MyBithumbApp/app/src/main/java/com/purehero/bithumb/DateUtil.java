package com.purehero.bithumb;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by MY on 2017-08-24.
 */

public class DateUtil {
    public static String getModifiedDate(long modified) {
        return getModifiedDate(Locale.getDefault(), modified);
    }

    public static String getModifiedDate(Locale locale, long modified) {
        SimpleDateFormat dateFormat = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            dateFormat = new SimpleDateFormat(getDateFormat(locale));
        } else {
            dateFormat = new SimpleDateFormat("MMM/dd/yyyy hh:mm:ss aa");
        }

        return dateFormat.format(new Date(modified));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getDateFormat(Locale locale) {
        return DateFormat.getBestDateTimePattern(locale, "MM/dd/yyyy hh:mm:ss aa");
    }

    public static Date ConvertToDate( String dateString, String dateStringFormat ){
        SimpleDateFormat dateFormat = new SimpleDateFormat( dateStringFormat );
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

}
