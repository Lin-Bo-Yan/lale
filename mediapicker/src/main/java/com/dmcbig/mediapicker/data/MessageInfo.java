package com.dmcbig.mediapicker.data;

import org.json.JSONObject;

import java.io.Serializable;

public class MessageInfo implements Serializable {
    public String _id;
    public String room_id;
    public String sender;
    public String senderName;
    public String senderAvatar;
    public String msg;
    public String _type;
    public String url;
    public String mimeType;
    public long receiveTime;

    public MessageInfo() {
        _id = "";
        room_id = "";
        sender = "";
        senderName = "";
        senderAvatar = "";
        msg = "";
        _type = "m.text";
        url = "";
        mimeType = "image/jpeg";
        receiveTime = -1;
    }

    public boolean fromJsonObject(JSONObject jsonObj) {
        try {
            if (jsonObj != null) {
                _id = jsonObj.optString("event_id");
                room_id = jsonObj.optString("room_id");
                sender = jsonObj.optString("sender_id");
                senderName = jsonObj.optString("sender_name");
                senderAvatar = jsonObj.optString("sender_avatar_url");
                msg = jsonObj.toString();
                _type = jsonObj.optString("msgtype");
                url = jsonObj.optString("url");
                mimeType = jsonObj.optString("mimetype");
                receiveTime = jsonObj.optLong("origin_server_ts");
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }
}
