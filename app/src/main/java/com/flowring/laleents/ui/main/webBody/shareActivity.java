package com.flowring.laleents.ui.main.webBody;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.phone.AllData;

public class shareActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(AllData.context, MainWebActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setType(getIntent().getType());
        StringUtils.HaoLog("testWebActivity BroadcastReceiver " + getIntent().getAction());
        if (getIntent().getParcelableExtra(Intent.EXTRA_STREAM) != null)
            intent.putExtras(getIntent());
        intent.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra(Intent.EXTRA_TEXT));
        intent.setAction(getIntent().getAction());
        {
            Intent intent1 = new Intent(getIntent().getAction());
            intent1.setAction(getIntent().getAction());
            if (getIntent().getParcelableExtra(Intent.EXTRA_STREAM) != null)
                intent1.putExtras(getIntent());
            intent1.putExtra(Intent.EXTRA_TEXT, getIntent().getStringExtra(Intent.EXTRA_TEXT));
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent1); //發送廣播訊息
        }
        startActivity(intent);
        finish();
//        Intent intent = getIntent();
//        if (intent != null) {
//
//            String action = intent.getAction();
//            String type = intent.getType();
//
//            StringUtils.HaoLog("testWebActivity= action =" + action);
//            StringUtils.HaoLog("testWebActivity= type =" + type);
//
//        }else
//        {
//            StringUtils.HaoLog("testWebActivity= intent=null" );
//        }
    }
}
