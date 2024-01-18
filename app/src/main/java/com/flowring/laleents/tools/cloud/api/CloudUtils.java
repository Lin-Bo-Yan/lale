package com.flowring.laleents.tools.cloud.api;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.flowring.laleents.model.Http2Return;
import com.flowring.laleents.model.HttpAfReturn;
import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.room.RoomInfoInPhone;
import com.flowring.laleents.model.stickerlibrary.CustomizeSticker;
import com.flowring.laleents.model.stickerlibrary.Stickerlibrary;
import com.flowring.laleents.model.user.AfTokenInfo;
import com.flowring.laleents.model.user.GetMinVersion;
import com.flowring.laleents.model.user.TokenInfo;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.model.user.UserMin;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.ui.main.webBody.MainWebActivity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CloudUtils implements ICloudUtils {


    static public ICloudUtils iCloudUtils = new CloudUtils();

    static public void init_I_Refactor_APIUtils(CloudUtils i_refactor_apiUtils) {
        CloudUtils.iCloudUtils = i_refactor_apiUtils;
    }


    @Override
    public boolean checkAppNeedUpdate(CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/util/app/version/android")
                .get()
                .addHeader("Content-Type", "application/json");
        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        if(httpReturn.status != 200){
            StringUtils.HaoLog("checkAppNeedUpdate= 錯誤碼 "+httpReturn.status);
            return false;
        }
        try {
            String data = new Gson().toJson(httpReturn.data);
            GetMinVersion dbVersion = new Gson().fromJson(data,GetMinVersion.class);
            String appVersion = MainWebActivity.getVersionName(AllData.context);
            StringUtils.HaoLog("checkAppNeedUpdate= appVersion " + appVersion);
            StringUtils.HaoLog("checkAppNeedUpdate= dbVersion " + dbVersion.version);
            boolean needUpdated = StringUtils.version(appVersion,dbVersion.version);
            StringUtils.HaoLog("checkAppNeedUpdate= 需要更新嗎? " + needUpdated);
            return needUpdated;
        }catch (JsonSyntaxException e){
            StringUtils.HaoLog("checkAppNeedUpdate error=" + request + " " + e);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public HttpReturn changePassword(String verifyCode, String UserId, String password) {
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject body_j = new JSONObject();
        try {
            body_j.put("userId", UserId);
            body_j.put("verificationCode", verifyCode);
            body_j.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mediaType, body_j.toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/resetpassword")
                .method("POST", body)
                .addHeader("Content-Type", "application/json");
        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn forgetPasswordCheckEmail(String userAccount) {
        JSONObject body_j = new JSONObject();
        try {
            body_j.put("userId", userAccount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), body_j.toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/forgetpassword")
                .method("POST", body)
                .addHeader("Authorization", "Bearer brice004")
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn signup(JSONObject body) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody requestBody = RequestBody.create(mediaType, body.toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/register")
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn signupCheck(String userId, String phone, String email) {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("userId", userId);
        map.put("phone", phone);
        map.put("email", email);
        RequestBody requestBody = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/info/duplicate")
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn login(Context context, String deviceID, String account, String password) {
        MediaType mediaType = MediaType.parse("application/json");
        String androidVersion = Build.VERSION.RELEASE;
        String manufacturer = Build.MANUFACTURER;
        String device = Build.DEVICE;
        String deviceName = String.format("%s-%s",manufacturer,device);

        JSONObject jbody = new JSONObject();
        try {
            jbody.put("userId",account);
            jbody.put("password",password);
            jbody.put("deviceId",deviceID);
            jbody.put("devicePlatform","android");
            jbody.put("devicePlatformVersion",androidVersion);
            jbody.put("deviceName",deviceName);
        }catch (JSONException e){
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(mediaType,jbody.toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/login")
                .method("POST", body)
                .addHeader("charset", "utf-8")
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn loginThirdParty(String displayName, String deviceID, int type, String sID, File image) {
        JSONObject jbody = new JSONObject();
        String androidVersion = Build.VERSION.RELEASE;
        String manufacturer = Build.MANUFACTURER;
        String device = Build.DEVICE;
        String deviceName = String.format("%s-%s",manufacturer,device);
        try {
            jbody.put("loginType", type);
            jbody.put("thirdPartyIdentifier", sID);
            if (displayName != null && !displayName.isEmpty()){
                jbody.put("displayName", displayName);
            }
            jbody.put("deviceId", deviceID);
            jbody.put("devicePlatform","android");
            jbody.put("devicePlatformVersion",androidVersion);
            jbody.put("deviceName",deviceName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body;
        if (image != null) {
            body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("user", jbody.toString())
                    .addFormDataPart("image", image.getName(),
                            RequestBody.create(MediaType.parse("application/octet-stream"),
                                    image)).build();
        } else {
            body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("user", jbody.toString()).build();
        }
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/thirdparty/login")
                .method("POST", body);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn alreadyLoddedIn(String loginType, String userId, String thirdPartyIdentifier, String deviceId, CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject jbody = new JSONObject();
        try {
            if(loginType != null && !loginType.isEmpty()){
                jbody.put("loginType",loginType);
                jbody.put("deviceId",deviceId);
                jbody.put("userId",userId);
                jbody.put("thirdPartyIdentifier",thirdPartyIdentifier);
            } else {
                StringUtils.HaoLog("alreadyLoddedIn loginType 為空");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringUtils.HaoLog("alreadyLoddedIn= "+ jbody);
        RequestBody body = RequestBody.create(mediaType,jbody.toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/mobile/logged")
                .method("POST", body)
                .addHeader("Content-Type", "application/json");
        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn loginSimpleThirdParty(String thirdPartyIdentifier,String userId, String deviceId, CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        String androidVersion = Build.VERSION.RELEASE;
        String manufacturer = Build.MANUFACTURER;
        String device = Build.DEVICE;
        String deviceName = String.format("%s-%s",manufacturer,device);
        //RequestBody body = RequestBody.create(mediaType, "{\"thirdPartyIdentifier\": \"" + thirdPartyIdentifier + "\",  \"deviceId\": \"" + deviceId + "\",  \"loginType\":6 }");
        JSONObject jbody = new JSONObject();
        try {
            jbody.put("loginType",6);
            jbody.put("thirdPartyIdentifier",thirdPartyIdentifier);
            jbody.put("displayName",UserControlCenter.getUserMinInfo().userId);
            jbody.put("deviceId",deviceId);
            jbody.put("devicePlatform","android");
            jbody.put("devicePlatformVersion",androidVersion);
            jbody.put("deviceName",deviceName);
            jbody.put("userId",userId);
        }catch (JSONException e){
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mediaType,jbody.toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/thirdparty/simple-login")
                .method("POST", body)
                .addHeader("charset", "utf-8")
                .addHeader("Content-Type", "application/json");
        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }


    @Override
    public void gotoGooglePlay(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(
                "https://play.google.com/store/apps/details?id=com.flowring.laleents"));
        intent.setPackage("com.android.vending");
        activity.startActivity(intent);
    }

    @Override
    public HttpAfReturn getEimQRcode(Context context, String af_token, String qrcode_info_url,CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(qrcode_info_url)
                .get()
                .addHeader("Authorization", "Bearer " + af_token);
        HttpAfReturn httpAfReturn = getJhttpAfReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpAfReturn;
    }

    @Override
    public HttpAfReturn getEimQRcodeNew(Context context, String af_token, String qrcode_info_url, String deviceId,CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject bodyJect = new JSONObject();
        try {
            bodyJect.put("deviceId", deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mediaType, bodyJect.toString());
        Request.Builder request = new Request.Builder()
                .url(qrcode_info_url)
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + af_token)
                .addHeader("Content-Type", "application/json");

        HttpAfReturn httpAfReturn = getJhttpAfReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpAfReturn;
    }

    @Override
    public HttpReturn getFestival() {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/util//festival/" + new Date().getTime())
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn changeNewPassword(String oldPassword, String newPassword) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"oldPassword\" : \"" + oldPassword + "\",\r\n    \"newPassword\" : \"" + newPassword + "\"\r\n}");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/password")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");


        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getUserInfo(CallbackUtils.TimeoutReturn timeoutReturn) {
        StringUtils.HaoLog("getUserInfo UserId=main");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);
        HttpReturn httpReturn = getJhttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getUserInfo(String UserId, CallbackUtils.TimeoutReturn timeoutReturn) {
        StringUtils.HaoLog("getUserInfo UserId=" + UserId);
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/id/" + UserId)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = getJhttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn uploadUserInfo(Map<String, Object> map) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn setPusher(String userId, String FCM_token, String uuid, String customerProperties, CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap();
        map.put("apId", "Lale");
        map.put("userId", userId);
        map.put("uuid", uuid);
        map.put("deviceName", "手機");
        map.put("devicePlatform", "android");
        map.put("deviceVersion", "11s");
        map.put("deviceModel", "android");
        map.put("token", FCM_token);
        map.put("allowed", true);
        map.put("customerProperties",customerProperties);

        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/push/device/register")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");
        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn updatePusher(String userId, String uuid, CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap();
        map.put("userId", userId);
        map.put("apId", "Lale");
        map.put("uuid", uuid);
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/push/device/user/allowed")
                .method("PUT", body);
        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn closePusher(String userId, String uuid, CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap();
        map.put("userId", userId);
        map.put("apId", "Lale");
        map.put("uuid", uuid);
        map.put("allowed", false);
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/push/device/allowed")
                .method("PUT", body);
        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn setAfPusher(String WFCI_URL, String memId,String userId, String FCM_token, String uuid, String customerProperties, CallbackUtils.TimeoutReturn timeoutReturn) {
        if (WFCI_URL == null || WFCI_URL.isEmpty()){
            return new HttpReturn();
        }
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap();
        map.put("memId", memId);
        map.put("apId", "AF");
        map.put("uuid", uuid);
        map.put("token", FCM_token);
        map.put("userId", userId);
        map.put("deviceName", "Android");
        map.put("devicePlatform", "Android");
        map.put("deviceVersion", MainWebActivity.getVersionName(AllData.context));
        map.put("deviceModel", "Android");
        map.put("allowed", true);
        map.put("customerProperties",customerProperties);
        StringUtils.HaoLog("setAfPusher json= " + new JSONObject(map));
        StringUtils.HaoLog("setAfPusher URL= " + WFCI_URL + "/api/app-pusher");
        StringUtils.HaoLog("setAfPusher method=POST");
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(WFCI_URL + "/api/app-pusher")
                .method("POST", body);
        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn closeAfPusher(String WFCI_URL, String memId, String userId, String FCM_token, String uuid, CallbackUtils.TimeoutReturn timeoutReturn) {
        if (WFCI_URL == null || WFCI_URL.isEmpty()){
            return new HttpReturn();
        }
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap();
        map.put("memId", memId);
        map.put("apId", "AF");
        map.put("uuid", uuid);
        map.put("token", FCM_token);
        map.put("userId", userId);
        map.put("allowed", false);
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(WFCI_URL + "/api/app-pusher")
                .method("PUT", body);
        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });

        return httpReturn;
    }

    @Override
    public HttpReturn userLogout(CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/logout")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = getJhttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });

        return httpReturn;
    }

    @Override
    public HttpReturn getSimpleFriends(String UserId) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/friend/list/status/-1")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getFriendInfo(String FriendId) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/friend/" + FriendId)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn updateFriendStatus(String friendId, String FriendName) {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap();
        map.put("userId", UserControlCenter.getUserMinInfo().userId);
        map.put("friendId", friendId);
        map.put("aliasName", FriendName);
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/friend/")
                .put(body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn updateFriendStatus(String friendId, int status) {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap();
        map.put("userId", UserControlCenter.getUserMinInfo().userId);
        map.put("friendId", friendId);
        map.put("status", status);
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/friend/")
                .put(body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }


    @Override
    public HttpReturn getUserID(String PhoneOrAccountOrEmail) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/keyword/" + StringUtils.unsafeCode(PhoneOrAccountOrEmail))
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getNotYetFriends(CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/friend/request/list")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = getJhttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });

        return httpReturn;
    }

    @Override
    public boolean getNotYetReceiverFriends(String friendId) {
        // 查詢對方是否已經把你加好友
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/friend/request/receiver/" + friendId)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn.status == 200;
    }

    @Override
    public HttpReturn setFriendInvite(String frId, boolean agree, String friendId) {
        StringUtils.HaoLog("frId=" + frId + " agree=" + agree + " friendId=" + friendId);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n  \"frId\":\"" + frId + "\",\r\n  \"senderUserId\":\"" + friendId + "\",\r\n  \"isApproved\": " + agree + "\r\n}");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/friend/request")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn addFriend(String FriendId) {

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/friend/request/" + FriendId)
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn addFriend(ArrayList<String> phones) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new Gson().toJson(phones));
        StringUtils.HaoLog("addFriend=" + new Gson().toJson(phones));
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/friend/auto/invitation")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getAllAlbum(String roomId) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/album/list/" + roomId)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn newAlbum(String roomId, String name) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n        \"roomId\": \"" + roomId + "\",\r\n        \"name\": \"" + name + "\"\r\n        }");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/album")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn delAlbum(String roomAlbumId) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"roomAlbumId\": \"" + roomAlbumId + "\"\r\n}");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/album")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn delAlbum(String roomId, ArrayList<String> roomAlbumId) {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap();
        map.put("roomId", roomId);
        String[] strings = new String[roomAlbumId.size()];
        roomAlbumId.toArray(strings);
        map.put("deletedRoomAlbumIdList", strings);
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/album")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getPhotos(String roomAlbumId) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/album/photo/list/" + roomAlbumId)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn newPhotos(String albumId, File picture) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("albumId", albumId);
        if (picture != null) {
            builder.addFormDataPart("picture", picture.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"),
                            picture));
        }
        RequestBody body = builder.build();

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/album/photo")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn delPhotos(String roomAlbumId, String roomAlbumPhotoId) {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap();
        map.put("roomAlbumId", roomAlbumId);
        map.put("roomAlbumPhotoId", roomAlbumPhotoId);
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/album/photo")
                .method("DELETE", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn delPhotos(String roomAlbumId, ArrayList<String> roomAlbumPhotoId) {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap();
        map.put("roomAlbumId", roomAlbumId);
        String[] strings = new String[roomAlbumPhotoId.size()];
        roomAlbumPhotoId.toArray(strings);
        map.put("deletedRoomAlbumPhotoIdList", strings);
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/album/photo/batch")
                .method("DELETE", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn reNameAlbum(String roomId, String name, String roomAlbumId) {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap();
        map.put("name", name);
        map.put("roomId", roomId);
        map.put("roomAlbumId", roomAlbumId);
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/album")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getLittleSimpleRooms() {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"userId\": \"" + UserControlCenter.getUserMinInfo().userId + "\"\r\n}");
        Request.Builder request = new Request.Builder()
                .url(UserControlCenter.getUserMinInfo().getExternalServerSetting().messageServerUrl + "/api/messages/all/last")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getRoomMembers(String roomId, CallbackUtils.TimeoutReturn timeoutReturn) {
        StringUtils.HaoLog("getRoomMembers roomId=" + roomId);
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/user/list?roomId=" + roomId)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);
        HttpReturn httpReturn = getJhttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        StringUtils.HaoLog("getRoomMembers", httpReturn);
        return httpReturn;

    }

    @Override
    public HttpReturn getGroupMembers(String groupId, CallbackUtils.TimeoutReturn timeoutReturn) {

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/user/list?groupId=" + groupId)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);
        HttpReturn httpReturn = getJhttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });

        return httpReturn;
    }

    @Override
    public HttpReturn getAllSimpleRooms(CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/list/type/0")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);
        HttpReturn httpReturn = getJhttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getSimpleRooms(int type, CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/list/type/" + type)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);
        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getOneRoom(String roomId, CallbackUtils.TimeoutReturn timeoutReturn) {

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/" + roomId + "/info")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    int MsgCount = 20;


    @Override
    public void getRoomMsgs(String roomId, CallbackUtils.ReturnData<JSONArray> OtherCallBack) {

        Map<String, Object> map = new HashMap();
        RoomInfoInPhone roomInfoInPhone = AllData.getRoomInPhone(roomId);
        map.put("countLimited", MsgCount);
        if (roomInfoInPhone.msgTime != null) {
            if (roomInfoInPhone.msgTime == -1L) {
                OtherCallBack.Callback(false, "end", null);
                return;
            } else {
                map.put("queryEndTime", roomInfoInPhone.msgTime);
            }
        } else {
            map.put("queryEndTime", new Date().getTime());
        }
        map.put("roomId", roomId);
        map.put("userId", UserControlCenter.getUserMinInfo().userId);
        RequestBody body = getRequestBody(MediaType.parse("application/json"), map);
        Request.Builder request = new Request.Builder()
                .url(UserControlCenter.getUserMinInfo().getExternalServerSetting().messageServerUrl + "/api/messages/history")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");
        try {
            JSONArray msgs;
            HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
                @Override
                public void Callback(IOException timeout) {
                    StringUtils.HaoLog("timeout");
                }
            });
            if (httpReturn.status == 200){
                msgs = new JSONObject(new Gson().toJson(httpReturn.data)).optJSONArray("events");
            } else {
                msgs = null;
            }
            OtherCallBack.Callback(msgs != null, "", msgs);
            StringUtils.HaoLog("msgs=" + msgs);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HttpReturn getAnnouncement(String roomId) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/" + roomId + "/announcement")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn netAnnouncement(String roomId, String eventId, String message) {

        Map<String, Object> map = new HashMap();
        map.put("roomId", roomId);
        map.put("eventId", eventId);
        map.put("message", message);

        RequestBody body = getRequestBody(MediaType.parse("application/json"), map);
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/announcement")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");


        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn delAnnouncement(String roomId, String eventId, String message) {

        Map<String, Object> map = new HashMap();
        map.put("roomId", roomId);
        map.put("annId", eventId);
        RequestBody body = getRequestBody(MediaType.parse("application/json"), map);
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/announcement")
                .method("DELETE", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn hideAnnouncement(String roomId) {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/" + roomId + "/announcement/all")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }


    @Override
    public HttpReturn newRoom(String friend,CallbackUtils.TimeoutReturn timeoutReturn) {

        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap();
        map.put("type", 1);
        map.put("userList", new String[]{UserControlCenter.getUserMinInfo().userId, friend});

        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = getJhttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });

        return httpReturn;
    }

    @Override
    public HttpReturn newGroupRoom(String name, int type, ArrayList<String> users, File image,CallbackUtils.TimeoutReturn timeoutReturn) {
        JSONObject map = new JSONObject();
        try {
            map.put("name", name);
            map.put("type", type);
            JSONArray userList = new JSONArray();
            for (int i = 0; i < users.size(); i++) {
                userList.put(users.get(i));
            }

            map.put("userList", userList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringUtils.HaoLog(map.toString());
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("group", map.toString());
        if (image != null) {
            builder.addFormDataPart("image", image.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"),
                            image));
        }
        RequestBody body = builder.build();

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = getJhttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });

        return httpReturn;
    }

    @Override
    public HttpReturn getGroupRoom(String groupId, CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/" + groupId)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);
        HttpReturn httpReturn = getJhttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn newPassworkRoom(String Passwork, CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/private/password/" + Passwork)
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = getJhttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getPassworkRoom(String Passwork,CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/private/password/" + Passwork)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = getJhttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn addPassworkRoom(int id, String password, String[] userList,CallbackUtils.TimeoutReturn timeoutReturn) {
        Map<String, Object> map = new HashMap();
        map.put("id", id);
        map.put("password", password);
        map.put("userList", userList);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/private")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = getJhttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn addPassworkRoom(String groupId, String Passwork,CallbackUtils.TimeoutReturn timeoutReturn) {
        Map<String, Object> map = new HashMap();

        map.put("password", Passwork);
        map.put("groupId", groupId);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/private/user")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = getJhttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn leavePassworkRoom(int id, String Passwork) {
        Map<String, Object> map = new HashMap();
        map.put("id", id);
        map.put("password", Passwork);
        map.put("userList", new String[]{UserControlCenter.getUserMinInfo().userId});
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/private/user/list")
                .method("DELETE", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn updateRoom(String roomId, JSONObject value, CallbackUtils.TimeoutReturn timeoutReturn) {

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, value.toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/user/setting")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }


    @Override
    public HttpReturn updateGroupAvatar(String groupId, File image) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("groupId", "\"" + groupId + "\"");

        if (image != null) {
            builder.addFormDataPart("image", image.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"),
                            image));
        }
        RequestBody body = builder.build();

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/avatar")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn addGroupMember(String roomId, String groupId, String[] userList) {

        Map<String, Object> map = new HashMap();
        map.put("roomId", roomId);
        map.put("groupId", groupId);
        map.put("userList", userList);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/user/list")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn delGroupMember(String roomId, String groupId, String[] userList) {
        Map<String, Object> map = new HashMap();
        map.put("roomId", roomId);
        map.put("groupId", groupId);
        map.put("userList", userList);
        StringUtils.HaoLog("delGroupMember=" + new JSONObject(map).toString());
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/user/list")
                .method("DELETE", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn updateGroup(String roomId, JSONObject value, CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, value.toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/user/setting")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn updateGroupSetting(String groupId, JSONObject value) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, value.toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/setting")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getGroupSetting(String groupId) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/" + groupId + "/user/setting")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);
        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getGroupInfo(String groupId) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/" + groupId)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getRoomSetting(String roomId) {

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/" + roomId + "/user/setting")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn updateGroupBackground(String groupId, File image) {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("groupId", "\"" + groupId + "\"");

        if (image != null) {
            builder.addFormDataPart("image", image.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"),
                            image));
        }
        RequestBody body = builder.build();

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/user/background")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getGroupVerification(String groupId) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/" + groupId + "/verification-code")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn gotoGroup(String groupId, String verificationCode) {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/group/" + groupId + "/verification-code/" + verificationCode)
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn delRecord(String roomId) {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room//" + roomId + "/msg/record")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn retractMsg(String roomId, String retract_eventId) {


        Map<String, Object> map = new HashMap();
        map.put("eventId", retract_eventId);
        map.put("roomId", roomId);
        map.put("userId", UserControlCenter.getUserMinInfo().userId);
        RequestBody body = getRequestBody(MediaType.parse("application/json"), map);
        Request.Builder request = new Request.Builder()
                .url(UserControlCenter.getUserMinInfo().externalServerSetting.messageServerUrl + "/api/messages/retract")
                .method("PATCH", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn sendFile(String roomId, File file) {

        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(MediaType.parse("application/octet-stream"),
                                file)).build();

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/room/" + roomId + "/file")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getKeeps() {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/keep/list/type/all")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getKeeps(String keepId) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/keep/" + keepId)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn searchMsg(String keyword, String roomId, CallbackUtils.TimeoutReturn timeoutReturn) {
        Map<String, Object> map = new HashMap();
        map.put("keyword", keyword);
        map.put("roomId", roomId);
        map.put("userId", UserControlCenter.getUserMinInfo().userId);
        RequestBody body = getRequestBody(MediaType.parse("application/json"), map);
        Request.Builder request = new Request.Builder()
                .url(UserControlCenter.getUserMinInfo().externalServerSetting.messageServerUrl + "/api/messages/search")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = getJhttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn newKeep(String eventId) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"eventId\": \"" + eventId + "\"\r\n}");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/keep")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn delKeep(String keepId) {

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"keepId\": \"" + keepId + "\"\r\n}");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/keep")
                .method("DELETE", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn delKeep(ArrayList<String> keepId) {
        MediaType mediaType = MediaType.parse("application/json");
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < keepId.size(); i++) {

                jsonArray.put(new JSONObject().put("keepId", keepId.get(i)));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mediaType, jsonArray.toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/keep/list")
                .method("DELETE", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getExplorePromotion() {

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/microapp/promotion/image")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "text/plain");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getMicroApps() {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/microapp/types")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "text/plain");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getFocusApps() {

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/microapp/list/recommend")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "text/plain");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public String getRecommendedNews() {

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "passId=106&page=10");
        Request.Builder request = new Request.Builder()
                .url(AllData.getNewsDomain() + "/getJournalsByPassId.do")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token).addHeader("Content-Type", "application/x-www-form-urlencoded");

        return getString(request);
    }

    @Override
    public String getMemia() {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMemiaDomain() + "/Memia/api/item/recommend/all")
                .get()
                .addHeader("Content-Type", "text/html; charset=UTF-8")
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/x-www-form-urlencoded");

        return getString(request);
    }

    @Override
    public HttpReturn getMicroappTypes() {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/microapp/types")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "text/plain");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn setMicroHistoryOpen(String microAppMenuId, String deviceId, String deviceBrand, String deviceModel, String deviceOsType) {
        Map<String, Object> map = new HashMap();
        map.put("microAppId", microAppMenuId);
        map.put("userId", UserControlCenter.getUserMinInfo().userId);
        map.put("deviceId", deviceId);
        map.put("deviceBrand", deviceBrand);
        map.put("deviceModel", deviceModel);
        map.put("deviceOsType", deviceOsType);
        map.put("openAppDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime()));
        RequestBody body = getRequestBody(MediaType.parse("application/json"), map);

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/microapp/use/time/history/open-app")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn setMicroHistoryClose(int id) {
        Map<String, Object> map = new HashMap();
        map.put("id", id);
        map.put("closeAppDateTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime()));
        RequestBody body = getRequestBody(MediaType.parse("application/json"), map);

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/microapp/use/time/history/close-app")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getMicroapps(String microAppMenuId) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/microapp/list/type/" + microAppMenuId)
                .get().addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "text/plain");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn addMicroappFavorite(String microAppId, CallbackUtils.TimeoutReturn timeoutReturn) {
        Map<String, Object> map = new HashMap();
        map.put("microAppId", microAppId);
        map.put("userId", UserControlCenter.getUserMinInfo().userId);
        RequestBody body = getRequestBody(MediaType.parse("application/json"), map);
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/microapp/favorite")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = getJhttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn delMicroappFavorite(String microAppId,CallbackUtils.TimeoutReturn timeoutReturn) {
        Map<String, Object> map = new HashMap();
        map.put("microAppId", microAppId);
        map.put("userId", UserControlCenter.getUserMinInfo().userId);
        RequestBody body = getRequestBody(MediaType.parse("application/json"), map);
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/microapp/favorite")
                .method("DELETE", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = getJhttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn isMicroappFavorite(String microAppId,CallbackUtils.TimeoutReturn timeoutReturn) {
        Map<String, Object> map = new HashMap();
        map.put("microAppId", microAppId);
        map.put("userId", UserControlCenter.getUserMinInfo().userId);
        RequestBody body = getRequestBody(MediaType.parse("application/json"), map);
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/microapp/favorite/exist")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = getJhttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getMicroappFavoriteList() {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/microapp/list/favorite/" + UserControlCenter.getUserMinInfo().userId)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "text/plain");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getMicroappUsedList() {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/microapp/list/used/" + UserControlCenter.getUserMinInfo().userId)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "text/plain");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getMicroapp(String microAppId) {

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/microapp/id/" + microAppId)
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "text/plain");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }


    @Override
    public HttpAfReturn getAfToken(String afServer, CallbackUtils.TimeoutReturn timeoutReturn) {

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), "{\"laleToken\": \"" + UserControlCenter.getUserMinInfo().token + "\"}\r\n");
        Request.Builder request = new Request.Builder()
                .url(afServer + "/api/auth/lale/aftoken")
                .method("POST", body)
                .addHeader("Content-Type", "application/json");

        HttpAfReturn httpAfReturn = getJhttpAfReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpAfReturn;
    }

    @Override
    public HttpAfReturn renewAfToken(String refreshToken) {
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject bodyJect = new JSONObject();
        try {
            bodyJect.put("refreshToken", refreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public HttpAfReturn getCompanyList(String afServer, String afToken, CallbackUtils.TimeoutReturn timeoutReturn) {
        StringUtils.HaoLog("afServer=" + afServer);
        Request.Builder request = new Request.Builder()
                .url(afServer + "/api/dau/enterprise/all/current-user")
                .addHeader("Authorization", "Bearer " + afToken)
                .get()
                .addHeader("Content-Type", "application/json");

        HttpAfReturn httpAfReturn = getJhttpAfReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpAfReturn;
    }

    @Override
    public HttpAfReturn getCompanyAnnouncement(String afServer, String afToken, CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(afServer + "/api/dau/dashboard/announcement/messages")
                .addHeader("Authorization", "Bearer " + afToken)
                .get()
                .addHeader("Content-Type", "application/json");

        HttpAfReturn httpAfReturn = getJhttpAfReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpAfReturn;
    }

    @Override
    public HttpAfReturn getCompanyDashboard(String afServer, String afToken, CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(afServer + "/api/dau/dashboard/info/all?layoutId=LAY000000000001&state=delay")
                .addHeader("Authorization", "Bearer " + afToken)
                .get()
                .addHeader("Content-Type", "application/json");

        HttpAfReturn httpAfReturn = getJhttpAfReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpAfReturn;
    }

    @Override
    public HttpAfReturn getCompanyModule(String afServer, String afToken, String companyId, CallbackUtils.TimeoutReturn timeoutReturn) {

        Request.Builder request = new Request.Builder()
                .url(afServer + "/api/dau/module/setting/all?comId=" + companyId)
                .get()
                .addHeader("Authorization", "Bearer " + afToken)
                .addHeader("Content-Type", "application/json");
        HttpAfReturn httpAfReturn = getJhttpAfReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpAfReturn;
    }

    @Override
    public ArrayList<Stickerlibrary> getAllStickerlibrary() {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/stickerlibrary/all")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");
        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        if (httpReturn.status == 200) {
            StringUtils.HaoLog(new Gson().toJson(httpReturn.data));
            return new Gson().fromJson(new Gson().toJson(httpReturn.data).toString(), new TypeToken<ArrayList<Stickerlibrary>>() {
            }.getType());
        }
        return null;
    }

    @Override
    public ArrayList<Stickerlibrary> getUserStickers() {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/stickerlibrary/user/all")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");
        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        if (httpReturn.status == 200) {
            return new Gson().fromJson(new Gson().toJson(httpReturn.data).toString(), new TypeToken<ArrayList<Stickerlibrary>>() {
            }.getType());
        }
        return null;
    }

    @Override
    public HttpReturn newStickerlibrary(String stickerLibraryId) {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "stickerLibraryId=" + stickerLibraryId);
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/stickerlibrary/user/download")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/x-www-form-urlencoded");

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public ArrayList<CustomizeSticker> getCustomizeStickerlibrary() {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/sticker/all")
                .get()
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);
        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        if (httpReturn.status == 200) {
            StringUtils.HaoLog(new Gson().toJson(httpReturn.data));
            return new Gson().fromJson(new Gson().toJson(httpReturn.data).toString(), new TypeToken<ArrayList<CustomizeSticker>>() {
            }.getType());
        }
        return null;
    }

    @Override
    public HttpReturn newCustomizeSticker(File image) {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (image != null) {
            builder.addFormDataPart("image", image.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"),
                            image));
        }
        RequestBody body = builder.build();
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/sticker")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn delCustomizeSticker(String imageId) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"imageId\": \"" + imageId + "\"\r\n}");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/sticker")
                .method("DELETE", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .addHeader("Content-Type", "application/json");
        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpAfReturn orgtreeuserimage(String afDomain, String[] UserIds, CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, Object> map = new HashMap<>();
        map.put("userList", UserIds);
        map.put("isAll", false);

        RequestBody requestBody = RequestBody.create(mediaType, new JSONObject(map).toString());
        StringUtils.HaoLog("getRoomMembers " + new JSONObject(map));
        StringUtils.HaoLog("getRoomMembers " + afDomain + "/api/dau/org-tree/user/image");
        Request.Builder request = new Request.Builder()
                .url(afDomain + "/api/dau/org-tree/user/image")
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json");
        HttpAfReturn httpAfReturn = getJhttpAfReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpAfReturn;
    }

    @Override
    public HttpReturn reToken(CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n        \"token\": \"" + UserControlCenter.getUserMinInfo().token + "\",\r\n        \"refreshToken\": \"" + UserControlCenter.getUserMinInfo().refreshToken + "\"\r\n}");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/token/verification")
                .method("POST", body)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        if (httpReturn.status == 200) {
            UserMin userMin = UserControlCenter.getUserMinInfo();
            TokenInfo tokenInfo = new Gson().fromJson(new Gson().toJson(httpReturn.data), TokenInfo.class);
            if (tokenInfo != null && userMin != null) {
                userMin.token = tokenInfo.token;
                userMin.refreshExpiration = tokenInfo.refreshExpiration;
                userMin.expiration = tokenInfo.expiration;
                userMin.refreshToken = tokenInfo.refreshToken;
                if (userMin.eimUserData != null) {
                    userMin.eimUserData.lale_token = tokenInfo.token;
                    userMin.eimUserData.refresh_token = tokenInfo.refreshToken;
                }
            }
            UserControlCenter.updateUserMinInfo(userMin);
        }
        return httpReturn;
    }

    @Override
    public HttpAfReturn renewTokenHaveDeviceId(String afDomain, String af_token, String deviceId, CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject bodyJect = new JSONObject();
        try {
            bodyJect.put("token", af_token);
            bodyJect.put("deviceId", deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mediaType, bodyJect.toString());
        Request.Builder request = new Request.Builder()
                .url(afDomain + "/api/auth/aftoken/refresh")
                .method("POST", body)
                .addHeader("Content-Type", "application/json");

        HttpAfReturn httpAfReturn = getJhttpAfReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        if(httpAfReturn.code == 200){
            UserMin userMin = UserControlCenter.getUserMinInfo();
            AfTokenInfo AfTokenInfo = new Gson().fromJson(new Gson().toJson(httpAfReturn.data), AfTokenInfo.class);
            if(AfTokenInfo != null && userMin != null){
                userMin.eimUserData.af_token = AfTokenInfo.token;
                userMin.eimUserData.afRefreshToken = AfTokenInfo.refreshToken;
                userMin.eimUserData.afTokenExpiration = AfTokenInfo.expiration;
                userMin.eimUserData.afRefreshTokenExpiration = AfTokenInfo.refreshExpiration;
                userMin.eimUserData.deviceId = AfTokenInfo.deviceId;
            }
            UserControlCenter.updateUserMinInfo(userMin);
        }
        return httpAfReturn;
    }

    @Override
    public HttpAfReturn renewToken(String afDomain, CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject bodyJect = new JSONObject();
        try {
            bodyJect.put("refreshToken", UserControlCenter.getUserMinInfo().eimUserData.afRefreshToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mediaType, bodyJect.toString());
        Request.Builder request = new Request.Builder()
                .url(afDomain + "/api/auth/aftoken/refresh")
                .method("POST", body)
                .addHeader("Content-Type", "application/json");

        HttpAfReturn httpAfReturn = getJhttpAfReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        if(httpAfReturn.code == 200){
            UserMin userMin = UserControlCenter.getUserMinInfo();
            AfTokenInfo AfTokenInfo = new Gson().fromJson(new Gson().toJson(httpAfReturn.data), AfTokenInfo.class);
            if(AfTokenInfo != null && userMin != null){
                userMin.eimUserData.af_token = AfTokenInfo.token;
                userMin.eimUserData.afRefreshToken = AfTokenInfo.refreshToken;
                userMin.eimUserData.afTokenExpiration = AfTokenInfo.expiration;
                userMin.eimUserData.afRefreshTokenExpiration = AfTokenInfo.refreshExpiration;
                userMin.eimUserData.deviceId = AfTokenInfo.deviceId;
            }
            UserControlCenter.updateUserMinInfo(userMin);
        }
        return httpAfReturn;
    }

    @Override
    public HttpAfReturn tokenValid() {
        return null;
    }

    @Override
    public HttpReturn uploadUserAvatar(File image) {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (image != null) {
            builder.addFormDataPart("image", image.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"),
                            image));
        }
        RequestBody body = builder.build();

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/avatar")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn uploadUserBackground(File image) {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (image != null) {
            builder.addFormDataPart("image", image.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"),
                            image));
        }
        RequestBody body = builder.build();
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/background")
                .method("POST", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn resetUserBackground() {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");

        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/background/reset")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn checkToken(CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"token\":\"" + UserControlCenter.getUserMinInfo().token + "\"\r\n}");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/token/validation")
                .method("POST", body)
                .addHeader("Content-Type", "application/json");

        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn updateToken(){
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"refreshToken\":\"" + UserControlCenter.getUserMinInfo().refreshToken + "\"\r\n}");
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/user/token")
                .method("PUT", body)
                .addHeader("Content-Type", "application/json");
        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn announceServer(CallbackUtils.TimeoutReturn timeoutReturn) {
        String announceUrl = AllData.regularServer(UserControlCenter.getUserMinInfo().eimUserData.announceServerUrl);
        if(announceUrl == null || announceUrl.isEmpty()){
            return new HttpReturn();
        }
        Request.Builder request = new Request.Builder()
                .url(announceUrl + "/ann/in-range/")
                .get();

        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn announceServerGivenTime(String givenTime) {
        String announceUrl = AllData.regularServer(UserControlCenter.getUserMinInfo().eimUserData.announceServerUrl);
        if(announceUrl == null || announceUrl.isEmpty()){
            return new HttpReturn();
        }
        Request.Builder request = new Request.Builder()
                .url(announceUrl + "/ann/in-range/" + givenTime)
                .get();
        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn latestAnnounce(CallbackUtils.TimeoutReturn timeoutReturn) {
        String announceUrl = AllData.regularServer(UserControlCenter.getUserMinInfo().eimUserData.announceServerUrl);
        if(announceUrl == null || announceUrl.isEmpty()){
            return new HttpReturn();
        }
        Request.Builder request = new Request.Builder()
                .url(announceUrl + "/ann/closest/")
                .get();
        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn latestAnnounceGivenTime(String givenTime) {
        String announceUrl = AllData.regularServer(UserControlCenter.getUserMinInfo().eimUserData.announceServerUrl);
        if(announceUrl == null || announceUrl.isEmpty()){
            return new HttpReturn();
        }
        Request.Builder request = new Request.Builder()
                .url(announceUrl + "/ann/closest/" + givenTime)
                .get();
        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getEimAllSystemInfor(CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/system/setting")
                .get();
        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn updataSystemInfor(JSONArray settingsArray, CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,settingsArray.toString());
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/system/adm/setting")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token);

        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getAllWatermarkTemplates(CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/watermark/adm/all")
                .addHeader("Authorization", "Bearer " + UserControlCenter.getUserMinInfo().token)
                .get();
        HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public HttpReturn getDefaultWatermarkTemplate(CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/watermark/default")
                .get();
        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }

    @Override
    public Http2Return textWatermark(String textContent, CallbackUtils.TimeoutReturn timeoutReturn) {
        String WFCI_URL = UserControlCenter.getUserMinInfo().eimUserData.af_wfci_service_url;
        if(WFCI_URL == null || WFCI_URL.isEmpty()){
            return new Http2Return();
        }
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject bodyJect = new JSONObject();
        try {
            bodyJect.put("textContent", textContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mediaType, bodyJect.toString());
        Request.Builder request = new Request.Builder()
                .url(WFCI_URL + "/api/lalesystem/watermark/construction")
                .method("POST", body)
                .addHeader("token", UserControlCenter.getUserMinInfo().eimUserData.af_token);

        Http2Return http2Return = gethttp2Return(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return http2Return;
    }

    @Override
    public Http2Return getAppWorkAllSystemInfor(CallbackUtils.TimeoutReturn timeoutReturn) {
        String afWfciServiceUrl = AllData.regularServer(UserControlCenter.getUserMinInfo().eimUserData.af_wfci_service_url);
        if(afWfciServiceUrl == null || afWfciServiceUrl.isEmpty()){
            return new Http2Return();
        }

        String sysId = "LSC00000000000000003";
        String WFCI_URL = String.format("%s/api/lalesystem/sysid/%s", afWfciServiceUrl, sysId);
        Request.Builder request = new Request.Builder()
                .url(WFCI_URL)
                .addHeader("token", UserControlCenter.getUserMinInfo().eimUserData.af_token)
                .get();

        Http2Return http2Return = gethttp2Return(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return http2Return;
    }

    @Override
    public String webVersion(String url,CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(url)
                .get();
        OkHttpClient client = getUnsafeOkHttpClient().newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
        try {
            Response response = client.newCall(request.build()).execute();
            if (response.code() == 200){
                return response.body().string();
            }
        } catch (IOException e) {
            if(e instanceof java.net.SocketTimeoutException){
                // 如果超時，通過回調通知主線程
                timeoutReturn.Callback(e);
            }
            e.printStackTrace();
            StringUtils.HaoLog("getWebVersion error=" + request + " / " + e);
        }
        return "";
    }

    @Override
    public HttpReturn googlePlatformVersion(CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(AllData.getMainServer() + "/util/app/platform/tob")
                .get();

        HttpReturn httpReturn = gethttpReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpReturn;
    }
    
    public HttpAfReturn aflogin(String account, String password, String url, CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        if (account == null) {
            account = "";
        }
        if (password == null) {
            password = "";
        }
        JSONObject body_j = new JSONObject();
        try {
            body_j.put("loginId", account);
            body_j.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mediaType, body_j.toString());
        Request.Builder request = new Request.Builder()
                .url(url + "/api/dau/EIM/aflogin")
                .method("POST", body)
                .addHeader("Content-Type", "application/json");
        HttpAfReturn httpAfReturn = getJhttpAfReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpAfReturn;
    }

    @Override
    public HttpAfReturn afloginNew(String account, String password, String url, CallbackUtils.TimeoutReturn timeoutReturn) {
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject bodyJect = new JSONObject();
        String uuid = Settings.Secure.getString(AllData.context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            bodyJect.put("loginId", account);
            bodyJect.put("password", password);
            bodyJect.put("deviceId", uuid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(mediaType, bodyJect.toString());
        Request.Builder request = new Request.Builder()
                .url(url + "/api/auth/aftoken/aflogin")
                .method("POST", body)
                .addHeader("Content-Type", "application/json");

        HttpAfReturn httpAfReturn = getJhttpAfReturn(request, 15, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpAfReturn;
    }

    @Override
    public HttpAfReturn afServerVersion(String afUrl, CallbackUtils.TimeoutReturn timeoutReturn) {
        Request.Builder request = new Request.Builder()
                .url(afUrl + "/api/dau/config/webagenda/version")
                .get();

        HttpAfReturn httpAfReturn = getJhttpAfReturn(request, 0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                timeoutReturn.Callback(timeout);
            }
        });
        return httpAfReturn;
    }

    public HttpReturn gethttpReturn(Request.Builder request, int timeoutInSeconds, CallbackUtils.TimeoutReturn timeoutReturn) {
        OkHttpClient client = getUnsafeOkHttpClient().newBuilder()
                .connectTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .build();
        try {
            Response response = client.newCall(request.build()).execute();
            if (response.code() == 200) {
                String body = response.body().string();
                StringUtils.HaoLog("body= " + body);
                HttpReturn httpReturn = new Gson().fromJson(body, HttpReturn.class);
                if (httpReturn != null) {
                    StringUtils.HaoLog(response.request().url().toString(), httpReturn);
                    return httpReturn;
                } else{
                    StringUtils.HaoLog(response.request().url() + " " + response.code() + " body= " + body);
                }
            }
        } catch (IOException | JsonSyntaxException | IllegalStateException e) {
            if(e instanceof java.net.SocketTimeoutException){
                timeoutReturn.Callback((IOException) e);
            }
            StringUtils.HaoLog("gethttpReturn error=" + request + " " + e);
            e.printStackTrace();
        }
        return new HttpReturn();
    }

    public Http2Return gethttp2Return(Request.Builder request, int timeoutInSeconds, CallbackUtils.TimeoutReturn timeoutReturn) {
        OkHttpClient client = getUnsafeOkHttpClient().newBuilder()
                .connectTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .build();
        try {
            Response response = client.newCall(request.build()).execute();
            if (response.code() == 200) {
                String body = response.body().string();
                StringUtils.HaoLog("body=" + body);
                Http2Return http2Return = new Gson().fromJson(body, Http2Return.class);

                if (http2Return != null) {
                    StringUtils.HaoLog("gethttp2Return= "+response.request().url());
                    return http2Return;
                } else{
                    StringUtils.HaoLog(response.request().url() + " " + response.code() + " body=" + body);
                    StringUtils.HaoLog("gethttp2Return end");
                }
            }
        } catch (IOException | JsonSyntaxException | IllegalStateException e) {
            if(e instanceof java.net.SocketTimeoutException){
                timeoutReturn.Callback((IOException) e);
            }
            StringUtils.HaoLog("gethttp2Return error= " + request + " " + e);
            e.printStackTrace();
        }
        return new Http2Return();
    }

    public HttpReturn getJhttpReturn(Request.Builder request, int timeoutInSeconds, CallbackUtils.TimeoutReturn timeoutReturn) {
        OkHttpClient client = getUnsafeOkHttpClient().newBuilder()
                .connectTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .build();
        try {
            Response response = client.newCall(request.build()).execute();
            String body = response.body().string();
            HttpReturn httpReturn = new Gson().fromJson(body, HttpReturn.class);
            httpReturn.data = new JSONObject(body).optString("data");
            return httpReturn;
        } catch (IOException | JSONException | IllegalStateException | JsonSyntaxException e) {
            if(e instanceof java.net.SocketTimeoutException){
                timeoutReturn.Callback((IOException) e);
            }
            e.printStackTrace();
            StringUtils.HaoLog("getJhttpReturn error=" + request + " " + e);
        }
        return new HttpReturn();
    }

    public HttpAfReturn getJhttpAfReturn(Request.Builder request, int timeoutInSeconds, CallbackUtils.TimeoutReturn timeoutReturn) {
        OkHttpClient client = getUnsafeOkHttpClient().newBuilder()
                .connectTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .writeTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
                .build();
        try {
            Response response = client.newCall(request.build()).execute();
            String body = response.body().string();
            StringUtils.HaoLog("body= " + body);
            HttpAfReturn httpReturn = new Gson().fromJson(body, HttpAfReturn.class);
            StringUtils.HaoLog("getJhttpAfReturn");
            StringUtils.HaoLog(response.header("url") + " " + httpReturn.message + " " + httpReturn.data);
            StringUtils.HaoLog("getJhttpAfReturn end");
            return httpReturn;
        } catch (IOException | JsonSyntaxException | IllegalStateException e) {
            if(e instanceof java.net.SocketTimeoutException){
                timeoutReturn.Callback((IOException) e);
            }
            e.printStackTrace();
            StringUtils.HaoLog("getJhttpAfReturn error= " + request + " " + e);
        }
        return new HttpAfReturn();
    }

    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {

                    }
                    //方法中默認允許了所有的證書
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        for(X509Certificate cert : chain){
                            try {
                                // 檢查伺服器 SSL 憑證是是否有效。
                                cert.checkValidity();
                            } catch (CertificateExpiredException e) {
                                throw new CertificateException("Certificate expired");
                            } catch (CertificateNotYetValidException e) {
                                throw new CertificateException("Certificate not yet valid");
                            }
                        }
                    }
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);

            //hostnameVerifier 是對服務端返回的一些信息進行相關校驗的地方
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Certificate[] localCertificates = new Certificate[0];
                    try{
                        //獲取證書鏈中的所有證書
                        localCertificates = session.getPeerCertificates();
                        }catch (SSLPeerUnverifiedException e){
                            e.printStackTrace();
                        }
                        //打印所有證書內容
                        //for(Certificate certificate : localCertificates){
                        //StringUtils.HaoLog("打印所有證書內容= "+certificate.toString());
                        //}
                    return true;
                    }
            });
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void createFileWithByte(byte[] bytes) {
        /**
         創建File對象，其中包含文件所在的目錄以及文件的命名
         */
        File file = new File(Environment.getExternalStorageDirectory(),
                "ca.cer");
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        try {
            // 如果文件存在則刪除
            if (file.exists()) {
                file.delete();
            }
            // 在文件系統中根據路徑創建一個新的空文件
            file.createNewFile();
            // 獲取FileOutputStream對象
            outputStream = new FileOutputStream(file);
            // 獲取BufferedOutputStream對象
            bufferedOutputStream = new BufferedOutputStream(outputStream);
            // 往文件所在的緩衝輸出流中寫byte數據
            bufferedOutputStream.write(bytes);
            // 刷出緩衝輸出流，該步很關鍵，要是不執行flush()方法，那麼文件的內容是空的。
            bufferedOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 關閉創建的流對象
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    public String getString(Request.Builder request) {
        OkHttpClient client = getUnsafeOkHttpClient().newBuilder().build();
        try {
            Response response = client.newCall(request.build()).execute();
            if (response.code() == 200){
                return response.body().string();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray getJSONArrayData(Request.Builder request) {
        try {
            HttpReturn httpReturn = gethttpReturn(request, 0, new CallbackUtils.TimeoutReturn() {
                @Override
                public void Callback(IOException timeout) {
                    StringUtils.HaoLog("timeout");
                }
            });
            if (httpReturn.status == 200){
                return new JSONArray(new Gson().toJson(httpReturn.data));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public RequestBody getRequestBody(MediaType mediaType, Map<String, Object> map) {
        JSONObject json = new JSONObject(map);
        return RequestBody.create(mediaType, json.toString());
    }

    @Override
    public void getRoomSetting(String roomId, CallbackUtils.ReturnData data, Class<?> classType) {
        new Thread(() -> {
            CloundTask(CloudUtils.iCloudUtils.getRoomSetting(roomId), data, classType);
        }).start();
    }

    public static void CloundTask(HttpReturn request, CallbackUtils.ReturnData data, Class<?> classType) {
        StringUtils.HaoLog("CloundTask", request);
        data.Callback(request.status == 200, request.msg, new Gson().fromJson(new Gson().toJson(request.data), classType));
    }

    public static Object ObjectToClass(HttpReturn data, java.lang.reflect.Type classType) {
        if (data.status == 200) {
            StringUtils.HaoLog(new Gson().toJson(data.data));
            return new Gson().fromJson(new Gson().toJson(data.data).toString(), classType);
        }
        return null;
    }
    
    public byte[] getFile(String url) {
        Request.Builder request = new Request.Builder()
                .url(url)
                .get();
        OkHttpClient client = getUnsafeOkHttpClient().newBuilder().build();
        try {
            Response response = client.newCall(request.build()).execute();
            if (response.code() == 200){
                return response.body().bytes();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
