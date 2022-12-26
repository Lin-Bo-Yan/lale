package com.flowring.laleents.model.room.albumIteam;


import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;

public class AlbumControlCenter {
    public static void getAllAlbum(String Roomid, CallbackUtils.ReturnData<ArrayList<AlbumIteam>> callback) {
        new Thread(() -> {
            HttpReturn h = CloudUtils.iCloudUtils.getAllAlbum(Roomid);

            if (h != null) {
                ArrayList<AlbumIteam> r = new Gson().fromJson(new Gson().toJson(h.data), new TypeToken<ArrayList<AlbumIteam>>() {
                }.getType());

                callback.Callback(h.status == 200, null, r);
            } else
                callback.Callback(false, h.msg, null);
        }).start();
    }

    public static void newAllAlbum(String Roomid, String name, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            HttpReturn h = CloudUtils.iCloudUtils.newAlbum(Roomid, name);
            callback.Callback(h);

        }).start();
    }

    public static void delAlbum(String roomAlbumId, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            HttpReturn h = CloudUtils.iCloudUtils.delAlbum(roomAlbumId);
            callback.Callback(h);

        }).start();
    }

    public static void delAlbum(String Roomid, ArrayList<String> roomAlbumId, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            HttpReturn h = CloudUtils.iCloudUtils.delAlbum(Roomid, roomAlbumId);
            callback.Callback(h);

        }).start();
    }

    public static void getAllPhoto(String roomAlbumId, CallbackUtils.ReturnData<ArrayList<PhotoInfo>> callback) {
        new Thread(() -> {
            HttpReturn h = CloudUtils.iCloudUtils.getPhotos(roomAlbumId);

            if (h != null) {
                ArrayList<PhotoInfo> r = new Gson().fromJson(new Gson().toJson(h.data), new TypeToken<ArrayList<PhotoInfo>>() {
                }.getType());

                callback.Callback(h.status == 200, null, r);
            } else
                callback.Callback(false, h.msg, null);
        }).start();
    }

    public static void newPhotos(String roomAlbumId, File file, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            HttpReturn h = CloudUtils.iCloudUtils.newPhotos(roomAlbumId, file);
            callback.Callback(h);

        }).start();
    }

    public static void delPhotos(String roomAlbumId, ArrayList<String> roomAlbumPhotoId, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            HttpReturn h = CloudUtils.iCloudUtils.delPhotos(roomAlbumId, roomAlbumPhotoId);
            callback.Callback(h);

        }).start();
    }

    public static void delPhotos(String roomAlbumId, String roomAlbumPhotoId, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            HttpReturn h = CloudUtils.iCloudUtils.delPhotos(roomAlbumId, roomAlbumPhotoId);
            callback.Callback(h);

        }).start();
    }

    public static void reNameAlbum(String roomId, String name, String roomAlbumId, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            HttpReturn h = CloudUtils.iCloudUtils.reNameAlbum(roomId, name, roomAlbumId);
            callback.Callback(h);

        }).start();
    }


}
