package com.flowring.laleents.tools.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.flowring.laleents.ui.main.webBody.MainWebActivity;

import java.io.File;

public class DownloadBroadcastReceiver {
    private static BroadcastReceiver broadcastReceiver = null;


    public static void sharelistener(Context context, final long Id, File folder){
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if(Id == ID){
                    MainWebActivity.shareFileType(folder,context);
                }
            }
        };
        context.registerReceiver(broadcastReceiver, intentFilter);
    }
}
