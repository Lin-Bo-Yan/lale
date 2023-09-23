package com.flowring.laleents.tools;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.tools.phone.DefinedUtils;

public class SharedPreferencesUtils {
    static SharedPreferences pref = null;
    public static void generalType(){
        pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        String generalType = String.valueOf(DefinedUtils.LOGIN_TYPE_AGENT_FLOW);
        pref.edit().putString("loginType", generalType).apply();
    }

    public static String getGeneralType(){
        pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        String generalType = pref.getString("loginType","");
        return generalType;
    }

    public static boolean clearGeneralType(Activity activity){
        pref = PreferenceManager.getDefaultSharedPreferences(activity);
        pref.edit().remove("loginType").apply();
        String value = pref.getString("loginType", "");
        return value.isEmpty();
    }

    public static void thirdPartyIdentifier(String sID){
        pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        pref.edit().putString("thirdPartyIdentifier", sID).apply();
    }

    public static String getThirdPartyIdentifier(){
        pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        String thirdPartyIdentifier = pref.getString("thirdPartyIdentifier","");
        return thirdPartyIdentifier;
    }

    public static boolean clearThirdPartyIdentifier(Activity activity){
        pref = PreferenceManager.getDefaultSharedPreferences(activity);
        pref.edit().remove("thirdPartyIdentifier").apply();
        String value = pref.getString("thirdPartyIdentifier", "");
        return value.isEmpty();
    }

    public static void isRepeatDevice(Boolean isRepeatDevice){
        pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        pref.edit().putBoolean("isRepeatDevice",isRepeatDevice).apply();
    }

    public static boolean getRepeatDevice(Activity activity){
        pref = PreferenceManager.getDefaultSharedPreferences(activity);
        return pref.getBoolean("isRepeatDevice",false);
    }

    public static boolean clearRepeatDevice(Activity activity){
        pref = PreferenceManager.getDefaultSharedPreferences(activity);
        pref.edit().remove("isRepeatDevice").apply();
        String value = pref.getString("isRepeatDevice", "");
        return value.isEmpty();
    }

    public static void firebasePusherErrorCode(int status){
        pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        pref.edit().putInt("errorCode",status).apply();
    }

    public static int getFirebasePusherErrorCode(Activity activity){
        pref = PreferenceManager.getDefaultSharedPreferences(activity);
        int errorCode = pref.getInt("errorCode", -1);
        return errorCode;
    }

    //設定screenshot布林值
    public static void setScreenshotForbidden(String settingValue){
        pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        boolean screenshotForbidden = Boolean.parseBoolean(settingValue);
        pref.edit().putBoolean("isScreenshotForbidden",screenshotForbidden).apply();
    }

    //取出screenshot布林值
    public static boolean getScreenshotForbidden(Activity activity){
        pref = PreferenceManager.getDefaultSharedPreferences(activity);
        return pref.getBoolean("isScreenshotForbidden",false);
    }

    //設定download布林值
    public static void setDownloadForbidden(String settingValue){
        pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        boolean downloadForbidden = Boolean.parseBoolean(settingValue);
        pref.edit().putBoolean("isDownloadForbidden",downloadForbidden).apply();
    }

    //取出download布林值
    public static boolean getDownloadForbidden(Activity activity){
        pref = PreferenceManager.getDefaultSharedPreferences(activity);
        return pref.getBoolean("isDownloadForbidden",false);
    }

    //設定restrict布林值 檔案名稱需要另做處理
    public static void setRestrictFileExt(String settingValue){
        pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        boolean restrictFileExt = Boolean.parseBoolean(settingValue);
        pref.edit().putBoolean("isRestrictFileExt",restrictFileExt).apply();
    }

    //取出restrict布林值
    public static boolean getRestrictFileExt(Activity activity){
        pref = PreferenceManager.getDefaultSharedPreferences(activity);
        return pref.getBoolean("isRestrictFileExt",false);
    }

    //存文件擴展名
    public static void saveFileExtension(String additionalValue){
        pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        pref.edit().putString("fileExtension", additionalValue).apply();
    }

    //取文件擴展名
    public static String getFileExtension(Activity activity){
        pref = PreferenceManager.getDefaultSharedPreferences(activity);
        String fileExtension = pref.getString("fileExtension","");
        return fileExtension;
    }

    //設定download_watermark布林值
    public static void setWatermark(String settingValue){
        pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        boolean downloadWatermark = Boolean.parseBoolean(settingValue);
        pref.edit().putBoolean("isDownloadWatermark",downloadWatermark).apply();
    }

    //取出download_watermark布林值
    public static boolean getWatermark(Activity activity){
        pref = PreferenceManager.getDefaultSharedPreferences(activity);
        return pref.getBoolean("isDownloadWatermark",false);
    }

    //存浮水印標籤
    public static void watermarkLabel(String additionalValue){
        pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        pref.edit().putString("watermarkLabel", additionalValue).apply();
    }

    //取出浮水印標籤
    public static String getWatermarkLabel(Activity activity){
        pref = PreferenceManager.getDefaultSharedPreferences(activity);
        String fileExtension = pref.getString("watermarkLabel","");
        return fileExtension;
    }

}
