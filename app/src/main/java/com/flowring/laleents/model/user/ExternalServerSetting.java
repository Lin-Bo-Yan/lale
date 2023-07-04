package com.flowring.laleents.model.user;

import java.io.Serializable;

public class ExternalServerSetting implements Serializable {
    public String messageServerUrl = "http://192.168.9.110:8080";
    public String jitsiServerUrl = "https://laledev0.flowring.com";
    public String mqttUrl = "wss://192.168.9.110:1883";
    public String callServiceUrl;
    public String applicationVersion;
    public String ret;
}
