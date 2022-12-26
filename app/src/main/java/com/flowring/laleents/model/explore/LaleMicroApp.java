package com.flowring.laleents.model.explore;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class LaleMicroApp implements Parcelable {


    public static final Parcelable.Creator<LaleMicroApp> CREATOR = new Parcelable.Creator<LaleMicroApp>() {
        @Override
        public LaleMicroApp createFromParcel(Parcel source) {
            return new LaleMicroApp(source);
        }

        @Override
        public LaleMicroApp[] newArray(int size) {
            return new LaleMicroApp[size];
        }
    };
    public long id;
    public String name;
    public String url;
    public String introduction;
    public boolean publish;
    public long appTypeId;
    public String picture;
    public String pictureMd5;
    public String downloadPicUrl;
    public String downloadRcmPicUrl;
    public boolean cache;

    public LaleMicroApp() {
        id = -1;
        name = "";
        url = "";
        introduction = "";
        publish = false;
        appTypeId = 0;
        picture = "";
        pictureMd5 = "";
        downloadPicUrl = "";
        downloadRcmPicUrl = "";
        cache = false;
    }

    public LaleMicroApp(String jsonString) {
        try {
            JSONObject jsonObj = new JSONObject(jsonString);
            if (jsonObj != null) {
                id = jsonObj.optLong("id");
                name = jsonObj.optString("name");
                url = jsonObj.optString("url");
                publish = jsonObj.optBoolean("publish");
                appTypeId = jsonObj.optLong("appTypeId");
                picture = jsonObj.optString("picture");
                pictureMd5 = jsonObj.optString("pictureMd5");
                downloadPicUrl = jsonObj.optString("downloadPicUrl");
                cache = jsonObj.optBoolean("cache");
                introduction = jsonObj.optString("introduction");
                downloadRcmPicUrl = jsonObj.optString("downloadRcmPicUrl");
            }
        } catch (Exception e) {

        }
    }

    private LaleMicroApp(Parcel source) {
        id = source.readLong();
        name = source.readString();
        url = source.readString();
        publish = source.readByte() != 0;
        appTypeId = source.readLong();
        picture = source.readString();
        pictureMd5 = source.readString();
        downloadPicUrl = source.readString();
        cache = source.readByte() != 0;
        introduction = source.readString();
        downloadRcmPicUrl = source.readString();
    }

    public boolean fromJsonObject(JSONObject jsonObj) {
        try {
            if (jsonObj != null) {
                id = jsonObj.optLong("id");
                name = jsonObj.optString("name");
                url = jsonObj.optString("url");
                publish = jsonObj.optBoolean("publish");
                appTypeId = jsonObj.optLong("appTypeId");
                picture = jsonObj.optString("picture");
                pictureMd5 = jsonObj.optString("pictureMd5");
                downloadPicUrl = jsonObj.optString("downloadPicUrl");
                cache = jsonObj.optBoolean("cache");
                introduction = jsonObj.optString("introduction");
                downloadRcmPicUrl = jsonObj.optString("downloadRcmPicUrl");
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public boolean fromPromotionJsonObject(JSONObject jsonObj) {
        try {
            if (jsonObj != null) {
                id = jsonObj.optLong("microAppId");
                name = jsonObj.optString("name");
                url = jsonObj.optString("url");
                publish = jsonObj.optBoolean("publish");
                appTypeId = jsonObj.optLong("appTypeId");
                downloadPicUrl = jsonObj.optString("downloadPicUrl");
                introduction = jsonObj.optString("introduction");
                downloadRcmPicUrl = jsonObj.optString("downloadRcmPicUrl");
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public boolean fromJsonString(String sJson) {
        try {
            JSONObject jsonObj = new JSONObject(sJson);
            if (jsonObj != null) {
                id = jsonObj.optLong("id");
                name = jsonObj.optString("name");
                url = jsonObj.optString("url");
                publish = jsonObj.optBoolean("publish");
                appTypeId = jsonObj.optLong("appTypeId");
                picture = jsonObj.optString("picture");
                pictureMd5 = jsonObj.optString("pictureMd5");
                downloadPicUrl = jsonObj.optString("downloadPicUrl");
                cache = jsonObj.optBoolean("cache");
                introduction = jsonObj.optString("introduction");
                downloadRcmPicUrl = jsonObj.optString("downloadRcmPicUrl");
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    public String toJsonString() {

        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", id);
            jsonObj.put("name", name);
            jsonObj.put("url", url);
            jsonObj.put("publish", publish);
            jsonObj.put("appTypeId", appTypeId);
            jsonObj.put("picture", picture);
            jsonObj.put("pictureMd5", pictureMd5);
            jsonObj.put("downloadPicUrl", downloadPicUrl);
            jsonObj.put("cache", cache);
            return jsonObj.toString();

        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(url);
        parcel.writeByte((byte) (publish ? 1 : 0));
        parcel.writeLong(appTypeId);
        parcel.writeString(picture);
        parcel.writeString(pictureMd5);
        parcel.writeString(downloadPicUrl);
        parcel.writeByte((byte) (cache ? 1 : 0));
        parcel.writeString(introduction);
        parcel.writeString(downloadRcmPicUrl);
    }
}
