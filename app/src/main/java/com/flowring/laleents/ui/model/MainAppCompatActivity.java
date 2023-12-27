package com.flowring.laleents.ui.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.flowring.laleents.R;
import com.flowring.laleents.model.msg.MessageInfo;
import com.flowring.laleents.model.msg.MessageItem;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.DialogUtils;
import com.flowring.laleents.tools.SharedPreferencesUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.phone.DefinedUtils;
import com.flowring.laleents.tools.phone.LocalBroadcastControlCenter;
import com.flowring.laleents.tools.phone.MultilingualControlCenter;
import com.flowring.laleents.ui.widget.dialog.DialogWait;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainAppCompatActivity extends AppCompatActivity {


    //websocket 配置
    public CallbackUtils.ActivityReturn activityReturn;
    public ActivityResultLauncher ActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<androidx.activity.result.ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (activityReturn != null)
                        activityReturn.Callback(result);
                }
            }
    );
//    protected String[] BroadcastMessageIds = new String[]{"test", Intent.ACTION_SEND_MULTIPLE, Intent.ACTION_SEND, LocalBroadcastControlCenter.ACTION_NOTIFI_AF, LocalBroadcastControlCenter.ACTION_MQTT_Error};
    protected BroadcastReceiver FireBaseMsgBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case LocalBroadcastControlCenter.ACTION_NOTIFI_AF:
                    MessageInfo messageInfo = null;
                    //收到辦公類型通知
                    try {
                        messageInfo = new MessageInfo(new JSONObject(intent.getStringExtra("data")));
                        MessageItem.ReCardInfo reCardInfo = messageInfo.getMessage(new HashMap<>()).getCard();
                        ArrayList<String> stringTaskArray = new ArrayList<>();
                        stringTaskArray.add(reCardInfo.getAvatarUrl());
                        stringTaskArray.add(reCardInfo.getUserName());
                        stringTaskArray.add(reCardInfo.notifyType);
                        stringTaskArray.add("" + reCardInfo.priority);
                        stringTaskArray.add(reCardInfo.keyword);
                        stringTaskArray.add(reCardInfo.processName);
                        stringTaskArray.add(reCardInfo.rootUserName);
                        stringTaskArray.add(reCardInfo.duedate);
                        stringTaskArray.add(reCardInfo.url);
                        ArrayList<String> formList = new ArrayList<>();
                        formList.addAll(intent.getStringArrayListExtra("formList"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case LocalBroadcastControlCenter.ACTION_MQTT_FRIEND: //收到好友邀請
                    String user_id = intent.getStringExtra("user_id");
                    String user_name = intent.getStringExtra("user_name");
                    String user_avatar_url = intent.getStringExtra("user_avatar_url");
                    break;
                case LocalBroadcastControlCenter.ACTION_MQTT_Error: //通訊連線異常
                    //DialogUtils.showDialogMessage(MainAppCompatActivity.this, getString(R.string.server_connection_abnormality));
                    break;
            }
        }
    };

    protected BroadcastReceiver shareActivityBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case Intent.ACTION_SEND:
                    StringUtils.HaoLog("ShareActivity ACTION_SEND");
                    break;
                case Intent.ACTION_SEND_MULTIPLE:
                    StringUtils.HaoLog("ShareActivity ACTION_SEND_MULTIPLE");
                    break;
            }
        }
    };

    protected IntentFilter itFilter = new IntentFilter();
    protected IntentFilter itShareActivityFilter = new IntentFilter();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itFilter.addAction(DefinedUtils.ACTION_FIREBASE_MESSAGE);
        itFilter.addAction(DefinedUtils.ACTION_FRIEND_INVITE);
        itShareActivityFilter.addAction(Intent.ACTION_SEND);
        itShareActivityFilter.addAction(Intent.ACTION_SEND_MULTIPLE);
        itFilter.addAction("test");
        //保持亮起
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //設定語系
        String language = SharedPreferencesUtils.getLanguageChoice(MainAppCompatActivity.this);
        MultilingualControlCenter.setLocaleForMainAppCompat(MainAppCompatActivity.this,language);

    }

    @Override
    protected void onPause() {
        //跳好友通邀請通知訊息

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean screenshotsProhibited = SharedPreferencesUtils.getScreenshotForbidden(MainAppCompatActivity.this);
        screenshotEnable(screenshotsProhibited);
    }

    protected DialogWait dialogWait;

    public void cancelWait() {
        runOnUiThread(() -> {
            if (dialogWait != null) {
                dialogWait.dismiss();
                dialogWait = null;
            }
        });

    }

    public void showWait() {
        runOnUiThread(() -> {
            FragmentManager fm = getSupportFragmentManager();
            dialogWait = (DialogWait) fm.findFragmentByTag("DIALOG_UPLOAD_THEME_WAIT");
            if (dialogWait == null) {
                dialogWait = DialogWait.newInstanceForTimeout();
                dialogWait.show(fm, "DIALOG_UPLOAD_THEME_WAIT");
            }
        });

    }

    public void screenshotEnable(boolean enableScreenshots){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(enableScreenshots){
                    //不可以截圖
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
                } else {
                    //可以截圖
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
                }
            }
        });
    }


}
