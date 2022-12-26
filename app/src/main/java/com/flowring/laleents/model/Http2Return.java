package com.flowring.laleents.model;

public class Http2Return {
    public int code = 500;
    public String msg = "連線失敗";
    public Object data = null;

    public Http2Return putMsg(String msg) {
        this.msg = msg;
        return this;

    }

}

