package com.flowring.laleents.tools.phone;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.flowring.laleents.R;

public class FinalData {
    public final static int DBVersion = 33;//28
    public final static String LineChannelId = "1653438100";
    public static final String WechatAppId = "wx696ee7f3d1e42f4f";
    public static final String WechatSecret = "7451d37de141561c297b8a261c6cea4a";

    static public String getVersionName(Context context) {
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

    static public int[][] table = new int[][]
            {{R.string.room_life},
                    {R.string.room_service},
                    {R.string.room_enterprise}
            };


}
