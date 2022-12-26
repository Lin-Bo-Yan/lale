package com.flowring.laleents.model.user;

import com.flowring.laleents.model.eim.EimUserData;

import java.io.Serializable;

public class UserMin implements Serializable {

    public String userId;            // @test:lalepass.lale.im
    public String displayName;          // account
    public String token;       //
    public String refreshToken;
    public long expiration;//token到期時間

    public String avatarUrl;
    public String avatarThumbnailUrl;//縮圖

    public String _type;              ////// 判斷是不是官方帳號 boolean isOfficialAccount

    public long refreshExpiration;

    public ExternalServerSetting externalServerSetting;

    public ExternalServerSetting getExternalServerSetting() {
        return externalServerSetting;
    }

    public EimUserData eimUserData;

    public UserMin() {
        userId = "";
        displayName = "";
        token = "";
        avatarUrl = "";
        _type = "text";
    }


}
