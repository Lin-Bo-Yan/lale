package com.flowring.laleents.model.room;

import java.io.Serializable;

public class RoomMinInfoByListType implements Serializable {
    static public String getKey() {
        return "id";
    }

    public String roomId;          //聊天室id
    public String name;        //聊天室名稱
    public int type;           // 1:生活, 2:買迷, 3:服務通, 4:工作
    public String groupId;        // 群組id
    public String avatarUrl;        //聊天室圖片
    public String userId;        //如果是一對一聊天室才有
    public boolean notification;          // 聊天室推播狀態
    public int status;          // 聊天室狀態 顯示1 刪除2 隱藏0

    public RoomMinInfo getRoomMinInfo() {
        RoomMinInfo roomMinInfo = new RoomMinInfo();
        roomMinInfo.id = roomId;
        roomMinInfo.name = name;
        roomMinInfo.type = type;
        roomMinInfo.groupId = groupId;
        roomMinInfo.avatarUrl = avatarUrl;
        roomMinInfo.userId = userId;
        roomMinInfo.isNotification = notification;
        roomMinInfo.status = status;
        return roomMinInfo;
    }
}
