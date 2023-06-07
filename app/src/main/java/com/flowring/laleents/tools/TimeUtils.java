package com.flowring.laleents.tools;

import static com.flowring.laleents.tools.StringUtils.HaoLog;

import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
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


    //判斷 跨日 or 跨月 or 跨年
    public static String yearMonthDay(String startTime, String endTime){
        String result = null;

        LocalDateTime startDateTime = LocalDateTime.parse(startTime.replace(" ", "T"));
        LocalDateTime endDateTime = LocalDateTime.parse(endTime.replace(" ", "T"));

        LocalDate startDate = startDateTime.toLocalDate();
        LocalDate endDate = endDateTime.toLocalDate();

        Month startMonth = startDateTime.getMonth();
        Month endMonth = endDateTime.getMonth();

        int startYear = startDateTime.getYear();
        int endYear = endDateTime.getYear();

        if(startYear != endYear){
            result = "跨年完成";
        } else if(startMonth != endMonth){
            //目前沒有此流程
            StringUtils.HaoLog("跨月完成");
        } else if(!startDate.isEqual(endDate)){
            result = "跨日完成";
        } else {
            result = "當日完成";
        }
        return result;
    }

    //根據 跨日 or 跨月 or 跨年 回傳相對應的字串格式
    public static String formatDate(String result,String startTime, String endTime){
        if(result != null && !result.isEmpty()){
            switch (result){
                case "跨年完成":
                    String yearDate = yearFormatDate(startTime,endTime);
                    return String.format("%s\n預計進行伺服器維護\n期間將無法登入",yearDate);
                case "跨日完成":
                    String crossDay = crossDayFormatDate(startTime,endTime);
                    return String.format("%s\n預計進行伺服器維護\n期間將無法登入",crossDay);
                case "當日完成":
                    String date = dayFormatDate(startTime,endTime);
                    return String.format("%s\n預計進行伺服器維護\n期間將無法登入",date);
            }
        }
        return "";
    }

    private static String dayFormatDate(String startTime, String endTime) {
        LocalDateTime startDateTime = LocalDateTime.parse(startTime.replace(" ", "T"));
        LocalDateTime endDateTime = LocalDateTime.parse(endTime.replace(" ", "T"));

        DateTimeFormatter startFormatter = DateTimeFormatter.ofPattern("M月d日 HH:mm");
        DateTimeFormatter endFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String formattedStartTime = startDateTime.format(startFormatter);
        String formattedEndTime = endDateTime.format(endFormatter);

        return String.format("%s ~ %s", formattedStartTime, formattedEndTime);
    }

    private static String yearFormatDate(String startTime, String endTime){
        LocalDateTime startDateTime = LocalDateTime.parse(startTime.replace(" ", "T"));
        LocalDateTime endDateTime = LocalDateTime.parse(endTime.replace(" ", "T"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("Y年M月d日 HH:mm");
        String formattedStartTime = startDateTime.format(formatter);
        String formattedEndTime = endDateTime.format(formatter);
        return String.format("%s ~ \n%s", formattedStartTime, formattedEndTime);
    }

    private static String crossDayFormatDate(String startTime, String endTime){
        LocalDateTime startDateTime = LocalDateTime.parse(startTime.replace(" ", "T"));
        LocalDateTime endDateTime = LocalDateTime.parse(endTime.replace(" ", "T"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M月d日 HH:mm");
        String formattedStartTime = startDateTime.format(formatter);
        String formattedEndTime = endDateTime.format(formatter);
        return String.format("%s ~ %s", formattedStartTime, formattedEndTime);
    }

    public static String formatDateTime(String dateTime) {
        if(dateTime != null && !dateTime.isEmpty()){
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat outputFormat = new SimpleDateFormat("M月d日 HH:mm");
                Date date = inputFormat.parse(dateTime);
                return outputFormat.format(date);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return "";
    }

    static public void EndTimestamp() {
        HaoLog("時戳:" + NowTimestamp());
    }

    static public void EndTimestamp(String value) {
        HaoLog("時戳" + "(" + value + "):" + NowTimestamp());
    }
    public static String customTime(){
        // 獲取當前時間的 Calendar 實例
        Calendar calendar = Calendar.getInstance();
        // 獲取當前小時和分鐘
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //StringBuilder builder = new StringBuilder();
        //builder.append(hour);
        //builder.append(":");
        //builder.append(minute);
        String time = String.format("%s:%s", hour, minute);

        return time;
    }
}
