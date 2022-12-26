package com.flowring.laleents.model.user;

import java.io.Serializable;

public class TokenInfo implements Serializable {


    public String token = null;
    public String refreshToken = null;
    public long expiration = -1l;//token到期時間
    public long refreshExpiration = -1l;


}
