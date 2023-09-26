package com.flowring.laleents.ui.main.webBody;

import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.FileUtils;
import com.flowring.laleents.tools.SharedPreferencesUtils;
import com.flowring.laleents.tools.TimeUtils;
import com.flowring.laleents.ui.model.FileReader.WatermarkDefault;
import com.flowring.laleents.ui.model.FileReader.WeterMarkBgView;
import com.flowring.laleents.ui.model.MainAppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.flowring.laleents.R;
import com.flowring.laleents.tools.download.DownloadUtils;
import com.flowring.laleents.tools.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileReaderActivity extends MainAppCompatActivity {
    private WebView webview;
    private ImageView share;
    private TextView watermarkTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_reader);

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initShareImageView();
        getWatermark();

        webview = findViewById(R.id.webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setAllowFileAccess(true);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return false;
            }
        });
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
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        WebView.setWebContentsDebuggingEnabled(true);
        webview.loadUrl(getIntent().getStringExtra("url"));
    }

    public static void shareFileType(File folder, Context context){

        StringUtils.HaoLog("shareFileType 分享檔案");
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
                } else {StringUtils.HaoLog("Uri 空值");}
            } else {StringUtils.HaoLog("檔案不存在");}
        } else {StringUtils.HaoLog("存儲設備不可用，無法進行讀寫操作");}
    }

    private void initShareImageView(){
        share = findViewById(R.id.share);
        boolean enableBanSharing = SharedPreferencesUtils.getDownloadForbidden(FileReaderActivity.this);
        if(enableBanSharing){
            share.setVisibility(View.GONE);
        } else {
            share.setVisibility(View.VISIBLE);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String jsonDataStr = getIntent().getStringExtra("jsonData");
                    if(jsonDataStr != null && !jsonDataStr.isEmpty()){
                        try{
                            JSONObject jsonData = new JSONObject(jsonDataStr);
                            String fileId = jsonData.optString("fileId");
                            String oldFileName = jsonData.optString("fileName");
                            String url = jsonData.optString("url");
                            DownloadUtils.openFile(url,oldFileName,fileId,FileReaderActivity.this);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void getWatermark(){
        watermarkTextView = findViewById(R.id.watermark);
        boolean watermarkEnable = SharedPreferencesUtils.getWatermark(FileReaderActivity.this);
        if(watermarkEnable){
            UserControlCenter.getDefaultWatermarkTemplate(new CallbackUtils.WatermarkDefaultReturn() {
                @Override
                public void Callback(WatermarkDefault watermark) {
                    if(watermark != null){
                        String textContent = UserControlCenter.obtainReorganizedTextWatermark(watermark.textContent);
                        List<String> labels = lineBreakCutter(StringUtils.replaceTextPlaceholders(textContent));
                        Bitmap bitmap = FileUtils.getBitmapFromURL(watermark.image);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                watermarkTextView.setBackground(new WeterMarkBgView(FileReaderActivity.this,labels,
                                        watermark.textRotate,
                                        watermark.textSize,
                                        watermark.textColor,
                                        watermark.textOpacity,
                                        watermark.imageOpacity,
                                        watermark.imageScale,
                                        watermark.textFont,
                                        bitmap));
                            }
                        });
                    }
                }
            });
        }
    }

    private List<String> lineBreakCutter(String textContent){
        String[] parts = textContent.split("\n");
        List<String> labels = new ArrayList<>();
        for(String part : parts){
            labels.add(part);
        }
        return labels;
    }

}