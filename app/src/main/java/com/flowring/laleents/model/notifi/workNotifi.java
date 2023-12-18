package com.flowring.laleents.model.notifi;

public class workNotifi {
    // 欄位跟 "AF 寄送訊息到 EIM 或 Lale App " 一樣
    public String msgType;
    public String memID;
    public String userName;
    public String exeMemID;
    public String exeUserName;
    public String notifyType;
    public String tskID;
    public String keyword;
    public String processName;
    public String taskName;
    public String rootUserMemID;
    public String rootUserName;
    public String frontMemID;
    public String frontUserName;
    public String front_avatar_url;
    public String duedate;
    public int priority;
    public String rootTaskProName;
    public String url;
    public String totalTaskCount;

    // 會議 新增、修改、刪除、會議開始通知
    // oldTitle為後端給的push開頭，因設計需要故將原始開頭保留並改成oldTitle
    public String oldTitle;
    public String title = "系統管理員";
    public String content;
    public String button_link;
    public String img_url;
    public MeetInfo meetInfo;
    public String type;

    // 辦公秘書 msgType變數共用
    public int subType = 0;
}