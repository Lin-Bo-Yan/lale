package com.flowring.laleents.ui.widget.jitsiMeet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.flowring.laleents.R;
import com.flowring.laleents.model.msg.MessageInfo;
import com.flowring.laleents.model.msg.MsgControlCenter;
import com.flowring.laleents.model.room.RoomMinInfo;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.tools.ActivityUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.tools.phone.LocalBroadcastControlCenter;
import com.flowring.laleents.ui.model.MainAppCompatActivity;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class WaitAnswerActivity extends MainAppCompatActivity {
    public LinearLayout end;
    public LinearLayout ok;
    public TextView video_user_caller_name;
    public TextView video_user_caller_txst;
    MessageInfo MessageInfo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_answer);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        MessageInfo = (MessageInfo) getIntent().getSerializableExtra("MessageInfo");


        FireBaseMsgBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                StringUtils.HaoLog("收到訊息");
                if (action.equals(LocalBroadcastControlCenter.ACTION_MQTT_CALL_MSG)) {
                    StringUtils.HaoLog("收到訊息 ACTION_MQTT_CALL_MSG");
                    try {
                        MessageInfo messageInfo = new MessageInfo(new JSONObject(intent.getStringExtra("data")));
                        StringUtils.HaoLog("收到訊息 messageInfo=" + messageInfo.getCallRequest().requestEventId);
                        if (MessageInfo.id.equals(messageInfo.getCallRequest().requestEventId)) {
                            StringUtils.HaoLog("收到訊息 result=" + messageInfo.content);
                            if (messageInfo.getCallRequest().result != null) {
                                if (messageInfo.sender.equals(UserControlCenter.getUserMinInfo().userId)) {
                                    finish();
                                } else {
                                    if (messageInfo.getCallRequest().result.equals("cancel") || messageInfo.getCallRequest().result.equals("timeout"))
                                        finish();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        itFilter.addAction(LocalBroadcastControlCenter.ACTION_MQTT_CALL_MSG);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(FireBaseMsgBroadcastReceiver, itFilter); //註冊廣播接收器
        if (AllData.context == null) {
            AllData.context = getApplicationContext();
        }

        if(AllData.activity == null){
            AllData.activity = WaitAnswerActivity.this;
        }
        if (AllData.context == null){
            finish();
        }
        new Thread(() -> {
            RoomMinInfo roomMinInfo = AllData.getRoomMinInfoNoNull(MessageInfo.room_id);
            UserControlCenter.getOrgtreeuserimage();
            runOnUiThread(() -> {
                if (roomMinInfo == null)
                    finish();
                StringUtils.HaoLog("roomMinInfo=" + new Gson().toJson(roomMinInfo));

                video_user_caller_txst = findViewById(R.id.video_user_caller_txst);
                video_user_caller_name = findViewById(R.id.video_user_caller_name);
                StringUtils.HaoLog("MessageInfo.getCallRequest().type=" + MessageInfo.getCallRequest().type);
                video_user_caller_txst.setText("Lale " + (MessageInfo.getCallRequest().type.equals("audio") ? "語音" : "視訊") + "...");
                video_user_caller_name.setText(roomMinInfo.name);
                end = findViewById(R.id.end);
                findViewById(R.id.endButton).setOnClickListener(view -> {

                    MsgControlCenter.sendRejectRequest(roomMinInfo.id, MessageInfo.id);
                    finish();
                });
                end.setOnClickListener(view -> {
                    MsgControlCenter.sendRejectRequest(roomMinInfo.id, MessageInfo.id);
                    finish();
                });

                ok = findViewById(R.id.ok);
                ok.setOnClickListener(view -> {
                    ActivityUtils.gotoWebJitisiMeet(this, UserControlCenter.getUserMinInfo().displayName,
                            UserControlCenter.getUserMinInfo().userId,
                            UserControlCenter.getUserMinInfo().avatarThumbnailUrl,
                            UserControlCenter.getUserMinInfo().token, UserControlCenter.getUserMinInfo().externalServerSetting.mqttUrl,
                            UserControlCenter.getUserMinInfo().externalServerSetting.jitsiServerUrl, MessageInfo.getCallRequest().type, MessageInfo.id, roomMinInfo.id, roomMinInfo.name, roomMinInfo.isGroup()
                    );
                    MsgControlCenter.sendApplyRequest(roomMinInfo.id, MessageInfo.id);
                    finish();
                });
                findViewById(R.id.call_light).setOnClickListener(view -> {

                    ActivityUtils.gotoWebJitisiMeet(this, UserControlCenter.getUserMinInfo().displayName,
                            UserControlCenter.getUserMinInfo().userId,
                            UserControlCenter.getUserMinInfo().avatarThumbnailUrl,
                            UserControlCenter.getUserMinInfo().token, UserControlCenter.getUserMinInfo().externalServerSetting.mqttUrl,
                            UserControlCenter.getUserMinInfo().externalServerSetting.jitsiServerUrl, MessageInfo.getCallRequest().type, MessageInfo.id, roomMinInfo.id, roomMinInfo.name, roomMinInfo.isGroup()
                    );
                    MsgControlCenter.sendApplyRequest(roomMinInfo.id, MessageInfo.id);
                    finish();
                });

            });
        }).start();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MsgControlCenter.stopRing();
    }
}
