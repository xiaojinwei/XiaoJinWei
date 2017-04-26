package com.cj.xjw.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by chenj on 2017/4/22.
 */

public class DateUtil {

    public static String formatDate(String before) {
        String after = "";
        SimpleDateFormat simpleDateFormatBefore = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat simpleDateFormatAfter = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        try {
            Date date = simpleDateFormatBefore.parse(before);
            after = simpleDateFormatAfter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return after;
        }
        return after;
    }
}
