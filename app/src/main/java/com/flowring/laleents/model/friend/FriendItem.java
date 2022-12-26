package com.flowring.laleents.model.friend;

import com.flowring.laleents.model.room.UserInRoom;

public class FriendItem {
    public FriendInfo friendInfo = null;
    public boolean check = false;
    public boolean isShow = true;

    public FriendItem(FriendInfo friendInfo) {
        this.friendInfo = friendInfo;
    }

    public FriendItem(UserInRoom userInRoom) {
        this.friendInfo = new FriendInfo();
        friendInfo.aliasName = userInRoom.displayName;
        friendInfo.aliasName = userInRoom.displayName;
        friendInfo.friendId = userInRoom.userId;
        friendInfo.friendAvatarUrl = userInRoom.avatarThumbnailUrl;

    }

}
