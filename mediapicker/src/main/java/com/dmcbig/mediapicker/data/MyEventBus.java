package com.dmcbig.mediapicker.data;

import com.dmcbig.mediapicker.data.MessageInfo;

import java.util.ArrayList;

public class MyEventBus {

    //此類別用來當作 EventBus 中傳遞的參數物件型別，可在這裡面定義要傳遞的資料。
    private ArrayList<com.dmcbig.mediapicker.data.MessageInfo> imageMsgList;
    private ArrayList<String> imageIdList;

    public ArrayList<com.dmcbig.mediapicker.data.MessageInfo> getMessageList() {
        return imageMsgList;
    }

    public ArrayList<String> getMessageIdList() {
        return imageIdList;
    }

    public void setMessageList(ArrayList<com.dmcbig.mediapicker.data.MessageInfo> imageMsgList) {
        this.imageMsgList = imageMsgList;
    }

    public void setMessageIdList(ArrayList<String> imageIdList) {
        this.imageIdList = imageIdList;
    }
}


