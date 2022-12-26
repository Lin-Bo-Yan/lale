package com.flowring.laleents.model.msg;

import com.flowring.laleents.BuildConfig;
import com.flowring.laleents.model.room.RoomMinInfo;
import com.flowring.laleents.model.user.MemberInfo;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.phone.AllData;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class MessageInfo implements Serializable, IMessage2 {
    static public String getKey() {
        return "id";
    }

    static public String getRoomKey() {
        return "room_id";
    }

    static public String getTimestampKey() {
        return "timestamp";
    }

    public String id = "";            // 訊息編號 唯一值
    public String type = "lale.nomessage";
    public String content;
    public String room_id = "";
    public String sender;         // sender user_id
    public String msg;
    public long timestamp = -1;
    public boolean isRetract = false;
    public int unreadCount = -1;

    public class callRequest {
        public String requestEventId;
        public String result = null;
        public String type;//"audio","video"
        public String unreadCount;
    }

    public class callSpendtime {
        public String requestEventId;
        public Long startTs = null;
        public Long endTs = null;
        public Long duration = null;
        public String type;
    }


    public callSpendtime getCallSpendtime() {

        return new Gson().fromJson(content, callSpendtime.class);
    }

    public callRequest getCallRequest() {

        return new Gson().fromJson(content, callRequest.class);
    }

    public MemberInvite getMemberInvite() {
        return new Gson().fromJson(content, MemberInvite.class);
    }

    public boolean is_lale_message_read() {
        return type.equals("lale.message.read");
    }

    public boolean is_lale_retracted() {
        return type.equals("lale.message.retracted");
    }

    public boolean is_lale_call_response() {
        return type.equals("lale.call.response");
    }

    public boolean is_lale_call_status() {
        return type.equals("lale.call.status");
    }

    public boolean is_lale_nomessage() {
        return type.equals("lale.nomessage");
    }

    public boolean is_lale_room_settings_name() {
        return type.equals("lale.room.settings.name");
    }

    public boolean is_lale_room_settings_desc() {
        return type.equals("lale.room.settings.desc");
    }

    public boolean is_lale_room_settings_admin() {
        return type.equals("lale.room.settings.admin");
    }

    public boolean is_lale_room_settings_avatar() {
        return type.equals("lale.room.settings.avatar");
    }

    public boolean is_lale_room_created() {
        return type.equals("lale.room.created");
    }

    public boolean is_lale_member_invite() {
        return type.equals("lale.member.invite");
    }


    public boolean is_lale_room() {

        if (type.startsWith("lale.room"))
            return true;

        return false;
    }

    public MessageItem getMessage(Map<String, MemberInfo> roomFriends) {
        Gson gson = new Gson();
        MessageItem messageItem =
                gson.fromJson(gson.toJson(this), MessageItem.class);
        MemberInfo friendInfo = roomFriends.get(sender);
        if (friendInfo != null) {
            messageItem.setAvatar(friendInfo.avatarThumbnailUrl);
            messageItem.setUserName(friendInfo.displayName);
        } else {
            messageItem.setAvatar("");
            messageItem.setUserName("");
        }

        return messageItem;
    }

    public boolean is_lale_bot_lami() {
        return type.equals("lale.bot.lami");
    }

    public boolean is_lale_image_received() {
        return type.equals("lale.image.received");
    }

    public boolean is_lale_file_received() {
        return type.equals("lale.file.received");
    }

    public boolean is_lale_reply() {
        return type.equals("lale.message.received.reply");
    }

    public boolean is_no_save() {
        if (is_lale_room_settings_name())
            return true;
        if (is_lale_room_settings_desc())
            return true;
        if (is_lale_room_settings_admin())
            return true;
        if (is_lale_room_settings_avatar())
            return true;
        if (is_lale_room_created())
            return true;

        if (is_lale_message_read())
            return true;
        if (is_lale_call_status())
            return true;
        if (is_lale_call_group_status())
            return true;
        if (is_lale_call_left_request())
            return true;
        return false;
    }

    public boolean is_lale_call_group_status() {
        return type.equals("lale.call.left.request");
    }

    public boolean is_lale_call_left_request() {
        return type.equals("lale.call.group.status");
    }

    public boolean is_update_room() {
        if (is_lale_room_settings_name())
            return true;
        if (is_lale_room_settings_desc())
            return true;
        if (is_lale_room_settings_admin())
            return true;
        if (is_lale_room_settings_avatar())
            return true;
        if (is_lale_room_created())
            return true;
        return false;
    }

    public boolean is_lale_read() {
        return type.equals("lale.message.read");
    }

    public boolean is_lale_member_join() {
        return type.equals("lale.member.join");
    }

    public boolean is_lale_member_left() {
        return type.equals("lale.member.left");
    }

    public boolean is_lale_call_spendtime() {
        return type.equals("lale.call.spendtime");
    }

    public boolean is_lale_call_request() {
        return type.equals("lale.call.request");
    }

    public boolean is_lale_video_received() {
        return type.equals("lale.video.received");
    }

    public boolean is_lale_message_sticker() {
        return type.equals("lale.message.sticker");
    }

    public boolean is_lale_audio_received() {
        return type.equals("lale.audio.received");
    }

    public boolean is_lale_location_received() {
        return type.equals("lale.location.received");
    }

    public boolean is_lale_message_received() {
        return type.equals("lale.message.received");
    }

    public boolean is_lale_message_retract() {
        return type.equals("lale.message.retract");
    }

    public boolean is_lale_message_app() {
        return type.equals("lale.message.app");
    }

    public boolean is_lale_ecosystem_af_notify() {
        return type.equals("lale.ecosystem.af.notify");
    }

    public boolean is_lale_message_announcement() {
        return type.equals("lale.message.announcement");
    }
    public boolean is_lale_message_announcement_cancel() {
        return type.equals("lale.message.announcement.cancel");
    }
    public boolean is_lale_bot_reply() {
        return type.equals("lale.bot.reply");
    }
    public boolean is_lale_bot_send() {
        return type.equals("lale.bot.send");
    }
    public boolean is_lale_message_share() {
        return type.equals("lale.message.share");
    }



    public MessageInfo() {
        id = "";
        room_id = "";
        sender = "";
        msg = "";
        type = "";
        timestamp = -1;
        content = "";
        unreadCount = -1;
    }


    public MessageInfo(JSONObject jsonObj) {
        try {
            if (jsonObj != null) {
                if (!jsonObj.isNull("id"))
                    id = jsonObj.optString("id");
                if (!jsonObj.isNull("roomId"))
                    room_id = jsonObj.optString("roomId");
                JSONObject _content = null;
                if (!jsonObj.isNull("content")) {
                    _content = jsonObj.optJSONObject("content");
                    content = _content.toString();
                }
                if (!jsonObj.isNull("sender"))
                    sender = jsonObj.optString("sender");
                if (!jsonObj.isNull("type"))
                    type = jsonObj.optString("type");
                if (_content != null) {
                    if (!_content.isNull("msg"))
                        msg = _content.optString("msg");
                    if (!_content.isNull("unreadCount"))
                        unreadCount = _content.optInt("unreadCount");
                }
                if (!jsonObj.isNull("timestamp"))
                    timestamp = jsonObj.optLong("timestamp");

            }
        } catch (Exception e) {
            StringUtils.HaoLog(e.toString());
        }
    }

    @Override
    public String getId() {
        return id;
    }

    public boolean isGroup() {

        RoomMinInfo roomMinInfo = AllData.getRoomMinInfo(room_id);
        if (roomMinInfo != null)
            return roomMinInfo.isGroup();
        return false;
    }

    String CallRequest(String value) {
        String type = "";
        if (getCallRequest() != null && getCallRequest().type != null)
            type = getCallRequest().type.equals("audio") ? "語音" : "視訊";

        if (value == null)
            return "通話邀請中";
        if (isGroup()) {
            switch (value) {
                case "call":
                    return "通話中";
                case "reject":
                    return "未接" + type + "通話";
                case "cancel":
                    return "取消" + type + "通話";
                case "timeout":
                    return "未接" + type + "通話";
                case "end":
                    return "已結束" + type + "通話";
                default:
                    return "未接" + type + "通話";
            }
        } else {
            switch (value) {
                case "call":
                    return "通話中";
                case "reject":
                    return "拒絕" + type + "通話";
                case "cancel":
                    return "取消" + type + "通話";
                case "timeout":
                    return "未接" + type + "通話";
                case "end":
                    return "已結束" + type + "通話";
                default:
                    return "未接" + type + "通話";
            }
        }
    }

    @Override
    public String getText() {
        try {

            if (is_lale_call_response())
                return CallRequest(getCallRequest().result);
            if (is_lale_call_group_status())
                return "離開群組通話";
            if (is_lale_retracted())
                return "一則訊息已被收回";
            if (is_lale_ecosystem_af_notify())
                return "您有工作待辦通知";
            if (is_lale_nomessage())
                return "";
            if (is_lale_message_received())
                return new JSONObject(content).optString("msg");
            if (is_lale_bot_lami())
                return new JSONObject(content).optString("msg");
            if (is_lale_location_received())
                return "標記的位置";
            if (is_lale_message_sticker())
                return "一張貼圖";
            if (is_lale_message_announcement())
                return "一則公告";
            if (is_lale_message_announcement_cancel())
                return "取消一則公告";
            if (is_lale_reply())
                return new JSONObject(content).optString("msg");
            if (is_lale_member_join())
                return new JSONObject(content).optString("userName") + "進入聊天室";
            if (is_lale_member_left())
                return new JSONObject(content).optString("userName") + "離開聊天室";
            if (is_lale_file_received())
                return "一個檔案";
            if (is_lale_image_received())
                return "一張圖片";
            if (is_lale_audio_received())
                return "一段音訊";
            if (is_lale_video_received())
                return "一段影片";
            if (is_lale_call_request())
                return CallRequest(getCallRequest().result);
            if (is_lale_call_spendtime())
                return "通話/視訊已結束";

            if  (is_lale_room_settings_name()){
                return "聊天室名稱變更";
            }
            if ( is_lale_room_settings_desc()){
                return "聊天室簡介變更";
            }
            if ( is_lale_room_settings_admin()){
                return "聊天室管理員變更";
            }
            if(  is_lale_room_settings_avatar()){
                return "聊天室頭像變更";
            }
            if ( is_lale_room_created()){
                return "建立了一個聊天室";
            }
            if ( is_lale_bot_reply()){
                return new JSONObject(content).optString("msg");
            }
            if ( is_lale_bot_send()){
                return new JSONObject(content).optString("msg");
            }
            if ( is_lale_message_share()){
                return "一則分享訊息";
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return BuildConfig.DEBUG ? type + " 種類訊息未定回復文字" : "";
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public String getUserID() {
        return sender;
    }


    @Override
    public Date getCreatedAt() {
        return new Date(timestamp);
    }

    @Override
    public String getMessageType() {
        return type;
    }

    @Override
    public boolean isRetract() {
        return isRetract;
    }

     boolean isSelect = false;

    @Override
    public boolean isSelect() {
        return isSelect;
    }

    @Override
    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(((MessageInfo) o).getCreatedAt(), getCreatedAt());
    }

    public boolean canCall() {
        callRequest callRequest = getCallRequest();
        callSpendtime callSpendtime = getCallSpendtime();
        if (callSpendtime.duration != null)
            return false;

        if (callRequest.result == null || callRequest.result.equals("incomming")) {
            return true;
        } else if (callRequest.result.equals("end") || callRequest.result.equals("cancel") || callRequest.result.equals("timeout") || callRequest.result.equals("unavailable") || (callRequest.result.equals("reject"))) {

            return false;

        } else {
            return true;

        }
    }
}
