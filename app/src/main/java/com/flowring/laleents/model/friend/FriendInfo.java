package com.flowring.laleents.model.friend;

import java.io.Serializable;

public class FriendInfo implements Serializable {
    static public String getKey() {
        return "friendId";
    }

    public String friendId;
    public String aliasName;
    public String friendAvatarUrl;
    public String friendAvatarThumbnailUrl;
    public String roomId;
    public int status;
    public String userId;

    public FriendInfo() {
        friendId = "";
        aliasName = "";
        friendAvatarUrl = "";
        friendAvatarThumbnailUrl = "";
        roomId = "";
        aliasName = "";
    }
}

