package com.flowring.laleents.model.explore;

import android.os.Parcel;
import android.os.Parcelable;

public class Microapp implements Parcelable {
    public String microAppTabId;
    public String microAppMenuId;
    public String microAppId;
    public String name;
    public String url;
    public int siblingOrder = -1;
    public boolean publish = false;
    public String picture;
    public String pictureUrl;

    public Microapp() {
        microAppTabId = "";
        microAppMenuId = "";
        microAppId = "";
        name = "";
        url = "";
        siblingOrder = -1;
        publish = false;
        picture = "";
        pictureUrl = "";
    }

    public Microapp(Parcel in) {
        microAppTabId = in.readString();
        microAppMenuId = in.readString();
        microAppId = in.readString();
        name = in.readString();
        url = in.readString();
        siblingOrder = in.readInt();
        publish = in.readByte() != 0;
        picture = in.readString();
        pictureUrl = in.readString();
    }

    public static final Creator<Microapp> CREATOR = new Creator<Microapp>() {
        @Override
        public Microapp createFromParcel(Parcel in) {
            return new Microapp(in);
        }

        @Override
        public Microapp[] newArray(int size) {
            return new Microapp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {

        parcel.writeString(microAppTabId);
        parcel.writeString(microAppMenuId);
        parcel.writeString(microAppId);
        parcel.writeString(name);
        parcel.writeString(url);
        parcel.writeInt(siblingOrder);
        parcel.writeByte((byte) (publish ? 1 : 0));
        parcel.writeString(picture);
        parcel.writeString(pictureUrl);
    }


}
