package com.flowring.laleents.model;

public class HttpAfReturn {
    public boolean success = false;
    public String message;
    public String errorMessage = "連線失敗";
    public Object data = null;
    public int total;
    public int code = 500;
    public String stackTrace;
}

