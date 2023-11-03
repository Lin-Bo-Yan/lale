package com.flowring.laleents.ui.main.webBody;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.auth0.android.jwt.JWT;
import com.flowring.laleents.R;
import com.flowring.laleents.model.HttpAfReturn;
import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.eim.EimUserData;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.model.user.UserMin;
import com.flowring.laleents.tools.ActivityUtils;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.CommonUtils;
import com.flowring.laleents.tools.DialogUtils;
import com.flowring.laleents.tools.FileUtils;
import com.flowring.laleents.tools.Log;
import com.flowring.laleents.tools.NetUtils;
import com.flowring.laleents.tools.SharedPreferencesUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.ui.model.EimLogin.LoginInAppFunc;
import com.flowring.laleents.ui.model.MainAppCompatActivity;
import com.flowring.laleents.ui.widget.qrCode.ScanCaptureActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EimLoginActivity extends MainAppCompatActivity {
    private static Button btn_login;
    static LoginInAppFunc loginFunction;
    private AppCompatTextView textView_login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_eim);
        signOut();
        loggedInDialog();
        loginFunction = new LoginInAppFunc(EimLoginActivity.this);
        readUrlValid();
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_login.isEnabled()){
                    saveUrlValid(loginFunction.urlValid);
                    boolean haveInternet = checkNetworkAndContinue();
                    if(haveInternet){
                        UserControlCenter.getAflogin(EimLoginActivity.this,loginFunction.accountValid, loginFunction.passwordValid, loginFunction.urlValid, new CallbackUtils.messageReturn() {
                            @Override
                            public void Callback(String message) {
                                StringUtils.HaoLog("結果:" + message);
                                Loginback(EimLoginActivity.this, message);
                            }
                        });
                    }
                }
            }
        });


        textView_login = findViewById(R.id.textView_login);
        textView_login.setOnClickListener(view -> {
            activityReturn = new CallbackUtils.ActivityReturn() {
                @Override
                public void Callback(androidx.activity.result.ActivityResult activityResult) {
                    if (activityResult.getResultCode() == Activity.RESULT_OK) {
                        String SCAN_QRCODE = activityResult.getData().getStringExtra("SCAN_QRCODE");
                        StringUtils.HaoLog("結果:" + SCAN_QRCODE);
                        boolean haveInternet = checkNetworkAndContinue();
                        if(haveInternet && SCAN_QRCODE != null){
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
                String af_token = result.optString("af_token");
                String qrcode_info_url = result.optString("qrcode_info_url");
                if (af_token.isEmpty() || qrcode_info_url.isEmpty()) {
                    activity.cancelWait();
                    DialogUtils.showDialogMessage(EimLoginActivity.this,"請輸入正確的帳號和密碼");
                    return;
                }

                //判斷是否有 dev_id
                JWT jwt = new JWT(af_token);
                String deviceId = jwt.getClaim("dev_id").asString();
                String uuid = Settings.Secure.getString(AllData.context.getContentResolver(), Settings.Secure.ANDROID_ID);
                if(deviceId != null && !deviceId.isEmpty()){
                    connection_server_post_httpReturn(activity,result,uuid);
                } else {
                    connection_server_get_httpReturn(activity,result);
                }
            } else {
                activity.cancelWait();
                saveLog(activity);
            }
        }).start();
    }

    public void connection_server_get_httpReturn(MainAppCompatActivity activity, JSONObject result){
        String af_token = result.optString("af_token");
        String qrcode_info_url = result.optString("qrcode_info_url");
        String errMsg = result.optString("errMsg");

        if (!errMsg.isEmpty() && errMsg != null) {
            activity.cancelWait();
            DialogUtils.showDialogMessage(EimLoginActivity.this,errMsg);
            return;
        }

        HttpAfReturn httpReturn = CloudUtils.iCloudUtils.getEimQRcode(activity, af_token, qrcode_info_url, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("getEimQRcode 網路異常");
                new Handler(Looper.getMainLooper()).post(() -> {
                    CommonUtils.showToast(EimLoginActivity.this,getLayoutInflater(),"網路異常",false);
                });
            }
        });

        if (httpReturn.success) {
            StringUtils.HaoLog("掃描成功");
            StringUtils.HaoLog("取得使用者資料:" + new Gson().toJson(httpReturn.data));

            String eimUserDataString = new Gson().toJson(httpReturn.data);
            EimUserData eimUserData = new Gson().fromJson(eimUserDataString, EimUserData.class);
            saveUrlValid(eimUserData.af_url);
            {
                UserMin userMin = eimUserData.getUserMin();
                StringUtils.HaoLog("userMin=" + userMin);
                UserControlCenter.setLogin(userMin);
                UserControlCenter.updateUserMinInfo(userMin);
            }

            if(eimUserData.isLaleAppEim) {
                String deviceID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                boolean isRepeatDevice = UserControlCenter.alreadyLoddedIn("6","",eimUserData.af_mem_id,deviceID);
                StringUtils.HaoLog("isRepeatDevice= "+isRepeatDevice);
                if(isRepeatDevice){
                    //跳出 dialog
                    StringUtils.HaoLog("是否重複登入? "+"登入");
                    String buttonString = "[\"ok\",\"cancel\"]";
                    Type listType = new TypeToken<List<String>>(){}.getType();
                    List<String> buttons = new Gson().fromJson(buttonString, listType);
                    List<CallbackUtils.noReturn> callbacks = new ArrayList<>();
                    for(int i = 0; i < buttons.size(); i++){
                        final int buttonIndex = i;
                        CallbackUtils.noReturn callback = new CallbackUtils.noReturn() {
                            @Override
                            public void Callback() {
                                String button = buttons.get(buttonIndex);
                                switch (button){
                                    case "ok":
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loginSimpleThirdParty(activity, eimUserData);
                                            }
                                        }).start();
                                        break;
                                }
                            }
                        };
                        callbacks.add(callback);
                    }
                    DialogUtils.showDialogCancelable(activity,getString(R.string.single_device_title),getString(R.string.single_device_text),buttons,callbacks);
                    activity.cancelWait();
                } else {
                    loginSimpleThirdParty(activity,eimUserData);
                }
            } else if (eimUserData.isLaleAppWork) {
                UserControlCenter.storeAfErrorCode(activity);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        } else {
            activity.cancelWait();
            saveLog(activity);
        }
    }

    public void connection_server_post_httpReturn(MainAppCompatActivity activity, JSONObject result,String uuid){
        String af_token = result.optString("af_token");
        String qrcode_info_url = result.optString("qrcode_info_url");
        String errMsg = result.optString("errMsg");

        if (!errMsg.isEmpty() && errMsg != null) {
            activity.cancelWait();
            DialogUtils.showDialogMessage(EimLoginActivity.this,errMsg);
            return;
        }
        HttpAfReturn httpReturn = CloudUtils.iCloudUtils.getEimQRcodeNew(activity, af_token, qrcode_info_url, uuid, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("getEimQRcodeNew 網路異常");
                new Handler(Looper.getMainLooper()).post(() -> {
                    CommonUtils.showToast(EimLoginActivity.this,getLayoutInflater(),"網路異常",false);
                });
            }
        });
        if(httpReturn.success){
            StringUtils.HaoLog("掃描新 getEimQRcode 成功");
            StringUtils.HaoLog("取得使用者資料:" + new Gson().toJson(httpReturn.data));
            String eimUserDataString = new Gson().toJson(httpReturn.data);
            EimUserData eimUserData = new Gson().fromJson(eimUserDataString, EimUserData.class);
            saveUrlValid(eimUserData.af_url);
            {
                UserMin userMin = eimUserData.getUserMin();
                StringUtils.HaoLog("userMin=" + userMin);
                UserControlCenter.setLogin(userMin);
                UserControlCenter.updateUserMinInfo(userMin);
            }
            if(eimUserData.isLaleAppEim) {
                String deviceID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                boolean isRepeatDevice = UserControlCenter.alreadyLoddedIn("6","",eimUserData.af_mem_id,deviceID);
                StringUtils.HaoLog("isRepeatDevice= "+isRepeatDevice);
                if(isRepeatDevice){
                    //跳出 dialog
                    StringUtils.HaoLog("是否重複登入? "+"登入");
                    String buttonString = "[\"ok\",\"cancel\"]";
                    Type listType = new TypeToken<List<String>>(){}.getType();
                    List<String> buttons = new Gson().fromJson(buttonString, listType);
                    List<CallbackUtils.noReturn> callbacks = new ArrayList<>();
                    for(int i = 0; i < buttons.size(); i++){
                        final int buttonIndex = i;
                        CallbackUtils.noReturn callback = new CallbackUtils.noReturn() {
                            @Override
                            public void Callback() {
                                String button = buttons.get(buttonIndex);
                                switch (button){
                                    case "ok":
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                loginSimpleThirdParty(activity, eimUserData);
                                            }
                                        }).start();
                                        break;
                                }
                            }
                        };
                        callbacks.add(callback);
                    }
                    DialogUtils.showDialogCancelable(activity,getString(R.string.single_device_title),getString(R.string.single_device_text),buttons,callbacks);
                    activity.cancelWait();
                } else {
                    loginSimpleThirdParty(activity,eimUserData);
                }
            } else if (eimUserData.isLaleAppWork) {
                UserControlCenter.storeAfErrorCode(activity);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
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
        HttpReturn httpReturn2 = CloudUtils.iCloudUtils.loginSimpleThirdParty(eimUserData.af_mem_id, Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID), new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    StringUtils.HaoLog("loginSimpleThirdParty 網路異常");
                    CommonUtils.showToast(EimLoginActivity.this,getLayoutInflater(),"網路異常",false);
                });
            }
        });

        if (httpReturn2.status == 200) {
            String userMinString = new Gson().toJson(httpReturn2.data);
            UserMin userMin = new Gson().fromJson(userMinString, UserMin.class);
            StringUtils.HaoLog("httpReturn2.data=" + new Gson().toJson(httpReturn2.data));
            userMin.eimUserData = eimUserData;
            userMin.eimUserData.lale_token = userMin.token;
            userMin.eimUserData.refresh_token = userMin.refreshToken;
            UserControlCenter.setLogin(userMin);
            UserControlCenter.updateUserMinInfo(userMin);
            UserControlCenter.storeEimErrorCode(activity);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
        } else {
            saveLog(activity);
            activity.cancelWait();
        }
    }

    private void signOut(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences((Context) EimLoginActivity.this);
        boolean signOut = pref.getBoolean("isSignOut",false);
        StringUtils.HaoLog("signOut 布林："+signOut);
        if(signOut){
            DialogUtils.showDialogMessage(EimLoginActivity.this,getString(R.string.unused_account_logged_out));
            pref.edit().putBoolean("isSignOut", false).apply();
        } else {
            StringUtils.HaoLog("SharedPreferences 沒有值");
        }
    }

    private void loggedInDialog(){
        boolean wasLoggedOut = SharedPreferencesUtils.getRepeatDevice(EimLoginActivity.this);
        StringUtils.HaoLog("已被已有其他設備登出 " + wasLoggedOut);
        if(wasLoggedOut){
            DialogUtils.showDialogMessage(EimLoginActivity.this,getString(R.string.single_device_sign_out_title),getString(R.string.single_device_sign_out_text));
            boolean clearRepeatDevice = SharedPreferencesUtils.clearRepeatDevice(EimLoginActivity.this);
            boolean clearGeneralType = SharedPreferencesUtils.clearGeneralType(EimLoginActivity.this);
            boolean clearThirdPartyIdentifier = SharedPreferencesUtils.clearThirdPartyIdentifier(EimLoginActivity.this);
            StringUtils.HaoLog("已被已有其他設備登出 " + clearRepeatDevice + " / " + clearGeneralType + " / " + clearThirdPartyIdentifier);
        }
    }

    /**
     * 確認使用者是否已有登入過的行動裝置
     */
    public static boolean alreadyLoddedIn(String loginType, String userId, String thirdPartyIdentifier, String deviceId){
        StringUtils.HaoLog("loginType=" + loginType + "\nuserId=" + userId + "\nthirdPartyIdentifier=" + thirdPartyIdentifier + "\ndeviceId=" + deviceId);
        HttpReturn httpReturn = CloudUtils.iCloudUtils.alreadyLoddedIn(loginType, userId, thirdPartyIdentifier, deviceId, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        if(httpReturn.status == 200){
            String msg = httpReturn.msg;
            boolean data = (boolean) httpReturn.data;
            switch (msg){
                case "Success":
                case "用戶 ID 不得為空":
                case "第三方登入 identifier 不可為空":
                    return data;
            }
        }
        return false;
    }

    public void close_Input_Board(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.toolbar_layout).getWindowToken(), 0);
    }

    public static void checkSignBtn() {
        StringUtils.HaoLog("canSign= " + loginFunction.canSign);
        //設置了一個登入按鈕的啟用狀態，如果兩個 canSign 都為 true，則登入按鈕將被啟用；否則，它將保持禁用狀態
        btn_login.setEnabled(loginFunction.canSign);
    }

    private void saveUrlValid(String value){
        String fileName = "UrlValid.txt";
        String filePath = getCacheDir().getAbsolutePath() + File.separator + fileName;
        File file = new File(filePath);
        if(file.exists()){
            FileUtils.saveTextInFile(value,file);
        }
    }

    private void readUrlValid(){
        String fileName = "UrlValid.txt";
        String filePath = getCacheDir().getAbsolutePath() + File.separator + fileName;
        File file = new File(filePath);
        if(file.exists()){
            String urlText = FileUtils.readTextFromFile(file);
            StringUtils.HaoLog("readUrlValid= 已有檔案 "+urlText);
            loginFunction.edit_url.setText(urlText);
        } else {
            StringUtils.HaoLog("readUrlValid= 第一次使用");
            FileUtils.saveTextInFile("",file);
        }
    }

    private boolean checkNetworkAndContinue() {
        if(NetUtils.isNetworkAvailable(EimLoginActivity.this)){
            return true;
        } else {
            DialogUtils.showDialogMessage(EimLoginActivity.this, getString(R.string.dialog_check_network_text), "", new CallbackUtils.noReturn() {
                @Override
                public void Callback() {
                    checkNetworkAndContinue();
                }
            });
        }
        return false;
    }
}
