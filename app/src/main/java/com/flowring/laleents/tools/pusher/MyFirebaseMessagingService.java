package com.flowring.laleents.tools.pusher;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static com.flowring.laleents.model.msg.MsgControlCenter.receiveMsg;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;

import androidx.core.app.NotificationCompat;

import com.flowring.laleents.R;
import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.msg.MessageInfo;
import com.flowring.laleents.model.msg.MsgControlCenter;
import com.flowring.laleents.model.notifi.SilenceNotifi;
import com.flowring.laleents.model.notifi.workNotifi;
import com.flowring.laleents.model.room.RoomControlCenter;
import com.flowring.laleents.model.room.RoomMinInfo;
import com.flowring.laleents.model.room.RoomSettingControlCenter;
import com.flowring.laleents.model.room.UserInRoom;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.CommonUtils;
import com.flowring.laleents.tools.FileUtils;
import com.flowring.laleents.tools.SharedPreferencesUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.tools.phone.LocalBroadcastControlCenter;
import com.flowring.laleents.ui.main.webBody.MainWebActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    static int afid = 5000;

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        StringUtils.HaoLog("FirebaseService= "+AllData.context);
        StringUtils.HaoLog("FirebaseService= "+UserControlCenter.getUserMinInfo());
        StringUtils.HaoLog("FirebaseService= "+remoteMessage.getData().get("body"));

        if (AllData.context == null){
            AllData.context = getApplicationContext();
        }
        //有使用者登入才會處理推播
        if (UserControlCenter.getUserMinInfo() != null) {
            if (UserControlCenter.getUserMinInfo().eimUserData.isLaleAppEim) {
                if (AllData.dbHelper == null){
                    AllData.initSQL(UserControlCenter.getUserMinInfo().userId);
                }
                if (remoteMessage.getData() != null) {
                    MessageInfo messageInfo = receiveMsg(remoteMessage.getData().get("body"), MsgControlCenter.Source.notifi);
                    StringUtils.HaoLog("messageInfo result1=" + messageInfo);
                    //判斷是否在前景
                    boolean isAppForeground = CommonUtils.foregrounded();
                    if (messageInfo.is_lale_call_request()) {
                            StringUtils.HaoLog("群組視訊/語音通話");
                    } else if (messageInfo.is_lale_call_response()) {
                        if (messageInfo.getCallRequest().result.equals("unavailable")) {
                            sendNotification(messageInfo, remoteMessage.getData().get("body"));
                        } else if (messageInfo.getCallRequest().result.equals("cancel")) {
                            sendNotification(messageInfo, remoteMessage.getData().get("body"));
                        } else if ((!messageInfo.isGroup()) && messageInfo.getCallRequest().result.equals("reject")) {
                            sendNotification(messageInfo, remoteMessage.getData().get("body"));
                        }
                    }else if(messageInfo.is_lale_member_left()){
                        StringUtils.HaoLog("人員離開通知");
                        if(!isAppForeground){
                            sendGroupNotification(messageInfo);
                        }else {
                            LocalBroadcastControlCenter.send(this, LocalBroadcastControlCenter.ACTION_NOTIFI_AF, remoteMessage.getData().get("body"));
                        }
                    }else if(messageInfo.is_lale_member_join()){
                        StringUtils.HaoLog("人員加入通知");
                        if(!isAppForeground){
                            sendGroupNotification(messageInfo);
                        }else {
                            LocalBroadcastControlCenter.send(this, LocalBroadcastControlCenter.ACTION_NOTIFI_AF, remoteMessage.getData().get("body"));
                        }
                    } else {
                        StringUtils.HaoLog("是否在前景"+isAppForeground);
                        if(messageInfo != null) {
                            if (!isAppForeground ) {
                                try {
                                    //檢查 EIM commit 是不是logout，如果不是就執行sendNotification
                                    if(!commandLogout(Objects.requireNonNull(remoteMessage.getData().get("body"),"remoteMessage的body不能為null"))){
                                        sendNotification(messageInfo, remoteMessage.getData().get("body"));
                                    }
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                    StringUtils.HaoLog("remoteMessage的body不能為null " + e);
                                }
                            } else {
                                LocalBroadcastControlCenter.send(this, LocalBroadcastControlCenter.ACTION_NOTIFI_AF, remoteMessage.getData().get("body"));
                            }
                        }
                    }
                }
            } else {
                boolean isAppForeground = CommonUtils.foregrounded();
                //是 AF
                StringUtils.HaoLog("是否在前景"+isAppForeground);
                if (!isAppForeground) {
                    sendNoChatNotification(remoteMessage);
                } else {
                    LocalBroadcastControlCenter.send(this, LocalBroadcastControlCenter.ACTION_NOTIFI_AF, remoteMessage.getData().get("body"));
                }
            }
        } else {
            //沒有使用者詳細資訊，表示離線登出，推播server沒有關閉的情況
            StringUtils.HaoLog("closure_pusher= " + remoteMessage.getData());
            if (remoteMessage.getData().containsKey("domain") &&
                    remoteMessage.getData().containsKey("userId") &&
                    remoteMessage.getData().containsKey("isAF")) {

                String isAF = remoteMessage.getData().get("isAF");
                boolean isAFBoolean = Boolean.parseBoolean(isAF);
                StringUtils.HaoLog("closure_pusher= 是否是AF? " + isAFBoolean);
                String domain = remoteMessage.getData().get("domain");
                String userid = remoteMessage.getData().get("userId");
                if(isAFBoolean){
                    //AF 關閉推播
                    String deviceToken = remoteMessage.getData().get("deviceToken");
                    String uuid = Settings.Secure.getString(AllData.context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    String memId = remoteMessage.getData().get("memId");
                    HttpReturn httpReturn = CloudUtils.iCloudUtils.closeAfPusher(domain,memId, userid, deviceToken, uuid, new CallbackUtils.TimeoutReturn() {
                        @Override
                        public void Callback(IOException timeout) {
                            StringUtils.HaoLog("AF 關閉推播 api timeout");
                        }
                    });
                    StringUtils.HaoLog("關閉AF推播成功 ", httpReturn);
                } else {
                    //EIM 關閉推播
                    if(AllData.getMainServer().equals(domain)){
                        String uuid = Settings.Secure.getString(AllData.context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        HttpReturn httpReturn = CloudUtils.iCloudUtils.closePusher(userid, uuid, new CallbackUtils.TimeoutReturn() {
                            @Override
                            public void Callback(IOException timeout) {
                                StringUtils.HaoLog("EIM 關閉推播 api timeout");
                            }
                        });
                        StringUtils.HaoLog("關閉EIM推播成功 ", httpReturn);
                    }
                }
            } else {
                StringUtils.HaoLog("沒有 domain 和 userId 和 isAF");
            }
        }
    }


    private void sendNoChatNotification(RemoteMessage remoteMessage) {
        String channel_id = "lale_channel_id";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String body = remoteMessage.getData().get("body");
        StringUtils.HaoLog("body= " + body);
        if (body != null) {
            if(body.contains("command")){
                SilenceNotifi silenceNotifi = new Gson().fromJson(body, SilenceNotifi.class);
                if(silenceNotifi != null){
                    if("logout".equals(silenceNotifi.command)){
                        StringUtils.HaoLog("推播登出");
                        UserControlCenter.setLogout(false,new CallbackUtils.LogoutReturn() {
                            @Override
                            public void Callback(int status, boolean isLaleAppEim) {
                                SharedPreferencesUtils.isRepeatDevice(true);
                            }
                        });
                    }
                }
            } else if (body.contains("msgType")) {
                workNotifi workNotifi = new Gson().fromJson(body, workNotifi.class);
                Intent intent = new Intent(this, MainWebActivity.class);
                intent.putExtra("bFromPhone", true);
                intent.putExtra("Notification", body);
                String title = remoteMessage.getData().get("title");
                if (workNotifi.notifyType != null && workNotifi.msgType.equals("AF_TASK")) {
                    title = workNotifi.frontUserName;
                    body = workNotifi.taskName + ":" + workNotifi.keyword + "\n您有一份工作需盡速處理";
                } else if (workNotifi.msgType != null && workNotifi.msgType.equals("AF_MEETING")) {
                    title = workNotifi.title;
                    body = workNotifi.content;
                }
                StringUtils.HaoLog("body= " + body);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, afid, intent,  FLAG_IMMUTABLE);

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel_id)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setLights(Color.RED, 1000, 300)
                        .setDefaults(Notification.DEFAULT_LIGHTS);
                // Notification Channel is required for Android O and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    StringUtils.HaoLog("Build.VERSION.SDK_INT >= Build.VERSION_CODES.O");
                    NotificationChannel channel = new NotificationChannel(
                            channel_id, "新訊息通知", NotificationManager.IMPORTANCE_DEFAULT
                    );
                    channel.setDescription("Lale 收到新訊息時使用的通知類型 (請注意，若未開啟可能無法接收新訊息通知)");
                    channel.setShowBadge(true);
                    channel.canShowBadge();
                    channel.enableLights(true);
                    channel.setLightColor(Color.RED);
                    channel.enableVibration(true);
                    channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
                    channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                }
                notificationManager.notify(afid++, notificationBuilder.build());
            }
        }
    }

    private void sendNotification(MessageInfo data, String notificationBody) {
        StringUtils.HaoLog("data=" + data);
        StringUtils.HaoLog("dbHelper=" + AllData.dbHelper);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channel_id = "lale_channel_id";

        int id = CommonUtils.letterToNumber(data.id);
        StringUtils.HaoLog("id= " + id);
        if (id < 0) {
            id = -id;
        }

        //處理點選訊息的跳轉
        Intent intent = new Intent(this, MainWebActivity.class);
        intent.putExtra("bFromPhone", true);
        intent.putExtra("roomInfo", data.room_id);
        workNotifi workNotifi = null;
        Bitmap bitmap = null;
        StringUtils.HaoLog("data.content=" + data.content);
        if (data.content == null){
            return;
        }
        StringUtils.HaoLog("is_lale_ecosystem_af_notify= " + data.is_lale_ecosystem_af_notify());
        if (data.is_lale_ecosystem_af_notify()) {
            try {
                String msg = new JSONObject(data.content).optString("data");
                StringUtils.HaoLog("Notification= "+msg);
                workNotifi = new Gson().fromJson(msg, workNotifi.class);
                intent.putExtra("Notification", notificationBody);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            StringUtils.HaoLog("Notification= " + data.room_id);
            intent.putExtra("roomInfo", data.room_id);
        }
        StringUtils.HaoLog("workNotifi= " + new Gson().toJson(workNotifi));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent, FLAG_IMMUTABLE );
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //設定標題 內文
        RoomMinInfo room = AllData.getRoomMinInfo(data.room_id);
        if (room == null) {
            RoomControlCenter.getAllRoom();
            room = AllData.getRoomMinInfo(data.room_id);
        }
        String body = data.getText();
        UserInRoom userInRoom = null;
        String title = data.type;
        String avatar_url = "";
        if (workNotifi == null) {
            if (room != null) {
                avatar_url = room.avatarUrl;
                title = room.name;
                userInRoom = AllData.getUserInRoom(room.id, data.sender);
                if (userInRoom != null) {
                    StringUtils.HaoLog("userInRoom !=null");
                } else {
                    StringUtils.HaoLog("room.type =" + room.type);
                    if (room.type == 6 || room.type == 4){
                        AllData.setUserInRoom(room.id, RoomSettingControlCenter.getGroupMembers(room.groupId));
                    } else{
                        AllData.setUserInRoom(room.id, RoomSettingControlCenter.getRoomMembers(room.id));
                    }
                    userInRoom = AllData.getUserInRoom(room.id, data.sender);
                }
            }
            StringUtils.HaoLog("userInRoom= " + room.type);
            StringUtils.HaoLog("userInRoom= " + body);
            StringUtils.HaoLog("userInRoom= " + title);
            StringUtils.HaoLog("userInRoom= " + data.room_id);

            //知道對方名稱
            if (userInRoom != null) {
                //群組通知 room.type = 6，1對1 通知 room.type = 1
                if (room.type != 1) {
                    body = userInRoom.displayName + ": " + body;
                }
                if (room.type != 6){
                    avatar_url = userInRoom.avatarUrl;
                }
            }

            if (room.type == 4) {
                bitmap = StringUtils.drawBitmap(room.name);
            } else {
                if (avatar_url.isEmpty()) {
                    bitmap = BitmapFactory.decodeResource(getResources(),
                            room.type == 6 ? R.drawable.default_group : R.drawable.default_person);
                } else {
                    bitmap = FileUtils.getBitmapFromURL(avatar_url);
                }
            }
        } else {
            StringUtils.HaoLog("到關通知= " + workNotifi.msgType);
            if (workNotifi.msgType.contains("AF_TASK")) {
                title = workNotifi.frontUserName;
                body = workNotifi.taskName + ":" + workNotifi.keyword + "\n您有一份工作需盡速處理";
            }
            if (workNotifi.msgType.contains("AF_MEETING")) {
                title = workNotifi.title;
                body = workNotifi.content;
            }
            if (workNotifi.msgType.contains("chatbotAf")) {
                title = data.roomName;
                body = MessageInfo.getSubType(workNotifi.subType,body);
            }
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel_id)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(uri)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_HIGH)
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_LIGHTS);
        if (workNotifi == null) {
            notificationBuilder.setLargeIcon(bitmap);
        } else {
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        }
        // Notification Channel is required for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            StringUtils.HaoLog("Build.VERSION.SDK_INT >= Build.VERSION_CODES.O");
            NotificationChannel channel = new NotificationChannel(
                    channel_id, "新訊息通知", NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Lale 收到新訊息時使用的通知類型 (請注意，若未開啟可能無法接收新訊息通知)");
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(uri, audioAttributes);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(id, notificationBuilder.build());
        StringUtils.HaoLog("發送通知  ");
    }

    private void sendGroupNotification(MessageInfo data){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String channel_id = "lale_channel_id";
        int id = CommonUtils.letterToNumber(data.id);
        StringUtils.HaoLog("id= " + id);
        if (id < 0) {
            id = -id;
        }

        //處理點選訊息的跳轉
        Intent intent = new Intent(this, MainWebActivity.class);
        intent.putExtra("bFromPhone",true);
        intent.putExtra("roomInfo", data.room_id);
        if(data.is_lale_member_join() || data.is_lale_member_left()){
            intent.putExtra("roomInfo", data.room_id);
        }
        StringUtils.HaoLog("data.content=" + data.content);
        if(data.content == null){
            return;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id, intent, FLAG_IMMUTABLE );
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //設定標題 內文
        RoomMinInfo room = AllData.getRoomMinInfo(data.room_id);
        if (room == null) {
            RoomControlCenter.getAllRoom();
            room = AllData.getRoomMinInfo(data.room_id);
        }
        String body = data.getText();
        UserInRoom userInRoom;
        String title = data.type;
        if (room != null) {
            title = room.name;
            userInRoom = AllData.getUserInRoom(room.id, data.sender);
            if (userInRoom != null) {
                StringUtils.HaoLog("userInRoom !=null");
            } else {
                StringUtils.HaoLog("room.type =" + room.type);
                if (room.type == 6 ){
                    AllData.setUserInRoom(room.id, RoomSettingControlCenter.getGroupMembers(room.groupId));
                } else {
                    AllData.setUserInRoom(room.id, RoomSettingControlCenter.getRoomMembers(room.id));
                }
                userInRoom = AllData.getUserInRoom(room.id, data.sender);
            }

        }
        StringUtils.HaoLog("群組= " + room.type);
        StringUtils.HaoLog("群組= " + body);
        StringUtils.HaoLog("群組= " + title);
        StringUtils.HaoLog("群組= " + data.room_id);
        //知道對方名稱

        //.setContent(remoteViews) RemoteViews使用
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel_id)
                .setSmallIcon(R.drawable.lale_logo)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(uri)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(Notification.PRIORITY_HIGH)
                .setLights(Color.RED, 1000, 300)
                .setDefaults(Notification.DEFAULT_LIGHTS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            StringUtils.HaoLog("Build.VERSION.SDK_INT >= Build.VERSION_CODES.O");
            NotificationChannel channel = new NotificationChannel(
                    channel_id, "新訊息通知", NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Lale 收到新訊息時使用的通知類型 (請注意，若未開啟可能無法接收新訊息通知)");
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableLights(true);

            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setSound(uri, audioAttributes);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(id, notificationBuilder.build());
        StringUtils.HaoLog("發送群組通知  ");
    }

    private boolean foregrounded() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }

    private boolean commandLogout(String body){
        if (body.contains("command")){
            SilenceNotifi silenceNotifi = new Gson().fromJson(body, SilenceNotifi.class);
            if(silenceNotifi.command == null){
                return false;
            }
            switch (silenceNotifi.command){
                case "logout":
                    StringUtils.HaoLog("推播登出");
                    UserControlCenter.setLogout(false,new CallbackUtils.LogoutReturn() {
                        @Override
                        public void Callback(int status, boolean isLaleAppEim) {
                            SharedPreferencesUtils.isRepeatDevice(true);
                        }
                    });
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    @Override
    public void onNewToken(String token) {
        //sendRegistrationToServer(token);
    }

    private static class SortByPostTime implements Comparator<StatusBarNotification> {
        public int compare(StatusBarNotification msg0, StatusBarNotification msg1) {
            Date date0 = new Date(msg0.getPostTime());
            Date date1 = new Date(msg1.getPostTime());
            int flag = date0.compareTo(date1);
            return flag;
        }
    }
}
