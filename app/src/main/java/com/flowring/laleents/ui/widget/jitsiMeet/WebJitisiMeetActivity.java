package com.flowring.laleents.ui.widget.jitsiMeet;

import static android.util.Base64.NO_WRAP;
import static com.flowring.laleents.tools.phone.AllData.context;
import static java.security.AccessController.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.KeyguardManager;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.PowerManager;
import android.util.Log;
import android.util.Rational;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.flowring.laleents.ui.model.MainAppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.flowring.laleents.R;
import com.flowring.laleents.model.msg.MsgControlCenter;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.tools.ActivityUtils;
import com.flowring.laleents.tools.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WebJitisiMeetActivity extends MainAppCompatActivity {


    PermissionRequest myRequest;

    private boolean checkPublishPermission() {
        List<String> permissions = new ArrayList<>();

        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(WebJitisiMeetActivity.this, Manifest.permission.CAMERA)) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(WebJitisiMeetActivity.this, Manifest.permission.RECORD_AUDIO)) {
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }


        if (permissions.size() != 0) {
            ActivityCompat.requestPermissions(WebJitisiMeetActivity.this, (String[]) permissions.toArray(new String[0]), 667);
            return false;
        }
        return true;
    }

    void cleanWebviewCache() {
        deleteDatabase("webview.db");
        deleteDatabase("webviewCache.db");

    }

    void setWebView(WebView webView, String url) {
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        if (webView == null)
            webView = new WebView(getApplicationContext());
        cleanWebviewCache();
        WebSettings webSettings = webView.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setTextZoom(100);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        WebChromeClient mWebChromeClient = new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {

                request.grant(request.getResources());
//                checkPublishPermission();
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                Log.e("hao", "onCreateWindow" + resultMsg);
//                WebView newWebView = new WebView(view.getContext());
//
//                newWebView.setWebViewClient(new WebViewClient());
//                newWebView.setWebChromeClient(this);
//                setWebView(newWebView, url);
//                view.addView(newWebView);
//                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
//                transport.setWebView(newWebView);
//                resultMsg.sendToTarget();
                return true;
            }

            // Android 5.0+
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                Log.e("hao", "onShowFileChooser");
                WebJitisiMeetActivity.this.webView = webView;
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                Log.e("hao", "onJsAlert");
                if (getContext() == null) {
                    return false;
                }
                AlertDialog.Builder b = new AlertDialog.Builder(WebJitisiMeetActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                Log.e("hao", "onJsConfirm");
                AlertDialog.Builder b = new AlertDialog.Builder(WebJitisiMeetActivity.this);
                b.setTitle("Confirm");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                b.create().show();
                return true;
            }

            @Override
            public void onGeolocationPermissionsHidePrompt() {
                super.onGeolocationPermissionsHidePrompt();

            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {

            }


        };
        webView.addJavascriptInterface(this, "laleIm");
        WebView.setWebContentsDebuggingEnabled(true);
        webView.setWebChromeClient(mWebChromeClient);
        webView.loadUrl(url);
    }

    WebView webView;

    void backtoActivity() {
        if (webView != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            if (webView.getParent() != null)
                ((ViewGroup) webView.getParent()).removeView(webView);
            ((ViewGroup) findViewById(R.id.all)).addView(webView, params);
        }
    }

    String url = "";
    private PictureInPictureParams.Builder mPictureInPictureParamsBuilder;

    private void lastPerson() {
        MsgControlCenter.sendAllEndRequest(getIntent().getStringExtra("roomId"), getIntent().getStringExtra("msgId"));
    }

    private void enterPiPMode() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mPictureInPictureParamsBuilder == null) {
                mPictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
            }
            Rational aspectRatio = new Rational(200, getIntent().getBooleanExtra("isGroupCall", false) ? 400 : 200);
            mPictureInPictureParamsBuilder.setAspectRatio(aspectRatio);
            //进入pip模式
            enterPictureInPictureMode(mPictureInPictureParamsBuilder.build());
            isInPictureInPictureMode = true;
        }
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        webView = new WebView(getApplicationContext());
        StringUtils.HaoLog("displayName=" + getIntent().getStringExtra("displayName"));
        StringUtils.HaoLog("callType=" + getIntent().getStringExtra("callType"));

        url = getUrl(getIntent().getStringExtra("displayName"),
                getIntent().getStringExtra("userId"),
                getIntent().getStringExtra("avatar"),
                getIntent().getStringExtra("laleToken"),
                getIntent().getStringExtra("mqttHost"),
                getIntent().getStringExtra("jitsiDomain"),
                getIntent().getStringExtra("callType"),
                getIntent().getStringExtra("msgId"),
                getIntent().getStringExtra("roomId"),
                getIntent().getStringExtra("roomName"),
                getIntent().getBooleanExtra("isGroupCall", false) ,   getIntent().getStringExtra("messageDomain")
                );
        if (checkPublishPermission()) {
            setWebView(webView, url);
            backtoActivity();
        }
