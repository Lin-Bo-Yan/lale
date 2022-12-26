package com.flowring.laleents.tools.phone;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.flowring.laleents.tools.cloud.mqtt.MqttService;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (MqttService.mqttControlCenter == null) {
            Intent intentServer = new Intent(context, MqttService.class);
            intentServer.putExtra("data", "new");
            context.startService(intentServer);
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
                        HttpReturn httpReturn = CloudUtils.iCloudUtils.reToken();
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
}