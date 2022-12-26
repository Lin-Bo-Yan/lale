package com.flowring.laleents.model.room.albumIteam;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class AlbumIteam implements Parcelable {

    /**
     * {
     * "roomAlbumId": "album_1547517038233255936",
     * "roomId": "room_1544227899018706944",
     * "name": "新相簿",
     * "userId": "test1",
     * "createdTime": "2022-07-14 09:43:08.069",
     * "lastUpdatedTime": "2022-07-14 09:43:08.069",
     * "amount": 1,
     * "coverImage": [
     * "https://laledev0.flowring.com/fileserver/file/thumbnail/7d644577cd294e4c8d048b625c2b6eab"
     * ]
     * }
     */

    public String roomAlbumId = "";
    public String roomId = "";
    public String creator = "";
    public String createdTime = "0";
    public String lastUpdatedTime = "0";
    public int amount = 0;
    public String userId;
    public List<String> coverImage = new ArrayList<>();
    private String name = "";
    public boolean isSelect = false;

    public AlbumIteam() {
        roomAlbumId = "";
        name = "";
        roomId = "";
        creator = "";
        createdTime = "0";
        lastUpdatedTime = "0";
        amount = 0;
        coverImage = new ArrayList<>();
    }

    private AlbumIteam(Parcel source) {
        roomAlbumId = source.readString();
        name = source.readString();
        roomId = source.readString();
        creator = source.readString();
        createdTime = source.readString();
        lastUpdatedTime = source.readString();
        amount = source.readInt();
        coverImage = source.createStringArrayList();
    }


    public static final Creator<AlbumIteam> CREATOR = new Creator<AlbumIteam>() {
        @Override
        public AlbumIteam createFromParcel(Parcel in) {
            return new AlbumIteam(in);
        }

        @Override
        public AlbumIteam[] newArray(int size) {
            return new AlbumIteam[size];
        }
    };

    public String getId() {
        return roomAlbumId;
    }

    public void setId(String id) {
        this.roomAlbumId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<String> getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(List<String> coverImage) {
        this.coverImage = coverImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(roomAlbumId);
        parcel.writeString(roomId);
        parcel.writeString(creator);
        parcel.writeString(createdTime);
        parcel.writeString(lastUpdatedTime);
        parcel.writeInt(amount);
        parcel.writeStringList(coverImage);
    }
}
