package com.flowring.laleents.model.user;

import java.io.Serializable;

public class AfTokenInfo implements Serializable {
    public String token = null;
    public String refreshToken = null;
    public Long expiration = null;
    public Long refreshExpiration = null;
    public String deviceId = null;
}
