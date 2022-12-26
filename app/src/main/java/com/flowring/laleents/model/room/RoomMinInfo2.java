package com.flowring.laleents.model.room;

import com.flowring.laleents.tools.TimeUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class RoomMinInfo2 implements Serializable {


    public String roomId;          //聊天室id
    public String name;        //聊天室名稱
    public int type;           // 1:生活, 2:買迷, 3:服務通, 4:工作
    public String last_msg;         //最後訊息
    public long last_msg_time;      //最後訊息的時間
    public int unread_count;        // 未讀訊息總數
    public String groupId;        // 群組id
    public String avatarUrl;        //聊天室圖片
    public String groupAdmin;        //群組聊天室的主持者
    public String userId;        //如果是一對一聊天室才有
    public String topTime;      // 聊天室釘選的TS
    public boolean isNotification;          // 聊天室推播狀態
    public int status;          // 聊天室狀態 顯示1 刪除2 隱藏0


    public RoomMinInfo2() {
        roomId = "";
        name = "";
        type = -1;
        last_msg = "";
        last_msg_time = 0;
        unread_count = 0;
        groupId = "";
        avatarUrl = "";
        groupAdmin = "";
        topTime = null;
        isNotification = true;
        status = 1;
    }

    public RoomMinInfo getRoomMinInfo() {
        try {
            RoomMinInfo roomMinInfo = new Gson().fromJson(new JSONObject(new Gson().toJson(this)).put("topTime", null).toString(), RoomMinInfo.class);
            roomMinInfo.id = roomId;
            if (topTime != null)
                roomMinInfo.topTime = TimeUtils.RoomTopToLong(topTime);
            return roomMinInfo;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
