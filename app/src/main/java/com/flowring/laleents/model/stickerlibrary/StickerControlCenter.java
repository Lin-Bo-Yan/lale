package com.flowring.laleents.model.stickerlibrary;


import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.flowring.laleents.tools.phone.AllData;

import java.io.File;
import java.util.ArrayList;

public class StickerControlCenter {
    public static void getAllStickerlibrary(CallbackUtils.ReturnData<ArrayList<Stickerlibrary>> callback) {
        new Thread(() -> {
            ArrayList<Stickerlibrary> stickerlibraries = CloudUtils.iCloudUtils.getAllStickerlibrary();
            callback.Callback(stickerlibraries != null, "", stickerlibraries);
        }).start();
    }

    public static void getUserStickers(CallbackUtils.ReturnData<ArrayList<Stickerlibrary>> callback) {
        new Thread(() -> {
            ArrayList<Stickerlibrary> stickerlibraries = CloudUtils.iCloudUtils.getUserStickers();

            callback.Callback(stickerlibraries != null, "", stickerlibraries);
            if (stickerlibraries != null)
                for (Stickerlibrary stickerlibrary : stickerlibraries) {
                    AllData.updatestickerlibrary(stickerlibrary);
                    AllData.updateSticker(stickerlibrary.stickerList);
                }
        }).start();
    }


    public static void newStickerlibrary(String stickerLibraryId, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            callback.Callback(CloudUtils.iCloudUtils.newStickerlibrary(stickerLibraryId));
        }).start();
    }

    public static void getCustomizeStickerlibrary(CallbackUtils.ReturnData<ArrayList<CustomizeSticker>> callback) {
        new Thread(() -> {
            ArrayList<CustomizeSticker> stickerlibraries = CloudUtils.iCloudUtils.getCustomizeStickerlibrary();
            callback.Callback(stickerlibraries != null, "", stickerlibraries);
            AllData.updateCustomizeSticker(stickerlibraries);
        }).start();
    }

    public static void newCustomizeSticker(File sticker, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            callback.Callback(CloudUtils.iCloudUtils.newCustomizeSticker(sticker));
        }).start();
    }

    public static void delCustomizeSticker(String imageId, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            callback.Callback(CloudUtils.iCloudUtils.delCustomizeSticker(imageId));
        }).start();
    }
}
