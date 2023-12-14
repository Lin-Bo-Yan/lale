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
import android.util.ArrayMap;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
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
import java.util.Arrays;

public class EimLoginActivity extends MainAppCompatActivity {
    private static Button btn_login;
    static LoginInAppFunc loginFunction;
    private AppCompatTextView textView_login;
    private AppCompatImageView ic_multilingual;
    private TextView app_name;
    private EditText edit_password, edit_account;
    private String language;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_eim);
        signOut();
        loggedInDialog();
        loginFunction = new LoginInAppFunc(EimLoginActivity.this);
        readUrlValid();
        englishVersionScreen();

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
                                if(message != null){
                                    Loginback(EimLoginActivity.this, message);
                                }
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

        ic_multilingual = findViewById(R.id.ic_multilingual);
        ic_multilingual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] languageSelector = new String[]{"繁體中文", "簡體中文", "English"};
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                        EimLoginActivity.this,
                        android.R.layout.simple_list_item_1,
                        languageSelector);
                DialogUtils.showLanguageSelection(EimLoginActivity.this,arrayAdapter);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        screenshotEnable(false);
        SharedPreferencesUtils.clearScreenshotForbidden(EimLoginActivity.this);
        SharedPreferencesUtils.clearDownloadForbidden(EimLoginActivity.this);
        SharedPreferencesUtils.clearRestrictFileExt(EimLoginActivity.this);
        SharedPreferencesUtils.clearFileExtension(EimLoginActivity.this);
        SharedPreferencesUtils.clearWatermark(EimLoginActivity.this);
    }

    public static void saveLog(MainAppCompatActivity activity) {
        activity.runOnUiThread(()->{
            DialogUtils.showDialogMessage(activity, activity.getString(R.string.saveLog_title), activity.getString(R.string.saveLog_text),new CallbackUtils.noReturn() {
                @Override
                    public void Callback() {
                    downloadDir();
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
                String errMsg = result.optString("errMsg");
                if (!errMsg.isEmpty()) {
                    activity.cancelWait();
                    // 根據語言設定替換錯誤訊息
                    if (Arrays.asList("zh-CN", "zh", "en").contains(language)) {
                        errMsg = getErrorMessage(errMsg);
                    }
                    DialogUtils.showDialogMessage(EimLoginActivity.this,errMsg);
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

        HttpAfReturn httpReturn = CloudUtils.iCloudUtils.getEimQRcode(activity, af_token, qrcode_info_url, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("getEimQRcode 網路異常");
                new Handler(Looper.getMainLooper()).post(() -> {
                    CommonUtils.showToast(EimLoginActivity.this,getLayoutInflater(),getString(R.string.network_anomaly),false);
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

        HttpAfReturn httpReturn = CloudUtils.iCloudUtils.getEimQRcodeNew(activity, af_token, qrcode_info_url, uuid, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("getEimQRcodeNew 網路異常");
                new Handler(Looper.getMainLooper()).post(() -> {
                    CommonUtils.showToast(EimLoginActivity.this,getLayoutInflater(),getString(R.string.network_anomaly),false);
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
        HttpReturn httpReturn = CloudUtils.iCloudUtils.loginSimpleThirdParty(eimUserData.af_mem_id, Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID), new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    StringUtils.HaoLog("loginSimpleThirdParty 網路異常");
                    CommonUtils.showToast(EimLoginActivity.this,getLayoutInflater(),getString(R.string.network_anomaly),false);
                });
            }
        });

        if (httpReturn.status == 200) {
            String userMinString = new Gson().toJson(httpReturn.data);
            UserMin userMin = new Gson().fromJson(userMinString, UserMin.class);
            StringUtils.HaoLog("httpReturn2.data=" + new Gson().toJson(httpReturn.data));
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
            switch (httpReturn.msg){
                case "LLUD-0002:人員裝置禁止登入":
                    runOnUiThread(()->{
                        DialogUtils.showDialogMessage(EimLoginActivity.this,getString(R.string.device_management_login_not_allowed_title),getString(R.string.device_management_login_not_allowed_text));
                    });
                    break;
                case "LLU-0002:用戶帳號已停用":
                    runOnUiThread(()->{
                        DialogUtils.showDialogMessage(EimLoginActivity.this,getString(R.string.personnel_usage_management_title),"");
                    });
                    break;
            }
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

    private String getErrorMessage(String errMsg) {
        ArrayMap<String, Integer> errorMessages = new ArrayMap<>();
        errorMessages.put("密碼錯誤!", R.string.account_password_error);
        errorMessages.put("使用者不存在!", R.string.account_password_errMsg);
        errorMessages.put("不允許登入!",R.string.login_not_allowed);
        errorMessages.put("無法找到 webAgendaUrl4LaleAuth 路徑設定!",R.string.unable_find_path);
        Integer resId = errorMessages.get(errMsg);
        if (resId != null) {
            return getString(resId);
        }
        return errMsg;
    }

    private void englishVersionScreen(){
        app_name = findViewById(R.id.app_name);
        edit_password = findViewById(R.id.edit_password);
        edit_account = findViewById(R.id.edit_account);
        language = SharedPreferencesUtils.getLanguageChoice(EimLoginActivity.this);
        if("en".equals(language)){
            app_name.setTextSize(33);
            app_name.setTranslationX(-80);
            app_name.setTranslationY(20);
            edit_account.setTextSize(15);
            edit_password.setTextSize(13);
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

    private static void downloadDir(){
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
