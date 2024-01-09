package org.jitsi.meet.sdk.tools.cloud;

import androidx.annotation.WorkerThread;

import org.jitsi.meet.sdk.model.HttpReturn;

public interface ICloudUtils {
    /**
     * 通話心跳包
     */
    @WorkerThread
    HttpReturn callHeartbeat(String jitsiDomain,String userId,String laleToken);
}
