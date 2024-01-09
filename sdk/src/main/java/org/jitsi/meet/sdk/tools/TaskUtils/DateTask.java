package org.jitsi.meet.sdk.tools.TaskUtils;

import org.jitsi.meet.sdk.log.StringUtils;
import org.jitsi.meet.sdk.model.HttpReturn;
import org.jitsi.meet.sdk.tools.cloud.CloudUtils;

import java.util.TimerTask;

public class DateTask extends TimerTask {
    private final String laleToken;
    private final String userId;
    private final String serverURL;

    public DateTask(String laleToken, String userId, String serverURL) {
        this.laleToken = laleToken;
        this.userId = userId;
        this.serverURL = serverURL;
    }

    @Override
    public void run() {
        HttpReturn httpReturn = CloudUtils.iCloudUtils.callHeartbeat(serverURL,userId,laleToken);
    }
}
