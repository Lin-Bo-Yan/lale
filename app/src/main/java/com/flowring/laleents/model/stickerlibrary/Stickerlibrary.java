package com.flowring.laleents.model.stickerlibrary;

import java.util.ArrayList;

public class Stickerlibrary {
    static public String getKey() {
        return "stickerLibraryId";
    }

    public String stickerLibraryId;
    public String name;
    public String authorId;
    public String imageId;
    public String imgUrl;
    public String createdTime;
    public boolean isDefault;
    public int status;
    public ArrayList<Sticker> stickerList;
    public boolean userOwns = false;
}

