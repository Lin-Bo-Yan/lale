package com.dmcbig.mediapicker.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by dmcBig on 2017/7/4.
 * Media模型，用來表示儲存在設備上的媒體檔案
 * Media 實作了 Parcelable 接口，這表示它可以在 Activity 或 Fragment 之間進行傳遞。
 */

public class Media implements Parcelable {
    public String path;         //path: 媒體檔案的路徑
    public String name;         //name: 媒體檔案的名稱
    public String extension;    //extension: 媒體檔案的副檔名
    public long time;           //time: 媒體檔案的修改時間
    public int mediaType;       //mediaType: 媒體檔案的類型（例如圖片、影片、音樂等等）
    public long size;           //size: 媒體檔案的大小
    public int id;              //id: 媒體檔案的唯一識別碼
    public String parentDir;    //parentDir: 媒體檔案所在的父目錄
    public String mimeType;     //mimeType: 媒體檔案的 MIME type

    public Media(String path, String name, long time, int mediaType, long size, int id, String parentDir, String mimeType) {
        this.path = path;
        this.name = name;
        if (!TextUtils.isEmpty(name) && name.indexOf(".") != -1) {
            this.extension = name.substring(name.lastIndexOf("."), name.length());
        } else {
            this.extension = "null";
        }
        this.time = time;
        this.mediaType = mediaType;
        this.size = size;
        this.id = id;
        this.parentDir = parentDir;
        this.mimeType = mimeType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.name);
        dest.writeString(this.extension);
        dest.writeLong(this.time);
        dest.writeInt(this.mediaType);
        dest.writeLong(this.size);
        dest.writeInt(this.id);
        dest.writeString(this.parentDir);
        dest.writeString(this.mimeType);
    }

    protected Media(Parcel in) {
        this.path = in.readString();
        this.name = in.readString();
        this.extension = in.readString();
        this.time = in.readLong();
        this.mediaType = in.readInt();
        this.size = in.readLong();
        this.id = in.readInt();
        this.parentDir = in.readString();
        this.mimeType = in.readString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Media) {
            Media media = (Media) obj;
            return this.id == media.id;
        }
        return super.equals(obj);
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
}
