package com.flowring.laleents.model.room;

import java.io.Serializable;

public class RoomInfoInPhone implements Serializable {
    static public String getKey() {
        return "id";
    }

    public String id;          //聊天室id
    public Long msgTime;      // 聊天室最後拿取最後一個訊息的時間
    public Long msgGetTime;      // 聊天室最後拿取的時間
    public String bg;         //背景

}
