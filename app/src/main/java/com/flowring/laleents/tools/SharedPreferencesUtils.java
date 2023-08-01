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

    public static Boolean getRepeatDevice(Activity activity){
        pref = PreferenceManager.getDefaultSharedPreferences(activity);
        return pref.getBoolean("isRepeatDevice",false);
    }

    public static boolean clearRepeatDevice(Activity activity){
        pref = PreferenceManager.getDefaultSharedPreferences(activity);
        pref.edit().remove("isRepeatDevice").apply();
        String value = pref.getString("isRepeatDevice", "");
        return value.isEmpty();
    }
}
