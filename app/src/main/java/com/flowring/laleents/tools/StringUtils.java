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
    static public String toExtension(String value) {
        switch (value) {
            case "application/epub+zip":
                return ".epub";
            case "application/fractals":
                return ".fif";
            case "application/futuresplash":
                return ".spl";
            case "application/hta":
                return ".hta";
            case "application/mac-binhex40":
                return ".hqx";
            case "application/ms-vsi":
                return ".vsi";
            case "application/msaccess":
                return ".accdb";
            case "application/msaccess.addin":
                return ".accda";
            case "application/msaccess.cab":
                return ".accdc";
            case "application/msaccess.exec":
                return ".accde";
            case "application/msaccess.ftemplate":
                return ".accft";
            case "application/msaccess.runtime":
                return ".accdr";
            case "application/msaccess.template":
                return ".accdt";
            case "application/msaccess.webapplication":
                return ".accdw";
            case "application/msonenote":
                return ".one";
            case "application/msword":
                return ".doc";
            case "application/opensearchdescription+xml":
                return ".osdx";
            case "application/pdf":
                return ".pdf";
            case "application/pkcs10":
                return ".p10";
            case "application/pkcs7-mime":
                return ".p7c";
            case "application/pkcs7-signature":
                return ".p7s";
            case "application/pkix-cert":
                return ".cer";
            case "application/pkix-crl":
                return ".crl";
            case "application/postscript":
                return ".ps";
            case "application/vnd.ms-excel":
                return ".xls";
            case "application/vnd.ms-excel.12":
                return ".xlsx";
            case "application/vnd.ms-excel.addin.macroEnabled.12":
                return ".xlam";
            case "application/vnd.ms-excel.sheet.binary.macroEnabled.12":
                return ".xlsb";
            case "application/vnd.ms-excel.sheet.macroEnabled.12":
                return ".xlsm";
            case "application/vnd.ms-excel.template.macroEnabled.12":
                return ".xltm";
            case "application/vnd.ms-officetheme":
                return ".thmx";
            case "application/vnd.ms-pki.certstore":
                return ".sst";
            case "application/vnd.ms-pki.pko":
                return ".pko";
            case "application/vnd.ms-pki.seccat":
                return ".cat";
            case "application/vnd.ms-powerpoint":
                return ".ppt";
            case "application/vnd.ms-powerpoint.12":
                return ".pptx";
            case "application/vnd.ms-powerpoint.addin.macroEnabled.12":
                return ".ppam";
            case "application/vnd.ms-powerpoint.presentation.macroEnabled.12":
                return ".pptm";
            case "application/vnd.ms-powerpoint.slide.macroEnabled.12":
                return ".sldm";
            case "application/vnd.ms-powerpoint.slideshow.macroEnabled.12":
                return ".ppsm";
            case "application/vnd.ms-powerpoint.template.macroEnabled.12":
                return ".potm";
            case "application/vnd.ms-publisher":
                return ".pub";
            case "application/vnd.ms-visio.viewer":
                return ".vsd";
            case "application/vnd.ms-word.document.12":
                return ".docx";
            case "application/vnd.ms-word.document.macroEnabled.12":
                return ".docm";
            case "application/vnd.ms-word.template.12":
                return ".dotx";
            case "application/vnd.ms-word.template.macroEnabled.12":
                return ".dotm";
            case "application/vnd.ms-wpl":
                return ".wpl";
            case "application/vnd.ms-xpsdocument":
                return ".xps";
            case "application/vnd.oasis.opendocument.presentation":
                return ".odp";
            case "application/vnd.oasis.opendocument.spreadsheet":
                return ".ods";
            case "application/vnd.oasis.opendocument.text":
                return ".odt";
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation":
                return ".pptx";
            case "application/vnd.openxmlformats-officedocument.presentationml.slide":
                return ".sldx";
            case "application/vnd.openxmlformats-officedocument.presentationml.slideshow":
                return ".ppsx";
            case "application/vnd.openxmlformats-officedocument.presentationml.template":
                return ".potx";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                return ".xlsx";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.template":
                return ".xltx";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return ".docx";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.template":
                return ".dotx";
            case "application/windows-appcontent+xml":
                return ".appcontent-ms";
            case "application/x-compress":
                return ".z";
            case "application/x-compressed":
                return ".solitairetheme8";
            case "application/x-dtcp1":
                return ".dtcp-ip";
            case "application/x-gzip":
                return ".gz";
            case "application/x-itunes-itls":
                return ".itls";
            case "application/x-itunes-itms":
                return ".itms";
            case "application/x-itunes-itpc":
                return ".itpc";
            case "application/x-jtx+xps":
                return ".jtx";
            case "application/x-latex":
                return ".latex";
            case "application/x-mix-transfer":
                return ".nix";
            case "application/x-mplayer2":
                return ".asx";
            case "application/x-ms-application":
                return ".application";
            case "application/x-ms-vsto":
                return ".vsto";
            case "application/x-ms-wmd":
                return ".wmd";
            case "application/x-ms-wmz":
                return ".wmz";
            case "application/x-ms-xbap":
                return ".xbap";
            case "application/x-mswebsite":
                return ".website";
            case "application/x-pkcs12":
                return ".p12";
            case "application/x-pkcs7-certificates":
                return ".p7b";
            case "application/x-pkcs7-certreqresp":
                return ".p7r";
            case "application/x-podcast":
                return ".pcast";
            case "application/x-shockwave-flash":
                return ".swf";
            case "application/x-stuffit":
                return ".sit";
            case "application/x-tar":
                return ".tar";
            case "application/x-troff-man":
                return ".man";
            case "application/x-wmplayer":
                return ".asx";
            case "application/x-x509-ca-cert":
                return ".cer";
            case "application/x-zip-compressed":
                return ".zip";
            case "application/xaml+xml":
                return ".xaml";
            case "application/xhtml+xml":
                return ".xht";
            case "application/xml":
                return ".xml";
            case "application/zip":
                return ".zip";
            case "audio/3gpp":
                return ".3gp";
            case "audio/3gpp2":
                return ".3g2";
            case "audio/aac":
                return ".aac";
            case "audio/aiff":
                return ".aiff";
            case "audio/amr":
                return ".amr";
            case "audio/basic":
                return ".au";
            case "audio/ec3":
                return ".ec3";
            case "audio/l16":
                return ".lpcm";
            case "audio/mid":
                return ".mid";
            case "audio/midi":
                return ".mid";
            case "audio/mp3":
                return ".mp3";
            case "audio/mp4":
                return ".m4a";
            case "audio/MP4A-LATM":
                return ".m4a";
            case "audio/mpeg":
                return ".mp3";
            case "audio/mpegurl":
                return ".m3u";
            case "audio/mpg":
                return ".mp3";
            case "audio/vnd.dlna.adts":
                return ".adts";
            case "audio/vnd.dolby.dd-raw":
                return ".ac3";
            case "audio/wav":
                return ".wav";
            case "audio/x-aiff":
                return ".aiff";
            case "audio/x-flac":
                return ".flac";
            case "audio/x-m4a":
                return ".m4a";
            case "audio/x-m4r":
                return ".m4r";
            case "audio/x-matroska":
                return ".mka";
            case "audio/x-mid":
                return ".mid";
            case "audio/x-midi":
                return ".mid";
            case "audio/x-mp3":
                return ".mp3";
            case "audio/x-mpeg":
                return ".mp3";
            case "audio/x-mpegurl":
                return ".m3u";
            case "audio/x-mpg":
                return ".mp3";
            case "audio/x-ms-wax":
                return ".wax";
            case "audio/x-ms-wma":
                return ".wma";
            case "audio/x-wav":
                return ".wav";
            case "image/bmp":
                return ".dib";
            case "image/gif":
                return ".gif";
            case "image/jpeg":
                return ".jpg";
            case "image/jps":
                return ".jps";
            case "image/mpo":
                return ".mpo";
            case "image/pjpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/pns":
                return ".pns";

        }
        return "data";
    }

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

    static public void HaoLog(String data, int showC) {
        if (BuildConfig.DEBUG) {
            StackTraceElement[] stes = Thread.currentThread().getStackTrace();
if(stes.length > showC)
    showC =stes.length -1;
            if (stes != null && stes.length > showC) {
                if (stes[showC].getFileName().equals("MainAppCompatActivity.java"))
                    showC++;
                if (stes[showC].getFileName().equals("MessageBaseActivity.java"))
                    showC++;

                String tag = "HaoLog";
                for (int i = showC; i < showC + 2; i++) {

                }
                tag += " (" + stes[showC].getFileName() + ":" + stes[showC].getLineNumber() + ") ";
                tag += stes[showC].getMethodName() + " Thread=" + Thread.currentThread().getName() + "　 ";
                if (data == null)
                    Log.d(tag, "null");
                else if (data.length() < MAX_LOG)
                    Log.d(tag, data);
                else {
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
    }

    static public void HaoLog(String data) {
        HaoLog(data, 4);

    }

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

        // 保存绘图为本地图片
        mCanvas.save();
        mCanvas.restore();


        return mBitmap;
    }
}
