package com.flowring.laleents.tools.download;

import android.app.DownloadManager;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.webkit.CookieManager;

import com.flowring.laleents.R;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.download.DownloadBroadcastReceiver;
import com.flowring.laleents.ui.main.webBody.FileNotSupportedActivity;
import com.flowring.laleents.ui.main.webBody.FileReaderActivity;
import com.flowring.laleents.ui.main.webBody.MainWebActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class DownloadUtils {

    public static void openFile(String uri, String oldFileName,String fileId,Context context){

        Resources resources = context.getResources();
        String app_name = resources.getString(R.string.app_name);
        String fileName = oldFileName == null ? StringUtils.getNewString(fileId) : oldFileName;
        String tableOfContents = Environment.DIRECTORY_PICTURES + File.separator + app_name + File.separator + "openfile";
        File folder = new File(Environment.getExternalStoragePublicDirectory(tableOfContents), fileName);
        //如果檔案存在則不下載，還要在判斷 newName 是否有等於 目錄下面的名子
        if(folder.exists()){
            StringUtils.HaoLog("檔案存在");
            new Handler(Looper.getMainLooper()).post(() -> {
                FileReaderActivity.shareFileType(folder,context);
            });
            //執行開檔
        } else {
            new Thread(() -> {
                DownloadManager.Request request;
                try{
                    request = new DownloadManager.Request(Uri.parse(uri));
                }catch (IllegalArgumentException e){
                    StringUtils.HaoLog("cacheShareFileType requestError=" + e);
                    return;
                }
                String cookies = CookieManager.getInstance().getCookie(uri);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", "");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

                //創建目錄及檔案
                request.setDestinationUri(Uri.fromFile(folder));
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
                long id = downloadManager.enqueue(request);
                DownloadBroadcastReceiver.downloadlistener(context,id,folder);
            }).start();
        }
    };

    public static void notSupportedFileInShare(String uri, String oldFileName, String fileId, Context context){
        Resources resources = context.getResources();
        String app_name = resources.getString(R.string.app_name);
        String fileName = oldFileName == null ? StringUtils.getNewString(fileId) : oldFileName;
        String tableOfContents = Environment.DIRECTORY_PICTURES + File.separator + app_name + File.separator + "openfile";
        File folder = new File(Environment.getExternalStoragePublicDirectory(tableOfContents), fileName);
        //如果檔案存在則不下載，還要在判斷 newName 是否有等於 目錄下面的名子
        if(folder.exists()){
            StringUtils.HaoLog("notSupportedFileInShare 檔案存在");
            new Handler(Looper.getMainLooper()).post(() -> {
                FileNotSupportedActivity.shareFileType(folder,context);
            });
        } else {
            StringUtils.HaoLog("DownloadManager ");
            new Thread(() -> {
                DownloadManager.Request request;
                try{
                    request = new DownloadManager.Request(Uri.parse(uri));
                }catch (IllegalArgumentException e){
                    StringUtils.HaoLog("cacheShareFileType requestError=" + e);
                    return;
                }
                String cookies = CookieManager.getInstance().getCookie(uri);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", "");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

                //創建目錄及檔案
                request.setDestinationUri(Uri.fromFile(folder));
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
                long id = downloadManager.enqueue(request);
                DownloadBroadcastReceiver.notSupportedFileSharelistener(context,id,folder);
            }).start();
        }
    }

    public static void shareFileTypeDownload(String inviteMessage, String oldFileName, Context context){
        String urlString = null;
        String onlyKey = null;
        try{
            JSONArray jsonArray = new JSONArray(inviteMessage);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            urlString = jsonObject.optString("url");
            onlyKey = jsonObject.optString("onlyKey");
        }catch (JSONException e){
            e.printStackTrace();
        }
        Resources resources = context.getResources();
        String app_name = resources.getString(R.string.app_name);
        String fileName = oldFileName == null ? StringUtils.getNewString(onlyKey) : oldFileName;
        String tableOfContents = Environment.DIRECTORY_PICTURES + File.separator + app_name + File.separator + "sharefile";
        File folder = new File(Environment.getExternalStoragePublicDirectory(tableOfContents), fileName);
        if(folder.exists()){
            StringUtils.HaoLog("檔案存在");
            new Handler(Looper.getMainLooper()).post(() -> {
                MainWebActivity.shareFileType(folder,context);
            });
        } else {
            String url = urlString;
            new Thread(() -> {
                DownloadManager.Request request;
                try{
                    request = new DownloadManager.Request(Uri.parse(url));
                }catch (IllegalArgumentException e){
                    StringUtils.HaoLog("cacheShareFileType requestError=" + e);
                    return;
                }
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", "");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

                request.setDestinationUri(Uri.fromFile(folder));
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
                long id = downloadManager.enqueue(request);
                DownloadBroadcastReceiver.sharelistener(context,id,folder);
            }).start();
        }
    }
}
