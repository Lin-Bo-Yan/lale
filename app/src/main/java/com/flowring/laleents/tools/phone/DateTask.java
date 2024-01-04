package com.flowring.laleents.tools.phone;

import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.tools.CallbackUtils;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;

import java.io.IOException;
import java.util.TimerTask;

public class DateTask extends TimerTask {
    @Override
    public void run() {
        HttpReturn httpReturn = CloudUtils.iCloudUtils.callHeartbeat(new CallbackUtils.TimeoutReturn() {
            @Override
            public void Callback(IOException timeout) {
                StringUtils.HaoLog("TimerTask 網路異常");
            }
        });
    }
}