//        windowManager.addView(webView, params);
     
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 667) {
            setWebView(webView, url);
            backtoActivity();
        }
    }

    String getUrl(String displayName, String userId, String avatar, String laleToken, String mqttHost, String jitsiDomain, String callType, String msgId, String roomId, String roomName, boolean isGroupCall,String messageDomain) {
        // web 需要的資訊

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("displayName", URLEncoder.encode(displayName, "UTF-8"));
            jsonObject.put("userId", userId);
            jsonObject.put("avatar", avatar);
            jsonObject.put("laleToken", laleToken);
            jsonObject.put("mqttHost", mqttHost);
            jsonObject.put("jitsiDomain", jitsiDomain.replace("/laleweb", ""));
            jsonObject.put("messageDomain", messageDomain);
            jsonObject.put("callType", callType);
            jsonObject.put("msgId", msgId);
            jsonObject.put("roomId", roomId);
            jsonObject.put("roomName", URLEncoder.encode(roomName, "UTF-8"));
            jsonObject.put("isGroupCall", isGroupCall);
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringUtils.HaoLog("jsonObject=" + jsonObject);

        // 轉換物件為 JSON 字串
        String userString = jsonObject.toString();
        String dataEncode = "";
        // 轉換資料為 token
        try {
            dataEncode = new String(android.util.Base64.encode(userString.getBytes("UTF-8"), NO_WRAP)).replaceAll("/", "%2F");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        // 建立提通給 jitsi 建立通話房間的獨立 code
        String roomIdParse = roomId.replace("room_", ""); //取 'room_' 後面數字
        String msgIdParse = msgId.replace("event_", ""); //取 'event_' 後面數字
        String roomSecret = roomIdParse + msgIdParse; // 組成房間獨立 code
        // 將以上資訊帶入 url 中開啟通話服務
        if (UserControlCenter.getUserMinInfo() != null && UserControlCenter.getUserMinInfo().eimUserData != null && !UserControlCenter.getUserMinInfo().eimUserData.call_service_url.isEmpty()) {
        StringUtils.HaoLog("有拿到callServiceUrl:"+UserControlCenter.getUserMinInfo().eimUserData.call_service_url + "/#/call/" + roomSecret + "/" + dataEncode);
            return UserControlCenter.getUserMinInfo().eimUserData.call_service_url + "/#/call/" + roomSecret + "/" + dataEncode;
        }
        {         StringUtils.HaoLog("沒有拿到callServiceUrl:");

            return  "https://agentflow.flowring.com:8443/appCall/#/call/" + roomSecret + "/" + dataEncode;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
//        windowManager.updateViewLayout(webView, params0);


    }

    @Override
    protected void onDestroy() {

        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
//            windowManager.removeView(webView);
            webView = null;
        }
        if(isLock())
        {
            ActivityUtils.gotoMainWebActivity(this);
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        windowManager.updateViewLayout(webView, params);
    }

    void close() {
        webView.evaluateJavascript("receiveAppMessage('disconnect')", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                Log.d("hao", "發訊息回傳:" + s);
            }
        });
    }

    void viewNormalize() {
        if (webView != null)
            webView.evaluateJavascript("receiveAppMessage('viewNormalize')", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    Log.d("hao", "發訊息回傳:" + s);
                }
            });
    }

    void maxAudio(boolean Speakerphone) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setMode(AudioManager.MODE_IN_CALL);
        am.setSpeakerphoneOn(Speakerphone);
    }

    @Override
    protected void onStop() {
        super.onStop();
        StringUtils.HaoLog("onStop=" + isInPictureInPictureMode);

            if (isInPictureInPictureMode||isLock()) {
                if (webView != null) {
                    webView.loadUrl("about:blank");
                    webView.destroy();
                    webView = null;
                    MsgControlCenter.sendEndRequest(getIntent().getStringExtra("roomId"), getIntent().getStringExtra("msgId"));
                }
                StringUtils.HaoLog("onStop ???=" + isInPictureInPictureMode);
                finish();
            }


    }



    boolean isInPictureInPictureMode = false;

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {

        if (isInPictureInPictureMode) {
            // Hide the full-screen UI (controls, etc.) while in picture-in-picture mode.
        } else {
            this.isInPictureInPictureMode = false;
            // Restore the full-screen UI.
            viewNormalize();
        }
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public void postMessage(String json) {

        Log.d("hao", "toAndroid: " + json);
        if (json == null || json.isEmpty())
            return;
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.isNull("command"))
                return;
            String command = jsonObject.optString("command");
            Log.d("hao", "命令 " + command);
            switch (command) {
                case "stop":
                    if (webView != null) {
                        runOnUiThread(() -> {
                            webView.loadUrl("about:blank");
                            MsgControlCenter.sendEndRequest(getIntent().getStringExtra("roomId"), getIntent().getStringExtra("msgId"));
                            finish();
                        });

                    }
                    break;
                case "last person":
                    lastPerson();
                case "zoom out":
                    enterPiPMode();
                    break;
                case "loudspeaker open":
                    maxAudio(true);
                    break;
                case "loudspeaker close":
                    maxAudio(false);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            Log.d("hao", "命令失敗");
            e.printStackTrace();
        }

    }

    public boolean isLock()
    {


        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean flag = mKeyguardManager.inKeyguardRestrictedInputMode();
StringUtils.HaoLog("flag="+flag);
        return flag;
    }
}