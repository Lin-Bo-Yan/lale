/*
 * Copyright @ 2019-present 8x8, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jitsi.meet.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.facebook.react.modules.core.PermissionListener;
import org.jitsi.meet.sdk.log.JitsiMeetLogger;
import org.jitsi.meet.sdk.tools.TimeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class JitsiMeetActivity extends AppCompatActivity
    implements JitsiMeetActivityInterface {

    protected static final String TAG = JitsiMeetActivity.class.getSimpleName();

    private static final String ACTION_JITSI_MEET_CONFERENCE = "org.jitsi.meet.CONFERENCE";
    private static final String JITSI_MEET_CONFERENCE_OPTIONS = "JitsiMeetConferenceOptions";
    private static final String ACTION_MQTT_MSG = "聊天室內訊息";
    private JitsiMeetView jitsiView;
    private String  roomId = "";
    private String  eventId = "";
    private boolean isGroupCall = false;
    private boolean isPipFinish = false;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceived(intent);
        }
    };

    /**
     * Instance of the {@link JitsiMeetView} which this activity will display.
     */

    public static void launch(Context context, JitsiMeetConferenceOptions options,String roomId,String eventId,boolean isGroupCall) {
        Intent intent = new Intent(context, JitsiMeetActivity.class);
        intent.setAction(ACTION_JITSI_MEET_CONFERENCE);
        intent.putExtra(JITSI_MEET_CONFERENCE_OPTIONS, options);
        intent.putExtra("roomId", roomId);
        intent.putExtra("eventId", eventId);
        intent.putExtra("isGroupCall", isGroupCall);
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public static void launch(Context context, String url) {
        JitsiMeetConferenceOptions options
            = new JitsiMeetConferenceOptions.Builder().setRoom(url).build();
        launch(context, options,"","",false);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Intent intent = new Intent("onConfigurationChanged");
        intent.putExtra("newConfig", newConfig);
        this.sendBroadcast(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        roomId = getIntent().getStringExtra("roomId");
        eventId = getIntent().getStringExtra("eventId");
        isGroupCall = getIntent().getBooleanExtra("isGroupCall",false);
        setContentView(R.layout.activity_jitsi_meet);
        this.jitsiView = findViewById(R.id.jitsiView);

        registerForBroadcastMessages();

        if (!extraInitialize()) {
            initialize();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        JitsiMeetActivityDelegate.onHostResume(this);
    }

    @Override
    public void onStop() {
        JitsiMeetActivityDelegate.onHostPause(this);
        Log.v("是pip離開的嗎？", String.valueOf(isPipFinish));
        if(isPipFinish){
            finish();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        // 這裡我們嘗試處理以下極端情況：使用 SDK 的應用程式
        // 正在使用此 Activity 來顯示會議，但還有另一個「主要」Activity
        // 與其他內容。如果此 Activity 從最近的清單中“刷出”，我們將得到
        // Activity#onDestroy() 呼叫時沒有警告。此時我們可以嘗試離開
        // 目前會議，但是當我們的視圖與 React 分離時，JS <-> Native 橋將不會
        // 可操作，因此外部 API 將無法通知本機端
        // 會議終止。因此，要盡力清理。
        leave();
        this.jitsiView = null;
        if (AudioModeModule.useConnectionService()) {
            ConnectionService.abortConnections();
        }
        JitsiMeetOngoingConferenceService.abort(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        JitsiMeetActivityDelegate.onHostDestroy(this);
        super.onDestroy();
    }

    @Override
    public void finish() {
        leave();
        super.finish();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        // 離開 pip 模式該值回傳false，進入 pip 模式該值回傳true
        isPipFinish = isInPictureInPictureMode;
    }

    protected JitsiMeetView getJitsiView() {
        return jitsiView;
    }

    public void join(@Nullable String url) {
        JitsiMeetConferenceOptions options
            = new JitsiMeetConferenceOptions.Builder()
            .setRoom(url)
            .build();
        join(options);
    }

    public void join(JitsiMeetConferenceOptions options) {
        if (this.jitsiView  != null) {
            TimeUtils.startCallHeartbeat(options.getToken(), options.getUserId(), String.valueOf(options.getServerURL()));
            this.jitsiView.join(options);
        } else {
            JitsiMeetLogger.w("Cannot join, view is null");
        }
    }

    protected void leave() {
        if (this.jitsiView != null) {
            TimeUtils.endCallHeartbeat();
            this.jitsiView.abort();
        } else {
            JitsiMeetLogger.w("Cannot leave, view is null");
        }
    }

    private JitsiMeetConferenceOptions getConferenceOptions(Intent intent) {
        String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {
                return new JitsiMeetConferenceOptions.Builder().setRoom(uri.toString()).build();
            }
        } else if (ACTION_JITSI_MEET_CONFERENCE.equals(action)) {
            return intent.getParcelableExtra(JITSI_MEET_CONFERENCE_OPTIONS);
        }

        return null;
    }

    /**
     * Helper function called during activity initialization. If {@code true} is returned, the
     * initialization is delayed and the {@link JitsiMeetActivity#initialize()} method is not
     * called. In this case, it's up to the subclass to call the initialize method when ready.
     * <p>
     * This is mainly required so we do some extra initialization in the Jitsi Meet app.
     *
     * @return {@code true} if the initialization will be delayed, {@code false} otherwise.
     */
    protected boolean extraInitialize() {
        return false;
    }

    protected void initialize() {
        // 加入啟動應用程式時使用的 URL 指定的房間。
        // 在沒有房間選項的情況下加入會顯示歡迎頁面。
        join(getConferenceOptions(getIntent()));
    }

    protected void onConferenceJoined(HashMap<String, Object> extraData) {
        JitsiMeetLogger.i("Conference joined: " + extraData);
        // 啟動持續通知的服務。
        JitsiMeetOngoingConferenceService.launch(this, extraData);
    }

    protected void onConferenceTerminated(HashMap<String, Object> extraData) {
        JitsiMeetLogger.i("Conference terminated: " + extraData);
        //這裡是通話離開時
    }

    protected void onConferenceWillJoin(HashMap<String, Object> extraData) {
        JitsiMeetLogger.i("Conference will join: " + extraData);
    }

    protected void onParticipantJoined(HashMap<String, Object> extraData) {
        try {
            JitsiMeetLogger.i("Participant joined: ", extraData);
        } catch (Exception e) {
            JitsiMeetLogger.w("Invalid participant joined extraData", e);
        }
    }

    protected void onParticipantLeft(HashMap<String, Object> extraData) {
        try {
            JitsiMeetLogger.i("Participant left: ", extraData);
        } catch (Exception e) {
            JitsiMeetLogger.w("Invalid participant left extraData", e);
        }
    }

    protected void onReadyToClose() {
        JitsiMeetLogger.i("SDK is ready to close");
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        JitsiMeetActivityDelegate.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        JitsiMeetActivityDelegate.onBackPressed();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        JitsiMeetConferenceOptions options;

        if ((options = getConferenceOptions(intent)) != null) {
            join(options);
            return;
        }

        JitsiMeetActivityDelegate.onNewIntent(intent);
    }

    @Override
    protected void onUserLeaveHint() {
        if (this.jitsiView  != null) {
            this.jitsiView .enterPictureInPicture();
        }
    }

    @Override
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        JitsiMeetActivityDelegate.requestPermissions(this, permissions, requestCode, listener);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void registerForBroadcastMessages() {
        IntentFilter intentFilter = new IntentFilter();

        for (BroadcastEvent.Type type : BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.getAction());
        }
        intentFilter.addAction(ACTION_MQTT_MSG);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }
    public void checkJsonAndExecute(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString.replace("data=","data:"));

            if (jsonObject.has("data")) {
                JSONObject data = jsonObject.getJSONObject("data");

                String type = data.optString("type");
                String roomId = data.optString("roomId");
                String id = data.optString("id");
                if ("lale.call.left.request".equals(type) &&
                      this.roomId.equals(roomId) &&
                       this.eventId.equals(id)) {
                    JSONObject content = data.optJSONObject("content");
                    if (content != null) {
                        String result = content.optString("result");
                        if ("left".equals(result) && !isGroupCall) {
                            finish();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void onBroadcastReceived(Intent intent) {
        if (intent != null) {
            BroadcastEvent event = new BroadcastEvent(intent);
            if(event.getType()!=null){
                switch (event.getType()) {
                    case CONFERENCE_JOINED:
                        onConferenceJoined(event.getData());
                        break;
                    case CONFERENCE_WILL_JOIN:
                        onConferenceWillJoin(event.getData());
                        break;
                    case CONFERENCE_TERMINATED:
                        onConferenceTerminated(event.getData());
                        break;
                    case PARTICIPANT_JOINED:
                        onParticipantJoined(event.getData());
                        break;
                    case PARTICIPANT_LEFT:
                        onParticipantLeft(event.getData());
                        break;
                    case READY_TO_CLOSE:
                        onReadyToClose();
                        break;
                }
            } else if ( event.getData() != null){
                checkJsonAndExecute(event.getData().toString());
            }
        }
    }
}
