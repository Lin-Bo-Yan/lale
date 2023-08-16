package com.flowring.laleents.ui.main.webBody;

import com.flowring.laleents.ui.model.MainAppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.flowring.laleents.R;
import com.flowring.laleents.tools.download.DownloadUtils;
import com.flowring.laleents.tools.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class FileReaderActivity extends MainAppCompatActivity {
    public WebView webview;
    public ImageView share;
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

        share = findViewById(R.id.share);
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
}