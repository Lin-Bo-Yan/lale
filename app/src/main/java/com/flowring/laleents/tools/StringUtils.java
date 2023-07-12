package com.flowring.laleents.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Base64;
import android.util.DisplayMetrics;

import com.flowring.laleents.BuildConfig;
import com.flowring.laleents.model.HttpAfReturn;
import com.flowring.laleents.model.HttpReturn;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        HaoLog(value + " httpAfReturn " + data.success + " " + data.errorMessage + " " + data.data);
    }

    static public void HaoLog(String value, HttpReturn data) {
        HaoLog(value + " httpReturn " + data.status + " " + data.msg + " " + data.data, 5);
    }

    static public void HaoLog(HttpReturn data) {
        HaoLog("httpReturn " + data.status + " " + data.msg + " " + data.data, 5);
    }

    public static void HaoLog(String data, int showC) {
        StackTraceElement[] stes = Thread.currentThread().getStackTrace();

        // 確保showC不超過stes的長度
        showC = Math.min(showC, stes.length - 1);

        // 忽略 "MainAppCompatActivity.java" 和 "MessageBaseActivity.java" 的訊息
        while (showC < stes.length &&
                (stes[showC].getFileName().equals("MainAppCompatActivity.java") ||
                        stes[showC].getFileName().equals("MessageBaseActivity.java"))) {
            showC++;
        }

        if (showC < stes.length) {
            String tag = "HaoLog (" + stes[showC].getFileName() + ":" + stes[showC].getLineNumber() + ") ";
            tag += stes[showC].getMethodName() + " Thread=" + Thread.currentThread().getName() + "　 ";

            // 數據為null的情況
            if (data == null) {
                Log.d(tag, "null");
            }
            // 數據長度超過最大限制的情況
            else if (data.length() > MAX_LOG) {
                // 將數據分段輸出
                int startIndex = 0;
                while (startIndex < data.length()) {
                    int endIndex = Math.min(startIndex + MAX_LOG, data.length());
                    Log.d(tag, data.substring(startIndex, endIndex));
                    startIndex = endIndex;
                }
            } else {
                // 正常輸出數據的情況
                Log.d(tag, data);
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

//            StringUtils.HaoLog("開頭:"+data.charAt(0)+" 結尾:"+data.charAt(data.length()-1));
        int start = Character.codePointAt(data, 0);
        int end = Character.codePointAt(data, data.length() - 1);
//            StringUtils.HaoLog("開頭:"+start+" 結尾:"+end);
//        StringUtils.HaoLog("開頭:"+start+" 結尾:"+end);
        String sum = "" + (start + end);
//        StringUtils.HaoLog("相加:"+sum);
        int at = Integer.parseInt(sum.substring(sum.length() - 1));
//        StringUtils.HaoLog("at:"+at);


        int bgColor = color[at];

        Canvas mCanvas;
        // 画笔－－写字
        Paint mTextPaint = new Paint();


        // 得到图片的宽、高


        // 创建一个你需要尺寸的Bitmap

        // 用这个Bitmap生成一个Canvas,然后canvas就会把内容绘制到上面这个bitmap中
        mCanvas = new Canvas(mBitmap);
        // 绘制背景图片
        mCanvas.drawColor(bgColor);
        // 绘制文字
        mTextPaint.setColor(Color.WHITE);// 白色画笔
        mTextPaint.setTextSize(80.0f);// 设置字体大小

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
}
