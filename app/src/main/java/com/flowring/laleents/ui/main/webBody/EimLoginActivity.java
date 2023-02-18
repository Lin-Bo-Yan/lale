package com.flowring.laleents.ui.main.webBody;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.flowring.laleents.R;
import com.flowring.laleents.model.HttpAfReturn;
import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.eim.EimUserData;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.model.user.UserMin;
import com.flowring.laleents.tools.ActivityUtils;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.DialogUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.ui.model.MainAppCompatActivity;
import com.flowring.laleents.ui.widget.qrCode.ScanCaptureActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class EimLoginActivity extends MainAppCompatActivity {
    public Button btn_login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_eim);

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

    public static void Loginback(MainAppCompatActivity activity, final String resultData) {
        activity.showWait();

        new Thread(() -> {
            StringUtils.HaoLog("onActivityResult Scan QRcode = " + resultData);
            if (resultData == null) {
                activity.cancelWait();
                DialogUtils.showDialogMessage(activity, "登入失敗，您的QRCode已失效");
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
                DialogUtils.showDialogMessage(activity, "登入失敗，您的QRCode已失效 ");
            }
        }).start();
    }
    public static void connection_server_get_httpReturn(MainAppCompatActivity activity, JSONObject result){
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
                    DialogUtils.showDialogMessage(activity, "登入失敗 請更新QR code");
                    activity.cancelWait();
                }
            }else if ( eimUserData.isLaleAppWork == true) {
                FirebasePusher_AF_push_registration(activity);
            }
        } else {
            activity.cancelWait();
            DialogUtils.showDialogMessage(activity, "登入失敗，您的QRCode已失效");
        }
    }


    public static void FirebasePusher_AF_push_registration(MainAppCompatActivity activity){
        StringUtils.HaoLog("AF_push_registration");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                StringUtils.HaoLog("deviceToken:"+deviceToken);
                new Thread(() -> {
                    String WFCI_URL = UserControlCenter.getUserMinInfo().eimUserData.af_wfci_service_url;
                    String memId = UserControlCenter.getUserMinInfo().eimUserData.af_mem_id;
                    String userId = UserControlCenter.getUserMinInfo().eimUserData.af_login_id;
                    String uuid = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                    HttpAfReturn pu = CloudUtils.iCloudUtils.setAfPusher(WFCI_URL, memId,userId, deviceToken, uuid);

                    StringUtils.HaoLog("AF推播註冊:", pu);
                    activity.runOnUiThread(() -> {
                        activity.finish();
                    });
                }).start();
            }
        });
    }

    public static void FirebasePusher_LaleAppEim(MainAppCompatActivity activity){
        StringUtils.HaoLog("測試帳號登入會觸發");
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
                String deviceToken = instanceIdResult.getToken();
                new Thread(() -> {
                    HttpReturn pu;
                    try {
                        JSONObject UserIds = new JSONObject(pref.getString("UserIds", "{}"));
                        StringUtils.HaoLog("UserIds=" + UserIds.toString());
                        StringUtils.HaoLog("deviceToken=" + deviceToken);

                        if (UserIds.length() <= 1){
                            String userId = UserControlCenter.getUserMinInfo().userId;
                            String uuid = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                            pu = CloudUtils.iCloudUtils.setPusher(userId, deviceToken, uuid);
                        } else {
                            String userId = UserControlCenter.getUserMinInfo().userId;
                            String uuid = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
                            UserControlCenter.switchAccounts(userId);
                            pu = CloudUtils.iCloudUtils.updatePusher(userId, uuid);
                        }
                        StringUtils.HaoLog("setPusher=" + pu);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    activity.runOnUiThread(() -> {
                        activity.finish();
                    });
                }).start();
            }
        });
    }
}
