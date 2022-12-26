package com.flowring.laleents.tools;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FormatUtils {
    private FormatUtils() {
        throw new AssertionError();
    }

    public static String formatDateTime(long mss) {

        String DateTimes = null;
        long days = mss / (60 * 60 * 24);
        long hours = (mss % (60 * 60 * 24)) / (60 * 60);
        long minutes = (mss % (60 * 60)) / 60;
        long seconds = mss % 60;
        if (days > 0) {
            DateTimes = String.format("%d %2d:%2d:%2d", days, hours, minutes, seconds);
        } else if (hours > 0) {
            DateTimes = String.format("%2d:%2d:%2d", hours, minutes, seconds);
        } else if (minutes > 0) {
            DateTimes = String.format("%2d:%2d", minutes, seconds);
        } else {
            DateTimes = String.format("00:%2d", seconds);

        }

        return DateTimes;
    }

    public static String getDurationString(int seconds) {
        Date date = new Date(seconds * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat(seconds >= 3600 ? "HH:mm:ss" : "mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date);
    }


    public static String getDateFormat(long milliSeconds, String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return formatter.format(calendar.getTime());
    }

    public static boolean isSameDay(long time1, long time2) {
        Date date1 = new Date(time1);
        Date date2 = new Date(time2);
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("Dates must not be null");
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("Dates must not be null");
        }

        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public static String getFileSizeString(long sizeB) {
        if (sizeB <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(sizeB) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(sizeB / Math.pow(1024, digitGroups)) + units[digitGroups];
    }

    public static ArrayList<String> getWebLinks(String text) {
        ArrayList<String> links = new ArrayList<String>();

        //String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        String regex = "\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(text);

        while (m.find()) {
            String urlStr = m.group();
            if (urlStr.startsWith("(") && urlStr.endsWith(")"))
                urlStr = urlStr.substring(1, urlStr.length() - 1);
            links.add(urlStr);
        }
        return links;
    }

    public static String covertColorToHexString(int color) {
        return String.format("#%06X", 0xFFFFFF & color);
    }

    public static Integer[] covertHexStringTotColor(String hex) {
        hex = hex.replace("#", "");
        Integer[] color = {Integer.valueOf(hex.substring(0, 2), 16), Integer.valueOf(hex.substring(2, 4), 16), Integer.valueOf(hex.substring(4, 6), 16)};
        return color;
    }
}
