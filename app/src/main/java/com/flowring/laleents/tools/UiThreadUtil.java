package com.flowring.laleents.tools;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;


public class UiThreadUtil {
    @Nullable
    private static Handler sMainHandler;

    public UiThreadUtil() {
    }

    public static boolean isOnUiThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }


    public static void runOnUiThread(Runnable runnable) {
        runOnUiThread(runnable, 0L);
    }

    public static void runOnUiThread(Runnable runnable, long delayInMs) {
        Class var3 = UiThreadUtil.class;
        synchronized (UiThreadUtil.class) {
            if (sMainHandler == null) {
                sMainHandler = new Handler(Looper.getMainLooper());
            }
        }

        sMainHandler.postDelayed(runnable, delayInMs);
    }
}
