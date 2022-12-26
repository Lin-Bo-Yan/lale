package com.flowring.laleents.model.explore;

import android.content.Context;
import android.provider.Settings;

import androidx.annotation.WorkerThread;

import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.explore.news.RecommendedNews;
import com.flowring.laleents.model.explore.store.StoreRoot;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class ExploreControlCenter {

    public static void promotion(CallbackUtils.ReturnExplorePromotion callback) {

        new Thread(() -> {
            HttpReturn httpReturn = CloudUtils.iCloudUtils.getExplorePromotion();
            if (httpReturn.status == 200) {
                Gson gson = new Gson();
                callback.Callback(gson.fromJson(gson.toJson(httpReturn.data), new TypeToken<ArrayList<Promotion>>() {
                }.getType()));
            } else
                callback.Callback(null);
        }).start();
    }

    public static void RecommendedNews(CallbackUtils.ReturnExploreRecommendedNews callback) {

        new Thread(() -> {

            Gson gson = new Gson();
            callback.Callback(gson.fromJson(CloudUtils.iCloudUtils.getRecommendedNews(), new TypeToken<ArrayList<RecommendedNews>>() {
            }.getType()));

        }).start();
    }

    public static void FocusApp(CallbackUtils.ReturnExploreDatum callback) {

        new Thread(() -> {
            HttpReturn httpReturn = CloudUtils.iCloudUtils.getFocusApps();
            if (httpReturn.status == 200) {
                Gson gson = new Gson();
                callback.Callback(gson.fromJson(gson.toJson(httpReturn.data), new TypeToken<ArrayList<FocusApp>>() {
                }.getType()));
            } else
                callback.Callback(null);
        }).start();
    }

    public static void StoreRoot(CallbackUtils.ReturnExploreStoreRoot callback) {

        new Thread(() -> {

            Gson gson = new Gson();
            String getMemia = CloudUtils.iCloudUtils.getMemia();
            if (getMemia != null)
                callback.Callback(gson.fromJson(getMemia, new TypeToken<ArrayList<StoreRoot>>() {
                }.getType()));
            else {
                callback.Callback(null);
            }
        }).start();
    }

    public static void MicroappType(CallbackUtils.ReturnExploreMicroappType callback) {

        new Thread(() -> {
            HttpReturn httpReturn = CloudUtils.iCloudUtils.getMicroappTypes();
            StringUtils.HaoLog("MicroappType");
            StringUtils.HaoLog(httpReturn);
            if (httpReturn.status == 200) {
                Gson gson = new Gson();
                callback.Callback(gson.fromJson(gson.toJson(httpReturn.data), new TypeToken<ArrayList<MicroappType>>() {
                }.getType()));
            } else
                callback.Callback(null);
        }).start();
    }

    public static void Microapp(MicroappType microappType, CallbackUtils.ReturnExploreMicroapp callback) {

        new Thread(() -> {
            HttpReturn httpReturn = CloudUtils.iCloudUtils.getMicroapps(microappType.microAppMenuId);
            if (httpReturn.status == 200) {
                Gson gson = new Gson();
                callback.Callback(gson.fromJson(gson.toJson(httpReturn.data), new TypeToken<ArrayList<Microapp>>() {
                }.getType()));
            } else
                callback.Callback(null);
        }).start();
    }

    public static void Microapp(String microAppId, CallbackUtils.ReturnData<Microapp> callback) {

        new Thread(() -> {
            HttpReturn httpReturn = CloudUtils.iCloudUtils.getMicroapp(microAppId);
            if (httpReturn.status == 200) {
                Gson gson = new Gson();
                callback.Callback(true, httpReturn.msg, gson.fromJson(gson.toJson(httpReturn.data), Microapp.class));
            } else
                callback.Callback(false, httpReturn.msg, null);
        }).start();
    }

    public static void isMicroappFavorite(String microAppId, CallbackUtils.ReturnHttp callback) {

        new Thread(() -> {
            callback.Callback(CloudUtils.iCloudUtils.isMicroappFavorite(microAppId));

        }).start();
    }

    public static void AddFavorite(String microAppId, CallbackUtils.ReturnHttp callback) {

        new Thread(() -> {
            callback.Callback(CloudUtils.iCloudUtils.addMicroappFavorite(microAppId));

        }).start();
    }

    public static void DelFavorite(String microAppId, CallbackUtils.ReturnHttp callback) {

        new Thread(() -> {
            callback.Callback(CloudUtils.iCloudUtils.delMicroappFavorite(microAppId));

        }).start();
    }

    public static void setMicroHistoryOpen(Context context, String microAppId, CallbackUtils.ReturnHttp callback) {

        new Thread(() -> {
            callback.Callback(CloudUtils.iCloudUtils.setMicroHistoryOpen(microAppId, Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID), "", "", "Android"));

        }).start();
    }

    @WorkerThread
    public static ArrayList<Microapp> Microapp(MicroappType microappType) {


        HttpReturn httpReturn = CloudUtils.iCloudUtils.getMicroapps(microappType.microAppMenuId);
        if (httpReturn.status == 200) {
            Gson gson = new Gson();
            return gson.fromJson(gson.toJson(httpReturn.data), new TypeToken<ArrayList<Microapp>>() {
            }.getType());
        } else
            return null;

    }

    public static void getMicroappFavoriteList(CallbackUtils.ReturnData<ArrayList<Microapp>> callback) {


        new Thread(() -> {
            HttpReturn httpReturn = CloudUtils.iCloudUtils.getMicroappFavoriteList();
            if (httpReturn.status == 200) {
                Gson gson = new Gson();
                callback.Callback(true, httpReturn.msg, gson.fromJson(gson.toJson(httpReturn.data), new TypeToken<ArrayList<Microapp>>() {
                }.getType()));
            } else
                callback.Callback(false, httpReturn.msg, null);
        }).start();

    }

    public static void getMicroappUsedList(CallbackUtils.ReturnData<ArrayList<Microapp>> callback) {


        new Thread(() -> {
            HttpReturn httpReturn = CloudUtils.iCloudUtils.getMicroappUsedList();
            if (httpReturn.status == 200) {
                Gson gson = new Gson();
                callback.Callback(true, httpReturn.msg, gson.fromJson(gson.toJson(httpReturn.data), new TypeToken<ArrayList<Microapp>>() {
                }.getType()));
            } else
                callback.Callback(false, httpReturn.msg, null);
        }).start();

    }


    public static void ExploreServices(MicroappType exploreServicesType, CallbackUtils.ReturnExploreMicroapp callback) {

        new Thread(() -> {
            HttpReturn httpReturn = CloudUtils.iCloudUtils.getMicroapps(exploreServicesType.microAppMenuId);
            if (httpReturn.status == 200) {
                Gson gson = new Gson();
                callback.Callback(gson.fromJson(gson.toJson(httpReturn.data), new TypeToken<ArrayList<Microapp>>() {
                }.getType()));
            } else
                callback.Callback(null);
        }).start();
    }

}
