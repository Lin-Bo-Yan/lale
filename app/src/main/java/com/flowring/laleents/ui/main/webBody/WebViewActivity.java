package com.flowring.laleents.ui.main.webBody;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.flowring.laleents.R;
import com.flowring.laleents.tools.ActivityUtils;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.TimeUtils;
import com.flowring.laleents.tools.room.GlobalDataManager;
import com.flowring.laleents.ui.model.MainAppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WebViewActivity extends MainAppCompatActivity {
    public TextView url;
    public WebView webview;
    private static final List<WebViewActivity> activities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        StringUtils.HaoLog("WebViewActivity onCreate");
        // 將當前實例添加到列表中
        activities.add(this);

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
                view.loadUrl(request.getUrl().toString());
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
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        WebView.setWebContentsDebuggingEnabled(true);
        webview.addJavascriptInterface(this, "laleIm");
        webview.loadUrl(url.getText().toString());
    }

    @SuppressLint("JavascriptInterface")
    @JavascriptInterface
    public String postMessage(String json) {
        StringUtils.HaoLog("WebViewActivity命令= " + TimeUtils.NowTime() + "/" + json);
        if (json == null || json.isEmpty()){
            return json;
        }
        try {
            JSONObject jsonObject = new JSONObject(json);
            if(jsonObject.isNull("command")){
                return json;
            }
            String command = jsonObject.optString("command");
            JSONObject data = null;
            if (!jsonObject.isNull("data")){
                data = jsonObject.optJSONObject("data");
            }

            switch (command) {
                case "closeWebView":
                    closeWebView(data);
                    break;
                case "openWebView":
                    openWebView(data);
                    break;
            }
        } catch (JSONException e) {
            StringUtils.HaoLog("WebViewActivity 命令失敗");
            e.printStackTrace();
        }
        return json;
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

    private void closeWebView(JSONObject data){
        String callBack = data.optString("callBack");
        if("workToTasks".equalsIgnoreCase(callBack)){
            // 將數據保存到全局數據管理器中
            GlobalDataManager.getInstance().setCloseWebViewData(data);
            for (WebViewActivity activity : new ArrayList<>(activities)) {
                activity.finish();
            }
        } else {
            // 設置返回結果
            Intent closeWebView = new Intent();
            closeWebView.putExtra("closeWebView", data.toString());
            setResult(Activity.RESULT_OK, closeWebView);
            finish();
        }
    }

    private void openWebView(JSONObject data) {
        activityReturn = new CallbackUtils.ActivityReturn() {
            @Override
            public void Callback(androidx.activity.result.ActivityResult activityResult) {}
        };
        ActivityUtils.gotoWebViewActivity(WebViewActivity.this, data.optString("url"), ActivityResult);
    }
}
