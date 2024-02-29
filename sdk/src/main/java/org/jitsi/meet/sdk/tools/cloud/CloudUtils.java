package org.jitsi.meet.sdk.tools.cloud;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.jitsi.meet.sdk.log.StringUtils;
import org.jitsi.meet.sdk.model.HttpReturn;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CloudUtils implements ICloudUtils{
    static public ICloudUtils iCloudUtils = new CloudUtils();

    @Override
    public HttpReturn callHeartbeat(String jitsiDomain, String userId, String laleToken) {
        if(jitsiDomain == null || jitsiDomain.isEmpty()){
            return new HttpReturn();
        }

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, new JSONObject(map).toString());

        Request.Builder request = new Request.Builder()
                .url(jitsiDomain + "/lalemessage/api/messages/calling/alive")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer " + laleToken);

        return gethttpReturn(request);
    }

    public HttpReturn gethttpReturn(Request.Builder request) {
        OkHttpClient client = new OkHttpClient.Builder().build();

        try {
            Response response = client.newCall(request.build()).execute();
            if (response.code() == 200) {
                String body = response.body().string();
                HttpReturn httpReturn = new Gson().fromJson(body, HttpReturn.class);
                if (httpReturn != null) {
                    StringUtils.HaoLog(response.request().url().toString(), httpReturn);
                    return httpReturn;
                } else{
                    StringUtils.HaoLog(response.request().url() + " " + response.code() + " body= " + body);
                }
            }
        } catch (IOException | JsonSyntaxException | IllegalStateException e) {
            StringUtils.HaoLog("gethttpReturn error=" + request + " " + e);
            e.printStackTrace();
        }
        return new HttpReturn();
    }
}
