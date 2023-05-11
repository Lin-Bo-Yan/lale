package com.flowring.laleents.model.msg;

import android.media.MediaPlayer;

import com.flowring.laleents.R;
import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.room.RoomControlCenter;
import com.flowring.laleents.model.room.RoomMinInfo;
import com.flowring.laleents.model.room.RoomSettingControlCenter;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.DialogUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.flowring.laleents.tools.cloud.mqtt.MqttService;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.tools.phone.LocalBroadcastControlCenter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MsgControlCenter {

    public static void searchMessage(String keyword, String roomId, CallbackUtils.SearchMsgsReturn callback) {
        new Thread(() -> {
            callback.Callback(new Gson().fromJson((String) CloudUtils.iCloudUtils.searchMsg(keyword, roomId).data, SearchMsgs.class));
        }).start();

    }



    public static void delKeeps(ArrayList<String> keepInfos, CallbackUtils.ReturnHttp callback) {
        new Thread(() -> {
            callback.Callback(CloudUtils.iCloudUtils.delKeep(keepInfos));
        }).start();

    }

    public static void collectMessage(MessageItem message) {
        StringUtils.HaoLog("collectMessage");
        new Thread(() -> {

            StringUtils.HaoLog("collectMessage", CloudUtils.iCloudUtils.newKeep(message.id));
        }).start();

    }

    // 回收雲端修改訊息
    public static void redactMessage(MessageItem message) {
        HttpReturn httpReturn = CloudUtils.iCloudUtils.retractMsg(message.room_id, message.id);
        if (httpReturn.status == 200) {
            StringUtils.HaoLog("redactMessage");
            message.isRedactInRoom = true;
            message.isRetract = true;

            AllData.updateMsg(message);
        }
    }

    public static void sendMsg(SendMessageInfo messageInfo) {
        MqttService.mqttControlCenter.publishMessage(new Gson().toJson(messageInfo));
    }

    public static void sendMsg(ArrayList<SendMessageInfo> messageInfos, ArrayList<String> RoomIds) {
        Gson s = new Gson();
        for (int i = 0; i < messageInfos.size(); i++) {
            for (int j = 0; j < RoomIds.size(); j++) {
                SendMessageInfo messageInfo = messageInfos.get(i);
                messageInfo.roomId = RoomIds.get(j);
                MqttService.mqttControlCenter.publishMessage(messageInfo.getJson().toString());
            }

        }

    }

    public static void sendText(String roomId, String value) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.message.send");
            JSONObject content = new JSONObject();
            content.put("msg", value);
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttService.mqttControlCenter.publishMessage(msg.toString());
    }

    public static void sendCrad(String roomId, String userId, String value) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.message.send");
            JSONObject content = new JSONObject();
            content.put("msg", value);
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttService.mqttControlCenter.publishMessage(msg.toString());
    }

    public static void sendRequestAudio(String roomId) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.call.send.request");
            JSONObject content = new JSONObject();
            content.put("type", "audio");
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttService.mqttControlCenter.publishMessage(msg.toString());
    }

    public static void sendRequestVideo(String roomId) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.call.send.request");
            JSONObject content = new JSONObject();
            content.put("type", "video");
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttService.mqttControlCenter.publishMessage(msg.toString());
    }

    public static void sendCancelRequest(String roomId, String eventId) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.call.cancel.request");
            JSONObject content = new JSONObject();
            content.put("eventId", eventId);
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttService.mqttControlCenter.publishMessage(msg.toString());
    }

    public static void sendApplyRequest(String roomId, String eventId) {

        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.call.apply.request");
            JSONObject content = new JSONObject();
            content.put("eventId", eventId);
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttService.mqttControlCenter.publishMessage(msg.toString());

    }

    public static void sendRejectRequest(String roomId, String eventId) {

        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.call.reject.request");
            JSONObject content = new JSONObject();
            content.put("eventId", eventId);
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttService.mqttControlCenter.publishMessage(msg.toString());

    }

    public static void sendAllEndRequest(String roomId, String eventId) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.call.end.request");
            JSONObject content = new JSONObject();
            content.put("eventId", eventId);
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttService.mqttControlCenter.publishMessage(msg.toString());
    }

    public static void sendEndRequest(String roomId, String eventId) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.call.left.request");
            JSONObject content = new JSONObject();
            content.put("eventId", eventId);
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttService.mqttControlCenter.publishMessage(msg.toString());
    }

    public static void sendSticker(String roomId, String stickerId, String imageId) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.message.sticker");
            JSONObject content = new JSONObject();
            content.put("stickerId", stickerId);
            content.put("imageId", imageId);
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttService.mqttControlCenter.publishMessage(msg.toString());
    }

    public static void sendStickerReply(String roomId, String targetEventId, String stickerId) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.message.sticker.reply");
            JSONObject content = new JSONObject();
            content.put("stickerId", stickerId);
            content.put("targetEventId", targetEventId);
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttService.mqttControlCenter.publishMessage(msg.toString());
    }

    public static void sendTextReply(String roomId, String targetEventId, String text) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.message.send.reply");
            JSONObject content = new JSONObject();
            content.put("msg", text);
            content.put("targetEventId", targetEventId);
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttService.mqttControlCenter.publishMessage(msg.toString());
    }

    public static void sendToBot(String roomId, String keyword) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.bot.send");
            JSONObject content = new JSONObject();
            content.put("keyword", keyword);
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttService.mqttControlCenter.publishMessage(msg.toString());
    }

    public static MessageItem sendBotLami(String keyword) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.bot.lami");
            JSONObject content = new JSONObject();
            content.put("msg", keyword);
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            MqttService.mqttControlCenter.publishMessage(msg.toString());
            msg.put("timestamp", new Date().getTime());
            msg.put("sender", UserControlCenter.getUserMinInfo().userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new MessageInfo(msg).getMessage(new HashMap<>()).setAvatar(UserControlCenter.getUserMinInfo().avatarThumbnailUrl);
    }

    public static void sendRead(String roomId) {
        JSONObject msg = new JSONObject();
        try {
            msg.put("type", "lale.message.read");
            JSONObject content = new JSONObject();
            content.put("read", true);
            msg.put("content", content);
            msg.put("userId", UserControlCenter.getUserMinInfo().userId);
            msg.put("roomId", roomId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (MqttService.mqttControlCenter != null)
            MqttService.mqttControlCenter.publishMessage(msg.toString());
    }
//此段是上傳檔案到聊天室，走MQTT傳到server，檔案上傳是由app做的，未來會用到
//    public static void sendFile(String roomId, File file) {
//        HttpReturn httpReturn = CloudUtils.iCloudUtils.sendFile(roomId, file);
//        if (httpReturn.status == 200) {
//            JSONObject msg = new JSONObject();
//            try {
//                msg.put("type", "lale.file.send");
//                JSONObject content = new JSONObject();
//                content.put("fileId", httpReturn.data);
//                msg.put("content", content);
//                msg.put("userId", UserControlCenter.getUserMinInfo().userId);
//                msg.put("roomId", roomId);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            MqttService.mqttControlCenter.publishMessage(msg.toString());
//        }
//    }

    // 此段程式是回給web端fileId，檔案上傳是由web做的
    public static HttpReturn sendFile(String roomId, File file) {
        HttpReturn httpReturn = CloudUtils.iCloudUtils.sendFile(roomId, file);
        if (httpReturn.status == 200) {
            return httpReturn;
        } else return httpReturn;
    }

    public static void sendCustomizeSticker(String roomId, String fileId) {

        if (fileId != null) {
            JSONObject msg = new JSONObject();
            try {
                msg.put("type", "lale.file.send");
                JSONObject content = new JSONObject();
                content.put("fileId", fileId);
                msg.put("content", content);
                msg.put("userId", UserControlCenter.getUserMinInfo().userId);
                msg.put("roomId", roomId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MqttService.mqttControlCenter.publishMessage(msg.toString());
        }
    }

    public static void sendLocation(String roomId, File image, Double lat, Double lon) {
        HttpReturn httpReturn = CloudUtils.iCloudUtils.sendFile(roomId, image);

        JSONObject msg = new JSONObject();
        if (httpReturn.status == 200) {
            try {
                msg.put("type", "lale.location.send");
                JSONObject content = new JSONObject();
                content.put("fileId", httpReturn.data);
                JSONObject geoInfo = new JSONObject();
                geoInfo.put("lat", lat);
                geoInfo.put("lon", lon);
                content.put("geoInfo", geoInfo);
                msg.put("content", content);
                msg.put("userId", UserControlCenter.getUserMinInfo().userId);
                msg.put("roomId", roomId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MqttService.mqttControlCenter.publishMessage(msg.toString());
        }
    }

    public enum Source {
        notifi,
        mqtt,
        api
    }

    public static MessageInfo receiveMsg(String value, Source source) {

        MessageInfo messageInfo = null;
        //檢查訊息是否符合格式;
        if (value == null)
            return messageInfo;
        try {
            messageInfo = new MessageInfo(new JSONObject(value));
            RoomMinInfo roomMinInfo = AllData.getRoomMinInfo(messageInfo.room_id);
            if (roomMinInfo == null) {
                RoomControlCenter.getAllRoom();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return messageInfo;
        }

        if (messageInfo != null) {
            //觸發接收到時的回調
            if (messageInfo.is_lale_room()) { // lale.room
                RoomControlCenter.getAllRoom();
                LocalBroadcastControlCenter.send(AllData.context, LocalBroadcastControlCenter.ACTION_MQTT_ROOM, value);
            } else if (messageInfo.is_lale_member_invite()) { // lale.member.invite
                if (messageInfo.getMemberInvite().userId.equals(UserControlCenter.getUserMinInfo().userId)) {
                    AllData.updateRoom(messageInfo.getMemberInvite().room);
                    LocalBroadcastControlCenter.send(AllData.context, LocalBroadcastControlCenter.ACTION_MQTT_ROOM, value);
                }
            } else {

                if (messageInfo.is_no_save()) {
                    RoomMinInfo roomMinInfo = AllData.getRoomMinInfo(messageInfo.room_id);
                    if (messageInfo.is_lale_read()) {
                        if (roomMinInfo != null) {
                            StringUtils.HaoLog("status=" + roomMinInfo.status);
                            roomMinInfo.unread_count = 0;
                            AllData.updateRoom(roomMinInfo);
                        }
                    } else if (messageInfo.is_update_room()) {
                        if (roomMinInfo == null)
                            roomMinInfo = new RoomMinInfo();

                        AllData.updateRoom(new Gson().fromJson(StringUtils.SetNoNull(roomMinInfo, messageInfo.content), RoomMinInfo.class));
                        LocalBroadcastControlCenter.send(AllData.context, LocalBroadcastControlCenter.ACTION_MQTT_ROOM, value);
                    }
                } else {
                    if (messageInfo.is_lale_call_request()) { // 通話視訊請求通知，通話類型 = lale.call.request
                        {
                            RoomMinInfo roomMinInfo = AllData.getRoomMinInfo(messageInfo.room_id);
                            if (roomMinInfo != null) {
                                if (messageInfo.getCallRequest().result == null){
                                    roomMinInfo.call_status = messageInfo.getCallRequest().type.equals("audio") ? 2 : 1;
                                }
                                if (messageInfo.getCallRequest().result.equals("unavailable")){
                                    roomMinInfo.call_status = 0;
                                }
                                AllData.updateRoom(roomMinInfo);
                            }
                            if (source == Source.mqtt) {
                                if (!messageInfo.sender.equals(UserControlCenter.getUserMinInfo().userId)&&!messageInfo.isGroup()){
                                    DialogUtils.showCall(AllData.context, messageInfo);
                                }
                                LocalBroadcastControlCenter.send(AllData.context, LocalBroadcastControlCenter.ACTION_MQTT_CALL_REQUEST, value);
                            }
                        }

                    } else if (messageInfo.is_lale_call_response()) {

                        RoomMinInfo roomMinInfo = AllData.getRoomMinInfo(messageInfo.room_id);
                        if (roomMinInfo != null) {
                            MessageInfo requestEvent = AllData.getMsg(messageInfo.getCallRequest().requestEventId);
                            if (requestEvent != null)
                                if (messageInfo.getCallRequest().result.equals("cancel") || (!roomMinInfo.isGroup() && messageInfo.getCallRequest().result.equals("reject")) || (!roomMinInfo.isGroup() && messageInfo.getCallRequest().result.equals("call"))) {
                                    roomMinInfo.call_status = 0;
                                    AllData.updateRoom(roomMinInfo);
                                    DialogUtils.hideCall(AllData.context,messageInfo);
//                                    MsgControlCenter.stopRing();
                                    requestEvent.content = messageInfo.content;
                                    AllData.updateMsg(requestEvent);
                                }
                        }
                        if (source == Source.mqtt) {
                            LocalBroadcastControlCenter.send(AllData.context, LocalBroadcastControlCenter.ACTION_MQTT_CALL_MSG, value);
                        }
                    } else if (messageInfo.is_lale_call_spendtime()) {
                        MessageInfo requestEventId = AllData.getMsg(messageInfo.getCallRequest().requestEventId);
                        if (requestEventId != null) {
                            try {
                                JSONObject content = new JSONObject(requestEventId.content);
                                content.put("result", "end");
                                requestEventId.content = content.toString();
                                AllData.updateMsg(requestEventId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        RoomMinInfo roomMinInfo = AllData.getRoomMinInfo(messageInfo.room_id);
                        if (roomMinInfo != null) {
                            roomMinInfo.call_status = 0;
                            AllData.updateRoom(roomMinInfo);
                        }
                        if (source == Source.mqtt) {
                            LocalBroadcastControlCenter.send(AllData.context, LocalBroadcastControlCenter.ACTION_MQTT_CALL_MSG, value);
                        }

                    }
                    boolean up = AllData.updateMsg(messageInfo);
                    StringUtils.HaoLog("up=" + up);
                    if (up) {
                        RoomMinInfo roomMinInfo = AllData.getRoomMinInfo(messageInfo.room_id);
                        if (roomMinInfo != null) {
                            if (roomMinInfo.last_msg_time < messageInfo.timestamp) {
                                if (messageInfo.unreadCount != -1)
                                    roomMinInfo.unread_count = messageInfo.unreadCount;
                                roomMinInfo.last_msg_time = messageInfo.timestamp;
                                roomMinInfo.last_msg = messageInfo.getText();
                            }
                            if (roomMinInfo.status == 0 || roomMinInfo.status == 2) {
                                roomMinInfo.status = 1;

                                RoomSettingControlCenter.setStatus(roomMinInfo, 1, new CallbackUtils.ReturnHttp() {
                                    @Override
                                    public void Callback(HttpReturn httpReturn) {
                                        StringUtils.HaoLog(httpReturn);
                                    }
                                });
                            }
                            boolean updateRoom = AllData.updateRoom(roomMinInfo);
                        }
                    }
                }
                if (source == Source.mqtt){
                    LocalBroadcastControlCenter.send(AllData.context, LocalBroadcastControlCenter.ACTION_MQTT_MSG, value);
                }
            }
            //存入dp

            //修改房間

        }
        return messageInfo;
    }






    static private MediaPlayer incomingCallPlayer;

    public static void playRing() {
        if (incomingCallPlayer == null) {
            incomingCallPlayer = MediaPlayer.create(AllData.context, R.raw.incoming_call);
            incomingCallPlayer.setLooping(true);
        }
        incomingCallPlayer.start();
    }

    public static void stopRing() {
        if (incomingCallPlayer != null) {
            if (incomingCallPlayer.isPlaying()) {
                incomingCallPlayer.stop();
                incomingCallPlayer = null;
            }
        }
    }
}
