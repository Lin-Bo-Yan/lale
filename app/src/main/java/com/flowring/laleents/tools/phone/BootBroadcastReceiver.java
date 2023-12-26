package com.flowring.laleents.tools.phone;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.flowring.laleents.R;
import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.msg.MessageInfo;
import com.flowring.laleents.model.msg.MsgControlCenter;
import com.flowring.laleents.model.room.RoomMinInfo;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.tools.ActivityUtils;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.CommonUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.flowring.laleents.tools.cloud.mqtt.MqttService;

import java.io.IOException;

public class BootBroadcastReceiver extends BroadcastReceiver {
    static public MessageInfo callMessageInfo = null;
    @Override
    public void onReceive(Context context, Intent intent) {

        if (MqttService.mqttControlCenter == null) {
            Intent intentServer = new Intent(context, MqttService.class);
            intentServer.putExtra("data", "new");
            context.startService(intentServer);
        }

        if(intent != null){
            if(intent.getAction().equals("reject_notification")){rejectCall(context,intent);}

            if(intent.getAction().equals("accept_notification")){acceptCall(context,intent);}
        } else {
            StringUtils.HaoLog("BroadcastReceiver錯誤");
        }
    }

    public static final String TEST_ACTION = "XXX.XXX.XXX" + "_TEST_ACTION";

    public static void setReToken(Context context) {
        IntentFilter intentFilter = new IntentFilter(TEST_ACTION);
        PendingIntent pendingIntent;
        AlarmManager alarmManager;
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                StringUtils.HaoLog("isReToken= 開始");
                if (AllData.context == null)
                    AllData.context = context.getApplicationContext();
                if (UserControlCenter.getUserMinInfo() != null && UserControlCenter.getUserMinInfo().eimUserData.isLaleAppEim) {
                    new Thread(() -> {
                        HttpReturn httpReturn = CloudUtils.iCloudUtils.reToken(new CallbackUtils.TimeoutReturn() {
                            @Override
                            public void Callback(IOException timeout) {
                                StringUtils.HaoLog("reToken 網路異常");
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (AllData.activity != null){
                                        CommonUtils.showToast(AllData.activity,AllData.activity.getLayoutInflater(),AllData.activity.getString(R.string.network_anomaly),false);
                                    } else {
                                        StringUtils.HaoLog("AllData.activity 為 null，無法顯示 Toast");
                                    }
                                });
                            }
                        });
                        StringUtils.HaoLog("isReToken=" + httpReturn.status);
                    }).start();

                }
            }
        }, intentFilter);
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setAction(TEST_ACTION);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 1000, AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    private void rejectCall(Context context, Intent intent){
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            int id = intent.getExtras().getInt("id");
            notificationManager.cancel(id);//取消來電
            String room_id = intent.getStringExtra("messageInfo_room_id");
            String eventId = intent.getStringExtra("messageInfo_eventId");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MsgControlCenter.sendRejectRequest(room_id,eventId);
                }
            }).start();
    }

    private void acceptCall(Context context, Intent intent){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int id = intent.getExtras().getInt("id");
        notificationManager.cancel(id);
        String room_id = intent.getStringExtra("messageInfo_room_id");
        String eventId = intent.getStringExtra("messageInfo_eventId");
        callMessageInfo = (MessageInfo) intent.getSerializableExtra("MessageInfo");

        boolean isGroup = false;
        String roomName = "";
        if (AllData.context != null) {
            RoomMinInfo roomMinInfo = AllData.getRoomMinInfo(room_id);
            if (roomMinInfo != null) {
                isGroup = roomMinInfo.isGroup();
                roomName = roomMinInfo.name;
            }
        }
        MsgControlCenter.sendApplyRequest(room_id, eventId);
        ActivityUtils.gotoWebJitisiMeet(context, UserControlCenter.getUserMinInfo().displayName,
                UserControlCenter.getUserMinInfo().userId,
                UserControlCenter.getUserMinInfo().avatarThumbnailUrl,
                UserControlCenter.getUserMinInfo().token, UserControlCenter.getUserMinInfo().externalServerSetting.mqttUrl,
                UserControlCenter.getUserMinInfo().externalServerSetting.jitsiServerUrl, callMessageInfo.getCallRequest().type, eventId, room_id, roomName, isGroup
        );
    }
}