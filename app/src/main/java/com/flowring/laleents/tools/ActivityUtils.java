package com.flowring.laleents.tools;


import static com.flowring.laleents.tools.UiThreadUtil.runOnUiThread;

import android.content.Context;
import android.content.Intent;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;

import com.flowring.laleents.model.msg.MsgControlCenter;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.model.user.UserInfo;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.ui.main.webBody.MainWebActivity;
import com.flowring.laleents.ui.main.webBody.WebViewActivity;
import com.flowring.laleents.ui.widget.jitsiMeet.WebJitisiMeetActivity;
import com.flowring.laleents.ui.widget.qrCode.ScanCaptureActivity;

import org.json.JSONObject;


public class ActivityUtils {

    static public void gotoWebViewActivity(Context context, String url) {
        runOnUiThread(() -> {
            MsgControlCenter.stopRing();
            Intent intent = new Intent(AllData.context, WebViewActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("url", url);
            context.startActivity(intent);
        });
    }
    static public void gotoMainWebActivity(Context context)
    {
        Intent intent = new Intent(AllData.context, MainWebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AllData.context.getApplicationContext().startActivity(intent);
    }
    static public void gotoWebJitisiMeet(Context context, String displayName,
                                         String userId,
                                         String avatar,
                                         String laleToken,
                                         String mqttHost, String jitsiDomain, String callType, String msgId, String roomId, String roomName, boolean isGroupCall) {
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

    static public void gotoQRcode(ComponentActivity activity, ScanCaptureActivity.ScanCaptureType type, ActivityResultLauncher RcodeResult) {
        Intent intent = new Intent((Context) activity, ScanCaptureActivity.class);
        intent.putExtra("ScanCaptureType", type);
        RcodeResult.launch(intent);
    }

    public static void goFileReaderActivity(Context context, String url, JSONObject data){
        String googleDocs = "http://docs.google.com/gview?embedded=true&url=";
//        runOnUiThread(() -> {
//            Intent intent = new Intent(context, FileReaderActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putExtra("url", googleDocs + url);
//            intent.putExtra("jsonData",data.toString());
//            context.startActivity(intent);
//        });
    }
}
