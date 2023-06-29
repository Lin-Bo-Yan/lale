package com.flowring.laleents.ui.main.webBody;

import static com.flowring.laleents.tools.UiThreadUtil.runOnUiThread;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.flowring.laleents.R;
import com.flowring.laleents.model.HttpAfReturn;
import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.eim.EimUserData;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.model.user.UserMin;
import com.flowring.laleents.tools.ActivityUtils;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.DialogUtils;
import com.flowring.laleents.tools.Log;
import com.flowring.laleents.tools.SharedPreferencesUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.ui.model.MainAppCompatActivity;
import com.flowring.laleents.ui.widget.qrCode.ScanCaptureActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class EimLoginActivity extends MainAppCompatActivity {
    public Button btn_login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_eim);
        signOut();
        loggedInDialog();
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(view -> {
            activityReturn = new CallbackUtils.ActivityReturn() {
                @Override
                public void Callback(androidx.activity.result.ActivityResult activityResult) {
                    if (activityResult.getResultCode() == Activity.RESULT_OK) {
                        String SCAN_QRCODE = activityResult.getData().getStringExtra("SCAN_QRCODE");
                        StringUtils.HaoLog("結果:" + SCAN_QRCODE);
                        if (SCAN_QRCODE != null){
                            Loginback(EimLoginActivity.this, SCAN_QRCODE);
                        }
                    }
                }
            };
            ScanCaptureActivity.ScanCaptureType ScanCaptureType = ScanCaptureActivity.ScanCaptureType.Json;
            ActivityUtils.gotoQRcode(this, ScanCaptureType, ActivityResult);
        });
    }
    public static void saveLog(MainAppCompatActivity activity) {
        activity.runOnUiThread(()->{
            DialogUtils.showDialogMessage(activity, "登入失敗，您的QRCode已失效","請重新登入" ,new CallbackUtils.noReturn() {
                @Override
                    public void Callback() {
                        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File targetFile = new File(downloadDir, "log.log");
                        try {
                            FileInputStream fis = new FileInputStream( Log.mLogFile);
                            FileOutputStream fos = new FileOutputStream(targetFile);
                            byte[] buffer = new byte[1024];
                            int read;
                            while ((read = fis.read(buffer)) != -1) {
                                fos.write(buffer, 0, read);
                            }
                            fis.close();
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            );
        });
    }

    public void Loginback(MainAppCompatActivity activity, final String resultData) {
        activity.showWait();

        new Thread(() -> {
            StringUtils.HaoLog("onActivityResult Scan QRcode = " + resultData);
            if (resultData == null) {
                activity.cancelWait();
                saveLog(activity);

                return;
            }

            JSONObject result = null;
            try {
                result = new JSONObject(resultData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result != null && result.has("qrcode_info_url") && result.has("af_token")) {
                connection_server_get_httpReturn(activity,result);
            } else {
                activity.cancelWait();
                saveLog(activity);
            }
        }).start();
    }

    public void connection_server_get_httpReturn(MainAppCompatActivity activity, JSONObject result){
        String af_token = result.optString("af_token");
        String qrcode_info_url = result.optString("qrcode_info_url");
        HttpAfReturn httpReturn = CloudUtils.iCloudUtils.getEimQRcode(activity, af_token, qrcode_info_url);
        if (httpReturn.success) {
            StringUtils.HaoLog("掃描成功");
            StringUtils.HaoLog("取得使用者資料:" + new Gson().toJson(httpReturn.data));
//                    String test="{\n" +
//                            "    \"isLaleAppEim\": false,\n" +
//                            "     \"isLaleAppWork\": true,\n" +
//                            "  \"af_url\": \"https://agentflow.flowring.com:8443/WebAgenda\",\n" +
//                            "  \"af_mem_id\": \"MEM00001091511489187\",\n" +
//                            "  \"af_login_id\": \"f0\",\n" +
//                            "  \"af_token\": \"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmMCIsImF1ZCI6Ik1FTTAwMDAxMDkxNTExNDg5MTg3IiwiaXNzIjoiRmxvd0Rlb20iLCJuYW1lIjoi5Y-k6JGjIiwiZXhwIjoxNjY2MTQ0MzA0LCJpYXQiOjE2NjU1Mzk1MDR9.AYfwO7xG8AdOKk43zb4lqcW8UhSQEXWQiH9rqBpFe50\",\n" +
//                            "  \"af_wfci_service_url\": \"http://192.168.3.53:8083\"\n" +
//                            "}";

            String eimUserDataString = new Gson().toJson(httpReturn.data);
            EimUserData eimUserData = new Gson().fromJson(eimUserDataString, EimUserData.class);
            {
                UserMin userMin = eimUserData.getUserMin();

                StringUtils.HaoLog("userMin=" + userMin);
                UserControlCenter.setLogin(userMin);
                UserControlCenter.updateUserMinInfo(userMin);
            }

            if(eimUserData.isLaleAppEim) {
                String deviceID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                Boolean isRepeatDevice = alreadyLoddedIn("6","",eimUserData.af_mem_id,deviceID);
                StringUtils.HaoLog("isRepeatDevice= "+isRepeatDevice);
                if(isRepeatDevice){
                    //跳出 dialog
                    StringUtils.HaoLog("是否重複登入? "+"登入");
                    DialogUtils.showDialogCheckMessage(activity, "是否登入此裝置", "您之前未正常登出或已於其他裝置登入，請確認是否登入此裝置(將登出其他裝置)", new CallbackUtils.noReturn() {
                        @Override
                        public void Callback() {
                            activity.cancelWait();
                        }
                    }, new CallbackUtils.noReturn() {
                        @Override
                        public void Callback() {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    loginSimpleThirdParty(activity, eimUserData);
                                }
                            }).start();
                        }
                    });
                } else {
                    loginSimpleThirdParty(activity,eimUserData);
                }
            } else if ( eimUserData.isLaleAppWork == true) {
                FirebasePusher_AF_push_registration(activity);
            }
        } else {
            activity.cancelWait();
            saveLog(activity);
        }
    }

    private void loginSimpleThirdParty(MainAppCompatActivity activity, EimUserData eimUserData){
        //存loginType
        SharedPreferencesUtils.generalType();
        SharedPreferencesUtils.thirdPartyIdentifier(eimUserData.af_mem_id);
        HttpReturn httpReturn2 = CloudUtils.iCloudUtils.loginSimpleThirdParty(eimUserData.af_mem_id, Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID));

        if (httpReturn2.status == 200) {

            String userMinString = new Gson().toJson(httpReturn2.data);
            UserMin userMin = new Gson().fromJson(userMinString, UserMin.class);
            StringUtils.HaoLog("httpReturn2.data=" + new Gson().toJson(httpReturn2.data));
            userMin.eimUserData = eimUserData;

            userMin.eimUserData.lale_token = userMin.token;
            userMin.eimUserData.refresh_token = userMin.refreshToken;
            UserControlCenter.setLogin(userMin);
            UserControlCenter.updateUserMinInfo(userMin);
            FirebasePusher_LaleAppEim(activity);
        } else {
            saveLog(activity);
            activity.cancelWait();
        }
    }

    public void FirebasePusher_AF_push_registration(MainAppCompatActivity activity){
        StringUtils.HaoLog("AF_push_registration");
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String deviceToken) {
                if (!deviceToken.isEmpty() && deviceToken != null) {
                    StringUtils.HaoLog("deviceToken: "+deviceToken);
                    new Thread(() -> {
                        String WFCI_URL = UserControlCenter.getUserMinInfo().eimUserData.af_wfci_service_url;
                        String memId = UserControlCenter.getUserMinInfo().eimUserData.af_mem_id;
                        String userId = UserControlCenter.getUserMinInfo().eimUserData.af_login_id;
                        String uuid = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                        String customerProperties = HashMapToJson(userId,WFCI_URL,true,deviceToken);
                        HttpAfReturn pu = CloudUtils.iCloudUtils.setAfPusher(WFCI_URL, memId,userId, deviceToken, uuid, customerProperties);
                        StringUtils.HaoLog("AF推播註冊:", pu);
                        activity.runOnUiThread(() -> {
                            activity.finish();
                        });
                    }).start();
                }
            }
        });
    }

    public static void FirebasePusher_LaleAppEim(MainAppCompatActivity activity){
        StringUtils.HaoLog("測試帳號登入會觸發");
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String deviceToken) {
                if (!deviceToken.isEmpty() && deviceToken != null) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
                    new Thread(() -> {
                        HttpReturn pu;
                        try {
                            JSONObject UserIds = new JSONObject(pref.getString("UserIds", "{}"));
                            StringUtils.HaoLog("UserIds= " + UserIds);
                            StringUtils.HaoLog("deviceToken= " + deviceToken);
                            if (UserIds.length() <= 1){
                                String userId = UserControlCenter.getUserMinInfo().userId;
                                String uuid = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                                String customerProperties = HashMapToJson(userId,AllData.getMainServer(),false, "");
                                pu = CloudUtils.iCloudUtils.setPusher(userId, deviceToken, uuid, customerProperties);
                            } else {
                                String userId = UserControlCenter.getUserMinInfo().userId;
                                String uuid = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                                UserControlCenter.switchAccounts(userId);
                                pu = CloudUtils.iCloudUtils.updatePusher(userId, uuid);
                            }
                            StringUtils.HaoLog("setPusher= " + pu);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        activity.runOnUiThread(() -> {
                            activity.finish();
                        });
                    }).start();
                }
            }
        });
    }

    private void signOut(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences((Context) EimLoginActivity.this);
        Boolean signOut = pref.getBoolean("isSignOut",false);
        StringUtils.HaoLog("getPref 布林："+signOut);
        if(signOut){
            DialogUtils.showDialog(EimLoginActivity.this, new CallbackUtils.tokenReturn() {
                @Override
                public void Callback() {}
            });
            pref.edit().putBoolean("isSignOut", false).apply();
        } else {
            StringUtils.HaoLog("SharedPreferences 沒有值");
        }
    }

    private void loggedInDialog(){
        Boolean wasLoggedOut = SharedPreferencesUtils.getRepeatDevice(EimLoginActivity.this);
        StringUtils.HaoLog("已被已有其他設備登出 "+wasLoggedOut);
        StringUtils.HaoLog("已被已有其他設備登出 "+Thread.currentThread().getName());
        if(wasLoggedOut){
            DialogUtils.showDialogMessage(EimLoginActivity.this,getString(R.string.single_device_sign_out_title),getString(R.string.single_device_sign_out_text));
            SharedPreferencesUtils.clearRepeatDevice(EimLoginActivity.this);
            SharedPreferencesUtils.clearGeneralType(EimLoginActivity.this);
            SharedPreferencesUtils.clearThirdPartyIdentifier(EimLoginActivity.this);
        }
    }

    /**
     * 額外自訂義推送資訊
     */
    private static String HashMapToJson(String userId, String domain, Boolean isAF, String deviceToken) {
        JSONObject json = new JSONObject();
        try{
            json.put("userId",userId);
            json.put("domain",domain);
            json.put("isAF",isAF);
            if(isAF){
                StringUtils.HaoLog("deviceToken= "+deviceToken);
                json.put("deviceToken",deviceToken);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 確認使用者是否已有登入過的行動裝置
     */
    public static boolean alreadyLoddedIn(String loginType, String userId, String thirdPartyIdentifier, String deviceId){
        StringUtils.HaoLog("loginType=" + loginType + "\nuserId=" + userId + "\nthirdPartyIdentifier=" + thirdPartyIdentifier + "\ndeviceId=" + deviceId);
        HttpReturn httpReturn = CloudUtils.iCloudUtils.alreadyLoddedIn(loginType,userId,thirdPartyIdentifier,deviceId);
        if(httpReturn.status == 200){
            String msg = httpReturn.msg;
            boolean data = (Boolean) httpReturn.data;
            switch (msg){
                case "Success":
                case "用戶 ID 不得為空":
                case "第三方登入 identifier 不可為空":
                    return data;
            }
        }
        return false;
    }
}
