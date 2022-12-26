package com.flowring.laleents.tools;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
import static android.content.Context.DOWNLOAD_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.utils.ScreenUtils;
import com.flowring.laleents.R;
import com.flowring.laleents.model.explore.Microapp;
import com.flowring.laleents.tools.cloud.api.AsynNetUtils;
import com.flowring.laleents.tools.phone.DefinedUtils;
import com.flowring.laleents.ui.main.webBody.MainWebActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CommonUtils extends Object {
    public static final int REQUEST_CHOOSE_PICTURE = 1103;

    public static final String SELECTED_ROOM_ID = "KEY_SELECTED_ROOM_ID";
    public static final String PREF_USER_TOKEN = "KEY_USER_TOKEN";
    public static final String PREF_USER_ID = "KEY_USER_ID";
    public static final String PREF_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";
    public static final String PREF_MATRIX_URL = "KEY_MATRIX_URL";
    public static final String PREF_LALE_URL = "KEY_LALE_URL";
    public static final String PREF_JITSI_URL = "KEY_JITSI_URL";
    public static final String PREF_WEBSOCKET_URL = "KEY_WEBSOCKET_URL";
    public static final String PREF_JWT = "KEY_JWT";
    public static final String PREF_DEVICE_TOKEN = "KEY_DEVICE_TOKEN";
    public static final String PREF_SCREEN_WIDTH = "KEY_SCREEN_WIDTH";
    public static final String PREF_SCREEN_HEIGHT = "KEY_SCREEN_HEIGHT";
    public static final String PREF_USER_EMAIL = "KEY_USER_EMAIL";
    public static final String PREF_JWT_TS = "KEY_JWT_TS";
    public static final String PREF_FRIEND_TEST = "KEY_FRIEND_TEST";
    public static final String PREF_CUSTOM_DOMAIN = "KEY_CUSTOM_DOMAIN ";
    public static final String PREF_BADGER_COUNT = "KEY_BADGER_COUNT";
    public static final String PREF_NOTIFY_POINT = "KEY_NOTIFY_POINT";
    public static final String PREF_BELL_COUNT = "KEY_BELL_COUNT";
    public static final String PREF_COMPANY_URL = "KEY_COMPANY_URL";
    public static final String PREF_COMPANY_URL_JSON = "KEY_COMPANY_URL_JSON";
    public static final String PREF_COMPANY_INFO = "KEY_COMPANY_INFO";
    public static final String PREF_COMPANY_NAME = "KEY_COMPANY_NAME";
    public static final String PREF_COMPANY_FIRMID = "KEY_COMPANY_FIRMID";
    public static final String PREF_COMPANY_DOMAIN = "KEY_COMPANY_DOMAIN";
    public static final String PREF_COMPANY_SUPERUSER = "KEY_COMPANY_SUPERUSER";
    public static final String PREF_IS_CALLING = "KEY_IS_CALLING";
    public static final String PREF_STICKER_CUSTOM = "KEY_STICKER_CUSTOM";
    public static final String PREF_STICKER_DOWNLOAD = "KEY_STICKER_DOWNLOAD";
    public static final String PREF_STICKER_DOWNLOAD_ID = "KEY_STICKER_DOWNLOAD_ID";
    public static final String PREF_COMPANY_LOCAL_STORAGE = "KEY_COMPANY_LOCAL_STORAGE";
    public static final String PREF_AF_TOKEN = "KEY_AF_TOKEN";
    public static final String PREF_APP_MODULES = "KEY_APP_MODULES";
    public static final String PREF_ACCOUNT_INFO = "KEY_ACCOUNT_INFO";
    public static final String PREF_IS_AD_SHOW = "KEY_IS_AD_SHOW";
    public static final String PREF_FRIEND_NEED_REFRESH = "KEY_FRIEND_NEED_REFRESH";
    public static final String PREF_NEWS_NEED_REFRESH = "KEY_NEWS_NEED_REFRESH";
    public static final String PREF_MICRO_TIME_INFO = "KEY_MICRO_TIME_INFO";
    public static final String PREF_FESTIVAL_INFO = "KEY_FESTIVAL_INFO";
    public static final String PREF_LONG_CLICK_MESSAGE_INFO = "KEY_LONG_CLICK_MESSAGE_INFO";
    public static final String PREF_COMPANY_APPS_JSON = "KEY_COMPANY_APPS_JSON";
    public static final String PREF_WX_ACCESS_TOKEN = "KEY_WX_ACCESS_TOKEN";
    public static final String PREF_WX_REFRESH_TOKEN = "KEY_WX_REFRESH_TOKEN";
    public static final String PREF_WX_OPENID = "PREF_WX_OPENID";
    public static final String PREF_BADGE_COUNT = "PREF_BADGE_COUNT";
    public static final int CORNER_TOP_LEFT = 1;
    public static final int CORNER_TOP_RIGHT = 1 << 1;
    public static final int CORNER_BOTTOM_LEFT = 1 << 2;
    public static final int CORNER_BOTTOM_RIGHT = 1 << 3;
    public static final int CORNER_ALL = CORNER_TOP_LEFT | CORNER_TOP_RIGHT | CORNER_BOTTOM_LEFT | CORNER_BOTTOM_RIGHT;
    public static boolean CIRCLE_OF_FRIENDS = true;
    public static String TYPE_PASS_SERVICE = "0";
    public static String TYPE_PASS_NEWS = "1";
    public static String TYPE_PASS_COMPANY = "2";
    private static final String TAG = CommonUtils.class.getSimpleName();
    private static boolean isSuccess = false;

    public static void showToast(Context context, LayoutInflater inflater, String message, boolean isSendInvite) {
        View layout = inflater.inflate(R.layout.toast_layout, null);

        TextView text = layout.findViewById(R.id.txt_message);
        text.setText(message);

        if (isSendInvite) {
            ImageView imageView = layout.findViewById(R.id.img_arrow);
            View view = layout.findViewById(R.id.v_toast);
            imageView.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            layout.setBackground(context.getDrawable(R.drawable.radius_10_solid_gray));
            layout.setMinimumWidth((int) convertDpToPixel(136, context));
        }

        Toast toast = new Toast(context);
        if (isSendInvite) {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static float convertDpToPixel(float dp, Context context) {
        if (context == null)
            return -1;
        float px = dp * getDensity(context);
        return px;
    }

    public static float convertPixelToDp(float px, Context context) {
        if (context == null)
            return -1;
        float dp = px / getDensity(context);
        return dp;
    }

    public static Spannable getTextColor(String sText, int color) {
        Spannable summary = new SpannableString(sText);
        summary.setSpan(new ForegroundColorSpan(color), 0, summary.length(), 0);

        return summary;
    }

    public static float getDensity(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.density;
    }

    public static File getAppPublicDirectory(boolean makeIt) {
        File dir = new File(Environment.getExternalStorageDirectory(), "");
        if (makeIt) {
            dir.mkdirs();
        }
        return dir;
    }

    private static String getTempFileName(Context context, String extName, boolean pubDir) {
        File folder;
        if (pubDir)
            folder = new File(getAppPublicDirectory(true), "temp");
        else
            folder = context.getDir("temp", Context.MODE_PRIVATE);

        if (!folder.exists())
            folder.mkdirs();
        Log.v(TAG, "getTempFileName, path=" + folder.getPath() +
                ", exists=" + folder.exists() + ", read=" + folder.canRead() + ", write=" + folder.canWrite());

        String sTemp = String.format("%s/%d.%s", folder.getPath(), System.currentTimeMillis(), extName);
        return sTemp;
    }

    public static File getLaleDirectory(Context context) {
        File fpDir;
        fpDir = context.getDir("lalePhoto", Context.MODE_PRIVATE);
        if (!fpDir.exists())
            fpDir.mkdirs();

        return fpDir;
    }

    public static File getSubFoler(Context context, String subFolderName, String uid) {
        File fFolder = getLaleDirectory(context);
        String sFolder = fFolder.getPath() + "/" + subFolderName;
        File fileSubFolder = new File(sFolder);
        if (!fileSubFolder.exists()) {
            fileSubFolder.mkdirs();
        }
        if (uid != null && !uid.isEmpty()) {
            String sUidFoler = fileSubFolder.getPath() + "/" + uid;
            File uidFolder = new File(sUidFoler);
            if (!uidFolder.exists())
                uidFolder.mkdirs();
            return uidFolder;
        }
        return fileSubFolder;
    }

    public static String savePhotoSticker(Context context, Bitmap bmp, String uid, boolean checkExisted) {
        File folder = getSubFoler(context, "photo_sticker", uid);
        String sUserPicFile = folder.getPath() + "/" + "sticker" + ".jpg";
        if (checkExisted && new File(sUserPicFile).exists()) {
            return null;
        }
        if (saveBmpToJpgFile(sUserPicFile, bmp)) {
            return sUserPicFile;
        }
        return null;
    }

    public static boolean saveInputStreamToFile(InputStream input, String sFilePath) {
        boolean bSuccess = true;
        try {
            final File file = new File(sFilePath);
            final OutputStream output = new FileOutputStream(file);
            try {
                final byte[] buffer = new byte[1024];
                int read;
                while ((read = input.read(buffer)) != -1)
                    output.write(buffer, 0, read);

                output.flush();
            } catch (Exception e) {
                bSuccess = false;
            }
            output.close();
            return bSuccess;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Bitmap getIconPhotoSticker(Context context, String uid) {

        File folder = getSubFoler(context, "photo_sticker", uid);
        String sPicFile = folder.getPath() + "/" + "sticker" + ".jpg";
        File file = new File(sPicFile);

        Bitmap bmp = null;
        if (file.exists()) {
            bmp = BitmapFactory.decodeFile(sPicFile);
        }
        if (bmp == null) {
            try {
                if (Long.valueOf(uid).longValue() == -1 ||
                        Long.valueOf(uid).longValue() == -2) {
                    Bitmap appIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_foreground);
                    return makeRoundCorner(appIcon, appIcon.getWidth());
                }
            } catch (Exception e) {
            }
            //return getUnknownPhotoSticker(context, gender);
        }

        return makeRoundCorner(bmp, bmp.getWidth());
    }

    public static Bitmap createPhotoSticker(String file, int width, int height) {
        Log.d(TAG, "createPhotoSticker file -> " + file + " " + width + " " + height);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap resizeBmp = decodeSampledBitmapFromFile(file, width, height);
        //Rect rc = getCenterRect(resizeBmp.getWidth(), resizeBmp.getHeight(), width, height);
        if (exif != null) {
            int rotationExif = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = rotationToDegrees(rotationExif);
            Log.d(TAG, "Exif existed !");
            Matrix matrix = new Matrix();
            if (rotationExif != 0f)
                matrix.preRotate(rotationInDegrees);

            Bitmap adjustedBitmap = Bitmap.createBitmap(resizeBmp,
                    0, 0, resizeBmp.getWidth(), resizeBmp.getHeight(), matrix, true);
            Bitmap scaled = Bitmap.createScaledBitmap(adjustedBitmap, width, height, true);
            return scaled;
        } else {
            Bitmap scaled = Bitmap.createScaledBitmap(resizeBmp, width, height, true);
            return scaled;
        }
    }

    public static Bitmap makeRoundCorner(Bitmap bitmap, float px) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, height);
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, px, px, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        //paint.setColor(0x7fC0C0C0);
        paint.setColor(0x7f202020);
        paint.setStyle(Paint.Style.STROKE);
        //paint.setStrokeWidth(5);
        canvas.drawRoundRect(rectF, px, px, paint);

        return output;
    }

    public static Bitmap getRounded3Bitmap(Bitmap srcBitmap) {
        if (srcBitmap == null)
            return null;

        Bitmap dstBitmap = Bitmap.createBitmap(
                srcBitmap.getWidth(), // Width
                srcBitmap.getHeight(), // Height

                Bitmap.Config.ARGB_8888 // Config
        );

        float cornerRadius = srcBitmap.getHeight() / 3;

        Canvas canvas = new Canvas(dstBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
        RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);
        //srcBitmap.recycle();
        return dstBitmap;
    }

    public static Bitmap getRoundedBitmap(Bitmap srcBitmap, float cornerRadius) {
        if (srcBitmap == null)
            return null;

        Bitmap dstBitmap = Bitmap.createBitmap(
                srcBitmap.getWidth(), // Width
                srcBitmap.getHeight(), // Height

                Bitmap.Config.ARGB_8888 // Config
        );

        Canvas canvas = new Canvas(dstBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
        RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);
        //srcBitmap.recycle();
        return dstBitmap;
    }

    /**
     * 把圖片某固定角變成圓角
     *
     * @param bitmap  需要修改的圖片
     * @param pixels  圓角的弧度
     * @param corners 需要顯示圓弧的位置
     * @return 圓角圖片
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels, int corners, int outWidth, int outHeight) {
        //創建一個等大的畫布
        Bitmap output = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        //獲取一個跟圖片相同大小的矩形
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        //生成包含坐標的矩形對象
        final RectF rectF = new RectF(rect);
        //圓角的半徑
        final float roundPx = pixels;

        paint.setAntiAlias(true); //去鋸齒
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        //繪製圓角矩形
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        //異或將需要變為圓角的位置的二進制變為0
        int notRoundedCorners = corners ^ CORNER_ALL;

        //哪個角不是圓角我再把你用矩形畫出來
        if ((notRoundedCorners & CORNER_TOP_LEFT) != 0) {
            canvas.drawRect(0, 0, roundPx, roundPx, paint);
        }
        if ((notRoundedCorners & CORNER_TOP_RIGHT) != 0) {
            canvas.drawRect(rectF.right - roundPx, 0, rectF.right, roundPx, paint);
        }
        if ((notRoundedCorners & CORNER_BOTTOM_LEFT) != 0) {
            canvas.drawRect(0, rectF.bottom - roundPx, roundPx, rectF.bottom, paint);
        }
        if ((notRoundedCorners & CORNER_BOTTOM_RIGHT) != 0) {
            canvas.drawRect(rectF.right - roundPx, rectF.bottom - roundPx, rectF.right, rectF.bottom, paint);
        }
        //通過SRC_IN的模式取源圖片和圓角矩形重疊部分
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //繪製成Bitmap對象
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;

        return Math.min(height / reqWidth, width / reqHeight);
    }

    public static Bitmap decodeSampledBitmapFromFile(String file, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file, options);
    }

    public static boolean saveBmpToJpgFile(String filePath, Bitmap bmp) {
        try {
            File file = new File(filePath);
            if (file.exists())
                file.delete();

            // save the image as png
            FileOutputStream out = new FileOutputStream(filePath);
            // compress the image to png and pass it to the output stream
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // save the image
            out.flush();
            out.close();
            return true;
        } catch (Exception error) {
            Log.e(TAG, "Exception -> saveBmpToJpgFile");
        }
        return false;
    }

    public static int rotationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static String mxcToUrl(Context context, String mxc) {
        if (!mxc.startsWith("mxc://"))
            return mxc;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(CommonUtils.PREF_MATRIX_URL, "") +
                "/_matrix/media/r0/download/" + mxc.replace("mxc://", "");
    }

    public static String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap getBitmapFromString(String stringPicture) {
        byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public static boolean isJWTTimeout(Context context) {
        boolean isTimeout = false;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        long now = System.currentTimeMillis();
        long jstTs = pref.getLong(PREF_JWT_TS, now);
        long diff = (now - jstTs) / (60 * 1000);
        if (diff > 14)
            isTimeout = true;

        Log.d(TAG, "isJWTTimeout = " + isTimeout);
        return isTimeout;
    }

    public static String ellipsizeString(TextView textView, String str, int width) {
        Paint paint = textView.getPaint();
        if (paint.measureText(str) < 2 * width)
            return str;

        List<String> list = new ArrayList<>();

        int len = 0;
        int start, end = 0;

        while (len < str.length()) {
            len += end;
            int count = paint.breakText(str, end, str.length(), true, width, null);
            start = end;
            end = end + count;
            list.add(str.substring(start, end));
        }
        String line1 = list.get(0);
        line1 = line1.substring(0, line1.length() - 3) + "...";

        String endLine = list.get(list.size() - 1);
        int endLineWidth = (int) paint.measureText(endLine);
        String minorEndLine = list.get(list.size() - 2);
        int minorCuteCount = paint.breakText(minorEndLine, 0, minorEndLine.length(), true, endLineWidth, null);
        String line2 = minorEndLine.substring(minorCuteCount) + endLine;

        return line1 + line2;
    }

    public static Intent openChromeCustomTabs(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(context.getColor(R.color.colorPrimaryDark));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.intent.setData(Uri.parse(url));
        return customTabsIntent.intent;
        //customTabsIntent.launchUrl(context, Uri.parse(url));
    }

    public static void updateBadgerCount(Context context, int addCount) {
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
//        int count = pref.getInt(CommonUtils.PREF_BADGER_COUNT, 0);
//        count += addCount;
//        if (count < 0)
//            count = 0;
//
//        pref.edit().putInt(CommonUtils.PREF_BADGER_COUNT, count).commit();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationManager notificationManager =
//                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            String CHANNEL_ID = context.getString(R.string.notification_channel_id);
//            NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
//            if (channel == null) {
//                channel = new NotificationChannel(CHANNEL_ID, "Lale Channel", NotificationManager.IMPORTANCE_DEFAULT);
//                notificationManager.createNotificationChannel(channel);
//            }
//
//            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setNumber(count)
//                    .build();
//            notificationManager.notify(1, notification);
//        }
//        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
//        int count = pref.getInt(CommonUtils.PREF_BADGER_COUNT, 0);
//        count += addCount;
//        if (count < 0)
//            count = 0;
//
//        pref.edit().putInt(CommonUtils.PREF_BADGER_COUNT, count).commit();
//        if (count > 0)
//            ShortcutBadger.applyCount(context, count);
//        else
//            ShortcutBadger.removeCount(context);
    }


    public static String getUserAccount(String userID) {
        String userId = userID.replace("@", "");
        String[] split = userId.split(":");
        return split[0];
    }

    public static String getUrl(String text) {
        Pattern pattern = Pattern.compile("[hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() && matcher.start() == 0 ? text : "http://" + text;
    }

    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        String versionName = "";
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int compareVersionNames(String verFromApp, String verFromServer) {
        int res = 0;
        String[] appNumbers = verFromApp.split("\\.");
        String[] servNumbers = verFromServer.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(appNumbers.length, servNumbers.length);

        for (int i = 0; i < maxIndex; i++) {
            int appVersionPart = Integer.valueOf(appNumbers[i]);
            int servVersionPart = Integer.valueOf(servNumbers[i]);
            if (appVersionPart < servVersionPart) {
                res = -1;
                break;
            } else if (appVersionPart > servVersionPart) {
                res = 1;
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && appNumbers.length != servNumbers.length) {
            res = (appNumbers.length > servNumbers.length) ? 1 : -1;
        }
        // app = server -> 0
        // app > server -> 1
        // app < server -> -1
        Log.d(TAG, "compareVersionNames version(app) = " + verFromApp +
                ", version{server) = " + verFromServer + "\n result = " + res);
        return res;
    }

    public static String getPubnubChannelID(String userID) {
        String[] split = userID.split(":");
        String domain = split[1];
        String[] splitDomain = domain.split("\\.");
        if (splitDomain.length > 3) {
            domain = splitDomain[0] + "." + splitDomain[1] + "." + splitDomain[2];
        }
        String account = split[0];
        account = account.replace(".", "_");
        return account + ":" + domain;
    }

    public static String getCheckDomainName(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String loginUserID = pref.getString(PREF_USER_ID, "");
        if (!loginUserID.isEmpty()) {
            String[] split = loginUserID.split(":");
            if (split.length > 1) {
                return split[1];
            }
        }
        return "";
    }


    /**
     * 在桌面添加快捷方式
     *
     * @param context
     * @param microApp 快捷方式名稱、圖標、intent Uri
     **/
    public static void addShortcut(Context context, Microapp microApp) {
        StringUtils.HaoLog("microApp.url=" + microApp.url);
        Glide.with(context)
                .asBitmap()
                .load(microApp.pictureUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.img_default)
                        .fitCenter())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        //點擊快捷方式打開的頁面
                        Intent actionIntent = new Intent(Intent.ACTION_MAIN);
                        actionIntent.putExtra("isHome", true);
                        actionIntent.putExtra("isHomeMICRO_APPurl", microApp.url);
                        actionIntent.putExtra("isHomeMICRO_APPName", microApp.name);
                        actionIntent.setClass(context, MainWebActivity.class);
                        actionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        actionIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        actionIntent.addCategory(Intent.CATEGORY_LAUNCHER);//添加categoryCATEGORY_LAUNCHER 应用被卸载时快捷方式也随之删除
                        installShortCut(context, microApp.microAppId, microApp.name, resource, actionIntent);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public static void installShortCut(Context context, String shortcutID, String name, Bitmap bitmap, Intent intent) {
        if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            intent.setAction(Intent.ACTION_VIEW);
            ShortcutInfoCompat pinShortcutInfo =
                    new ShortcutInfoCompat.Builder(context, shortcutID).setShortLabel(name).setIntent(intent).setIcon(IconCompat.createWithAdaptiveBitmap(bitmap)).build();

            PendingIntent successCallback = PendingIntent.getBroadcast(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            ShortcutManagerCompat.requestPinShortcut(context, pinShortcutInfo, successCallback.getIntentSender());
        }
    }

    // Sticker HashMap
    public static void saveStickerMapToSP(Context context, HashMap<String, String> sticker) {
        String jsonString = new Gson().toJson(sticker);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CommonUtils.PREF_STICKER_DOWNLOAD, jsonString);
        editor.commit();
    }

    public static HashMap<String, String> getStickerMapFromSP(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defValue = new Gson().toJson(new HashMap<String, String>());
        String json = sharedPreferences.getString(CommonUtils.PREF_STICKER_DOWNLOAD, defValue);
        TypeToken<HashMap<String, String>> token = new TypeToken<HashMap<String, String>>() {
        };
        HashMap<String, String> retrievedMap = new Gson().fromJson(json, token.getType());
        return retrievedMap;
    }

    public static void addStickerMapToSP(Context context, String productIdKey, String value) {
        HashMap<String, String> stickers = getStickerMapFromSP(context);
        stickers.put(productIdKey, value);
        saveStickerMapToSP(context, stickers);
    }

    public static void deleteStickerMapToSP(Context context, String productIdKey) {
        HashMap<String, String> stickers = getStickerMapFromSP(context);
        stickers.remove(productIdKey);
        saveStickerMapToSP(context, stickers);
    }

    public static String getStoreUrlFromStickerUrl(String stickerUrl) {
        stickerUrl = stickerUrl.replace("/download/", "");

        return "";
    }

    /**
     * 關鍵字變色
     **/
    public static SpannableString matcherSearchTitle(int color, String text, String keyword) {
        String string = text.toLowerCase();
        String key = keyword.toLowerCase();

        Pattern pattern = Pattern.compile(key);
        Matcher matcher = pattern.matcher(string);

        SpannableString ss = new SpannableString(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            ss.setSpan(new ForegroundColorSpan(color), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }

    /**
     * 關鍵字背景變色
     **/
    public static SpannableString matcherSearchTitleBackground(int color, String text, String keyword) {
        String string = text.toLowerCase();
        String key = keyword.toLowerCase();

        Pattern pattern = Pattern.compile(key);
        Matcher matcher = pattern.matcher(string);

        SpannableString ss = new SpannableString(text);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            ss.setSpan(new BackgroundColorSpan(color), start, end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }

    /**
     * 字串轉換unicode
     */
    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一個字元
            char c = string.charAt(i);
            // 轉換為unicode
            unicode.append("\\\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }

    public static void setStatusBarTransparent(Activity activity, androidx.appcompat.widget.Toolbar toolbar,
                                               CollapsingToolbarLayout ctoolbar, AppBarLayout appBarLayout, int layoutType) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
        if (toolbar != null) {
            toolbar.setFitsSystemWindows(false);
            if (toolbar.getPaddingTop() == 0) {
                toolBarPadding(activity, toolbar, null, appBarLayout, layoutType);
            }
        } else {
            ctoolbar.setFitsSystemWindows(false);
            if (ctoolbar.getPaddingTop() == 0) {
                toolBarPadding(activity, null, ctoolbar, appBarLayout, layoutType);
            }
        }

    }

    private static void toolBarPadding(Activity activity, androidx.appcompat.widget.Toolbar toolbar,
                                       CollapsingToolbarLayout ctoolbar, AppBarLayout appBarLayout, int layoutType) {
        int statusBarHeight = getStatusBarHeight(activity);
        if (layoutType == DefinedUtils.LAYOUT_TYPE_LINEARLAYOUT) {
            LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) appBarLayout.getLayoutParams();
            llp.height += statusBarHeight;
            appBarLayout.setLayoutParams(llp);
        } else if (layoutType == DefinedUtils.LAYOUT_TYPE_COORDINATORLAYOUT) {
            CoordinatorLayout.LayoutParams clp = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            clp.height += statusBarHeight;
            appBarLayout.setLayoutParams(clp);
        } else if (layoutType == DefinedUtils.LAYOUT_TYPE_RELATIVELAYOUT) {
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) appBarLayout.getLayoutParams();
            rlp.height += statusBarHeight;
            appBarLayout.setLayoutParams(rlp);
        }
        if (toolbar != null) {
            AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
            lp.height += statusBarHeight;
            toolbar.setLayoutParams(lp);
            toolbar.setPadding(toolbar.getPaddingLeft(), toolbar.getPaddingTop() + statusBarHeight,
                    toolbar.getPaddingRight(), toolbar.getPaddingBottom());
        } else {
            AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) ctoolbar.getLayoutParams();
            lp.height += statusBarHeight;
            ctoolbar.setLayoutParams(lp);
            ctoolbar.setPadding(ctoolbar.getPaddingLeft(), ctoolbar.getPaddingTop() + statusBarHeight,
                    ctoolbar.getPaddingRight(), ctoolbar.getPaddingBottom());
        }

    }

    private static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setStatusBarBg(Activity activity, androidx.appcompat.widget.Toolbar toolbar, int color, int layoutType) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        window.setStatusBarColor(color);

        if (toolbar != null) {
            if (layoutType == 1) {
                AppBarLayout.LayoutParams lp = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                lp.height = (int) CommonUtils.convertDpToPixel(44, activity.getApplicationContext());
                toolbar.setLayoutParams(lp);
            } else if (layoutType == 2) {
                CollapsingToolbarLayout.LayoutParams lp = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
                lp.height = (int) CommonUtils.convertDpToPixel(44, activity.getApplicationContext());
                toolbar.setLayoutParams(lp);
            }
            int statusBarHeight = getStatusBarHeight(activity);
            if (toolbar.getPaddingTop() > 0) {
                toolbar.setPadding(toolbar.getPaddingLeft(), toolbar.getPaddingTop() - statusBarHeight, toolbar.getPaddingRight(), toolbar.getPaddingBottom());
            }
        }
    }

    public static void setfullScreenStatusBarTransparent(Activity activity) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); // 確認取消半透明設置。
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // 全螢幕顯示，status bar 不隱藏，activity 上方 layout 會被 status bar 覆蓋。
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE); // 配合其他 flag 使用，防止 system bar 改變後 layout 的變動。
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); // 跟系統表示要渲染 system bar 背景。
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /**
     * 狀態列亮色模式，設定狀態列黑色文字、圖示，
     * 適配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @param activity
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public static int StatusBarLightMode(Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity, true)) {
                //小米
                result = 1;
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                //魅族
                result = 2;
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //6.0以上
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result = 3;
            } else {
                //其他的都設定狀態列成半透明的,以下設定半透明是呼叫第三方的，根據個人需求更改
                //    ImmersionBar.with(activity).statusBarDarkFont(true, 0.5f).init();
            }
        }
        return result;
    }

    /**
     * 設定狀態列圖示為深色和魅族特定的文字風格
     * 可以用來判斷是否為Flyme使用者
     *
     * @param window 需要設定的視窗
     * @param dark   是否把狀態列文字及圖示顏色設定為深色
     * @return boolean 成功執行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * 需要MIUIV6以上
     *
     * @param activity
     * @param dark     是否把狀態列文字及圖示顏色設定為深色
     * @return boolean 成功執行返回true
     */
    public static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//狀態列透明且黑色字型
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字型
                }
                result = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//開發版 7.7.13 及以後版本採用了系統API，舊方法無效但不會報錯，所以兩個方式都要加上
                    if (dark) {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    } else {
                        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            } catch (Exception e) {
            }
        }
        return result;
    }

    public static String dateDiff(String endTime) {
        String strTime = null;
        // 按照傳入的格式生成一個simpledateformate對象
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
        long nd = 1000 * 24 * 60 * 60;// 一天的毫秒數
        long nh = 1000 * 60 * 60;// 一小時的毫秒數
        long nm = 1000 * 60;// 一分鐘的毫秒數
        long ns = 1000;// 一秒鐘的毫秒數
        long diff;
        long day = 0;
        Date curDate = new Date(System.currentTimeMillis());//獲取當前時間
        String str = sd.format(curDate);
        try {
            // 獲得兩個時間的毫秒時間差異
            diff = sd.parse(str).getTime()
                    - sd.parse(endTime).getTime();
            day = diff / nd;// 計算差多少天
            long hour = diff % nd / nh;// 計算差多少小時
            long min = diff % nd % nh / nm;// 計算差多少分鐘
            long sec = diff % nd % nh % nm / ns;// 計算差多少秒
            // 輸出結果
            if (day < 0 || hour < 0 || min < 0) {
                strTime = endTime;
            } else {
                strTime = "已逾時" + day + "天" + hour + "小時" + min + "分";
            }
            return strTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //計算popupwindow彈出位置
    public static String[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int[] anchorLoc = new int[2];
        //獲取瞄點View在螢幕上的左上座標位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 獲取螢幕的高
        final int screenHeight = ScreenUtils.getScreenHeight(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 計算contentview的高
        final int windowHeight = contentView.getMeasuredHeight();
        // 判斷需要向下彈出還是向下彈出
        boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight
                - (int) CommonUtils.convertDpToPixel(20, anchorView.getContext()) < windowHeight);
        // 判斷是否從中間彈出
        boolean isOverShow = anchorHeight + windowHeight > screenHeight * 0.75;
        if (isOverShow) {
            if (anchorLoc[1] > 0) {
                isOverShow = anchorLoc[1] <= screenHeight / 3;
            } else {
                isOverShow = anchorHeight + anchorLoc[1] > 0;
                if (!isNeedShowUp) {
                    isOverShow = !isOverShow;
                }
            }
        }
        Log.d(TAG, "向上彈出" + isNeedShowUp + "超出可視範圍" + isOverShow);
        String[] array = new String[]{String.valueOf(isNeedShowUp), String.valueOf(isOverShow)};
        return array;
    }


    public static String checkIsFriendNickNameExist(String user_id, Context context) {
        return "暱稱";
//        AppDbMgr dbMgr = new AppDbMgr(context);
//        FriendInfo friend = dbMgr.queryFriendById(user_id);
//        if (friend != null) {
//            return friend.nickname.isEmpty() ?
//                    friend.displayname : friend.nickname;
//        } else {
//            return null;
//        }
    }


    public static void choosePicture(Activity activity, int count, int type) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CHOOSE_PICTURE);
        } else {
            Intent intent = new Intent(activity, PickerActivity.class);
            intent.putExtra(PickerConfig.SELECT_MODE, type);
            long maxSize = 104857600L;//long long long long類型
            intent.putExtra(PickerConfig.MAX_SELECT_SIZE, maxSize);
            intent.putExtra(PickerConfig.MAX_SELECT_COUNT, count);
            activity.startActivityForResult(intent, DefinedUtils.REQUEST_IMAGE_PICKER);
        }
    }

    public static void choosePicture(Activity activity, int count, int type, CallbackUtils.APIReturn callback) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CHOOSE_PICTURE);
            callback.Callback(false, "checkPermission");
        } else {
            Intent intent = new Intent(activity, PickerActivity.class);
            intent.putExtra(PickerConfig.SELECT_MODE, type);
            long maxSize = 104857600L;//long long long long類型
            intent.putExtra(PickerConfig.MAX_SELECT_SIZE, maxSize);
            intent.putExtra(PickerConfig.MAX_SELECT_COUNT, count);
            activity.startActivityForResult(intent, DefinedUtils.REQUEST_IMAGE_PICKER);
        }
    }

    public static int letterToNumber(String letter) {
        int length = letter.length();
        int num = 0;
        int number = 0;
        for (int i = 0; i < length; i++) {
            char ch = letter.charAt(length - i - 1);
            num = (int) (ch - 'A' + 1);
            num *= Math.pow(26, i);
            number += num;
        }
        return number;
    }

    public static String numberToLetter(int num) {
        if (num <= 0) {
            return null;
        }
        String letter = "";
        num--;
        do {
            if (letter.length() > 0) {
                num--;
            }
            letter = ((char) (num % 26 + (int) 'A')) + letter;
            num = (int) ((num - num % 26) / 26);
        } while (num > 0);

        return letter;
    }

    public static String spiltRoomId(String roomId, Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String https = pref.getString(CommonUtils.PREF_LALE_URL, "");
        if (https.equals(DefinedUtils.DOMAIN_DEV6)) {
            roomId = roomId.replace(DefinedUtils.DOMAIN_DEV6.replace("https://", ""), "");
        } else {
            roomId = roomId.replace(DefinedUtils.DOMAIN_LALEPASS.replace("https://", ""), "");
        }
        String[] str = roomId.split(":");
        String newRoomId = str[0].replace("!", "");
        return newRoomId;
    }


    private static JSONObject getFriendJson(JSONArray array, String friendSendInvite) {
        try {
            JSONObject profileJson = new JSONObject();
            profileJson.put("module", "user");
            profileJson.put("method", "profile");
            JSONObject params = new JSONObject();
            params.put("user_lists", array);
            profileJson.put("params", params);
            JSONObject args = new JSONObject();
            args.put("friend_type", friendSendInvite);
            profileJson.put("args", args);
            return profileJson;
        } catch (Exception e) {
            Log.d(TAG, "getFriendJson Error" + e);
            return null;
        }
    }

