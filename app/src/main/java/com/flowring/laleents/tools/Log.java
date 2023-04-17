package com.flowring.laleents.tools;

import android.content.Context;

import com.flowring.laleents.BuildConfig;
import com.flowring.laleents.tools.phone.DefinedUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    private static final String NEW_LINE = System.getProperty("line.separator");
    public static boolean mLogcatAppender = BuildConfig.DEBUG;
  public   static File mLogFile = null;


    public static void i(String TAG, String message) {
        appendLog(TAG + " : " + message);
        if (mLogcatAppender) {
            android.util.Log.i(TAG, message);
        }
    }

    public static void d(String TAG, String message) {
        appendLog(TAG + " : " + message);
        if (mLogcatAppender) {
            android.util.Log.d(TAG, message);
        }
    }

    public static void e(String TAG, String message) {
        appendLog(TAG + " : " + message);
        if (mLogcatAppender) {
            android.util.Log.e(TAG, message);
        }
    }

    public static void v(String TAG, String message) {
        appendLog(TAG + " : " + message);
        if (mLogcatAppender) {
            android.util.Log.v(TAG, message);
        }
    }

    public static void w(String TAG, String message) {
        appendLog(TAG + " : " + message);
        if (mLogcatAppender) {
            android.util.Log.w(TAG, message);
        }
    }

    public static void setContext(Context mContext) {
        if(mLogFile == null)
        {
            mLogFile = new File(FileUtils.getApplicationFolder(mContext, DefinedUtils.FOLDER_FILES) +
                "/" + "laletoB_logs.log");
            if (!mLogFile.exists()) {
                try {
                    mLogFile.createNewFile();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
            logDeviceInfo();
        }
    }

    private static synchronized void appendLog(String text) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        android.util.Log.d("TAG", "進行寫入");
        if (mLogFile != null) {
            try {
                final FileWriter fileOut = new FileWriter(mLogFile, true);
                fileOut.append(sdf.format(new Date()) + " : " + text + NEW_LINE);
                fileOut.close();
                android.util.Log.d("TAG", "寫入成功");
            } catch (final IOException e) {
                android.util.Log.d("TAG", "寫入失敗");
                e.printStackTrace();
            }
        }else
        {
            android.util.Log.d("TAG", "寫入失敗 mLogFile = null");
        }
    }

    private static void logDeviceInfo() {
        appendLog("Model : " + android.os.Build.MODEL);
        appendLog("Brand : " + android.os.Build.BRAND);
        appendLog("Product : " + android.os.Build.PRODUCT);
        appendLog("Device : " + android.os.Build.DEVICE);
        appendLog("Codename : " + android.os.Build.VERSION.CODENAME);
        appendLog("Release : " + android.os.Build.VERSION.RELEASE);
    }
}
