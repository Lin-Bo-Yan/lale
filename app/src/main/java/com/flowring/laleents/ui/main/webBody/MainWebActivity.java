package com.flowring.laleents.ui.main.webBody;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;
import com.flowring.laleents.R;
import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.explore.Microapp;
import com.flowring.laleents.model.msg.MsgControlCenter;
import com.flowring.laleents.model.notifi.SilenceNotifi;
import com.flowring.laleents.model.room.RoomControlCenter;
import com.flowring.laleents.model.room.RoomInfoInPhone;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.model.user.UserInfo;
import com.flowring.laleents.model.user.UserMin;
import com.flowring.laleents.tools.ActivityUtils;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.CommonUtils;
import com.flowring.laleents.tools.ThumbnailUtils;
import com.flowring.laleents.tools.DialogUtils;
import com.flowring.laleents.tools.DownloadUtils;
import com.flowring.laleents.tools.FileUtils;
import com.flowring.laleents.tools.FormatUtils;
import com.flowring.laleents.tools.SharedPreferencesUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.TimeUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.flowring.laleents.tools.cloud.mqtt.MqttService;
import com.flowring.laleents.tools.phone.AllData;
import com.flowring.laleents.tools.phone.BootBroadcastReceiver;
import com.flowring.laleents.tools.phone.DefinedUtils;
import com.flowring.laleents.tools.phone.LocalBroadcastControlCenter;
import com.flowring.laleents.tools.phone.PermissionUtils;
import com.flowring.laleents.tools.phone.ServiceUtils;
import com.flowring.laleents.ui.model.MainAppCompatActivity;
import com.flowring.laleents.ui.widget.qrCode.ScanCaptureActivity;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class MainWebActivity extends MainAppCompatActivity {
    //region  Permission
    AlertDialog requestDrawOverlaysDialog = null;

    public void checkPermission() {
        StringUtils.HaoLog("checkPermission");
        runOnUiThread(() -> {
            if (requestDrawOverlaysDialog != null) {
                requestDrawOverlaysDialog.dismiss();
                requestDrawOverlaysDialog = null;
            }
            requestDrawOverlaysDialog = PermissionUtils.requestDrawOverlays(this);
            if (!ServiceUtils.isIgnoringBatteryOptimizations(this)) {
                ServiceUtils.requestIgnoreBatteryOptimizations(this);
            }
            if (PermissionUtils.checkPermission(getApplicationContext(), Manifest.permission.FOREGROUND_SERVICE)) {
                StringUtils.HaoLog("可以使用背景執行");
            } else {
                PermissionUtils.requestPermission(MainWebActivity.this, Manifest.permission.FOREGROUND_SERVICE, "背景執行權限");
            }
            if (PermissionUtils.checkPermission(getApplicationContext(), Manifest.permission.RECEIVE_BOOT_COMPLETED)) {
                StringUtils.HaoLog("可以使用重啟");
            } else {
                StringUtils.HaoLog("詢問使用重啟");
                PermissionUtils.requestPermission(MainWebActivity.this, Manifest.permission.RECEIVE_BOOT_COMPLETED, "開機後重新啟動背景服務");
            }
            if (PermissionUtils.checkPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) || (Build.VERSION.SDK_INT > Build.VERSION_CODES.R)) {
                StringUtils.HaoLog("可以使用讀寫");
            } else {
                PermissionUtils.requestPermission(MainWebActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, "該功能需要下載權限");
            }
        });

    }

    private boolean checkContactaPermission() {
        boolean check = PermissionUtils.checkPermission(MainWebActivity.this,Manifest.permission.READ_CONTACTS);
        return check;
        //        boolean check = PermissionChecker.checkSelfPermission(MainWebActivity.this, Manifest.permission.READ_CONTACTS)
        //                == PermissionChecker.PERMISSION_GRANTED;
    }
/*
* 登入 - token 傳遞, 保持 token 更新避免 mqtt 斷線及 api auth 失效
呼叫token更新-ok
呼叫相機拍照-ok
呼叫相機相簿-ok
呼叫 QRCode 掃描, 需整理目前各 qrCode 用途及資料格式
呼叫分享功能
呼叫交友邀請(手機聯絡人, 簡訊, 郵件, Line, Wechat)
呼叫信箱傳送
呼叫下載
聊天室背景, 跟手機db取得?-ok
點擊系統通知-訊息資料如何傳遞開啟 app 並導向聊天室
點擊系統通知-接聽電話資料如何傳遞並開啟 app 進入通話
點擊系統通知-掛斷電話是否可以直接發送 mqtt 掛斷電話 (不開啟 app)
通話擴音功能 - web 是否可以切換為手機喇叭並調整音量
登出
* */


    //endregion

    //region  test
    final Handler handler = new Handler();
    Runnable log = new Runnable() {
        @Override
        public void run() {
            StringUtils.HaoLog("還活著 " + webView.hashCode());
            sendToWebtest();
            handler.postDelayed(log, 5000);

        }
    };

    public void testOpenChrome() {
        Intent intent = CommonUtils.openChromeCustomTabs(this, "https://portal.flowring.com/WebAgenda/");
        startActivityForResult(intent, DefinedUtils.REQUEST_CHROME_TAB);
    }

    public void testGoNewActivity() {
        handler.post(log);
        new Thread(() -> {
            try {
                Thread.sleep(10000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                ActivityUtils.gotoWebJitisiMeet(this, UserControlCenter.getUserMinInfo().displayName,
                        UserControlCenter.getUserMinInfo().userId,
                        UserControlCenter.getUserMinInfo().avatarThumbnailUrl,
                        UserControlCenter.getUserMinInfo().token, UserControlCenter.getUserMinInfo().externalServerSetting.mqttUrl,
                        UserControlCenter.getUserMinInfo().externalServerSetting.jitsiServerUrl, "video", "video", "video", "video", false
                );
            });
        }).start();
    }

    private void testDownloadFile(String url) {
        StringUtils.HaoLog("url=" + url);
        DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(
                    Uri.parse(url));
        } catch (IllegalArgumentException e) {
            StringUtils.HaoLog("downloadFile Error=" + e);
            try {
                sendToWeb(new JSONObject().put("type", "downloadFile").put("data", new JSONObject().put("isSuccess", false)).toString());
            } catch (JSONException e2) {
                StringUtils.HaoLog("sendToWeb Error=" + e2);
                e.printStackTrace();
            }
            return;
        }

        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        request.addRequestHeader("User-Agent", "");
        request.setDescription("Downloading File...");
        request.allowScanningByMediaScanner();

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HHmmss SSS");
        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, sdf.format(new Date().getDate()));
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        new Thread(() -> {
            dm.enqueue(request);
        }).start();