//    public static boolean reconnect(JWebSocketClient client) {
//        try {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    if (client.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
//                        try {
//                            client.connect();
//                            isSuccess = true;
//                        } catch (Exception e) {
//                            isSuccess = false;
//                            Log.d(TAG, "WebSocket reconnect exception error" + e);
//                        }
//                    } else if (client.getReadyState().equals(ReadyState.CLOSING) || client.getReadyState().equals(ReadyState.CLOSED)) {
//                        try {
//                            client.reconnect();
//                            isSuccess = true;
//                        } catch (Exception e) {
//                            isSuccess = false;
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//            }).start();
//            Log.i("JWebSocketClientService", "connect");
//        } catch (Exception e) {
//            isSuccess = false;
//            Log.d("JWebSocketClientService", "WebSocket conncect Exception" + e);
//        }
//        return isSuccess;
//    }

    public static boolean checkIsOtherCompany(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String domain = pref.getString(CommonUtils.PREF_LALE_URL, "");
        return domain.equals("https://lale.gce.com.tw") || domain.equals("https://lale1.china-motor.com.tw:8443");
    }

    public static boolean foregrounded() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        try {
            ActivityManager.getMyMemoryState(appProcessInfo);
            return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    //加密
    public static String selfEncode(String key, String data) {

        SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, spec);
            final byte[] finalResult = cipher.doFinal(data.getBytes());
            byte[] encode = Base64.encode(finalResult, Base64.DEFAULT);
            String result = new String(encode);
            selfDecode(key, result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //解密
    public static String selfDecode(String key, String data) {
        SecretKeySpec spec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, spec);
            byte[] decode = Base64.decode(data, Base64.DEFAULT);
            final byte[] finalResult = cipher.doFinal(decode);
            String result = new String(finalResult);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void downloadFile(String url, String userAgent, String contentDisposition, String mimeType, Context context) {
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url));
        request.setMimeType(mimeType);
        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        request.addRequestHeader("User-Agent", userAgent);
        request.setDescription("Downloading File...");
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        if (contentDisposition.contains("filename=")) {
            fileName = contentDisposition.split("filename=")[1].split(";")[0].replaceAll("\"", "");
        }
        if (fileName.contains("UTF8")) {
            fileName = fileName.replace("=?UTF8?B?", "")
                    .replaceAll("=", "").replaceAll("[?]", "");
            try {
                String name = fileName.split("\\.")[0];
                String type = fileName.split("\\.")[1];
                fileName = new String(android.util.Base64.decode(name, Base64.URL_SAFE));
                if (!fileName.contains(".")) {
                    fileName = fileName + "." + type;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        request.setTitle(fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, fileName);
        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(request);
    }

    public static void deleteAccessToken(String access_token, String user_id, String next_batch, Context context, boolean isMain) {
        try {
            JSONObject json = new JSONObject();
            json.put("account", user_id);
            json.put("accessToken", access_token);
            json.put("nextBatch", next_batch);
            String sURL = "/customers/logout";
            if (!isMain) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                String https = pref.getString(CommonUtils.PREF_LALE_URL, "");
                String header = "Bearer " + pref.getString(CommonUtils.PREF_JWT, "");
                String response = NetUtils.POST(https + sURL, json, header);
                Log.d(TAG, "deleteAccessToken JSON = " + response);
            } else {
                AsynNetUtils.POST(context, sURL, json, new AsynNetUtils.Callback() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "deleteAccessToken JSON = " + response);
                    }
                });
            }

        } catch (Exception e) {
            Log.d(TAG, "deleteAccessToken ERROR = " + e);
        }
    }

    public static Uri genQRcodeAndSave(Context context, String userName) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String userID = pref.getString(CommonUtils.PREF_USER_ID, "");
        String fileExtension = FileUtils.getImageExtension("image/png");
        String fileName = String.format("%s%s", userName + "_QrCode", fileExtension);
        BarcodeEncoder encode = new BarcodeEncoder();
        try {
            int pixel = (int) CommonUtils.convertDpToPixel(200, context);
            String sQRcodeData = pref.getString(CommonUtils.PREF_LALE_URL, "") + DefinedUtils.userCardURL + userID;
            Bitmap bitmap = encode.encodeBitmap(sQRcodeData, BarcodeFormat.QR_CODE, pixel, pixel);
            File file = FileUtils.saveCachePic(context, bitmap, fileName);
            if (file != null) {
                Uri uri = FileProvider.getUriForFile(context, "com.flowring.laleents.fileprovider", file);
                return uri;
            } else {
                return null;
            }
        } catch (WriterException e) {
            Log.d(TAG, "下載QRCODE圖片 ERROR" + e);
            e.printStackTrace();
            return null;
        }
    }
}
