package com.flowring.laleents.model.room;


import static com.flowring.laleents.model.user.UserControlCenter.getUserMinInfo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.flowring.laleents.tools.phone.AllData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RoomControlCenter {

    /**
     * type 聊天室種類 0:全部 1:一般 2:買迷 3:服務通 4:工作 5: 免費版辦公 6:群組。目前支援 0,1,6
     */

    public static void getAllRoom() {

        HttpReturn httpReturn = CloudUtils.iCloudUtils.getSimpleRooms(0, new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("timeout");
            }
        });
        if (httpReturn.status == 200) {
            ArrayList<RoomMinInfo2> RoomMinInfo2 = new Gson().fromJson(new Gson().toJson(httpReturn.data), new TypeToken<ArrayList<RoomMinInfo2>>() {
            }.getType());
            ArrayList<RoomMinInfo> roomMinInfos = new ArrayList<>();
            for (RoomMinInfo2 roomMinInfo2 : RoomMinInfo2) {
                StringUtils.HaoLog("roomMinInfo2=" + new Gson().toJson(roomMinInfo2));
                StringUtils.HaoLog("roomMinInfo=" + new Gson().toJson(roomMinInfo2.getRoomMinInfo()));
                if (roomMinInfo2 != null)
                    roomMinInfos.add(roomMinInfo2.getRoomMinInfo());
            }
            AllData.updateRooms(roomMinInfos);

        }


    }

    class Room {
        public String roomId;
    }

    public static void getRoom0(String roomId, CallbackUtils.APIReturn callback) {
        new Thread(() -> {
            HttpReturn httpReturn = CloudUtils.iCloudUtils.getAllSimpleRooms();
            if (httpReturn.status == 200) {
                ArrayList<RoomMinInfoByListType> room0 = new Gson().fromJson(httpReturn.data.toString(), new TypeToken<ArrayList<RoomMinInfoByListType>>() {
                }.getType());
                StringUtils.HaoLog("roomId=" + roomId + " " + room0.size());
                StringUtils.HaoLog("AllData.getRoomId(roomId)=" + AllData.getRoomMinInfo(roomId));
                for (int i = 0; i < room0.size(); i++) {
                    if (room0.get(i).roomId.equals(roomId)) {
                        room0.get(i).status = 1;
                        boolean upOk = AllData.updateRoom(room0.get(i).getRoomMinInfo());
                        StringUtils.HaoLog(roomId + " upOk=" + upOk);
                        RoomSettingControlCenter.setStatus(room0.get(i).getRoomMinInfo(), 1, new CallbackUtils.ReturnHttp() {
                            @Override
                            public void Callback(HttpReturn httpReturn) {
                                StringUtils.HaoLog(httpReturn);

                            }
                        });

                        StringUtils.HaoLog(roomId + " = room0.get(i).id");
                    }

                }
            }

            StringUtils.HaoLog("AllData.getRoomId(roomId) end=" + AllData.getRoomMinInfo(roomId));
            callback.Callback(AllData.getRoomMinInfo(roomId) != null, "");

        }).start();

    }

    public static void newG(String name, int type, ArrayList<String> users, File image, CallbackUtils.APIReturn callback) {
        new Thread(() -> {
            // 建立新群組房間
            HttpReturn h = CloudUtils.iCloudUtils.newGroupRoom(name, type, users, image);
            ArrayList<RoomMinInfoByListType> room0 = new Gson().fromJson(CloudUtils.iCloudUtils.getAllSimpleRooms().data.toString(), new TypeToken<ArrayList<RoomMinInfoByListType>>() {
            }.getType());
            String newRoomId = new Gson().fromJson((String) h.data, Room.class).roomId;
            StringUtils.HaoLog("h.data=" + h.data);
            StringUtils.HaoLog("newRoomId=" + newRoomId);
            StringUtils.HaoLog("room0.size()=" + room0.size());
            for (int i = 0; i < room0.size(); i++) {
                if (room0.get(i).roomId.equals(newRoomId)) {
                    AllData.updateRoom(room0.get(i).getRoomMinInfo());
                    StringUtils.HaoLog(newRoomId + " = room0.get(i).id");
                }

            }
            callback.Callback(h.msg.equals("Success"), newRoomId);
        }).start();

    }

    public static void newP(String password, CallbackUtils.PasswordRoomReturn callback) {
        new Thread(() -> {
            // 建立新群組房間
            HttpReturn h = CloudUtils.iCloudUtils.newPassworkRoom(password);
            StringUtils.HaoLog(h);
            PasswordRoomMinInfo r = new Gson().fromJson((String) h.data, PasswordRoomMinInfo.class);

            StringUtils.HaoLog("h.data=" + h.data);
            if (r != null) {
                StringUtils.HaoLog("r=" + r.userList);
                StringUtils.HaoLog("r=" + r.userInfoList);
                callback.Callback(r);
            } else
                StringUtils.HaoLog("密碼建房失敗");

        }).start();

    }

    public static void getP(String password, CallbackUtils.PasswordRoomReturn callback) {
        new Thread(() -> {
            // 拿密碼房間
            HttpReturn h = CloudUtils.iCloudUtils.getPassworkRoom(password);
            StringUtils.HaoLog(h);
            PasswordRoomMinInfo r = new Gson().fromJson((String) h.data, PasswordRoomMinInfo.class);


            if (r != null) {
                callback.Callback(r);
            } else
                callback.Callback(null);


        }).start();

    }

    public static void getPtoR(int id, String password, String[] userList, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            // 以密碼建立房間
            HttpReturn h = CloudUtils.iCloudUtils.addPassworkRoom(id, password, userList);
            StringUtils.HaoLog(h);
            callback.Callback(h);
            StringUtils.HaoLog("h.data=" + h.data);


        }).start();

    }

    public static void getPtoR(String groupId, String password, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            // 以密碼建立房間
            HttpReturn h = CloudUtils.iCloudUtils.addPassworkRoom(groupId, password);
            StringUtils.HaoLog(h);
            callback.Callback(h);
            StringUtils.HaoLog("h.data=" + h.data);


        }).start();

    }

    public static void leftP(int id, String password, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            // 離開密碼群
            HttpReturn h = CloudUtils.iCloudUtils.leavePassworkRoom(id, password);
            StringUtils.HaoLog(h);
            callback.Callback(h);
            StringUtils.HaoLog("h.data=" + h.data);


        }).start();

    }

    public static void getGroupRoom(String groupId, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            //取得群組的房間
            HttpReturn h = CloudUtils.iCloudUtils.getGroupRoom(groupId);
            StringUtils.HaoLog(h);
            callback.Callback(h);
            StringUtils.HaoLog("h.data=" + h.data);


        }).start();

    }

    public static void updateBackground(String roomId, String background, CallbackUtils.APIReturn callback) {
        new Thread(() -> {
            // 聊天室的聊天室背景
            HttpReturn httpReturn;
            try {
                httpReturn = CloudUtils.iCloudUtils.updateRoom(roomId, new JSONObject().put("background", background).put("roomId", roomId), new CallbackUtils.TimeoutReturn() {
                    @Override
                    public void Callback(IOException timeout) {
                        StringUtils.HaoLog("timeout");
                    }
                });
                StringUtils.HaoLog(httpReturn);
                callback.Callback(httpReturn.status == 200, httpReturn.msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void updateNotification(String roomId, boolean isNotification, CallbackUtils.APIReturn callback) {
        new Thread(() -> {
            // 是否開啟提醒 example: false
            HttpReturn httpReturn;
            try {
                httpReturn = CloudUtils.iCloudUtils.updateRoom(roomId, new JSONObject().put("isNotification", isNotification).put("roomId", roomId), new CallbackUtils.TimeoutReturn() {
                    @Override
                    public void Callback(IOException timeout) {
                        StringUtils.HaoLog("timeout");
                    }
                });
                StringUtils.HaoLog(httpReturn);
                callback.Callback(httpReturn.status == 200, httpReturn.msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void updateTop(String roomId, boolean isTop, CallbackUtils.APIReturn callback) {
        new Thread(() -> {
            // 是否置頂聊天室  example: false
            HttpReturn httpReturn;
            try {
                httpReturn = CloudUtils.iCloudUtils.updateRoom(roomId, new JSONObject().put("isTop", isTop).put("roomId", roomId), new CallbackUtils.TimeoutReturn() {
                    @Override
                    public void Callback(IOException timeout) {
                        StringUtils.HaoLog("timeout");
                    }
                });
                StringUtils.HaoLog(httpReturn);
                callback.Callback(httpReturn.status == 200, httpReturn.msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void updatesSatus(String roomId, int status, CallbackUtils.APIReturn callback) {
        new Thread(() -> {
            // 聊天室狀態：0: 隱藏; 1: 顯示; 2:刪除
            HttpReturn httpReturn;
            try {
                httpReturn = CloudUtils.iCloudUtils.updateRoom(roomId, new JSONObject().put("status", status).put("roomId", roomId), new CallbackUtils.TimeoutReturn() {
                    @Override
                    public void Callback(IOException timeout) {
                        StringUtils.HaoLog("timeout");
                    }
                });
                StringUtils.HaoLog(httpReturn);
                callback.Callback(httpReturn.status == 200, httpReturn.msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static String getChatroomBG(String roomId) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
        return pref.getString(getUserMinInfo().userId + "_" + roomId + "_bg", "");
    }
}
