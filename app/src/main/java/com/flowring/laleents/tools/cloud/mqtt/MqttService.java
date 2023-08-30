package com.flowring.laleents.tools.cloud.mqtt;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.flowring.laleents.R;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.phone.AllData;

public class MqttService extends Service {
    public static MqttControlCenter mqttControlCenter = null;
    String data;

    private void startForeground() {
        String channelId = null;
        // 8.0 以上需要特殊处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("flowring", "ForegroundService");
        } else {
            channelId = "";
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        Notification notification = builder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);
    }

    /**
     * 创建通知通道
     *
     * @param channelId
     * @param channelName
     * @return
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        NotificationManager service = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(chan);
        return channelId;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        StringUtils.HaoLog("onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        StringUtils.HaoLog("onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (AllData.context == null){
            AllData.context = getApplicationContext();
        }
        if (AllData.context != null && UserControlCenter.getUserMinInfo() != null) {
            if (UserControlCenter.getUserMinInfo().eimUserData.isLaleAppEim) {
                if (AllData.dbHelper == null){
                    AllData.initSQL(UserControlCenter.getUserMinInfo().userId);
                }
                new Thread(() -> {
                    StringUtils.HaoLog("onStart");
                    if (MqttService.mqttControlCenter == null){
                        MqttService.mqttControlCenter = new MqttControlCenter();
                    } else {
                        StringUtils.HaoLog("NewConnect");
                        MqttService.mqttControlCenter.NewConnect();
                    }
                }).start();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        data = intent.getStringExtra("data");
        StringUtils.HaoLog("onStartCommand " + data);
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        StringUtils.HaoLog("onDestroy");
//        handler.removeCallbacks(a);
    }
}