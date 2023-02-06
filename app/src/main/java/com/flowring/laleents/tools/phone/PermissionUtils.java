package com.flowring.laleents.tools.phone;

import static com.flowring.laleents.tools.UiThreadUtil.runOnUiThread;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.DialogUtils;
import com.flowring.laleents.tools.Log;
import com.flowring.laleents.tools.StringUtils;

public class PermissionUtils {

    /*
        PackageManager.PERMISSION_GRANTED = 0     //表示授予权限；
        PackageManager.PERMISSION_DENIED  = -1    //表示权限未开启；

        Manifest.permission.READ_CONTACTS //取得手機內聯繫人資料權限 V
        Manifest.permission.RECORD_AUDIO,//錄音 V
        Manifest.permission.CAMERA,//拍照權限 V
        Manifest.permission.READ_EXTERNAL_STORAGE,//讀取手機 選取圖片時需要- 如果使用原生api拿取就不需要 V
        Manifest.permission.WRITE_EXTERNAL_STORAGE,//寫入手機
        Manifest.permission.WAKE_LOCK,// 螢幕保持喚醒 不鎖屏 配合getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 使用
        Manifest.permission.ACCESS_FINE_LOCATION,//使用者位置 v

        onRequestPermissionsResult為求權限結果
        */


    static int requestCode = 666;

    public static void requestPermission(Activity activity, @NonNull String permission, Integer newRequestCode, String requestText) {

        requestPermission(activity, new String[]{permission}, newRequestCode, requestText);

    }

    public static void requestPermission(Activity activity, @NonNull String permission, String requestText) {

        requestPermission(activity, new String[]{permission}, null, requestText);

    }

    public static void requestPermission(Activity activity, @NonNull String[] permission, Integer newRequestCode, String requestText) {
        StringUtils.HaoLog("activity=" + activity);

        new AlertDialog.Builder(activity)
                .setMessage(requestText)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (newRequestCode != null)
                            activity.requestPermissions(permission, newRequestCode);
                        else
                            activity.requestPermissions(permission, requestCode++);
                    }
                }).setCancelable(true)

                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true)
                .create().show();


    }

    public static void requestPermission(Fragment fragment, @NonNull String permission, Integer newRequestCode, String requestText) {
        requestPermission(fragment, new String[]{permission}, newRequestCode, requestText);

    }

    public static void requestPermission(Fragment fragment, @NonNull String permission, String requestText) {
        requestPermission(fragment, new String[]{permission}, null, requestText);

    }

    public static void requestPermission(Fragment fragment, @NonNull String[] permission, Integer newRequestCode, String requestText) {
        StringUtils.HaoLog("fragment=" + fragment);
        new AlertDialog.Builder(fragment.getContext())
                .setMessage(requestText)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (newRequestCode != null)
                            fragment.requestPermissions(permission, newRequestCode);
                        else
                            fragment.requestPermissions(permission, requestCode++);
                    }
                }).setCancelable(true)

                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true)
                .create().show();


    }

    public static boolean checkPermission(Context context, @NonNull String permission) {
        boolean check = PermissionChecker.checkSelfPermission(context, permission)
                == PermissionChecker.PERMISSION_GRANTED;
        return check;
    }


    private static final int REQUEST_GPS_ENABLE = 1001;

    public static void checkLocation(Context context) {
        LocationManager locMgr = (LocationManager) (context.getSystemService(Context.LOCATION_SERVICE));
        if (locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) || locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d("checkLocation", "請開啟地圖權限");
            } else {
                Location lastLoc = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                float[] results = new float[3];
                if (lastLoc != null) {
                    Location.distanceBetween(22.659513224307336, 120.49697529680047, lastLoc.getLatitude(), lastLoc.getLongitude(), results);
                }
                if (results[0] / 1000 < 20 && results[0] != 0) {
//                    initDialogAD();
                }
                Log.d("checkLocation", "距離屏東" + results[0] / 1000 + "公里");
            }
        } else {
            Toast.makeText(context, "請開啟定位服務", Toast.LENGTH_LONG).show();

            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

    public static AlertDialog requestDrawOverlays(Activity activity) {
        if (Settings.canDrawOverlays(activity)) {
            return null;
        } else {
            //若没有权限，提示获取.

            return DialogUtils.showDialogMessage(activity, "請求懸浮視窗權限", "視訊/通話接通訊息需要此權限", new CallbackUtils.noReturn() {
                @Override
                public void Callback() {
                    runOnUiThread(() -> {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + activity.getPackageName()));
                        activity.startActivity(intent);
                    });
                }
            });


        }

    }
}
