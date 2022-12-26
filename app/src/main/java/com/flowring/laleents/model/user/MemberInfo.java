package com.flowring.laleents.model.user;

import java.io.Serializable;

public class MemberInfo implements Serializable {
    public String userId;
    public String displayName;
    public String avatarThumbnailUrl;

    public MemberInfo() {
        userId = "";
        displayName = "";
        avatarThumbnailUrl = "";

    }
}
