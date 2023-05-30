package com.flowring.laleents.model.user;

import static com.flowring.laleents.tools.phone.AllData.delectAll;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.flowring.laleents.model.AFtoken;
import com.flowring.laleents.model.HttpAfReturn;
import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.CommonUtils;
import com.flowring.laleents.tools.SharedPreferencesUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.flowring.laleents.tools.cloud.mqtt.MqttService;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.ui.main.webBody.EimLoginActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserControlCenter {
    static UserMin userMin = null;
    static UserInfo userInfo;
    static Company company = null;
    public static boolean hasInitUI = false;

    public class Company {
        public String companyId;
        public String companyName;
        public String roomId;
        public String groupId;
        public boolean superuser;
        public int coWorkerCount;
    }

    static ArrayList<CompanyModule> companyModules = null;
    static ArrayList<Dashboard> dashboards = null;
    static ArrayList<Announcement> announcements = null;

    public static void cleanUser() {
        afToken = null;
        company = null;
        userInfo = null;
        companyModules = null;
        dashboards = null;
        announcements = null;
        hasInitUI = false;

    }


    public static int getWaitWorkCount() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        return pref.getInt(UserControlCenter.getUserMinInfo().userId + "_" + "WaitWorkCount", 0);
    }

    public class Dashboard {
        public class DashboardInfo {
            public int count;
            public boolean priorityUrgent;
            public int firstEventStartTime;
            public int lastEventEndTime;
            public String lastAlbumID;
        }

        public boolean isModuleAvailable;
        public String name;
        public String icon;
        public String id;
        public DashboardInfo dashboardInfo;
        public String url;
    }

    public class CompanyModule {

        public boolean isModuleAvailable;
        public String name;
        public String icon;
        public String id;
        public String url;
    }

    public static class Announcement {
        public String id = null;
        public String createUser;
        public String createUserImg;
        public String createUserLaleId;
        public long createTime;
        public int endTime;
        public String title;
        public Object content;
        public String boardId;
        public String boardName;
        public int viewCount;
        public String fileList;

        public Announcement() {
            id = null;
            createUser = null;
            createUserImg = null;
            createUserLaleId = null;
            createTime = -1;
            endTime = -1;
            title = null;
            content = null;
            boardId = null;
            boardName = null;
            viewCount = -1;
            fileList = null;
        }
    }

    public static void getCompanyModule(CallbackUtils.CompanyModuleReturn callback) {
        if (companyModules != null) {
            callback.Callback(companyModules);
        } else
            getNewCompanyModule(callback);
    }

    public static void getNewCompanyModule(CallbackUtils.CompanyModuleReturn callback) {
        new Thread(() -> {
            HttpAfReturn httpReturn = CloudUtils.iCloudUtils.getCompanyModule(userInfo.webagendaUrl, afToken, company.companyId);
            if (httpReturn.success) {
                Gson gson = new Gson();
                companyModules = gson.fromJson(gson.toJson(httpReturn.data), new TypeToken<ArrayList<CompanyModule>>() {
                }.getType());
                callback.Callback(companyModules);
            } else
                callback.Callback(null);

        }).start();

    }

    public static void getCompanyDashboard(CallbackUtils.CompanyDashboardReturn callback) {
        if (dashboards != null) {
            for (Dashboard dashboard : dashboards) {
                if (dashboard.name != null && dashboard.name.equals("待處理工作") && dashboard.dashboardInfo != null) {
                    StringUtils.HaoLog("紀錄待辦數量");

                }
            }
            callback.Callback(dashboards);
        } else
            getNewCompanyDashboard(callback);
    }

    public static void getNewCompanyDashboard(CallbackUtils.CompanyDashboardReturn callback) {
        new Thread(() -> {
            HttpAfReturn httpReturn = CloudUtils.iCloudUtils.getCompanyDashboard(userInfo.webagendaUrl, afToken);
            if (httpReturn.success) {
                Gson gson = new Gson();
                dashboards = gson.fromJson(gson.toJson(httpReturn.data), new TypeToken<ArrayList<Dashboard>>() {
                }.getType());
                callback.Callback(dashboards);
            } else
                callback.Callback(null);

        }).start();

    }

    public static void getCompanyAnnouncement(CallbackUtils.CompanyAnnouncementReturn callback) {
        if (announcements != null) {
            callback.Callback(announcements);
        } else
            getNewCompanyAnnouncement(callback);
    }

    public static void getNewCompanyAnnouncement(CallbackUtils.CompanyAnnouncementReturn callback) {
        new Thread(() -> {
            HttpAfReturn httpReturn = CloudUtils.iCloudUtils.getCompanyAnnouncement(userInfo.webagendaUrl, afToken);
            if (httpReturn.success) {
                Gson gson = new Gson();
                announcements = gson.fromJson(gson.toJson(httpReturn.data), new TypeToken<ArrayList<Announcement>>() {
                }.getType());
                callback.Callback(announcements);
            } else
                callback.Callback(null);

        }).start();

    }

    public static String getAfToken() {
        return afToken;
    }

    static String afToken;


    public static Company getCompanyData() {
        return company;
    }

    public static void tokenRefresh(CallbackUtils.ReturnHttp callback) {
        if (UserControlCenter.getUserMinInfo().eimUserData.isLaleAppEim) {
            new Thread(() -> {
                StringUtils.HaoLog("tokenRefresh 開始");
                HttpReturn httpReturn = CloudUtils.iCloudUtils.reToken();

                callback.Callback(httpReturn);
                StringUtils.HaoLog("tokenRefresh 結束");
            }).start();
        }
    }

    public static void tokenRefresh_noThread(CallbackUtils.ReturnHttp callback) {
        if (UserControlCenter.getUserMinInfo().eimUserData.isLaleAppEim) {
            StringUtils.HaoLog("tokenRefresh 開始");
            HttpReturn httpReturn = CloudUtils.iCloudUtils.reToken();
            callback.Callback(httpReturn);
            StringUtils.HaoLog("tokenRefresh 結束");
        }
    }


    public static void checkToken(CallbackUtils.ReturnHttp callback){
        StringUtils.HaoLog("checkToken 開始");
        HttpReturn correct = CloudUtils.iCloudUtils.checkToken();
        callback.Callback(correct);
        StringUtils.HaoLog("checkToken 結束");
    }

    public static UserInfo getMainUserInfo() {

        return userInfo;
    }

    public static void uploadMainUserInfo(Map<String, Object> value, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            callback.Callback(CloudUtils.iCloudUtils.uploadUserInfo(value));
        }).start();
    }

    public static void uploadUserAvatar(File value, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            callback.Callback(CloudUtils.iCloudUtils.uploadUserAvatar(value));
        }).start();
    }

    public static void uploadUserBackground(File value, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            callback.Callback(CloudUtils.iCloudUtils.uploadUserBackground(value));
        }).start();
    }


    public static void resetUserBackground(CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            callback.Callback(CloudUtils.iCloudUtils.resetUserBackground());
        }).start();
    }

    public static void getMainUserInfo(CallbackUtils.userReturn callback) {
        StringUtils.HaoLog("getMainUserInfo callback");
        new Thread(() -> {
            userInfo = new Gson().fromJson((String) CloudUtils.iCloudUtils.getUserInfo().data, UserInfo.class);
            if (userInfo != null) {
                userMin.displayName = userInfo.displayName;
                userMin.avatarThumbnailUrl = userInfo.avatarThumbnailUrl;
                userMin.avatarUrl = userInfo.avatarUrl;
                updateUserMinInfo(userMin);
            }
            callback.Callback(userInfo);
        }).start();
    }

    public static void getCompany(CallbackUtils.CompanyReturn callback) {
        if (company != null) {
            callback.Callback(company);
        } else {
            getNewCompany(callback);
        }
    }

    public static void getNewCompany(CallbackUtils.CompanyReturn callback) {
        new Thread(() -> {
            HttpAfReturn httpReturn = CloudUtils.iCloudUtils.getCompanyList(userInfo.webagendaUrl, afToken);
            if (httpReturn.success) {
                Gson gson = new Gson();
                StringUtils.HaoLog("data=" + httpReturn.data);
                StringUtils.HaoLog("data2=" + gson.toJson(httpReturn.data));
                ArrayList<Company> Companies = gson.fromJson(gson.toJson(httpReturn.data), new TypeToken<ArrayList<Company>>() {
                }.getType());
                if (Companies != null && Companies.size() > 0) {
                    company = Companies.get(0);
                    callback.Callback(company);
                } else{
                    callback.Callback(null);
                }
            } else{
                callback.Callback(null);
            }
        }).start();

    }

    public static void getAfToken(CallbackUtils.messageReturn callback) {
        if (afToken != null) {
            callback.Callback(afToken);
        } else{
            getNewAfToken(callback);
        }
    }

    public static void getNewAfToken(CallbackUtils.messageReturn callback) {
        StringUtils.HaoLog("取aftoken第一步");
        getMainUserWebagendaUrl(new CallbackUtils.messageReturn() {
            @Override
            public void Callback(String message) {
                userInfo.webagendaUrl = message;
                StringUtils.HaoLog("取aftoken WebagendaUrl" + message);
                if (message != null && !message.isEmpty()) {
                    new Thread(() -> {
                        HttpAfReturn httpReturn = CloudUtils.iCloudUtils.getAfToken(message);
                        StringUtils.HaoLog("取aftoken httpReturn" + httpReturn);
                        if (httpReturn.success) {
                            Gson gson = new Gson();
                            AFtoken aFtoken = gson.fromJson(gson.toJson(httpReturn.data), AFtoken.class);
                            afToken = aFtoken.token;
                            callback.Callback(afToken);
                        } else{
                            callback.Callback(null);
                        }
                    }).start();

                } else
                    callback.Callback(null);
            }
        });
    }

    public static void getMainUserWebagendaUrl(CallbackUtils.messageReturn callback) {
        getMainUserInfo(new CallbackUtils.userReturn() {
            @Override
            public void Callback(UserInfo userInfo) {
                callback.Callback(userInfo.webagendaUrl);

            }
        });
    }


    public static void setLogin(UserMin userMin) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        pref.edit().putString(userMin.userId + CommonUtils.PREF_JWT, userMin.token).apply();
        pref.edit().putString("nowUserId", userMin.userId).apply();
        try {
            JSONObject UserIds = new JSONObject(pref.getString("UserIds", "{}"));
            UserIds.put(userMin.userId, new Gson().toJson(userMin));
            pref.edit().putString("UserIds", UserIds.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        UserControlCenter.userMin = userMin;

    }

    public static void updateUserMinInfo(UserMin newUserMin) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        pref.edit().putString(userMin.userId + CommonUtils.PREF_JWT, userMin.token).apply();
        pref.edit().putString("nowUserId", userMin.userId).apply();
        try {
            JSONObject UserIds = new JSONObject(pref.getString("UserIds", "{}"));
            UserIds.put(userMin.userId, new Gson().toJson(userMin));
            pref.edit().putString("UserIds", UserIds.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        userMin = newUserMin;

    }

    public static void switchAccounts(String nowUserId) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        pref.edit().putString("nowUserId", nowUserId).apply();
        userMin = getUserMinInfoByPhone();

        cleanUser();
        StringUtils.HaoLog("NewConnect");
        MqttService.mqttControlCenter.NewConnect();


    }

    public static void switchAccounts(String nowUserId, CallbackUtils.ReturnHttp callback) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        pref.edit().putString("nowUserId", nowUserId).apply();
        userMin = getUserMinInfoByPhone();

        cleanUser();
        StringUtils.HaoLog("NewConnect");
        MqttService.mqttControlCenter.NewConnect();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String deviceToken = instanceIdResult.getToken();
                new Thread(() -> {
                    StringUtils.HaoLog("nowUserId=" + nowUserId);
                    StringUtils.HaoLog("uuid=" + Settings.Secure.getString(AllData.context.getContentResolver(), Settings.Secure.ANDROID_ID));
                    callback.Callback(CloudUtils.iCloudUtils.updatePusher(nowUserId, Settings.Secure.getString(AllData.context.getContentResolver(), Settings.Secure.ANDROID_ID)));

                }).start();

            }
        });

    }

    public static void wasLoggedOut(CallbackUtils.deviceReturn deviceReturn){
        new Thread(() -> {
            String loginType = SharedPreferencesUtils.getGeneralType();
            String thirdPartyIdentifier = SharedPreferencesUtils.getThirdPartyIdentifier();
            String userId = UserControlCenter.getUserMinInfo().userId;
            String deviceID = Settings.Secure.getString(AllData.context.getContentResolver(), Settings.Secure.ANDROID_ID);
            StringUtils.HaoLog("檢查是否已被登出 "+"\nloginType=" + loginType + "\nuserId=" + userId + "\nthirdPartyIdentifier=" + thirdPartyIdentifier + "\ndeviceId=" + deviceID);
            if(loginType != null && !loginType.isEmpty() &&
                    userId!= null && !userId.isEmpty()){
                Boolean isRepeatDevice = EimLoginActivity.alreadyLoddedIn(loginType, userId, thirdPartyIdentifier,deviceID);
                SharedPreferencesUtils.isRepeatDevice(isRepeatDevice);
                deviceReturn.Callback(isRepeatDevice);
            }
        }).start();
    }

    public static void setLogout(CallbackUtils.ReturnHttp callback) {
        if (UserControlCenter.getUserMinInfo() != null) {
            if (UserControlCenter.getUserMinInfo().eimUserData.isLaleAppEim) {
                StringUtils.HaoLog("登出 2");
                new Thread(() -> {
                    StringUtils.HaoLog("登出 3");
                    HttpReturn httpReturn = new HttpReturn();
                    httpReturn = CloudUtils.iCloudUtils.closePusher(UserControlCenter.getUserMinInfo().eimUserData.af_login_id, Settings.Secure.getString(AllData.context.getContentResolver(), Settings.Secure.ANDROID_ID));
                    HttpReturn httpReturn2 = CloudUtils.iCloudUtils.userLogout();
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
                    pref.edit().putString("nowUserId", "").apply();
                    pref.edit().putString("UserIds", "{}").apply();
                    if (MqttService.mqttControlCenter != null){MqttService.mqttControlCenter.DisConnect();}
                    userMin = null;
                    cleanUser();
                    delectAll();
                    StringUtils.HaoLog("登出 4 httpReturn2 "+httpReturn2.status);
                    callback.Callback(httpReturn);
                }).start();
            } else {
                StringUtils.HaoLog("登出 2-1");
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String deviceToken = instanceIdResult.getToken();
                        new Thread(() -> {
                            StringUtils.HaoLog("登出 3-1");
                            HttpReturn httpReturn = CloudUtils.iCloudUtils.closeAfPusher(UserControlCenter.getUserMinInfo().eimUserData.af_url, UserControlCenter.getUserMinInfo().eimUserData.af_login_id, deviceToken, Settings.Secure.getString(AllData.context.getContentResolver(), Settings.Secure.ANDROID_ID));
                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
                            pref.edit().putString("nowUserId", "").apply();
                            pref.edit().putString("UserIds", "{}").apply();
                            userMin = null;
                            cleanUser();
                            delectAll();
                            callback.Callback(httpReturn);
                        }).start();
                    }
                });
            }
        StringUtils.HaoLog("登出 結束");
        }
    }

    public static UserMin getUserMinInfo() {
        if (userMin != null){return userMin;}
        userMin = getUserMinInfoByPhone();
        return userMin;
    }

    static UserMin getUserMinInfoByPhone() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        String id = pref.getString("nowUserId", "");
        if (id.isEmpty()){
            return null;
        } else{
            try {
                JSONObject UserIds = new JSONObject(pref.getString("UserIds", "[]"));
                Gson gson = new Gson();
                StringUtils.HaoLog(UserIds.getString(id));
                return gson.fromJson(UserIds.getString(id), UserMin.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void getUserInfo(String userId, CallbackUtils.userReturn callback) {
        new Thread(() -> {
            callback.Callback(new Gson().fromJson((String) CloudUtils.iCloudUtils.getUserInfo(userId).data, UserInfo.class));

        }).start();

    }


    public static List<UserMin> getUserMinInfos() {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);

        List<UserMin> userMins = new ArrayList<>();
        try {
            JSONObject UserIds = new JSONObject(pref.getString("UserIds", "[]"));
            Iterator<String> keys = UserIds.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                StringUtils.HaoLog(UserIds.get(key).toString());
                userMins.add(new Gson().fromJson(UserIds.get(key).toString(), UserMin.class));
            }
            StringUtils.HaoLog("" + userMins.size());
            return userMins;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void getOrgtreeuserimage() {
        HttpAfReturn httpReturn2 = CloudUtils.iCloudUtils.orgtreeuserimage(UserControlCenter.getUserMinInfo().eimUserData.af_url, new String[]{UserControlCenter.getUserMinInfo().eimUserData.lale_user_id});
        StringUtils.HaoLog("getRoomMembers=", httpReturn2);
        if (httpReturn2.code == 200) {
            try {
                JSONObject jsonObject = new JSONObject(new Gson().toJson(httpReturn2.data));
                UserMin userMin = UserControlCenter.getUserMinInfo();
                userMin.avatarUrl = jsonObject.optString(userMin.eimUserData.lale_user_id);
                userMin.avatarThumbnailUrl = jsonObject.optString(userMin.eimUserData.lale_user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