//        try {
//
//            sendToWeb(new JSONObject().put("type", "downloadFile").put("data", new JSONObject().put("isSuccess", true)).toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        StringUtils.HaoLog("DownloadManager=end");
    }
    //endregion

    private void initFireBaseMsgBroadcastReceiver() {
        FireBaseMsgBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                StringUtils.HaoLog("BroadcastReceiver=" + action + " " + intent.getStringExtra("data"));
                switch (action){
                    case LocalBroadcastControlCenter.ACTION_NOTIFI_AF:
                        String data = intent.getStringExtra("data");
                        if (data != null && data.contains("msgType") && (data.contains("AF_MEETING") || data.contains("AF_TASK"))){
                            try {
                                sendToWeb("Notification", new JSONObject(data));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (data != null && data.contains("userId") && (data.contains("command"))){
                            SilenceNotifi silenceNotifi = new Gson().fromJson(data, SilenceNotifi.class);
                            switch (silenceNotifi.command){
                                case "logout":
                                    StringUtils.HaoLog("設備被登出");
                                    Logout();
                                    SharedPreferencesUtils.isRepeatDevice(true);
                                    break;
                            }
                        }
                        break;
                    case LocalBroadcastControlCenter.ACTION_MQTT_FRIEND:
                        String user_id = intent.getStringExtra("user_id");
                        String user_name = intent.getStringExtra("user_name");
                        String user_avatar_url = intent.getStringExtra("user_avatar_url");
                        break;
                    case LocalBroadcastControlCenter.ACTION_MQTT_Error:
                        DialogUtils.showDialogMessage(MainWebActivity.this, "伺服器連線異常");
                        break;
                }
            }
        };
        itFilter.addAction(DefinedUtils.ACTION_FIREBASE_MESSAGE);
        itFilter.addAction(DefinedUtils.ACTION_FRIEND_INVITE);
        itFilter.addAction(LocalBroadcastControlCenter.ACTION_NOTIFI_AF);
        itFilter.addAction(LocalBroadcastControlCenter.ACTION_MQTT_FRIEND);
        itFilter.addAction(LocalBroadcastControlCenter.ACTION_MQTT_Error);
        LocalBroadcastManager.getInstance((Context) this).registerReceiver(FireBaseMsgBroadcastReceiver, itFilter); //註冊廣播接收器
    }

    private void initShareActivityBroadcastReceiver(){
        shareActivityBroadcastReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                StringUtils.HaoLog("ShareActivityBroadcastReceiver= " + action + " " + intent.getStringExtra("data"));
                switch (action){
                    case Intent.ACTION_SEND:
                        shareToWeb(intent);
                        StringUtils.HaoLog("ShareActivity ACTION_SEND");
                        break;
                    case Intent.ACTION_SEND_MULTIPLE:
                        multipleShareToWeb(intent);
                        StringUtils.HaoLog("ShareActivity ACTION_SEND_MULTIPLE");
                        break;
                }
            }
        };
        itShareActivityFilter.addAction(Intent.ACTION_SEND);
        itShareActivityFilter.addAction(Intent.ACTION_SEND_MULTIPLE);
        try {
            itShareActivityFilter.addDataType("*/*");
        }catch (IntentFilter.MalformedMimeTypeException e){
            StringUtils.HaoLog( e.toString());
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(shareActivityBroadcastReceiver, itShareActivityFilter); //註冊廣播接收器
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        initFireBaseMsgBroadcastReceiver();
        initShareActivityBroadcastReceiver();
        com.flowring.laleents.tools.Log.setContext(getApplicationContext());
        AllData.init(getApplicationContext());
        if (!init) {
            BootBroadcastReceiver.setReToken(getApplicationContext());
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkUpApp(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AllData.context == null){
            AllData.context = getApplicationContext();
        }
        UserMin userMin = UserControlCenter.getUserMinInfo();
        checkHasWebView();
        StringUtils.HaoLog("onResume= " + userMin);
        if (userMin != null && !userMin.userId.isEmpty()) {
            if(userMin.eimUserData.isLaleAppEim){
                new Thread(() -> {
                    censorToken();
                }).start();
//                UserControlCenter.wasLoggedOut(new CallbackUtils.deviceReturn() {
//                    @Override
//                    public void Callback(Boolean deviceReturn) {
//                        StringUtils.HaoLog("onResume= 重複登入? "+deviceReturn);
//                        if(deviceReturn){
//                            Logout();
//                            SharedPreferencesUtils.isRepeatDevice(true);
//                        }
//                    }
//                });
            }
        } else {
            goLogin();
        }
    }

    @Override
    protected void onDestroy() {
        StringUtils.HaoLog("onDestroy " + webView);
        LocalBroadcastControlCenter.unregisterReceiver(this,shareActivityBroadcastReceiver);
        LocalBroadcastControlCenter.unregisterReceiver(this,FireBaseMsgBroadcastReceiver);
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
            webView = null;
        }
        if (MqttService.mqttControlCenter != null)
            MqttService.mqttControlCenter.DisConnect();

        super.onDestroy();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == DefinedUtils.ACCESS_FINE_LOCATION_CODE)
        {
            try {
                JSONObject j = new JSONObject().put("type", "authorize").put("data", new JSONObject().put("type","location").put("isSuccess",grantResults[0]==PackageManager.PERMISSION_GRANTED));
                sendToWeb(j.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        StringUtils.HaoLog("onActivityResult=" + data);
        StringUtils.HaoLog("onActivityResult=" + resultCode);
        if (requestCode == DefinedUtils.FILE_CHOOSER_RESULT_CODE) {
            if (null == mUploadMessage) return;
            //如果沒選照片就設定收到的值為null，使下次可以再次選取
            if (resultCode != RESULT_OK) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
                return;
            }
            //若選擇拍照上傳data會是null
            Uri result = (data == null) ? null : data.getData();
            if (result == null) {
                if (data != null && data.getClipData() != null) {

                    ClipData clipData = data.getClipData();
                    if (clipData == null) {
                        mUploadMessage.onReceiveValue(null);
                        mUploadMessage = null;
                        return;
                    }
                    //取得相簿選取的相片
                    uploadImages(clipData);
                    return;
                } else {
                    if (TextUtils.isEmpty(currentPhotoPath)) {
                        mUploadMessage.onReceiveValue(null);
                        mUploadMessage = null;
                        return;
                    }
                    //取得拍照上傳的相片
                    Uri uri = Uri.fromFile(new File(currentPhotoPath));
                    if (mUploadMessage != null) {
                        mUploadMessage.onReceiveValue(new Uri[]{uri});
                        mUploadMessage = null;
                    }
                    return;
                }
            }
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(new Uri[]{result});
                mUploadMessage = null;
            }
        }
        if (requestCode == DefinedUtils.REQUEST_IMAGE_PICKER) {
            ArrayList<Media> images;
            images = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            if (images != null) {
                // 宣告長度為0的陣列，做給 ValueCallback 讓他回傳，因為現在不用ValueCallback上傳檔案，但又需要重複開啟相簿
                Uri[] uriFake = new Uri[0];
                File[] files = new File[images.size()];
                for (int i = 0; i < images.size(); i++) {
                    Media media = images.get(i);
                    File picture = new File(media.path);
                    files[i] = picture;
                }
                // sendFileInfo 通知選擇結果
                sendFileInfo(files);
                // sendFile 逐筆回傳上傳結果
                recursiveUpload(DefinedUtils.roomId, files, 0);

                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(uriFake);
                    mUploadMessage = null;
                }
            }
        }
        if (requestCode == DefinedUtils.REQUEST_CHROME_TAB) {
            if (resultCode == Activity.RESULT_CANCELED) {
                closeNativeBrowser();
            }
        }
    }
    public void checkAppNeedUpdate() {
        new Thread(() -> {
            Boolean appNeedUpdate = CloudUtils.iCloudUtils.checkAppNeedUpdate();
            if (appNeedUpdate)
                runOnUiThread(() -> {
                    showUpgradeDialog();
                });
        }).start();
    }

    public void showUpgradeDialog() {
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder((Context) this)
                        .setMessage(getString(R.string.update_app_text))
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            dialog.dismiss();
                            CloudUtils.iCloudUtils.gotoGooglePlay(MainWebActivity.this);
                            finish();
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, i) -> {
                            dialog.dismiss();
                            finish();
                        })
                        .setCancelable(false);
        alertDialogBuilder.show();
    }

    //region   WebView
    void checkHasWebView() {
        StringUtils.HaoLog("checkHasWebView webView=" + webView);
        if (webView == null) {
            StringUtils.HaoLog("init webView");
            webView = new MyWebView(this);
            setWebView(webView, getMainWebURL());
            backtoActivity();
        }
    }


    public String getMainWebURL() {
        UserMin userMin = UserControlCenter.getUserMinInfo();
        if (userMin != null &&
                userMin.eimUserData != null &&
                userMin.eimUserData.af_url != null){
            return userMin.eimUserData.af_url + "/eimApp/index.html#/";
        }
        return "";
    }


    private ValueCallback<Uri[]> mUploadMessage;
    boolean init = false;

    MyWebView webView;
    private ViewGroup viewGroup = null;
    private View overlay = null;

    void cleanWebviewCache() {
        deleteDatabase("webview.db");
        deleteDatabase("webviewCache.db");
        //WebView 缓存文件
        File appCacheDir = new File(getFilesDir().getAbsolutePath() + "/webcache");
        File webviewCacheDir = new File(getCacheDir().getAbsolutePath() + "/webviewCache");

        //删除webview 缓存目录
        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir.getPath());
        }
        //删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir.getPath());
        }
    }

    private void chooseCAMERA() {
        //Intent開啟相簿
        Intent intentFile = new Intent(Intent.ACTION_GET_CONTENT);
        intentFile.addCategory(Intent.CATEGORY_OPENABLE);
        intentFile.setType("*/*");

        //Intent開啟相機
        Intent intentCamera = null;
        //判斷是否有載入儲存空間
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            if (!PermissionUtils.checkPermission(this, Manifest.permission.CAMERA)) {
                StringUtils.HaoLog("前往相機或相簿");
                PermissionUtils.requestPermission(this, Manifest.permission.CAMERA, "該功能需要相機權限");
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
                return;
            }
            intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.flowring.laleents.fileprovider", photoFile);
                intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }

        }
        //跳出選擇拍照或是相簿
        startActivityForResult(intentCamera, DefinedUtils.FILE_CHOOSER_RESULT_CODE);
    }

    private void chooseFile() {
        //Intent開啟相簿
        Intent intentFile = new Intent(Intent.ACTION_GET_CONTENT);
        intentFile.addCategory(Intent.CATEGORY_OPENABLE);
        intentFile.setType("*/*");

        //Intent開啟相機
        Intent intentCamera = null;
        //判斷是否有載入儲存空間
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {

            intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.flowring.laleents.fileprovider", photoFile);
                intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            }

        }


        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        //設定title，放入兩種intent
        chooser.putExtra(Intent.EXTRA_TITLE, "選擇添加項目");
        chooser.putExtra(Intent.EXTRA_INTENT, intentFile);
        if (intentCamera != null) {
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentCamera});
        }
        //跳出選擇拍照或是相簿
        startActivityForResult(chooser, DefinedUtils.FILE_CHOOSER_RESULT_CODE);
    }

    private void chooseFileOnlyForRoom(){
        Intent intentFile = new Intent(Intent.ACTION_GET_CONTENT);
        intentFile.addCategory(Intent.CATEGORY_OPENABLE);
        intentFile.setType("*/*");
        startActivityForResult(intentFile, DefinedUtils.FILE_CHOOSER_RESULT_CODE);
    }

    private String currentPhotoPath = "";

    //創建照片路徑
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File image = new File(getCacheDir(), imageFileName);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void uploadImages(ClipData clipData) {
        Uri[] uriList = new Uri[clipData.getItemCount()];
        for (int i = 0; i < clipData.getItemCount(); i++) {
            clipData.getItemCount();
            ClipData.Item item = clipData.getItemAt(i);
            StringUtils.HaoLog("item.getUri()=" + item.getUri());
            uriList[i] = item.getUri();
        }
        mUploadMessage.onReceiveValue(uriList);
        mUploadMessage = null;
    }

    File outputFile = null;
    String fileName = null;
    File[] outputFiles = null;
    void shareToWeb(Intent intent) {
        String type = intent.getType();
        String action = intent.getAction();
        Parcelable stream = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        StringUtils.HaoLog("BroadcastReceiver EXTRA_TEXT=" + intent.getStringExtra(Intent.EXTRA_TEXT));
        StringUtils.HaoLog("BroadcastReceiver EXTRA_STREAM=" + intent.getParcelableExtra(Intent.EXTRA_STREAM));
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            //從手機分享純文字，但txt檔案會被當成純文字，txt檔案exteaStream會有值
            if (type.startsWith("text/")) {
                if(stream != null){
                    StringUtils.HaoLog("txt檔分享");
                    Uri txtUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    fileName = FileUtils.getContentURIFileName(this,txtUri);
                    outputFile = FileUtils.getFilePathFromUri(this,txtUri,fileName);
                    try {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("onlyKey","hashcode");
                        jsonObject.put("mimeType",type);
                        jsonObject.put("name",fileName);
                        jsonObject.put("thumbnail","thumbnail");//縮圖
                        jsonArray.put(jsonObject);
                        sendToWeb(new JSONObject().put("type","gotoShare").put("data",jsonArray).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    StringUtils.HaoLog("文字分享");
                    plainText(intent);
                }
            }
            //從手機分享單張圖片
            else if (type.startsWith("image/")) {
                //需要下載權限
                Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                fileName = FileUtils.getContentURIFileName(this,imageUri);
                outputFile = FileUtils.getFilePathFromUri(this,imageUri,fileName);
                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("onlyKey","hashcode");
                    jsonObject.put("mimeType",type);
                    jsonObject.put("name",fileName);
                    jsonObject.put("thumbnail", ThumbnailUtils.resizeAndConvertToBase64(outputFile.getAbsolutePath(),50));//縮圖
                    jsonArray.put(jsonObject);
                    sendToWeb(new JSONObject().put("type","gotoShare").put("data",jsonArray).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (type.startsWith("video/")) {
                Uri videoUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                fileName = FileUtils.getContentURIFileName(this,videoUri);
                outputFile = FileUtils.getFilePathFromUri(this,videoUri,fileName);
                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("onlyKey","hashcode");
                    jsonObject.put("mimeType",type);
                    jsonObject.put("name",fileName);
                    jsonObject.put("thumbnail","thumbnail");//縮圖
                    jsonArray.put(jsonObject);
                    sendToWeb(new JSONObject().put("type","gotoShare").put("data",jsonArray).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //從手機其他檔案
            else {
                Uri fileUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                fileName = FileUtils.getContentURIFileName(this,fileUri);
                outputFile = FileUtils.getFilePathFromUri(this,fileUri,fileName);
                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("onlyKey","hashcode");
                    jsonObject.put("mimeType",type);
                    jsonObject.put("name",fileName);
                    jsonObject.put("thumbnail","thumbnail");//縮圖
                    jsonArray.put(jsonObject);
                    sendToWeb(new JSONObject().put("type","gotoShare").put("data",jsonArray).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void plainText(Intent intent){
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        try {
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mimeType","message");
            jsonObject.put("string",sharedText);
            jsonArray.put(jsonObject);
            sendToWeb(new JSONObject().put("type","gotoShare").put("data",jsonArray).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void multipleShareToWeb(Intent intent){
        JSONArray jsonArray = new JSONArray();
        String type = intent.getType();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String text = extras.getString(Intent.EXTRA_TEXT);
            ArrayList<Uri> uris = extras.getParcelableArrayList(Intent.EXTRA_STREAM);
            if(text != null && !text.isEmpty()){
                Pattern pattern = Pattern.compile("(?<=\\n\\n)[^\\n]+");
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("mimeType","message");
                        jsonObject.put("string",matcher.group());
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(uris != null){
                // 這裡就可以對 Uri 做處理了
                String[] fileNames = FileUtils.getContentURIFileNames(this,uris);
                outputFiles = FileUtils.getFilePathsFromUris(this,uris,fileNames);
                for(String fileName :fileNames){
                    int hashCode = fileName.hashCode();
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("onlyKey",String.valueOf(hashCode));
                        jsonObject.put("mimeType",type);
                        jsonObject.put("name",fileName);
                        jsonObject.put("thumbnail","thumbnail");//縮圖
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            try{
                //StringUtils.HaoLog("jsp postMessage:"+jsonArray);
                sendToWeb(new JSONObject().put("type","gotoShare").put("data",jsonArray).toString());
            } catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    void initOnMainWebPageFinished() {
        StringUtils.HaoLog("initOnMainWebPageFinished= tob 1"+init);
        if (!init) {
            init = true;
//            if (UserControlCenter.getUserMinInfo() != null && !UserControlCenter.getUserMinInfo().userId.isEmpty()) {
//                StringUtils.HaoLog("initOnMainWebPageFinished " + UserControlCenter.getUserMinInfo());
//                Login();
//
//            }
            if (getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_SEND)) {
                shareToWeb(getIntent());
            } else if (getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_SEND_MULTIPLE)) {
                multipleShareToWeb(getIntent());
            } else if (UserControlCenter.getUserMinInfo().eimUserData.isLaleAppEim){
                checkAppNeedUpdate();
            } else {
                checkUpApp(getIntent());
            }
        }
    }

    void checkUpApp(Intent intent) {

        if (init) {
            StringUtils.HaoLog("checkUpApp2 " +intent.hasExtra("isHome"));
            if (intent.getBooleanExtra("isHome", false)) {
                try {

                    JSONObject j = new JSONObject().put("type", "gotoWeb").put("data", new JSONObject().put("url",intent.getStringExtra("isHomeMICRO_APPurl")).put("title",intent.getStringExtra("isHomeMICRO_APPName")));
                    sendToWeb(j.toString());
                   intent.removeExtra("isHome");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (intent.getBooleanExtra("bFromPhone", false) &&intent.getStringExtra("Notification") != null) {
                try {
                    sendToWeb("Notification", new JSONObject(intent.getStringExtra("Notification")));
                   intent.removeExtra("bFromPhone");
                   intent.removeExtra("Notification");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    DownloadListener mWebDownloadListener = new DownloadListener() {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
            StringUtils.HaoLog("onDownloadStart url=" + url);
            StringUtils.HaoLog("onDownloadStart userAgent=" + userAgent);
            StringUtils.HaoLog("onDownloadStart contentDisposition=" + contentDisposition);
            StringUtils.HaoLog("onDownloadStart mimeType=" + mimeType);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
//            if (ContextCompat.checkSelfPermission(MainWebActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                    PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 50);
//            } else {
//                downloadFile(url, userAgent, contentDisposition, mimeType);
//            }
        }
    };

    @SuppressLint("JavascriptInterface")
    void setWebView(WebView webView, String url) {
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        if (webView == null){webView = new WebView(getApplicationContext());}
        webView.setVisibility(View.INVISIBLE);
        webView.setDownloadListener(mWebDownloadListener);
        cleanWebviewCache();
        WebSettings webSettings = webView.getSettings();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                findViewById(R.id.logo).setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);

                CookieManager cookieManager = CookieManager.getInstance();
                String CookieStr = cookieManager.getCookie(url);
                StringUtils.HaoLog("還活著 onPageFinished= " + url);
                StringUtils.HaoLog("監聽 CookieStr=  " + CookieStr);
                DefinedUtils.URL = url;
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                StringUtils.HaoLog("還活著 onReceivedError 請求超時= " + error.toString());
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                StringUtils.HaoLog("還活著 onReceivedHttpError getUrl= " + request.getUrl());
                StringUtils.HaoLog("還活著 onReceivedHttpError 請求的詳細資訊= " + errorResponse.getData());
                StringUtils.HaoLog("還活著 onReceivedHttpError 錯誤狀態碼= " + errorResponse.getStatusCode());
                final int MIN_ERROR_STATUS_CODE = 500;
                final int MAX_ERROR_STATUS_CODE = 600;
                if (errorResponse.getStatusCode() >= MIN_ERROR_STATUS_CODE
                        && errorResponse.getStatusCode() < MAX_ERROR_STATUS_CODE
                        && getMainWebURL().equals(request.getUrl().toString())) {
                    Logout();
                }
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                StringUtils.HaoLog("還活著 onReceivedSslError error=" + error.toString());

                StringUtils.HaoLog("還活著 SSL憑證過期" + error.toString());
                //handler.proceed();

                super.onReceivedSslError(view, handler, error);
            }
        });
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSupportZoom(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setTextZoom(100);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        WebChromeClient mWebChromeClient = new WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                StringUtils.HaoLog("webView_Log= "+consoleMessage.messageLevel());
                switch (consoleMessage.messageLevel()){
                    case ERROR://將error信息上報到服務端
                        StringUtils.HaoLog("webView_Log= " + " "+consoleMessage.message());
                        break;
                    case LOG:
                        StringUtils.HaoLog("webView_Log= " + " "+consoleMessage.message());
                        break;
                    case WARNING:
                        StringUtils.HaoLog("webView_Log= " + " "+consoleMessage.message());
                        break;
                }
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {

                String[]  PermissionRequest=request.getResources();
                for (int i = 0; i < PermissionRequest.length; i++) {
                    StringUtils.HaoLog("onPermissionRequest:"+PermissionRequest[i]);
                    if(PermissionRequest[i].equals("android.webkit.resource.AUDIO_CAPTURE"))
                    {
                        if(   PermissionUtils.checkPermission(MainWebActivity.this,"android.permission.RECORD_AUDIO"))
                        {
                        }else
                        {
                            runOnUiThread(()->{
                                requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 466);
                            });

                        }
                    }
                }
                request.grant(request.getResources());
            }

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                Log.e("hao", "onCreateWindow" + resultMsg);
                WebView newWebView = new WebView(view.getContext());

                newWebView.setWebViewClient(new WebViewClient());
                newWebView.setWebChromeClient(this);
                setWebView(newWebView, url);
                view.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }

            // Android 5.0+
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                StringUtils.HaoLog("onShowFileChooser");
                Intent intent = fileChooserParams.createIntent();

                MainWebActivity.this.webView = (MyWebView) webView;
                if (mUploadMessage != null){
                    mUploadMessage.onReceiveValue(null);
                }
                mUploadMessage = filePathCallback;
                String type = intent.getType();
                StringUtils.HaoLog("ddd= "+type);
                switch(type){
                    case "image/*":
                        if (fileChooserParams.isCaptureEnabled()) {
                            chooseCAMERA();
                        } else {
                            CommonUtils.choosePicture(MainWebActivity.this, intent.getBooleanExtra("android.intent.extra.ALLOW_MULTIPLE", false) ? 9 : 1, PickerConfig.PICKER_IMAGE_VIDEO, new CallbackUtils.APIReturn() {
                                @Override
                                public void Callback(boolean isok, String DataOrErrorMsg) {
                                    if (mUploadMessage != null) {
                                        mUploadMessage.onReceiveValue(null);
                                        mUploadMessage = null;
                                    }
                                }
                            });
                        }
                        break;
                    case "*/*":
                        String str = DefinedUtils.URL.substring(getMainWebURL().length());
                        Boolean isRoom = str.matches(DefinedUtils.CHATROOM);
                        if(isRoom){
                            StringUtils.HaoLog("onShowFileChooser "+"直接開檔案");
                            chooseFileOnlyForRoom();
                        } else {
                            StringUtils.HaoLog("onShowFileChooser "+"檔案選擇器");
                            chooseFile();
                        }
                        break;
                }
                return true;
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                Log.e("hao", "onJsAlert");
                if (getContext() == null) {
                    return false;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) MainWebActivity.this);
                builder.setTitle("Alert");
                builder.setMessage(message);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                Log.e("hao", "onJsConfirm");
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) MainWebActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage(message);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                });
                builder.create().show();
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
        webView.addJavascriptInterface(this, "LaleTon");
        webView.addJavascriptInterface(this, "FlowringLale");
        WebView.setWebContentsDebuggingEnabled(true);
        webView.setWebChromeClient(mWebChromeClient);
//        webView.loadData(testUrl, "text/html; charset=utf-8", "UTF-8");
        if (!init) {
            String roomInfo = getIntent().getStringExtra("roomInfo");
            boolean bFromPhone = getIntent().getBooleanExtra("bFromPhone", false);
            if(bFromPhone && roomInfo != null){
                String urlToLoad = bFromPhone && roomInfo != null ? getMainWebURL() + "chatroom/" + roomInfo : url;
                StringUtils.HaoLog("init setWebView 1 " + (init ? "true" : "false")+" urlToLoad= "+urlToLoad);
                StringUtils.HaoLog("init setWebView 1 " + url);
                StringUtils.HaoLog("init setWebView 1 " + roomInfo);
                StringUtils.HaoLog("init setWebView 1 " + bFromPhone);
                webView.loadUrl(urlToLoad);
                StringUtils.HaoLog("initOnMainWebPageFinished= tob 2"+init);
                init = true;
            } else {
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancelAll();
                webView.loadUrl(url);
            }
        } else {
            StringUtils.HaoLog("init setWebView 2");
            webView.loadUrl(url);
            StringUtils.HaoLog("init setWebView 3");
        }
    }

    void backtoActivity() {
        if (webView != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            if (webView.getParent() != null){
                ((ViewGroup) webView.getParent()).removeView(webView);
            }
            // 取得 viewGroup 的 ViewGroup，並加入 WebView 與 activity_call
            viewGroup = findViewById(R.id.all);
            viewGroup.addView(webView, params);
            overlay = getLayoutInflater().inflate(R.layout.activity_call, viewGroup, false);
            viewGroup.addView(overlay);
        }
    }

    private void webRendered(int second){
        StringUtils.HaoLog("webRendered= "+Thread.currentThread().getName());
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                LayoutTransition layoutTransition = new LayoutTransition();
                PropertyValuesHolder aniChan = PropertyValuesHolder.ofFloat("rotation",0,50,0);
                ObjectAnimator changeDis = ObjectAnimator.ofPropertyValuesHolder(this,aniChan);
                layoutTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING,changeDis);
                viewGroup.setLayoutTransition(layoutTransition);
                viewGroup.removeView(overlay);
                StringUtils.HaoLog("webRendered= "+Thread.currentThread().getName());
            }
        },second);
    }
    //endregion

    //region  postMessage
    @JavascriptInterface
    public String postMessage(String json) {
        StringUtils.HaoLog("jsp postMessage:" + TimeUtils.NowTimestamp() + "/" + json);
        if (json == null || json.isEmpty())
            return null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.isNull("command"))
                return json;
            String command = jsonObject.optString("command");
            JSONObject data = null;
            if (!jsonObject.isNull("data"))
                data = jsonObject.optJSONObject("data");

            switch (command) {
                case "NewUrl":
                    NewUrl(data);
                    break;
                case "authorize":
                    authorize(data);
                    break;
                case "feedback":
                    feedback();
                    break;
                case "openWebView":
                    openWebView(data);
                    break;
                case "openQRcode":
                    openQRcode(data);
                    break;
                case "openPhoneWebView":
                    openPhoneWebView(data);
                    break;
                case "openChrome":
                    openChrome(data);
                    break;
                case "getAddressBook":
                    getAddressBook();
                    break;
                case "newLinkApp":
                    newLinkApp(data);
                    break;
                case "Logout":
                    Logout();
                    break;
                case "downloadByUrl":
                    downloadByUrl(data);
                    break;
                case "downloadByUrls":
                    downloadByUrls(data);
                    break;
                case "downloadByBytesBase64":
                    downloadByBytesBase64(data);
                    break;
                case "tokenRefresh":
                    tokenRefresh();
                    break;
                case "getRoomBackground":
                    getRoomBackground(data);
                    break;
                case "setRoomBackground":
                    setRoomBackground(data);
                    break;
                case "share":
                    share(data);
                    break;
                case "updateUser":
                    updateUser();
                    break;
                case "updateRooms":
                    updateRooms();
                    break;
                case "webOk":
                    webOk(data);
                    break;
                case "getAPPVersion":
                    getAPPVersion();
                    break;
                case "quickLook":
                    classificationFileType(data);
                    break;
                case "webLog":
                    webLog(data);
                    break;
                case "webRendered":
                    webRendered(700);
                    break;
                case "webMessage":
                    break;
                case "shareFileToCloud":
                    shareFileToCloud(data);
                    break;
                case "getPhotoLibrary":
                    getPhotoLibrary(data);
                    break;
                default:
                    unDo(json);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void feedback() {

        ShareCompat.IntentBuilder sb = ShareCompat.IntentBuilder.from(this);
        String[] tos = {"lalereport@flowring.com"};
        sb.setEmailTo(tos);
        sb.setText("使用者Lale ID : " + UserControlCenter.getUserMinInfo().userId + "\n問題描述:");
        sb.setType("message/rfc822");


        Uri NuriForFile = FileProvider.getUriForFile(this, "com.flowring.laleents.fileprovider", com.flowring.laleents.tools.Log.mLogFile);
        sb.setStream(NuriForFile);
        sb.setSubject("問題回報");
        sb.startChooser();
        Toast.makeText((Context) this, "請選擇電子信箱進行傳送", Toast.LENGTH_LONG).show();

    }

    private void openWebView(JSONObject data) {
        ActivityUtils.gotoWebViewActivity(MainWebActivity.this, data.optString("url"));
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void downloadFile(String url, String fileName) {
        DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(
                    Uri.parse(url));
        } catch (IllegalArgumentException e) {
            StringUtils.HaoLog("downloadFile Error=" + e);
            try {

                sendToWeb(new JSONObject().put("type", "downloadFile").put("data", new JSONObject().put("isSuccess", false)).toString());
            } catch (JSONException e2) {
                StringUtils.HaoLog("sendToWeb Error=" + e2);
                e.printStackTrace();
            }
            return;
        }

        String cookies = CookieManager.getInstance().getCookie(url);

        URLConnection conection = null;
        try {
            conection = new URL(url).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        request.addRequestHeader("cookie", cookies);
        request.addRequestHeader("User-Agent", "");
        request.setDescription("Downloading File...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH mm ss SSS");
        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, fileName == null ? sdf.format(new Date().getDate()) + FileUtils.toExtension(conection.getContentType()) : fileName);
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(request);
        try {

            sendToWeb(new JSONObject().put("type", "downloadFile").put("data", new JSONObject().put("isSuccess", true)).toString());
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        StringUtils.HaoLog("DownloadManager=end");
    }

    private void downloadFile(String[] urls, String fileName, downloadByUrlsCallback back) {
        DownloadManager.Request request;

        if (urlsNew < urlsMax) {
            StringUtils.HaoLog("urlsNew:" + urlsNew + " urlsMax=" + urlsMax);
            try {
                String cookies = CookieManager.getInstance().getCookie(urls[urlsNew]);
                URLConnection conection = null;
                request = new DownloadManager.Request(
                        Uri.parse(urls[urlsNew]));
                conection = new URL(urls[urlsNew]).openConnection();
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", "");
                request.setDescription("Downloading File...");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH mm ss SSS");
                request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS, fileName == null ? sdf.format(new Date().getDate()) + FileUtils.toExtension(conection.getContentType()) : fileName);
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                urlsNew++;
                downloadFile(urls, fileName, back);
            } catch (IOException | IllegalArgumentException e) {
                e.printStackTrace();
                urlsNew++;
                downloadFile(urls, fileName, back);
                urlsError.add(urls[urlsNew]);
            }

        } else {

            String[] error = new String[urlsError.size()];
            urlsError.toArray(error);
            back.onEnd(urlsIsOk, error);
        }

    }

    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            //獲取軟件版本號，對應AndroidManifest.xml下android:versionName
            versionName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    private void getAPPVersion() {
        try {
            StringUtils.HaoLog("getAPPVersion= "+getVersionName(MainWebActivity.this));
            sendToWeb(new JSONObject().put("type", "getAPPVersion").put("data", new JSONObject().put("Version", getVersionName(MainWebActivity.this))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void classificationFileType(JSONObject data){
        String contentType = null;
        String fileName;
        String fileType = null;
        if(data.has("url")){
            try {
                fileName = data.getString("fileName");
                fileType = FileUtils.fileType(fileName);
                contentType = FileUtils.contentType(fileType);
            }catch (JSONException e){e.printStackTrace();}
        } else {StringUtils.HaoLog("資料不存在");}

        if(contentType != null && !contentType.isEmpty()){
            switch (contentType){
                case "data":
                    StringUtils.HaoLog("檔案不支援 "+fileType);
                    Intent intent = new Intent(getApplicationContext(),FileNotSupportedActivity.class);
                    intent.putExtra("JSONObject",data.toString());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return;
                default:
                    judgmentFileName(data,ActionType.openFile);
            }
        }
    }

    public enum  ActionType{
        download,openFile
    }

    //因為下載檔案都要判別檔案名稱是否空白，設計成共用
    private void judgmentFileName(JSONObject data, ActionType type){
        if (PermissionUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || (Build.VERSION.SDK_INT > Build.VERSION_CODES.R)) {
            String dataUrl = data.optString("url");
            if (data.isNull("fileName")){
                switch (type){
                    case download:
                        // downloadFile(dataUrl, null); // 原設計沒有開執行續
                        break;
                    case openFile:
                        ActivityUtils.goFileReaderActivity(this,dataUrl,data);
                        break;
                }
            } else {
                switch (type) {
                    case download:
                        //downloadFile(dataUrl, data.optString("fileName")); // 原設計沒有開執行續
                        break;
                    case openFile:
                        ActivityUtils.goFileReaderActivity(this,dataUrl,data);
                        break;
                }
            }
        } else {
            switch (type){
                case download:
                    try{
                        sendToWeb(new JSONObject().put("type", "downloadFile").put("data", new JSONObject().put("isSuccess", false)).toString());
                    }catch(JSONException e2){
                        StringUtils.HaoLog("sendToWeb Error=" + e2);
                        e2.printStackTrace();
                    }
                    break;
            }
            PermissionUtils.requestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, null, "該功能需要下載權限");
        }
    }

    private void webLog(JSONObject data){
        if(data.has("data")){
            String webLog = data.optString("data");
            StringUtils.HaoLog(webLog);
        }
    }

    private void shareFileToCloud(JSONObject data){
        String roomId = null;
        if(data.has("roomId")){
            roomId = data.optString("roomId");
        } else {
            StringUtils.HaoLog("資料不存在");
        }
        if(roomId != null && !roomId.isEmpty()){
            if(outputFile != null && outputFile.length() > 0){
                final String finalRoomId = roomId;
                new Thread(() -> {
                    HttpReturn httpReturn = MsgControlCenter.webSideSendFile(finalRoomId,outputFile);
                    try {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("onlyKey","hashcode");
                        jsonObject.put("fileId",httpReturn.data);
                        jsonObject.put("name",fileName);
                        jsonObject.put("thumbnail","thumbnail");//縮圖
                        jsonArray.put(jsonObject);
                        sendToWeb(new JSONObject().put("type","shareFileToCloud").put("data",jsonArray).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            if(outputFiles != null && outputFiles.length > 0){
                final String finalRoomId = roomId;
                new Thread(() -> {
                    JSONArray jsonArray = new JSONArray();
                    for(File field : outputFiles){
                        JSONObject jsonObject = new JSONObject();
                        String name = field.getName();
                        String lastPathComponent = name.substring(name.lastIndexOf('/') + 1);
                        int hashCode = lastPathComponent.hashCode();
                        HttpReturn httpReturn = MsgControlCenter.webSideSendFile(finalRoomId,field);
                        try {
                            jsonObject.put("fileId",httpReturn.data);
                            jsonObject.put("onlyKey",String.valueOf(hashCode));
                            jsonObject.put("name",name);
                            jsonObject.put("thumbnail","thumbnail");//縮圖
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jsonArray.put(jsonObject);
                    }
                    try{
                        sendToWeb(new JSONObject().put("type","shareFileToCloud").put("data",jsonArray).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }

    private void getPhotoLibrary(JSONObject data){
        String count = null;
        if(data.has("roomId")){
            DefinedUtils.roomId = data.optString("roomId");
            count = data.optString("count");
        } else {
            StringUtils.HaoLog("資料不存在");
        }
        CommonUtils.choosePicture(MainWebActivity.this, Integer.parseInt(count), PickerConfig.PICKER_IMAGE_VIDEO, new CallbackUtils.APIReturn() {
            @Override
            public void Callback(boolean isok, String DataOrErrorMsg) {}
        });
    }

    private void webOk(JSONObject data) {
        if(data == null){
            webRendered(3000);
        } else if(data.has("webViewVersion")){
            String webViewVersion = data.optString("webViewVersion");
        }
        UserMin userMin = UserControlCenter.getUserMinInfo();

        StringUtils.HaoLog("webOk");
        if (userMin != null && !userMin.userId.isEmpty()) {
           checkPermission();
           Login();
        }else
        {
            StringUtils.HaoLog("login 沒開始"+userMin+" "+userMin.userId);
        }
        initOnMainWebPageFinished();
    }

    private void updateRooms() {
        new Thread(() -> {
            RoomControlCenter.getAllRoom();
        }).start();

    }

    private void updateUser() {
        UserControlCenter.getMainUserInfo(new CallbackUtils.userReturn() {
            @Override
            public void Callback(UserInfo userInfo) {

            }
        });
    }

    private void unDo(String json) {
        sendToWeb(json);
    }

    boolean isLoggedIn = false;

    private void Login() {
        if(isLoggedIn){
            return;
        }
        StringUtils.HaoLog("isLoggedIn= "+isLoggedIn);
        if (AllData.dbHelper == null) {
            StringUtils.HaoLog("userId= " + UserControlCenter.getUserMinInfo().userId);
            AllData.initSQL(UserControlCenter.getUserMinInfo().userId);
        }
        try {
            StringUtils.HaoLog("Login 成功=" + UserControlCenter.getUserMinInfo());
            StringUtils.HaoLog("Login=" + new Gson().toJson(UserControlCenter.getUserMinInfo().eimUserData));
            String json = new JSONObject().put("type", "loginEim").put("data", new JSONObject(new Gson().toJson(UserControlCenter.getUserMinInfo().eimUserData))).toString();
            sendToWeb(json);
        } catch (JSONException e) {
            StringUtils.HaoLog("Login 失敗=" + UserControlCenter.getUserMinInfo());

            e.printStackTrace();
        }
        if (MqttService.mqttControlCenter == null) {
            Intent intentServer = new Intent((Context) this, MqttService.class);
            intentServer.putExtra("data", "new");
            startService(intentServer);
        } else {
            MqttService.mqttControlCenter.NewConnect();
        }
        isLoggedIn = true;
    }

    private void setRoomBackground(JSONObject data) {
        String roomId = data.optString("roomId");

        String bytesBase64 = null;
        if (data.has("bytesBase64"))
            bytesBase64 = data.optString("bytesBase64");

        RoomInfoInPhone roomInfoInPhone = AllData.getRoomInPhone(roomId);
        if (roomInfoInPhone == null) {
            roomInfoInPhone = new RoomInfoInPhone();

        }
        roomInfoInPhone.id = roomId;
        roomInfoInPhone.bg = bytesBase64;
        AllData.updateRoomInPhone(roomInfoInPhone);

    }

    private void newLinkApp(JSONObject data) {
        CommonUtils.addShortcut(this, new Gson().fromJson(data.toString(), Microapp.class));
    }

    private void Logout() {
        StringUtils.HaoLog("登出");

        UserControlCenter.setLogout(new CallbackUtils.ReturnHttp() {
            @Override
            public void Callback(HttpReturn httpReturn) {
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancelAll();
                runOnUiThread(() -> {
                    isLoggedIn = false;
                    goLogin();
                });

            }
        });
    }

    public void goLogin() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
            webView = null;
        }
        Intent intent = new Intent((Context) MainWebActivity.this, EimLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void sendFileInfo(File[] files){
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray= FileUtils.getFileInfo(files);
            jsonObject.put("roomId",DefinedUtils.roomId);
            jsonObject.put("files",jsonArray);
            sendToWeb(new JSONObject().put("type","sendFileInfo").put("data",jsonObject).toString());
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void recursiveUpload(String roomId, File[] files, int index) {
        if(DefinedUtils.roomId != null && !DefinedUtils.roomId.isEmpty()){
            JSONObject dataObject = new JSONObject();
            JSONObject fileObject = new JSONObject();
            JSONObject errorMsg = new JSONObject();
            StringUtils.HaoLog("recursiveUpload= index "+index);
            StringUtils.HaoLog("recursiveUpload= files.length "+files.length);
            if (index >= files.length) {
                // 所有檔案都已經上傳完成，結束遞迴
                return;
            }
            File file = files[index];
            Uri url = Uri.fromFile(file);
            MsgControlCenter.webSideSendFile(roomId, file, new CallbackUtils.ReturnHttp() {
                @Override
                public void Callback(HttpReturn httpReturn) {
                    if (httpReturn.status == 200) {
                        // 上傳成功
                        try {
                            dataObject.put("roomId", DefinedUtils.roomId);
                            fileObject.put("name", file.getName());
                            fileObject.put("url", url);
                            fileObject.put("fileId", httpReturn.data);
                            dataObject.put("file", fileObject);
                            sendToWeb(new JSONObject().put("type","sendFile").put("data",dataObject).toString());
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                        // 遞迴調用，上傳下一個檔案
                        recursiveUpload(roomId, files, index + 1);
                    } else {
                        // 上傳失敗
                        try {
                            dataObject.put("roomId", DefinedUtils.roomId);
                            fileObject.put("name", file.getName());
                            fileObject.put("url", url);
                            fileObject.put("fileId", null);
                            errorMsg.put("status",500);
                            errorMsg.put("msg","使用者不是聊天室成員");
                            errorMsg.put("data",null);
                            fileObject.put("errorMsg", errorMsg);
                            dataObject.put("file", fileObject);
                            sendToWeb(new JSONObject().put("type","sendFile").put("data",dataObject).toString());
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                        // 遞迴調用，上傳下一個檔案
                        recursiveUpload(roomId, files, index + 1);
                    }
                }
            });
        }
    }

    private void share(JSONObject data) {
        if (data.has("type")) {
            String inviteMessage = "";
            if (data.has("data"))
                inviteMessage = data.optString("data");
            StringUtils.HaoLog("share command："+data.optString("type"));
            switch (data.optString("type")) {
                case "sms":
                    openMessage(inviteMessage);
                    break;
                case "mail":
                    openGmail("user Name", inviteMessage);
                    break;
                case "weChat":
                    openWechat(inviteMessage);
                    break;
                case "Line":
                    openLine(inviteMessage);
                    break;
                case "QRcodeImage":
                    openQRcodeImage(inviteMessage);
                    break;
                case "otherApp":
                    hyperlinkSharingOrFileSharing(inviteMessage);
                    break;
            }
        }
    }

    private void openQRcodeImage(String inviteMessage) {
        BarcodeEncoder encode = new BarcodeEncoder();
        try {
            int pixel = (int) CommonUtils.convertDpToPixel(200, this);
            Bitmap bitmap = encode.encodeBitmap(inviteMessage, BarcodeFormat.QR_CODE, pixel, pixel);
            if (bitmap != null) {
                File file = FileUtils.saveCachePic(this, bitmap, "分享Lale群組QrCode");
                if (file != null) {
                    Uri uri = FileProvider.getUriForFile(this, "com.flowring.laleents.fileprovider", file);
                    if (uri != null) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/png");
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(intent, "分享Lale群組Qr Code"));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getRoomBackground(JSONObject data) {
        String roomId = data.optString("roomId");
        RoomInfoInPhone roomInfoInPhone = AllData.getRoomInPhone(roomId);
        if (roomInfoInPhone == null) {
            roomInfoInPhone = new RoomInfoInPhone();
            roomInfoInPhone.id = roomId;

        }
        AllData.updateRoomInPhone(roomInfoInPhone);
        try {
            JSONObject j = new JSONObject().put("type", "getRoomBackground").put("bytesBase64", roomInfoInPhone.bg);
            sendToWeb(j.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void tokenRefresh() {
        UserControlCenter.tokenRefresh(new CallbackUtils.ReturnHttp() {
            @Override
            public void Callback(HttpReturn httpReturn) {
                try {
                    if (httpReturn.status != 200) {
                        if ("refresh token 逾時".equals(httpReturn.msg)) {
                            StringUtils.HaoLog("App過久未使用您的帳號已被登出");
                            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
                            pref.edit().putBoolean("isSignOut", true).apply();
                            Logout();
                        } else {
                            DialogUtils.showDialogMessage(MainWebActivity.this, httpReturn.msg, "連線狀態異常，是否要登出？", new CallbackUtils.noReturn() {
                                @Override
                                public void Callback() {
                                    Logout();
                                }
                            }, new CallbackUtils.noReturn() {
                                @Override
                                public void Callback() {

                                }
                            });
                        }
                    } else {
                        JSONObject j = new JSONObject().put("type", "tokenRefresh").put("data", new JSONObject(new Gson().toJson(httpReturn.data)));
                        sendToWeb(j.toString());
                    }

                } catch (JSONException e) {
                    StringUtils.HaoLog("錯誤：" + e);
                    e.printStackTrace();
                }
            }
        });
    }

    Boolean isFirstCheck = true;
    private void censorToken() {
        UserControlCenter.tokenRefresh_noThread(new CallbackUtils.ReturnHttp() {
            @Override
            public void Callback(HttpReturn httpReturn) {
                StringUtils.HaoLog("censorToken= 1 "+httpReturn.msg + " "+Thread.currentThread().getName());
                if(httpReturn.status != 200){
                    // token 和 refresh token 都過期 -> 登出
                    if ("refresh token 逾時".equals(httpReturn.msg)) {
                        StringUtils.HaoLog("App過久未使用您的帳號已被登出");
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AllData.context);
                        pref.edit().putBoolean("isSignOut", true).apply();
                        Logout();
                    }
                } else if(httpReturn.status == 200){
                    StringUtils.HaoLog("censorToken= 2 "+httpReturn.msg + " "+Thread.currentThread().getName());
                    // 驗證 token 是否正確
                    if(isFirstCheck){
                        checkToken();
                    } else {
                        isFirstCheck = true;
                    }
                }
            }
        });
    }

    private void checkToken(){
        UserControlCenter.checkToken(new CallbackUtils.ReturnHttp() {
            @Override
            public void Callback(HttpReturn httpReturn) {
                if(httpReturn.status != 400){
                    //token有效
                    StringUtils.HaoLog("censorToken= 3 "+Thread.currentThread().getName()+" token有效");
                } else {
                    String msg = httpReturn.msg;
                    switch (msg){
                        case "token 不存在":
                            //登出
                            StringUtils.HaoLog("censorToken= 4 "+Thread.currentThread().getName()+" token 不存在");
                            SharedPreferencesUtils.isRepeatDevice(true);
                            Logout();
                            break;
                        case "token 逾時":
                            //更新token 要延遲
                            StringUtils.HaoLog("censorToken= 5 "+Thread.currentThread().getName()+" token 逾時");
                            isFirstCheck = false;
                            censorToken();
                            break;
                        case "token 資料錯誤":
                            StringUtils.HaoLog("censorToken= 5 "+Thread.currentThread().getName()+" token 資料錯誤");
                            Logout();
                            break;
                    }
                }
            }
        });
    }

    private void downloadByBytesBase64(JSONObject data) {
        unDo(data.toString());
    }

    private int urlsMax = 0;
    private boolean urlsIsOk = true;
    private int urlsNew = 0;
    private ArrayList<String> urlsError = new ArrayList<>();

    private void downloadByUrls(JSONObject data) {

        if (PermissionUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || (Build.VERSION.SDK_INT > Build.VERSION_CODES.R)) {
            new Thread(() -> {

                StringUtils.HaoLog(data.toString());
                try {
                    JSONArray urls = data.getJSONArray("urls");
                    String[] urlsArray = new String[urls.length()];
                    for (int i = 0; i < urls.length(); i++) {
                        urlsArray[i] = urls.getString(i);
                    }

                    urlsMax = urls.length();
                    urlsNew = 0;
                    urlsError = new ArrayList<>();
                    downloadFile(urlsArray, null, new downloadByUrlsCallback() {
                        @Override
                        public void onEnd(boolean isSuccess, String[] errorUrl) {
                            downloadByUrlsReturn(isSuccess, errorUrl);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }).start();
        } else {
            StringUtils.HaoLog("詢問權限");
            runOnUiThread(() -> {
                downloadByUrlsReturn(false, null);
                PermissionUtils.requestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, null, "該功能需要下載權限");

            });
        }

    }

    interface downloadByUrlsCallback {
        void onEnd(boolean isSuccess, String[] errorUrl);
    }

    private void downloadByUrlsReturn(boolean isSuccess, String[] errorUrl) {
        runOnUiThread(() -> {
            try {
                JSONArray errorUrls = new JSONArray();
                for (int i = 0; i < errorUrl.length; i++) {
                    errorUrls.put(errorUrl[i]);
                }
                String json = new JSONObject().put("type", "downloadFiles").put("data", new JSONObject().put("isSuccess", isSuccess).put("errorUrl", errorUrls)).toString();
                sendToWeb(json);
            } catch (JSONException e2) {
                StringUtils.HaoLog("sendToWeb Error=" + e2);
                e2.printStackTrace();
            }


        });
    }

    private void downloadByUrl(JSONObject data) {

        if (PermissionUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || (Build.VERSION.SDK_INT > Build.VERSION_CODES.R)) {
            new Thread(() -> {
                StringUtils.HaoLog(data.toString());
                if (data.isNull("fileName"))
                    downloadFile(data.optString("url"), null);
                else
                    downloadFile(data.optString("url"), data.optString("fileName"));
            }).start();
        } else {
            StringUtils.HaoLog("詢問權限");
            try {

                sendToWeb(new JSONObject().put("type", "downloadFile").put("data", new JSONObject().put("isSuccess", false)).toString());
            } catch (JSONException e2) {
                StringUtils.HaoLog("sendToWeb Error=" + e2);
                e2.printStackTrace();
            }
            runOnUiThread(() -> {

                PermissionUtils.requestPermission(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, null, "該功能需要下載權限");

            });
        }

    }

    private void hyperlinkSharingOrFileSharing(String inviteMessage){
        if(PermissionUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || (Build.VERSION.SDK_INT > Build.VERSION_CODES.R)) {
            String string = null;
            String url = null;
            try {
                JSONArray jsonArray = new JSONArray(inviteMessage);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                string = jsonObject.optString("string");
                url = jsonObject.optString("url");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (url != null && !url.isEmpty()) {
                judgmentFileName(inviteMessage);
            } else if (string != null && !string.isEmpty()) {
                //文字超文本分享
                textHypertextSharing(string);
            }
        } else {
            PermissionUtils.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE , "該功能需要下載權限");
        }
    }

    private void textHypertextSharing(String textMessage){
        try {
            Intent thirdPartyAppIntent = new Intent(Intent.ACTION_SEND);
            thirdPartyAppIntent.setType("text/plain");
            thirdPartyAppIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
            startActivity(Intent.createChooser(thirdPartyAppIntent,"分享文本訊息"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "尚未安裝應用程式。", Toast.LENGTH_SHORT).show();
        }
    }

    private void judgmentFileName(String inviteMessage){
        String oldFileName = null;
        try{
            JSONArray jsonArray = new JSONArray(inviteMessage);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            oldFileName = jsonObject.optString("name");
        }catch (JSONException e){
            e.printStackTrace();
        }
        if (oldFileName.isEmpty()){
            DownloadUtils.shareFileTypeDownload(inviteMessage,null,this);
        } else {
            DownloadUtils.shareFileTypeDownload(inviteMessage,oldFileName,this);
        }
    }

    public static void shareFileType(File folder, Context context){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            if(folder.exists()){
                Uri cacheDirUri = FileProvider.getUriForFile(context, "com.flowring.laleents.fileprovider", folder);
                if(cacheDirUri != null){
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM,cacheDirUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.setType("application/*");
                    context.startActivity(Intent.createChooser(intent,"分享檔案"));
                }
            } else {StringUtils.HaoLog("檔案不存在");}
        } else {StringUtils.HaoLog("存儲設備不可用，無法進行讀寫操作");}
    }

    private void getAddressBook() {
        if (PermissionUtils.checkPermission(this, Manifest.permission.READ_CONTACTS)) {

            try {
                JSONArray jsonArray = new JSONArray();
                for (String phone : getPhoneContactsData()) {
                    jsonArray.put(phone);
                }
                sendToWeb(new JSONObject().put("type", "getAddressBook").put("data", new JSONObject().put("phones", jsonArray)).toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            try {
                sendToWeb(new JSONObject().put("type", "getAddressBook").put("data", new JSONObject().put("error", "NoPermission")).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void openPhoneWebView(JSONObject data) {

        ActivityUtils.gotoWebJitisiMeet(this,
                data.optString("displayName"),
                data.optString("userId")
                , data.optString("avatar")
                , data.optString("laleToken")
                , data.optString("mqttHost")
                , data.optString("jitsiDomain")
                , data.optString("callType")
                , data.optString("msgId")
                , data.optString("roomId")
                , data.optString("roomName")
                , data.optBoolean("isGroupCall")
        );
    }

    void NewUrl(JSONObject data) {
        String sURL;
        if (data.has("url")) {
            sURL = data.optString("url");
            webView.loadUrl(sURL);
        }

    }
    void authorize(JSONObject data) {
        String type;
        if (data.has("type")) {
            type = data.optString("type");
            if(type.equals("location"))
            {
                if(PermissionUtils.checkPermission(MainWebActivity.this,"android.permission.ACCESS_FINE_LOCATION"))
                {
                    try {
                        JSONObject j = new JSONObject().put("type", "authorize").put("data", new JSONObject().put("type","location").put("isSuccess",true));
                        sendToWeb(j.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else PermissionUtils.requestPermission(MainWebActivity.this,"android.permission.ACCESS_FINE_LOCATION","需要您的位置權限");
            }
        }

    }
    void openChrome(JSONObject data) {
        String sURL;
        try {

            if (data.has("url")) {
                sURL = data.getString("url");
                chromeCallbackUrl = data.optString("callBackUrl");
                needBack = data.optBoolean("webBack", false);
                Intent intent = CommonUtils.openChromeCustomTabs(this, sURL);
                startActivityForResult(intent, DefinedUtils.REQUEST_CHROME_TAB);
            }
        } catch (Exception e) {
            StringUtils.HaoLog("JS openWebViewByChrome ERROR = " + e);
        }

    }

    private void openQRcode(JSONObject data) {
        activityReturn = new CallbackUtils.ActivityReturn() {
            @Override
            public void Callback(androidx.activity.result.ActivityResult activityResult) {
                String SCAN_QRCODE = null;
                if (activityResult.getData() != null)
                    SCAN_QRCODE = activityResult.getData().getStringExtra("SCAN_QRCODE");
                try {
                    JSONObject j = new JSONObject().put("type", "getQRcode").put("data", SCAN_QRCODE);
                    sendToWeb(j.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        ActivityUtils.gotoQRcode(this, ScanCaptureActivity.ScanCaptureType.Json, ActivityResult);
    }

    private void openLine(String inviteMessage) {
        try {
            String msg = URLEncoder.encode(inviteMessage, "UTF-8");
            String scheme = "line://msg/text/" + msg;
            Uri uri = Uri.parse(scheme);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException e) {
            Toast.makeText((Context) MainWebActivity.this, "尚未安裝Line。", Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void openWechat(String inviteMessage) {
        try {
            Intent wechatIntent = new Intent(Intent.ACTION_SEND);
            wechatIntent.setPackage("com.tencent.mm");
            wechatIntent.setType("text/plain");
            wechatIntent.putExtra(Intent.EXTRA_TEXT, inviteMessage);
            startActivity(wechatIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText((Context) MainWebActivity.this, "尚未安裝Wechat。", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGmail(String displayName, String inviteEmaiMessage) {
        try {
            Intent wechatIntent = new Intent(Intent.ACTION_SEND);
            wechatIntent.setPackage("com.google.android.gm");
            wechatIntent.setType("text/plain");
            wechatIntent.putExtra(Intent.EXTRA_TEXT, inviteEmaiMessage);
            {
                Uri uri = CommonUtils.genQRcodeAndSave(MainWebActivity.this, displayName);
                if (uri != null) {
                    wechatIntent.putExtra(Intent.EXTRA_STREAM, uri);
                }
            }
            wechatIntent.putExtra(Intent.EXTRA_SUBJECT, "一起來用Lale吧!");
            startActivity(wechatIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText((Context) MainWebActivity.this, "尚未安裝Gmail。", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMessage(String inviteMessage) {

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("sms_body", inviteMessage);
        startActivity(smsIntent);

    }

    private ArrayList<String> getPhoneContactsData() {

        ArrayList<String> phones = new ArrayList<>();

        ContentResolver reContentResolverol = getContentResolver();

        Uri contactData = Uri.parse("content://com.android.contacts/contacts");
        @SuppressWarnings("deprecation")
        Cursor cursor = reContentResolverol.query(contactData, null, null, null, null);
        try {
            while (cursor.moveToNext())  // 將資料讀到最後一筆時會回傳false
            {

                @SuppressLint("Range") String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phone = reContentResolverol.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

                while (phone.moveToNext()) {
                    @SuppressLint("Range") String phoneNumber = phone.getString(phone.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phones.add(phoneNumber);


                }
            }
        } catch (Exception e) {
        }
        return phones;
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         **/
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         **/
        @Override
        protected String doInBackground(String... f_url) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                int count;
                try {
                    URL url = new URL(f_url[0]);
                    URLConnection conection = url.openConnection();
                    conection.connect();

                    // this will be useful so that you can show a tipical 0-100%
                    // progress bar
                    int lenghtOfFile = conection.getContentLength();

                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream(),
                            8192);
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
                    StringUtils.HaoLog("getContentType=" + conection.getContentType());
                    StringUtils.HaoLog("getContent=" + conection.getContent());
                    String fileName = sdf.format(new Date().getDate()) + FileUtils.toExtension(conection.getContentType());
                    String filePath = getExternalFilesDir(null) + "/"
                            + fileName;
                    // Output stream
                    OutputStream output = new FileOutputStream(filePath);

                    final int CREATE_FILE = 1;


                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    File file = new File(filePath);
                    intent.setType(conection.getContentType());
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, file);
                    activityReturn = new CallbackUtils.ActivityReturn() {
                        @Override
                        public void Callback(androidx.activity.result.ActivityResult activityResult) {
                            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                                Uri uri = activityResult.getData().getData();
                                StringUtils.HaoLog("uri=" + uri);
                                int compareTo = new File(fileName).compareTo(new File(uri.getPath()));
                                StringUtils.HaoLog("compareTo=" + compareTo);
                            } else {
                                try {
                                    StringUtils.HaoLog("DownloadManager=end nook");
                                    sendToWeb(new JSONObject().put("type", "downloadFile").put("data", new JSONObject().put("isSuccess", false)).toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                            }


                        }
                    };
                    runOnUiThread(() -> {
                        ActivityResult.launch(intent);
                    });


                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }


            } else {
                int count;
                try {
                    URL url = new URL(f_url[0]);
                    URLConnection conection = url.openConnection();
                    conection.connect();

                    // this will be useful so that you can show a tipical 0-100%
                    // progress bar
                    int lenghtOfFile = conection.getContentLength();

                    // download the file
                    InputStream input = new BufferedInputStream(url.openStream(),
                            8192);
                    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
                    StringUtils.HaoLog("getContentType=" + conection.getContentType());
                    StringUtils.HaoLog("getContent=" + conection.getContent());

                    // Output stream
                    OutputStream output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/"
                            + sdf.format(new Date().getDate()) + FileUtils.toExtension(conection.getContentType()));
                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                    try {
                        StringUtils.HaoLog("DownloadManager=end nook");
                        sendToWeb(new JSONObject().put("type", "downloadFile").put("data", new JSONObject().put("isSuccess", true)).toString());
                        return null;
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }

                try {
                    StringUtils.HaoLog("DownloadManager=end nook");
                    sendToWeb(new JSONObject().put("type", "downloadFile").put("data", new JSONObject().put("isSuccess", false)).toString());

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
            return null;
        }

        /**
         * Updating progress bar
         **/
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage

        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloade
        }
    }
    //endregion

    //region  sendToWebtest

    void sendToWebtest() {

        webView.evaluateJavascript("callAndroid2()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                Log.d("hao", "發訊息回傳:" + s);
            }
        });
    }

    void sendToWeb(String type, JSONObject data) {

        try {

            sendToWeb(new JSONObject().put("type", type).put("data", data).toString());
        } catch (JSONException e2) {
            StringUtils.HaoLog("sendToWeb Error=" + e2);
        }
    }

    void sendToWeb(String json) {
        checkHasWebView();
        webView.post(new Runnable() {
            @Override
            public void run() {
                StringUtils.HaoLog("jsp sendToWeb:" + TimeUtils.NowTimestamp() + "/" + json);

                webView.evaluateJavascript("receiveAppMessage('" + json + "')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d("hao", "發訊息回傳:" + s);
                    }
                });

            }
        });
    }

    @Override
    public void onBackPressed() {
        try {

            sendToWeb(new JSONObject().put("type", "back").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region  舊版webView JavascriptInterface

    private String chromeCallbackUrl = "";
    private boolean needBack = false;

    @JavascriptInterface
    public void openWebViewByChrome(String url) {
        try {
            JSONObject j = new JSONObject().put("type", "webviewJI").put("name", "openWebViewByChrome(String url)").put("url", url);
            sendToWeb(j.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @JavascriptInterface
    public void startScanQRCode(String msg) {
        try {
            openQRcode(new JSONObject().put("title", "QR code"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void closeNativeBrowser() {

        webView.evaluateJavascript("closeNativeBrowser()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String s) {
                if (!chromeCallbackUrl.isEmpty()) {
                    wvloadUrl(chromeCallbackUrl);

                    chromeCallbackUrl = "";
                }
                if (needBack) {
                    wvGoBack();
                    needBack = false;
                }
            }

            private void wvGoBack() {

            }

            private void wvloadUrl(String chromeCallbackUrl) {
            }
        });
    }

    @JavascriptInterface
    public void freeWorkGroup(String msg)//創建免費工作群組 不明-等免費工作能做時再做
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "freeWorkGroup(String msg)").put("data", new JSONObject().put("msg", msg))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void showSettingIcon(String json)//友圈網頁的退回變成設定按鈕 不知道用途
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "showSettingIcon(String json)").put("data", new JSONObject().put("json", json))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void setNotificationBell(String notifyCount)//修改為讀數量 可能已經因為改版失效了
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "setNotificationBell(String notifyCount)").put("data", new JSONObject().put("notifyCount", notifyCount))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void downloadSticker(String text)//下載貼圖 疑似改版完後用不到
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "downloadSticker(String text)").put("data", new JSONObject().put("text", text))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void deleteSticker(String text)//刪除貼圖 疑似改版完後用不到
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "deleteSticker(String text)").put("data", new JSONObject().put("text", text))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void isInLaleApp()//回復 evaluateJavascript(String.format("isInLaleApp('true')") 不知道幹啥用的
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "isInLaleApp()")).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void gotoCompanyTab()//退回辦公的首頁 從程式碼裡面看是把webview關掉
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "gotoCompanyTab()")).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void setToolbarTitle(String title)//修改該頁的標題
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "setToolbarTitle(String title)").put("data", new JSONObject().put("title", title))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void backToPrevious(String backDashboard)//關掉這頁退回前一頁並刷新前一頁
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "backToPrevious(String backDashboard)").put("data", new JSONObject().put("backDashboard", backDashboard))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void testCallback(String url)//Toast文字
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "testCallback(String url)").put("data", new JSONObject().put("url", url))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void changeNickName(String displayName)//使用者改暱稱
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "changeNickName(String displayName)").put("data", new JSONObject().put("displayName", displayName))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void getMyFriendList(String json)//回傳好友清單-需要參考資料
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "getMyFriendList(String json)").put("data", new JSONObject().put("json", json))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void openChooseMember(String userIds, String id)//前往好友選取頁面 並把id記錄起來之後發送回去-需要參考資料
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "openChooseMember(String userIds, String id)").put("data", new JSONObject().put("userIds", userIds).put("id", id))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void showToolbarMoreIcon(String type)//修改右上角按鈕的按下事件類型和換圖-需要參考資料
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "showToolbarMoreIcon(String type)").put("data", new JSONObject().put("type", type))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void showToolbarSearchIcon(String url)//修改右上角按鈕的按下事件類型和換圖-需要參考資料
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "showToolbarSearchIcon(String url)").put("data", new JSONObject().put("url", url))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void gotoAddFriend()//前往加好友頁面
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "gotoAddFriend()")).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void gotoFriendTabSetting()//前往我的設定 好友設定
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "gotoFriendTabSetting()")).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void reloadWeb()//刷新網頁
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "reloadWeb()")).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void openLaleCard(String userID)//開啟該使用者的卡片頁面
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "openLaleCard(String userID)").put("data", new JSONObject().put("userID", userID))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void openMemiaRoom(String msg)//前往指定房間
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "openMemiaRoom(String msg)").put("data", new JSONObject().put("msg", msg))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void getLaleJWT(String msg)//取得token
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "getLaleJWT(String msg)").put("data", new JSONObject().put("msg", msg))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void openChatRoom(String msg)//前往指定房間
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "openChatRoom(String msg)").put("data", new JSONObject().put("msg", msg))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void joinGroup(String msg)//前往加入群組的頁面
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "joinGroup(String msg)").put("data", new JSONObject().put("msg", msg))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void createGroup(String msg)//創建群組
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "createGroup(String msg)").put("data", new JSONObject().put("msg", msg))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void goBackAndReload()//退回上一頁
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "goBackAndReload()")).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void cancelFollowSuccess()//關掉此頁
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "cancelFollowSuccess")).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void openNewBrowser(String URL, boolean isForm) //開啟一個新的web view頁面
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "openNewBrowser(String URL, boolean isForm)").put("data", new JSONObject().put("URL", URL).put("isForm", isForm))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void changeTitle(String Title)//修改標題文字
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "changeTitle(String Title)").put("data", new JSONObject().put("Title", Title))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void closeWebView(String msg)//到該webView的第一頁
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "closeWebView(String msg)").put("data", new JSONObject().put("msg", msg))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void shareToChatRoom(String msg)//分享訊息->前往分享頁面
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "shareToChatRoom(String msg)").put("data", new JSONObject().put("msg", msg))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void returnEnterpriseMemberIDs(String members)//以這些成員資料前往創建群組頁面
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "returnEnterpriseMemberIDs(String members)").put("data", new JSONObject().put("members", members))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void isCanShare(boolean bShare)//該網頁是否可以分享 右上角的按鈕按下的彈跳視窗中的分享功能
    {
        try {
            sendToWeb(new JSONObject().put("type", "webViewJSI").put("data", new JSONObject().put("name", "isCanShare(boolean bShare)").put("data", new JSONObject().put("bShare", bShare))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void getBase64FromBlobData(String base64Data) throws IOException {
        convertBase64StringToPdfAndStoreIt(base64Data);
    }

    private void convertBase64StringToPdfAndStoreIt(String base64Data) throws IOException {
        String fileName = FormatUtils.getDateFormat(System.currentTimeMillis(), "yyyyMMdd_HHmm");
        final File downloadFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS) + "/TaiwanPay_" + fileName + ".jpg");
        String base64Img = base64Data.split(",")[1];
        byte[] imgBytesData = android.util.Base64.decode(base64Img, android.util.Base64.DEFAULT);
        FileOutputStream os;
        os = new FileOutputStream(downloadFile);
        os.write(imgBytesData);
        os.flush();

        if (downloadFile.exists()) {
            Toast.makeText((Context) this, "下載成功", Toast.LENGTH_SHORT).show();
        }
    }

    @JavascriptInterface
    public void openWithBrowser(String url) {
        webView.post(new Runnable() {
            @Override
            public void run() {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });
    }

    @JavascriptInterface
    public void openWithChrome(String url) {
        try {
            JSONObject j = new JSONObject().put("type", "webviewJI").put("name", "openWithChrome(String url)").put("url", url);
            sendToWeb(j.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private Bitmap mBitmap;

    @JavascriptInterface
    public void downloadPhotoFromWeb(String json) {
        StringUtils.HaoLog("JS downloadPhotoFromWeb()");
        showWait();
        mBitmap = null;
        List<String> photos = new ArrayList<>();
        webView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jo = new JSONObject(json);
                    com.flowring.laleents.tools.Log.d("downloadPhotoFromWeb", jo.toString());

                    JSONArray data = jo.optJSONArray("photos");
                    for (int i = 0; i < data.length(); i++) {
                        photos.add((String) data.opt(i));
                    }

                    AtomicInteger count = new AtomicInteger();
                    ArrayList<Observable<Boolean>> observables = new ArrayList<>();
                    for (String imagePath : photos) {
                        observables.add(Observable.create(new ObservableOnSubscribe<Boolean>() {
                            @Override
                            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                                Glide.with(MainWebActivity.this)
                                        .asBitmap()
                                        .load(imagePath)
                                        .apply(new RequestOptions()
                                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .placeholder(R.drawable.img_default)
                                                .fitCenter())
                                        .into(new CustomTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                mBitmap = resource;
                                                if (mBitmap != null) {
                                                    //图片信息不为空时才保存
                                                    String fileName = FormatUtils.getDateFormat(System.currentTimeMillis(), "yyyyMMddHHmmss");
                                                    Uri uri = FileUtils.saveBitmapToGallery(MainWebActivity.this, fileName, mBitmap);
                                                    emitter.onNext(uri != null);
                                                }
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                            }
                                        });
                            }
                        }).subscribeOn(AndroidSchedulers.mainThread()));
                    }
                    Observable.merge(observables).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(Boolean b) throws Exception {
                                    if (b) {
                                        count.addAndGet(1);
                                    }
                                    if (photos.size() == count.get()) {
                                        cancelWait();
                                        Toast.makeText((Context) MainWebActivity.this, "已下載", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText((Context) MainWebActivity.this, "下載失敗", Toast.LENGTH_SHORT).show();
                                }
                            }, new Action() {
                                @Override
                                public void run() throws Exception {

                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    //endregion


}
