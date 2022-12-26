package com.flowring.laleents.model.room;

import android.os.Parcel;
import android.os.Parcelable;

import com.flowring.laleents.model.user.MemberInfo;
import com.google.gson.Gson;

public class UserInRoom implements Parcelable {
    public static String[] getKeys() {
        return new String[]{"roomId", "userId"};
    }

    public String roomId = null;
    public String userId;
    public String avatar;
    public String avatarUrl;
    public String avatarThumbnailUrl;
    public String displayName;

    public MemberInfo getMemberInfo() {

        return new Gson().fromJson(new Gson().toJson(this), MemberInfo.class);
    }

    public UserInRoom() {
        userId = null;
        avatar = null;
        avatarUrl = null;
        avatarThumbnailUrl = null;
        displayName = null;
    }

    protected UserInRoom(Parcel in) {
        userId = in.readString();
        avatar = in.readString();
        avatarUrl = in.readString();
        avatarThumbnailUrl = in.readString();
        displayName = in.readString();
    }

    public static final Creator<UserInRoom> CREATOR = new Creator<UserInRoom>() {
        @Override
        public UserInRoom createFromParcel(Parcel in) {
            return new UserInRoom(in);
        }

        @Override
        public UserInRoom[] newArray(int size) {
            return new UserInRoom[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(avatar);
        dest.writeString(avatarUrl);
        dest.writeString(avatarThumbnailUrl);
        dest.writeString(displayName);
    }
}
