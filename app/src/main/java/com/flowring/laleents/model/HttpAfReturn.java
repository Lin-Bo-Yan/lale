package com.flowring.laleents.model;

public class HttpAfReturn {
    public String msg = "";
    public int code = 500;
    public Object data = null;

    public boolean success = false;
    public String message;
    public String errorMessage = "連線失敗";
    public int total;
    public String stackTrace;
}

