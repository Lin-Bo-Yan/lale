package com.flowring.laleents.tools;

import static com.flowring.laleents.tools.UiThreadUtil.runOnUiThread;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.flowring.laleents.model.msg.MsgControlCenter;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.model.user.UserInfo;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.ui.main.webBody.FileReaderActivity;
import com.flowring.laleents.ui.main.webBody.MainWebActivity;
import com.flowring.laleents.ui.main.webBody.WebViewActivity;
import com.flowring.laleents.ui.widget.jitsiMeet.WebJitisiMeetActivity;
import com.flowring.laleents.ui.widget.qrCode.ScanCaptureActivity;
import org.json.JSONObject;
import org.jitsi.meet.sdk.BroadcastEvent;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.log.StringUtils;
import java.net.MalformedURLException;
import java.net.URL;


public class ActivityUtils {

    static String msgIdStatic = "";
    static String roomIdStatic = "";

    static public void gotoWebViewActivity(Activity activity, String url, ActivityResultLauncher<Intent> recodeResult) {
        runOnUiThread(() -> {
            MsgControlCenter.stopRing();
            Intent intent = new Intent(activity, WebViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("url", url);
            recodeResult.launch(intent);
        });
    }

    static public void gotoMainWebActivity(Context context) {
        Intent intent = new Intent(AllData.context, MainWebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AllData.context.getApplicationContext().startActivity(intent);
    }
    static private  BroadcastReceiver broadcastReceiver ;
    static public void gotoWebJitisiMeet(Context context, String displayName,
                                         String userId,
                                         String avatar,
                                         String laleToken,
                                         String mqttHost, String jitsiDomain, String callType, String msgId, String roomId, String roomName, boolean isGroupCall
    ) {
        //判斷lale server是不是新版
        if(true){
            Bundle bundle = new Bundle();
            bundle.putString("displayName", displayName);
            bundle.putString("email", "your.email@example.com");
            bundle.putString("avatar", avatar);

            JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo(bundle);
            MsgControlCenter.stopRing();
            JitsiMeetConferenceOptions options;
            String roomIdParse = roomId.replace("room_", ""); //取 'room_' 後面數字
            String msgIdParse = msgId.replace("event_", ""); //取 'event_' 後面數字
            String roomSecret = String.format("%s%s",roomIdParse,msgIdParse);// 組成房間獨立 code
            msgIdStatic = msgId;
            roomIdStatic = roomId;
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent != null) {
                        BroadcastEvent event = new BroadcastEvent(intent);
                        if(event.getType() != null){
                            switch (event.getType()){
                                case READY_TO_CLOSE: // 自己關閉會議室，
                                    MsgControlCenter.sendEndRequest(roomIdStatic,msgIdStatic);
                                    LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
                                    broadcastReceiver = null ;
                                    msgIdStatic = "";
                                    roomIdStatic = "";
                                    break;
                            }
                        }
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();

            for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
                intentFilter.addAction(type.getAction());
            }
            if (broadcastReceiver!= null) {
                LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, intentFilter);
            }
            try {
                options = new JitsiMeetConferenceOptions.Builder()
                        .setServerURL(new URL(jitsiDomain))
                        .setFeatureFlag("pip.enabled",  true)
                        .setFeatureFlag("welcomepage.enabled",  false)
                        .setFeatureFlag("readOnlyName",  true)
                        .setFeatureFlag("prejoinpage.enabled",  false)
                        .setFeatureFlag("meeting-name.enabled",  true)
                        .setFeatureFlag("raise-hand.enabled",  false)
                        .setFeatureFlag("reactions.enabled",  false)
                        .setFeatureFlag("recording.enabled",  false)
                        .setAvatar(avatar)
                        .setUserInfo(userInfo)
                        .setCallType(callType)
                        .isGroupCall(String.valueOf(isGroupCall))
                        .setUserId(userId)
                        .setDisplayName(displayName)
                        .setRoomName(roomName)
                        .setRoom(roomSecret)
                        .setToken(laleToken)
                        .build();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            JitsiMeetActivity.launch(context,options,roomId,msgId,isGroupCall);
        } else {
            UserControlCenter.getMainUserInfo(new CallbackUtils.userReturn() {
                @Override
                public void Callback(UserInfo userInfo) {
                    runOnUiThread(() -> {
                        MsgControlCenter.stopRing();
                        Intent intent = new Intent(AllData.context, WebJitisiMeetActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (displayName == null || displayName.isEmpty()){
                            intent.putExtra("displayName", UserControlCenter.getUserMinInfo().displayName);
                        } else{
                            intent.putExtra("displayName", displayName);
                        }
                        intent.putExtra("userId", userId);
                        if (avatar == null || avatar.isEmpty()){
                            intent.putExtra("avatar", UserControlCenter.getUserMinInfo().avatarUrl);
                        } else {
                            intent.putExtra("avatar", avatar);
                        }
                        intent.putExtra("laleToken", laleToken);
                        intent.putExtra("mqttHost", mqttHost);
                        intent.putExtra("jitsiDomain", jitsiDomain);
                        intent.putExtra("callType", callType);
                        intent.putExtra("msgId", msgId);
                        intent.putExtra("roomId", roomId);
                        intent.putExtra("roomName", roomName);

                        intent.putExtra("messageDomain", UserControlCenter.getUserMinInfo().eimUserData.lale_external_server_info.messageServerUrl);
                        intent.putExtra("isGroupCall", isGroupCall);

                        AllData.context.getApplicationContext().startActivity(intent);
                    });
                }
            });
        }

    }

    static public void gotoQRcode(Activity activity, ScanCaptureActivity.ScanCaptureType type, ActivityResultLauncher<Intent> recodeResult) {
        Intent intent = new Intent(activity, ScanCaptureActivity.class);
        intent.putExtra("ScanCaptureType", type);
        recodeResult.launch(intent);
    }

    public static void goFileReaderActivity(Activity activity, String url, JSONObject data){
        String googleDocs = "http://docs.google.com/gview?embedded=true&url=";
        runOnUiThread(() -> {
            Intent intent = new Intent(activity, FileReaderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("url", googleDocs + url);
            intent.putExtra("jsonData",data.toString());
            activity.startActivity(intent);
        });
    }
}
