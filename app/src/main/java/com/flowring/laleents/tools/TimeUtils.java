package com.flowring.laleents.tools;

import static com.flowring.laleents.tools.StringUtils.HaoLog;

import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.Date;

public class TimeUtils {

    static long Timestamp = 0;

    public static long NowTimestamp() {
        long newTimestamp = new Date().getTime();
        long timeInterval = newTimestamp - Timestamp;
        Timestamp = newTimestamp;
        return timeInterval;
    }

    public static long RoomTopToLong(String dateString) {
        if (dateString == null)
            return 0;

        //設定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
        //進行轉換
        try {
            return sdf.parse(dateString).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }
    //欲轉換的日期字串


    static public void EndTimestamp() {
        HaoLog("時戳:" + NowTimestamp());
    }

    static public void EndTimestamp(String value) {
        HaoLog("時戳" + "(" + value + "):" + NowTimestamp());
    }

}
