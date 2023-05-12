package com.flowring.laleents.ui.main.webBody;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.phone.AllData;

import java.util.ArrayList;

public class shareActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(shareActivity.this, MainWebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setType(getIntent().getType());
        StringUtils.HaoLog("testWebActivity BroadcastReceiver " + getIntent().getAction());
        Uri uri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
        ArrayList<Uri> uris = getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        String extraText = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        if (uri != null){
            intent.putExtras(getIntent());
        }
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        intent.setAction(getIntent().getAction());

        Intent intent1 = new Intent(getIntent().getAction());
        intent1.setAction(getIntent().getAction());
        intent1.setType(getIntent().getType());
        if (uri != null){
            StringUtils.HaoLog("單筆資料");
            intent1.putExtras(getIntent());
        }
        if (uris != null){
            StringUtils.HaoLog("多筆資料");
            intent1.putExtras(getIntent());
        }
        intent1.putExtra(Intent.EXTRA_TEXT, extraText);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent1); //發送廣播訊息

        startActivity(intent);
        finish();
    }
}
