package com.flowring.laleents.model.room;

import java.io.Serializable;

public class PasswordRoomMinInfo implements Serializable {

    public String createTime;     // 建立密碼群組時間
    public String groupId;        // 群組 ID
    public int id;          // ID
    public String password;     // 群組密碼
    public String[] userList = new String[]{};     // 群組人員 ID 列表
    public userInfo[] userInfoList = new userInfo[]{};        // 未讀訊息總數

    public class userInfo   // 群組人員資訊列表
    {
        public String avatar;// 使用者頭像
        public String avatarThumbnailUrl;  // 使用者縮圖頭像 url
        public String avatarUrl;    // 使用者頭像 url
        public String displayName;  // 使用者名稱
        public String userId;    // 使用者 ID
    }

}
