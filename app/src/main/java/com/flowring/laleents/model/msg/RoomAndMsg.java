package com.flowring.laleents.model.msg;

import com.flowring.laleents.model.room.RoomMinInfo;

public class RoomAndMsg {
    public RoomMinInfo room;
    public MessageInfok event;
    public int unreadCount = 0;
    public boolean hasTag = false;
    public boolean onCalling = false;
}
