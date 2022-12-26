package com.flowring.laleents.model;

public class HttpReturn {
    public int status = 500;
    public String msg = "連線失敗";
    public Object data = null;

    public HttpReturn putMsg(String msg) {
        this.msg = msg;
        return this;

    }

    ;
}

