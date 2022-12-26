package com.flowring.laleents.model.msg;

import com.google.gson.Gson;

import java.io.Serializable;

public class MessageInfok implements Serializable {


    public String id;
    public String type;
    public Object content;
    public String roomId;
    public String sender;
    public String msg;
    public long timestamp;

    public class Content {
        public String msg;
    }

    public MessageInfo getMessageInfo() {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.id = id;
        messageInfo.type = type;
        messageInfo.content = new Gson().toJson(content);
        messageInfo.room_id = roomId;
        messageInfo.sender = sender;
        messageInfo.msg = msg;
        messageInfo.timestamp = timestamp;
        return messageInfo;
    }

}
