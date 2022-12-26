package com.flowring.laleents.tools.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LocalBroadcastControlCenter {

    public static final String ACTION_NOTIFI_AF = "收到到關通知";
    public static final String ACTION_MQTT_FRIEND = "收到好友邀請";
    public static final String ACTION_MQTT_ROOM = "收到房間修改";
    public static final String ACTION_MQTT_MSG = "聊天室內訊息";
    public static final String ACTION_MQTT_CALL_REQUEST = "聊天室內視訊通知請求";
    public static final String ACTION_MQTT_CALL_MSG = "聊天室內視訊通知回復";
    public static final String ACTION_MQTT_Error = "通訊連線異常";
//    public static final String ACTION_MQTT_CALL = "視訊通話訊息";

    static public void send(Context context, String MessageId, String data) {
        Intent intent = new Intent(MessageId); //設定廣播識別碼
        intent.putExtra("data", data);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent); //發送廣播訊息
    }

    static public void registerReceiver(Context context, String[] MessageIds, BroadcastReceiver FireBaseMsgBroadcastReceiver) {
        IntentFilter itFilter = new IntentFilter();
        for (int i = 0; i < MessageIds.length; i++) {
            itFilter.addAction(MessageIds[i]);
        }

        LocalBroadcastManager.getInstance(context).registerReceiver(FireBaseMsgBroadcastReceiver, itFilter); //註冊廣播接收器
    }

    static public void unregisterReceiver(Context context, BroadcastReceiver FireBaseMsgBroadcastReceiver) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(FireBaseMsgBroadcastReceiver); //解除註冊廣播接收器
    }

}
