package com.flowring.laleents.ui.main.webBody;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.flowring.laleents.R;
import com.flowring.laleents.tools.ActivityUtils;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.ui.model.MainAppCompatActivity;
import com.flowring.laleents.ui.widget.qrCode.ScanCaptureActivity;

public class EimLoginActivity extends MainAppCompatActivity {
    public Button btn_login;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_eim);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(view -> {

            activityReturn = new CallbackUtils.ActivityReturn() {
                @Override
                public void Callback(androidx.activity.result.ActivityResult activityResult) {
                    if (activityResult.getResultCode() == Activity.RESULT_OK) {
                        String SCAN_QRCODE = activityResult.getData().getStringExtra("SCAN_QRCODE");
                        StringUtils.HaoLog("結果:" + SCAN_QRCODE);
                        if (SCAN_QRCODE != null)
                            ScanCaptureActivity.Loginback(EimLoginActivity.this, SCAN_QRCODE);
                    }
                }
            };
            ActivityUtils.gotoQRcode(this, ScanCaptureActivity.ScanCaptureType.Json, ActivityResult);
        });
    }
}
