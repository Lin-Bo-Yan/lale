package com.flowring.laleents.tools.room;

import org.json.JSONObject;

public class GlobalDataManager {
    private static final GlobalDataManager ourInstance = new GlobalDataManager();
    private JSONObject closeWebViewData = null;

    public static GlobalDataManager getInstance() {
        return ourInstance;
    }

    private GlobalDataManager() {
    }

    public void setCloseWebViewData(JSONObject data) {
        closeWebViewData = data;
    }

    public JSONObject getCloseWebViewData() {
        return closeWebViewData;
    }

    public void clearCloseWebViewData() {
        closeWebViewData = null;
    }
}
