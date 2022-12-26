package com.flowring.laleents.model.msg;

import android.os.Parcel;
import android.os.Parcelable;

import com.flowring.laleents.model.user.UserControlCenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class SendMessageInfo implements Serializable, Parcelable {


    public String type;
    public String content;
    public String userId;
    public String roomId;

    public SendMessageInfo(MessageItem messageItem) {
        type = messageItem.type.replace("received", "send");
        content = messageItem.content;
        userId = UserControlCenter.getUserMinInfo().userId;
        roomId = messageItem.room_id;
    }


    public SendMessageInfo() {
        type = "";
        content = "";
        userId = UserControlCenter.getUserMinInfo().userId;
        roomId = "";
    }

    public JSONObject getJson() {
        try {
            JSONObject data = new JSONObject();
            data.put("type", type);
            data.put("content", new JSONObject(content));
            data.put("userId", userId);
            data.put("roomId", roomId);
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(type);
        parcel.writeString(content);
        parcel.writeString(userId);
        parcel.writeString(roomId);
    }
}

