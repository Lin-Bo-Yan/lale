package com.flowring.laleents.model.msg;

import com.flowring.laleents.BuildConfig;
import com.flowring.laleents.R;
import com.flowring.laleents.model.room.RoomMinInfo;
import com.flowring.laleents.model.user.MemberInfo;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.model.user.UserInfo;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.tools.phone.DefinedUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
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
    public String roomName;
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
        return "lale.message.read".equals(type);
    }

    public boolean is_lale_retracted() {
        return "lale.message.retracted".equals(type);
    }

    public boolean is_lale_call_response() {
        return "lale.call.response".equals(type);
    }

    public boolean is_lale_call_status() {
        return "lale.call.status".equals(type);
    }

    public boolean is_lale_nomessage() {
        return "lale.nomessage".equals(type);
    }

    public boolean is_lale_room_settings_name() {
        return "lale.room.settings.name".equals(type);
    }

    public boolean is_lale_room_settings_desc() {
        return "lale.room.settings.desc".equals(type);
    }

    public boolean is_lale_room_settings_admin() {
        return "lale.room.settings.admin".equals(type);
    }

    public boolean is_lale_room_settings_avatar() {
        return "lale.room.settings.avatar".equals(type);
    }

    public boolean is_lale_room_created() {
        return "lale.room.created".equals(type);
    }

    public boolean is_lale_member_invite() {
        return "lale.member.invite".equals(type);
    }


    public boolean is_lale_room() {
        return "lale.room".startsWith(type);
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
        return "lale.bot.lami".equals(type);
    }

    public boolean is_lale_image_received() {
        return "lale.image.received".equals(type);
    }

    public boolean is_lale_file_received() {
        return "lale.file.received".equals(type);
    }

    public boolean is_lale_reply() {
        return "lale.message.received.reply".equals(type);
    }

    public boolean is_no_save() {
        if (is_lale_room_settings_name()){
            return true;
        }
        if (is_lale_room_settings_desc()){
            return true;
        }
        if (is_lale_room_settings_admin()){
            return true;
        }
        if (is_lale_room_settings_avatar()){
            return true;
        }
        if (is_lale_room_created()){
            return true;
        }
        if (is_lale_message_read()){
            return true;
        }
        if (is_lale_call_status()){
            return true;
        }
        if (is_lale_call_group_status()){
            return true;
        }
        if (is_lale_call_left_request()){
            return true;
        }
        return false;
    }

    public boolean is_lale_call_group_status() {
        return "lale.call.left.request".equals(type);
    }

    public boolean is_lale_call_left_request() {
        return "lale.call.group.status".equals(type);
    }

    public boolean is_update_room() {
        if (is_lale_room_settings_name()){
            return true;
        }
        if (is_lale_room_settings_desc()){
            return true;
        }
        if (is_lale_room_settings_admin()){
            return true;
        }
        if (is_lale_room_settings_avatar()){
            return true;
        }
        if (is_lale_room_created()){
            return true;
        }
        return false;
    }

    public boolean is_lale_read() {
        return "lale.message.read".equals(type);
    }

    public boolean is_lale_member_join() {
        return "lale.member.join".equals(type);
    }

    public boolean is_lale_member_left() {
        return "lale.member.left".equals(type);
    }

    public boolean is_lale_call_spendtime() {
        return "lale.call.spendtime".equals(type);
    }

    public boolean is_lale_call_request() {
        return "lale.call.request".equals(type);
    }

    public boolean is_lale_video_received() {
        return "lale.video.received".equals(type);
    }

    public boolean is_lale_message_sticker() {
        return "lale.message.sticker".equals(type);
    }

    public boolean is_lale_audio_received() {
        return "lale.audio.received".equals(type);
    }

    public boolean is_lale_location_received() {
        return "lale.location.received".equals(type);
    }

    public boolean is_lale_message_received() {
        return "lale.message.received".equals(type);
    }

    public boolean is_lale_message_retract() {
        return "lale.message.retract".equals(type);
    }

    public boolean is_lale_message_app() {
        return "lale.message.app".equals(type);
    }

    public boolean is_lale_ecosystem_af_notify() {
        return "lale.ecosystem.af.notify".equals(type);
    }

    public boolean is_lale_message_announcement() {
        return "lale.message.announcement".equals(type);
    }
    public boolean is_lale_message_announcement_cancel() {
        return "lale.message.announcement.cancel".equals(type);
    }
    public boolean is_lale_bot_reply() {
        return "lale.bot.reply".equals(type);
    }
    public boolean is_lale_bot_send() {
        return "lale.bot.send".equals(type);
    }
    public boolean is_lale_message_share() {
        return "lale.message.share".equals(type);
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
                StringUtils.HaoLog("messageInfo= " + jsonObj);
                if (!jsonObj.isNull("id")){
                    id = jsonObj.optString("id");
                }
                if (!jsonObj.isNull("roomId")){
                    room_id = jsonObj.optString("roomId");
                }
                if(!jsonObj.isNull("roomName")){
                    roomName = jsonObj.optString("roomName");
                }
                JSONObject _content = null;
                if (!jsonObj.isNull("content")){
                    _content = jsonObj.optJSONObject("content");
                    content = _content.toString();
                }
                if (!jsonObj.isNull("sender")){
                    sender = jsonObj.optString("sender");
                }
                if (!jsonObj.isNull("type")) {
                    type = jsonObj.optString("type");
                }
                if (_content != null) {
                    if (!_content.isNull("msg")){
                        msg = _content.optString("msg");
                    }
                    if (!_content.isNull("unreadCount")){
                        unreadCount = _content.optInt("unreadCount");
                    }
                }
                if (!jsonObj.isNull("timestamp")){
                    timestamp = jsonObj.optLong("timestamp");
                }
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
        if (roomMinInfo != null){
            return roomMinInfo.isGroup();
        }
        return false;
    }

    String CallRequest(String value) {
        String type = "";
        if (getCallRequest() != null && getCallRequest().type != null){
            type = "audio".equals(getCallRequest().type) ? AllData.context.getString(R.string.call_voice_text) : AllData.context.getString(R.string.call_video_text);
        }

        if (value == null){
            // 通話邀請中
            return AllData.context.getString(R.string.inviting_call);
        }
        if (isGroup()) {
            switch (value) {
                case "call":
                    // [通話中]
                    return AllData.context.getString(R.string.call);
                case "reject":
                    // [未接" + type + "通話]
                    return String.format(AllData.context.getString(R.string.call_missed_text), type);
                case "cancel":
                    // [取消" + type + "通話]
                    return String.format(AllData.context.getString(R.string.call_cancel_text), type);
                case "timeout":
                    // [未接" + type + "通話]
                    return String.format(AllData.context.getString(R.string.call_missed_text), type);
                case "end":
                    // [已結束" + type + "通話]
                    return String.format(AllData.context.getString(R.string.call_over_text), type);
                default:
                    // [未接" + type + "通話]
                    return String.format(AllData.context.getString(R.string.call_missed_text), type);
            }
        } else {
            switch (value) {
                case "call":
                    // [通話中]
                    return AllData.context.getString(R.string.call);
                case "reject":
                    // [拒絕" + type + "通話]
                    return String.format(AllData.context.getString(R.string.call_reject_text), type);
                case "cancel":
                    // [取消" + type + "通話]
                    return String.format(AllData.context.getString(R.string.call_cancel_text), type);
                case "timeout":
                    // [未接" + type + "通話]
                    return String.format(AllData.context.getString(R.string.call_missed_text), type);
                case "end":
                    // [已結束" + type + "通話]
                    return String.format(AllData.context.getString(R.string.call_over_text), type);
                default:
                    // [未接" + type + "通話]
                    return String.format(AllData.context.getString(R.string.call_missed_text), type);
            }
        }
    }

    @Override
    public String getText() {
        try {
            if(is_lale_call_response()){
                return CallRequest(getCallRequest().result);
            }
            if(is_lale_call_group_status()){
                return AllData.context.getString(R.string.leave_group_call);
            }
            if(is_lale_retracted()){
                return AllData.context.getString(R.string.message_retracted);
            }
            if(is_lale_ecosystem_af_notify()){
                return AllData.context.getString(R.string.work_notification);
            }
            if(is_lale_nomessage()){
                return "";
            }
            if(is_lale_bot_lami()){
                return new JSONObject(content).optString("msg");
            }
            if(is_lale_location_received()){
                return AllData.context.getString(R.string.send_location_information);
            }
            if(is_lale_message_sticker()){
                return AllData.context.getString(R.string.stickers);
            }
            if(is_lale_message_announcement()){
                return AllData.context.getString(R.string.announcement_set_up);
            }
            if(is_lale_message_announcement_cancel()){
                return AllData.context.getString(R.string.announcement_canceled);
            }
            if(is_lale_reply()){
                return new JSONObject(content).optString("msg");
            }
            if(is_lale_file_received()){
                return AllData.context.getString(R.string.file);
            }
            if(is_lale_image_received()){
                return AllData.context.getString(R.string.picture);
            }
            if(is_lale_audio_received()){
                return AllData.context.getString(R.string.voice);
            }
            if(is_lale_video_received()){
                return AllData.context.getString(R.string.film);
            }
            if(is_lale_call_request()){
                return CallRequest(getCallRequest().result);
            }
            if(is_lale_call_spendtime()){
                return AllData.context.getString(R.string.video_call_ended);
            }
            if(is_lale_message_received()){
                return isLink();
            }
            if(is_lale_member_join()){
                String userName = new JSONObject(content).optString("userName");
                String result = String.format(AllData.context.getString(R.string.join_group_format), userName);
                return result;
            }
            if(is_lale_member_left()){
                if(isAdm()){
                    String userName = new JSONObject(content).optString("userName");
                    String result = String.format(AllData.context.getString(R.string.removed_by_admin_format), userName);
                    return result;
                }
                String userName = new JSONObject(content).optString("userName");
                String result = String.format(AllData.context.getString(R.string.left_group_format), userName);
                return result;
            }
            if(is_lale_room_settings_name()){
                String chatroomFormat = AllData.context.getString(R.string.change_chatroom_format);
                String result = String.format(chatroomFormat,roomName);
                return result;
            }
            if(is_lale_room_settings_desc()){
                return AllData.context.getString(R.string.chatroom_profile_changed);
            }
            if(is_lale_room_settings_admin()){
                //String changedAdmin = AllData.context.getString(R.string.chatroom_administrator_change);
                //StringUtils.HaoLog("新管理員 "+roomName);
                //String.format(changedAdmin,roomName); 還拿不到新管理員，所以這段先不放
                return AllData.context.getString(R.string.chatroom_administrator_change);
            }
            if(is_lale_room_settings_avatar()){
                return AllData.context.getString(R.string.change_chatroom_avatar);
            }
            if(is_lale_room_created()){
                return AllData.context.getString(R.string.chatroom_created);
            }
            if(is_lale_bot_reply()){
                return new JSONObject(content).optString("msg");
            }
            if(is_lale_bot_send()){
                return new JSONObject(content).optString("msg");
            }
            if(is_lale_message_share()){
                return AllData.context.getString(R.string.shared_message);
            }
            if(is_lale_message_retract()){
                return AllData.context.getString(R.string.retracted_message);
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
    public boolean isAdm() {
        String userId = null;
        try {
            userId = new JSONObject(content).optString("userId");
        }catch (JSONException e){
            e.printStackTrace();
        }
        if(userId != null&& !userId.isEmpty()){
            if(sender.equals(userId)){
                return false;
            }
        }
        return true;
    }

    @Override
    public String isLink() {
        String msg = null;
        try {
            msg = new JSONObject(content).optString("msg");
        }catch (JSONException e){
            e.printStackTrace();
        }
        if(msg != null && !msg.isEmpty()){
            if(msg.contains("http:") || msg.contains("https:")){
                String title = null;
                try {
                    URL url = new URL(msg);
                    //向網頁伺服發出請求，並將回應分析成document。
                    Document doc = Jsoup.parse(url, 3000);
                    title = doc.title();
                }catch (IOException e){
                    e.printStackTrace();
                }
                String linkFormat = AllData.context.getString(R.string.link_format);
                if(title != null && !title.isEmpty()){
                    return String.format(linkFormat,title);
                }
                return String.format(linkFormat,msg);
            }
        }
        return msg;
    }

    @Override
    public void newAdmin() {
        UserControlCenter.getUserInfo(sender, new CallbackUtils.userReturn() {
            @Override
            public void Callback(UserInfo userInfo) {
                DefinedUtils.displayName = userInfo.displayName;
            }
        });
    }

    public static String getSubType(int subType,String body) {
        switch (subType){
            case 1:
                return AllData.context.getString(R.string.instruction);
            case 2:
                return AllData.context.getString(R.string.process_list);
            case 3:
                return AllData.context.getString(R.string.process_list_keywords);
            case 4:
                return AllData.context.getString(R.string.view_todo);
            case 5:
                return AllData.context.getString(R.string.view_todo_number_of_pens);
            case 6:
                return AllData.context.getString(R.string.view_itinerary);
            case 7:
                return AllData.context.getString(R.string.view_announcement);
            case 8:
                return AllData.context.getString(R.string.view_announcement_number_of_pens);
            case 9:
                return AllData.context.getString(R.string.view_meeting);
            case 10:
                return AllData.context.getString(R.string.view_meeting_number_of_pens);
            case 11:
                return AllData.context.getString(R.string.view_project);
            default:
                return body;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        return Objects.equals(((MessageInfo) o).getCreatedAt(), getCreatedAt());
    }

    public boolean canCall() {
        callRequest callRequest = getCallRequest();
        callSpendtime callSpendtime = getCallSpendtime();
        if (callSpendtime.duration != null){
            return false;
        }
        if (callRequest.result == null || "incomming".equals(callRequest.result)) {
            return true;
        } else if ("end".equals(callRequest.result) || "cancel".equals(callRequest.result) || "timeout".equals(callRequest.result) || "unavailable".equals(callRequest.result) || "reject".equals(callRequest.result)) {
            return false;
        } else {
            return true;
        }
    }
}
