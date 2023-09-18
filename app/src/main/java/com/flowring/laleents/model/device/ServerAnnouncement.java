package com.flowring.laleents.model.device;

public class ServerAnnouncement {
    public String annId ;
    public int annType;
    public String startTime;
    public String endTime;
    public String content;
    public Boolean enabled;

    public ServerAnnouncement() {
        annId = null;
        annType = 0;
        startTime = null;
        endTime = null;
        content = null;
        enabled = false;
    }


}
