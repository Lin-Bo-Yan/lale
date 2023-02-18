package com.flowring.laleents.model.eim;

import com.flowring.laleents.model.user.ExternalServerSetting;
import com.flowring.laleents.model.user.UserMin;
import com.flowring.laleents.tools.phone.AllData;

public class EimUserData {

    public boolean isLaleAppEim = false;
    public boolean isLaleAppWork = false;
    public String af_url = null;
    public String af_mem_id = null;
    public String af_login_id = null;
    public String af_token = null;
    public String af_wfci_service_url = null;


    public String lale_third_party_identifier = null;
    public String lale_server = null;
    public String lale_token = null;
    public String refresh_token = null;
    public String lale_user_id = null;
    public LaleExternalServerInfo lale_external_server_info = null;
    public boolean isLaleCall = false;
    public String call_service_url = "";


    public UserMin getUserMin() {
        UserMin userMin = new UserMin();
        userMin.userId = lale_user_id;

        if (!isLaleAppEim){
            userMin.userId = af_mem_id;
        }
        userMin.token = lale_token;
        userMin.refreshToken = refresh_token;

        AllData.setMainServer(lale_server);
        if (lale_external_server_info != null) {
            userMin.externalServerSetting = new ExternalServerSetting();

            userMin.externalServerSetting.jitsiServerUrl = lale_external_server_info.jitsiServerUrl;
            userMin.externalServerSetting.messageServerUrl = lale_external_server_info.messageServerUrl;
            userMin.externalServerSetting.mqttUrl = lale_external_server_info.mqttUrl;
        }
        userMin.eimUserData = this;
        return userMin;

    }
}

        /*
        userMin.displayName=;
        userMin.refreshToken;
        userMin. expiration;//token到期時間

        userMin. avatarUrl;
        userMin. avatarThumbnailUrl;//縮圖
        userMin. refreshExpiration;
        */