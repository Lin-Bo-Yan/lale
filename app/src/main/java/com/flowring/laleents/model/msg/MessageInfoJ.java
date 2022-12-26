package com.flowring.laleents.model.msg;


import java.io.Serializable;

public class MessageInfoJ implements Serializable {


    public String id;
    public String type;
    public Content content;
    public String roomId;
    public String sender;
    public String msg;
    public long timestamp;

    public class Content {
        public String msg;
    }

    public String getText() {
        if (content == null)
            return "";
        return content.msg;
    }

}


