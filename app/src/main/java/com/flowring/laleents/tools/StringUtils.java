package com.flowring.laleents.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Base64;
import android.util.DisplayMetrics;

import com.flowring.laleents.model.HttpAfReturn;
import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.tools.phone.AllData;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    static public String SetNoNull(Object value, String jsonString) {
        JSONObject json = null;
        Field[] fields = value.getClass().getFields();
        try {
            json = new JSONObject(jsonString);
            for (int i = 0; i < fields.length; i++) {
                String name = fields[i].getName();
                if (!json.has(name)) {
                    if (fields[i].getType().equals(String.class)) {
                        json.put(name, fields[i].get(value));
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (json == null)
            return null;
        return json.toString();
    }

    static final int MAX_LOG = 800;

    static public void HaoLog(String value, HttpAfReturn data) {
        HaoLog(value + data.message + " " + data.code + " " + data.data);
    }

    static public void HaoLog(String value, HttpReturn data) {
        HaoLog(value + data.status + " " + data.msg + " " + data.data, 5);
    }

    static public void HaoLog(HttpReturn data) {
        HaoLog("httpReturn " + data.status + " " + data.msg + " " + data.data, 5);
    }

    public static void HaoLog(String data, int showC) {
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();
        if(stes.length > showC){
            showC =stes.length -1;
        }
        if (stes != null && stes.length > showC) {
            if (stes[showC].getFileName().equals("MainAppCompatActivity.java")){
                showC++;
            }
            if (stes[showC].getFileName().equals("MessageBaseActivity.java")){
                showC++;
            }
            String tag = "HaoLog";
            tag += " (" + stes[showC].getFileName() + ":" + stes[showC].getLineNumber() + ") ";
            tag += stes[showC].getMethodName() + " Thread=" + Thread.currentThread().getName() + "　 ";
            if (data == null){
                Log.d(tag, "null");
            } else if (data.length() < MAX_LOG){
                Log.d(tag, data);
            } else {
                int p = data.length() / MAX_LOG;
                if (data.length() % MAX_LOG == 0) {
                    for (int i = 0; i < p; i++) {
                        Log.d(tag, data.substring(i * MAX_LOG, (i + 1) * MAX_LOG));
                    }
                } else {
                    for (int i = 0; i < p; i++) {
                        Log.d(tag, data.substring(i * MAX_LOG, (i + 1) * MAX_LOG));
                    }
                    Log.d(tag, data.substring(p * MAX_LOG));
                }
            }
        }
    }


    static public void HaoLog(String data) {
        HaoLog(data, 4);

    }

    // 將字串中的特殊字符轉換為 URL 編碼的格式，以便在 URL 中使用
    static public String unsafeCode(String data) {
        if (data == null)
            return null;


        return data.replace(";", "%3B")
                .replace("/", "%2F")
                .replace("?", "%3F")
                .replace(":", "%3A")
                .replace("@", "%40")
                .replace("=", "%3D")
                .replace("&", "%26")
                .replace("<", "%3C")
                .replace(">", "%3E")
                .replace("\"", "%22")
                .replace("#", "%23")
                .replace("%", "%25")
                .replace("{", "%7B")
                .replace("}", "%7D")
                .replace("|", "%7C")
                .replace("\\", "%5C")
                .replace("^", "%5E")
                .replace("~", "%7E")
                .replace("[", "%5B")
                .replace("]", "%5D")
                .replace("`", "%60")
                .replace(" ", "%20");

    }

    //移除 .
    public static String replace(String data){
        return data.replace(".", "");
    }

    /**
     * Covert dp to px
     *
     * @param dp
     * @param context
     * @return pixel
     */
    public static float convertDpToPixel(float dp, Context context) {
        float px = dp * getDensity(context);
        return px;
    }

    /**
     * Covert px to dp
     *
     * @param px
     * @param context
     * @return dp
     */
    public static float convertPixelToDp(float px, Context context) {
        float dp = px / getDensity(context);
        return dp;
    }

    /**
     * 取得螢幕密度
     * 120dpi = 0.75
     * 160dpi = 1 (default)
     * 240dpi = 1.5
     *
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }

    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String format_yyyy_MM_dd(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date));
        return formatter.format(calendar.getTime());
    }

    public static boolean version(String appVersion, String dbVersion) {
        String[] appVersionParts = appVersion.split("\\.");
        String[] dbVersionParts = dbVersion.split("\\.");
        int length = Math.min(appVersionParts.length, dbVersionParts.length);
        for (int i = 0; i < length; i++) {
            int appPart = Integer.parseInt(appVersionParts[i]);
            int dbPart = Integer.parseInt(dbVersionParts[i]);
            if (dbPart > appPart) {
                return true;
            } else if (dbPart < appPart) {
                return false;
            }
        }
        if (dbVersionParts.length > appVersionParts.length) {
            return true;
        }
        return false;
    }

    public static Bitmap drawBitmap(String data) {

        int[] color = new int[]{
                Color.parseColor("#B93160"),
                Color.parseColor("#355764"),
                Color.parseColor("#1C3879"),
                Color.parseColor("#0F3D3E"),
                Color.parseColor("#513252"),
                Color.parseColor("#EB4747"),
                Color.parseColor("#377D71"),
                Color.parseColor("#1A4D2E"),
                Color.parseColor("#FF5B00"),
                Color.parseColor("#4C3575")
        };
        int width_bg = 100;
        int height_bg = 100;
        Bitmap mBitmap = Bitmap.createBitmap(width_bg, height_bg, Bitmap.Config.ARGB_8888);

//        StringUtils.HaoLog("開頭:"+data.charAt(0)+" 結尾:"+data.charAt(data.length()-1));
        int start = Character.codePointAt(data, 0);
        int end = Character.codePointAt(data, data.length() - 1);
//        StringUtils.HaoLog("開頭:"+start+" 結尾:"+end);
//        StringUtils.HaoLog("開頭:"+start+" 結尾:"+end);
        String sum = "" + (start + end);
//        StringUtils.HaoLog("相加:"+sum);
        int at = Integer.parseInt(sum.substring(sum.length() - 1));
//        StringUtils.HaoLog("at:"+at);

        int bgColor = color[at];

        Canvas mCanvas;
        // 畫筆－－寫字
        Paint mTextPaint = new Paint();


        // 得到圖片的寬、高


        // 建立一個你需要尺寸的Bitmap

        // 用這個Bitmap產生一個Canvas,然後canvas就會把內容繪製到上面這個bitmap中
        mCanvas = new Canvas(mBitmap);
        // 繪製背景圖片
        mCanvas.drawColor(bgColor);
        // 繪製文字
        mTextPaint.setColor(Color.WHITE);// 白色畫筆
        mTextPaint.setTextSize(80.0f);// 設定字體大小

        float distanceTextString_width = mTextPaint.measureText(
                data.substring(0, 1), 0, 1);

        float x = (width_bg - distanceTextString_width) / 2;
        float y = 80.0f;

        mCanvas.drawText(
                data.substring(0, 1),
                x,
                y,
                mTextPaint
        );

        // 保存繪圖為本地圖片
        mCanvas.save();
        mCanvas.restore();
        return mBitmap;
    }

    public static String getNewString(String fileId){
        // event_1638440675785703424
        int length = fileId.length();
        String result = fileId.substring(length - 10);
        return result;
    }

    public static String replaceTextPlaceholders(String textContent){
        Pattern pattern = Pattern.compile("\\$\\{memName\\}|\\$\\{depName\\}|\\$\\{roleName\\}|\\$\\{memEmail\\}|\\$\\{ip\\}|\\$\\{downloadTime\\}");
        Matcher matcher = pattern.matcher(textContent);
        StringBuffer replacedText = new StringBuffer();
        while (matcher.find()){
            String match = matcher.group();
            if(match.equals("${memName}")){
                matcher.appendReplacement(replacedText, UserControlCenter.getUserMinInfo().displayName);
            } else if (match.equals("${depName}")) {
                matcher.appendReplacement(replacedText, "部門");
            } else if (match.equals("${roleName}")) {
                matcher.appendReplacement(replacedText, "職務");
            } else if(match.equals("${memEmail}")){
                matcher.appendReplacement(replacedText, "E-mail");
            }else if(match.equals("${ip}")){
                matcher.appendReplacement(replacedText, getLocalIpAddress(AllData.context));
            }else if(match.equals("${downloadTime}")){
                matcher.appendReplacement(replacedText, TimeUtils.NowTime());
            }
        }
        matcher.appendTail(replacedText);
        textContent = replacedText.toString();
        return textContent;
    }

    private static String getLocalIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();

            // 格式化 IP 地址
            String ip = String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));

            return ip;
        } else {
            return "";
        }
    }

}
