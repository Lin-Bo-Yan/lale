package com.flowring.laleents.tools;
import com.flowring.laleents.R;
import com.flowring.laleents.tools.StringUtils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;

import java.io.File;


public class DeleteCache {

    public static void checkExternalSharing(Context context){
        Resources resources = context.getResources();
        String app_name = resources.getString(R.string.app_name);
        String tableOfContents = Environment.DIRECTORY_PICTURES + File.separator + app_name + File.separator + "externalSharing";
        File download = Environment.getExternalStoragePublicDirectory(tableOfContents);
        StringUtils.HaoLog("checkExternalSharing " + download);
        if(download.exists()) {
            File[] files = download.listFiles();
            if(files != null && files.length >0){
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        StringUtils.HaoLog("checkExternalSharing " + fileName);
                        boolean isDeleted = deleteExternalSharing(file);
                        StringUtils.HaoLog("checkExternalSharing 刪除了嗎？" + isDeleted);
                    }
                }
                boolean isDeleted = download.delete();
            }else {
                StringUtils.HaoLog("沒有檔案");
                boolean isDeleted = download.delete();
                return;
            }
        }
    }

    private static boolean deleteExternalSharing(File file){
        boolean isDeleted = false;
        if(file.isFile()){
            isDeleted = file.delete();
        }
        return isDeleted;
    }

    public static void checkOpenfile(Context context){
        Resources resources = context.getResources();
        String app_name = resources.getString(R.string.app_name);
        String tableOfContents = Environment.DIRECTORY_PICTURES + File.separator + app_name + File.separator + "openfile";
        File download = Environment.getExternalStoragePublicDirectory(tableOfContents);
        StringUtils.HaoLog("checkOpenfile " + download);
        if(download.exists()) {
            File[] files = download.listFiles();
            if(files != null && files.length >0){
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        StringUtils.HaoLog("checkOpenfile " + fileName);
                        boolean isDeleted = deleteOpenfile(file);
                        StringUtils.HaoLog("checkOpenfile 刪除了嗎？" + isDeleted);
                    }
                }
                boolean isDeleted = download.delete();
            } else {
                StringUtils.HaoLog("沒有檔案");
                boolean isDeleted = download.delete();
                return;
            }
        }
    }

    private static boolean deleteOpenfile(File file){
        boolean isDeleted = false;
        if(file.isFile()){
            isDeleted = file.delete();
        }
        return isDeleted;
    }

    public static void checkSharefile(Context context){
        Resources resources = context.getResources();
        String app_name = resources.getString(R.string.app_name);
        String tableOfContents = Environment.DIRECTORY_PICTURES + File.separator + app_name + File.separator + "sharefile";
        File download = Environment.getExternalStoragePublicDirectory(tableOfContents);
        StringUtils.HaoLog("checkSharefile " + download);
        if(download.exists()) {
            File[] files = download.listFiles();
            if(files != null && files.length >0){
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        StringUtils.HaoLog("checkSharefile " + fileName);
                        boolean isDeleted = deleteSharefile(file);
                        StringUtils.HaoLog("checkSharefile 刪除了嗎？" + isDeleted);
                    }
                }
                boolean isDeleted = download.delete();
            }else {
                StringUtils.HaoLog("沒有檔案");
                boolean isDeleted = download.delete();
                return;
            }
        }
    }

    private static boolean deleteSharefile(File file){
        boolean isDeleted = false;
        if(file.isFile()){
            isDeleted = file.delete();
        }
        return isDeleted;
    }
}
