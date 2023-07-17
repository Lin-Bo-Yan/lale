package com.flowring.laleents.ui.main.webBody;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dmcbig.mediapicker.PickerConfig;
import com.flowring.laleents.R;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.CommonUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.ui.model.MainAppCompatActivity;

public class WebViewActivity extends MainAppCompatActivity {
    public TextView url;
    public WebView webview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        StringUtils.HaoLog("WebViewActivity onCreate");
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        url = findViewById(R.id.url);
        url.setText(getIntent().getStringExtra("url"));

        webview = findViewById(R.id.webview);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(  request.getUrl().toString());
                return false;
            }
        });

        webview.setDownloadListener(mWebDownloadListener);
        WebSettings webSettings = webview.getSettings();
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
        webSettings.setGeolocationEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        WebView.setWebContentsDebuggingEnabled(true);
        webview.loadUrl(url.getText().toString());
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
        }
    };
}
