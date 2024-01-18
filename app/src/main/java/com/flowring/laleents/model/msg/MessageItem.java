package com.flowring.laleents.model.msg;

import android.webkit.URLUtil;

import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.tools.FormatUtils;
import com.flowring.laleents.tools.StringUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MessageItem extends MessageInfo {

    public static class WebLink {
        private final String title;
        private final String content;
        private String pic_url;

        public WebLink(String title, String content) {
            this.title = title;
            this.content = content;
            this.pic_url = "";
        }

        public WebLink(String title, String content, String pic_url) {
            this.title = title;
            this.content = content;
            this.pic_url = pic_url;
        }


        public String getTitle() {
            return this.title;
        }

        public String getContent() {
            return this.content;
        }

        public String getPictureUrl() {
            return this.pic_url;
        }

        public void setPictureUrl(String url) {
            this.pic_url = url;
        }
    }

    public class ReVideoInfo {
        public String id = "fileId";
        public String title = "flowring.mp4";
        public String mimetype = "mp4";
        public long size = 20000;
        public String thumbnailUrl = "https://imgur.dcard.tw/tn729YT.gif";
        public String url = "https://imgur.dcard.tw/tn729YT.gif";
        public long uploadTs = 1650438532;
        public int duration = 10;

        public String getUrl() {
            return url;
        }

        public String getMimeType() {
            return mimetype;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }
    }

    public class ReCardInfo {


        String front_avatar_url = "front_avatar_url";
        String urge_avatar_url = "urge_avatar_url";

        public String getAvatarUrl() {
            if (front_avatar_url != null)
                return front_avatar_url;
            if (urge_avatar_url != null)
                return urge_avatar_url;
            return null;
        }

        String frontUserName = "frontUserName";
        String urgeUserName = "urgeUserName";

        public String getUserName() {
            if (frontUserName != null)
                return frontUserName;
            if (urgeUserName != null)
                return urgeUserName;
            return null;
        }

        public String notifyType = "notifyType";
        public String keyword = "keyword";
        public String processName = "processName";
        public String rootUserName = "rootUserName";
        public String duedate = "duedate";
        public String url = "https://imgur.dcard.tw/tn729YT.gif";
        public int priority = 2;
    }

    public class ReLocation {


        public class GeoInfo {
            public double lat = 0;
            public double lon = 0;
        }

        public String thumbnailUrl = "https://imgur.dcard.tw/tn729YT.gif";
        public String url = "https://imgur.dcard.tw/tn729YT.gif";
        public GeoInfo geoInfo = new GeoInfo();

        public String getUrl() {
            return url;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }
    }

    public class ReImageInfo {
        public String id = "fileId";
        public String title = "flowring.gif";
        public String mimetype = "gif";
        public long size = 20000;
        public String thumbnailUrl = "https://imgur.dcard.tw/tn729YT.gif";
        public String url = "https://imgur.dcard.tw/tn729YT.gif";
        public long uploadTs = 1650438532;

        public String getUrl() {
            return url;
        }

        public String getMimeType() {
            return mimetype;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }
    }

    public class ReFileInfo {
        public String id = "fileId";
        public String title = "flowring.gif";
        public String mimetype = "gif";
        public long size = 20000;
        public String url = "https://imgur.dcard.tw/tn729YT.gif";
        public long uploadTs = 1650438532;

        public String getUrl() {
            return url;
        }

        public String getMimeType() {
            return mimetype;
        }

        public boolean isFileExpired() {
            StringUtils.HaoLog("isFileExpired uploadTs=" + uploadTs + " getTime=" + new Date().getTime());
            return (uploadTs > new Date().getTime());
        }
    }

    public class ReStickerInfo {
        public String stickerId = "";
        public String stickerUrl = "";
    }

    public static class ReMicroAppCard {
        public String name;
        public long id;
        public String url;
        public boolean publish;
        public String downloadPicUrl;
        public int queues;
        public int appTypeId;

    }

    public boolean isRedactInRoom = false;


    public int getLayout() {
        return -1;

    }

    public boolean isRoomListMsg() {
        switch (type) {
            case "lale.message.sticker":
            case "lale.image.received":
            case "lale.file.received":
            case "lale.member.join":
            case "lale.member.left":
            case "lale.call.spendtime":
            case "lale.call.request":
            case "lale.video.received":
            case "lale.audio.received":
            case "lale.location.received":
            case "lale.message.received":
            case "lale.message.app":
            case "lale.message.system":
            case "lale.message.received.reply":
            case "lale.ecosystem.af.notify":
                return true;
            default:
                return false;
        }
    }

    public boolean canReRdit() {
        return isMainUser() && isRedactInRoom && ("lale.message.received".equals(type) ||
                "lale.message.announcement".equals(type) ||
                "lale.message.received.reply".equals(type));
    }

    public boolean CanCheckMessage() {
        return true;
    }

    public boolean isMainUser() {
        String loginUserID = UserControlCenter.getUserMinInfo().userId;
        return loginUserID.equals(getUserID());
    }

    public boolean canRedactVisible() {
        if (isMainUser()) {
            long nowTS = System.currentTimeMillis();
            long sec = (nowTS - getCreatedAt().getTime()) / 1000;
            if (sec < 86400) {
                return true;
            }
        }
        return false;
    }

    private boolean isSelected = false;
    private boolean isMultiChoice = false;
    private WebLink webLink;
    String avatar_id = "";
    int R_avatar_id = -1;
    String user_name = "";
    public Object itemData = null;

    public WebLink getWebLink() {
        return this.webLink;
    }

    public void setWebLink(WebLink webLink) {
        this.webLink = webLink;
    }

    public boolean isWebUrl() {
        return URLUtil.isValidUrl(msg);
    }

    public boolean isMicroAppCard() {
        //todo微服務
        return URLUtil.isValidUrl(msg);
    }

    public boolean isUserCard() {
        //todo是使用者卡片
        return URLUtil.isValidUrl(msg);
    }

    public String getVoiceUrl() {
        return "";
    }

    public ReVideoInfo getVideo() {
        return new Gson().fromJson(content, ReVideoInfo.class);
    }

    public String getSpendtime() {
        try {
            JSONObject jsonObject = new JSONObject(content);

            String type = "audio".equals(jsonObject.optString("type")) ? "語音" : "視訊";

            String duration = FormatUtils.formatDateTime(jsonObject.optLong("duration") / 1000);
            String strDesc = String.format("%s\n已結束%s通話", duration, type);
            StringUtils.HaoLog("lale.call.spendtime=" + "" + strDesc);
            return strDesc;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }


    public String getAvatar() {
        return avatar_id;
    }

    public int getRAvatar() {
        return R_avatar_id;
    }

    public MessageItem setRAvatar(int avatar) {
        R_avatar_id = avatar;
        return this;
    }


    public String getUserName() {
        return user_name;
    }

    public void setUserName(String name) {
        user_name = name;
    }


    public int getStickerResource() {
        return -1;
    }

    public MessageInfo getTargetEvent() {
        try {
            return new MessageInfo(new JSONObject(content).getJSONObject("targetEvent"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ReImageInfo getImage() {
        return new Gson().fromJson(content, ReImageInfo.class);
    }

    public ReStickerInfo getReStickerInfo() {
        return new Gson().fromJson(content, ReStickerInfo.class);
    }

    public String getFileName() {
        return "";
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean b) {
        isSelected = b;
    }

    public ReLocation getLocation() {
        return new Gson().fromJson(content, ReLocation.class);
    }

    public ReMicroAppCard getMicroAppCard() {
        return new Gson().fromJson(content, ReMicroAppCard.class);
    }

    public ReFileInfo getFile() {
        return new Gson().fromJson(content, ReFileInfo.class);
    }

    public ReCardInfo getCard() {

        try {
            return new Gson().fromJson(new JSONObject(content).getString("data"), ReCardInfo.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ReCardInfo();
    }

    public boolean getIsMultiChoice() {
        return isMultiChoice;
    }

    public void setIsMultiChoice(boolean b) {
        isMultiChoice = b;
    }

    public MessageItem setAvatar(String avatar_id) {
        this.avatar_id = avatar_id;
        return this;
    }
}
