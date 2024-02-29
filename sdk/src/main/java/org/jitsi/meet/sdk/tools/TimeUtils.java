package org.jitsi.meet.sdk.tools;

import org.jitsi.meet.sdk.tools.TaskUtils.DateTask;
import java.util.Timer;

public class TimeUtils {
    public static Timer timer;

    public static void startCallHeartbeat(String laleToken, String userId, String serverURL){
        timer = new Timer();
        DateTask heartbeatTask  = new DateTask(laleToken, userId, serverURL);
        long period = 5000; // 5秒
        timer.scheduleAtFixedRate(heartbeatTask, 0, period); //scheduleAtFixedRate 補償機制
    }

    public static void endCallHeartbeat(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }
}
