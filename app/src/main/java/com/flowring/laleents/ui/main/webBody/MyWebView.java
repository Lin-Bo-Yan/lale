package com.flowring.laleents.ui.main.webBody;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

import com.flowring.laleents.tools.StringUtils;

public class MyWebView extends WebView {

    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        //作為webview保活使用
        StringUtils.HaoLog("onWindowVisibilityChanged=" + (visibility != View.GONE));
        if (visibility != View.GONE) super.onWindowVisibilityChanged(visibility);
    }
}
