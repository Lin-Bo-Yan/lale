package com.flowring.laleents.tools.phone;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.flowring.laleents.tools.CommonUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;

public class DefinedUtils {

    public static final int REQUEST_LOGIN = 0x11;               //10進字：17 ，請求登錄
    public static final int REQUEST_CHATROOM = 0x12;            //10進字：18， 請求聊天室
    public static final int REQUEST_SCAN_QRCODE = 0x13;         //10進字：19， 請求_掃描_二維碼
    public static final int REQUEST_MENU_MEDIA = 0x14;          //10進字：20， 請求_菜單_媒體
    public static final int REQUEST_MENU_CAMERA = 0x15;         //10進字：21， 請求_菜單_相機
    public static final int REQUEST_LOCATION = 0x16;            //10進字：22， 請求位置
    public static final int REQUEST_MAP_MARKED = 0x17;          //10進字：23， 請求_地圖_標記
    public static final int REQUEST_SEARCH = 0x18;              //10進字：24， 請求_搜索
    public static final int REQUEST_VIDEO_CALLING = 0x19;       //10進字：25， 請求_視頻_通話
    public static final int REQUEST_VOICE_CALLING = 0x20;       //10進字：32， 請求_語音_通話
    public static final int REQUEST_CHOOSE_PHOTO = 0x21;        //10進字：33， 請求_選擇_照片
    public static final int REQUEST_LALETON_CARD = 0x22;        //10進字：34，
    public static final int REQUEST_CREATE_GROUP = 0x23;        //10進字：35，請求創建組
    public static final int REQUEST_MODIFY_MEMBER = 0x24;       //10進字：36，請求修改成員
    public static final int REQUEST_ROOM_SETTING = 0x25;        //10進字：37，要求房間設置
    public static final int REQUEST_SET_PASSWORD = 0x26;        //10進字：38，請求設置密碼
    public static final int REQUEST_PROFILE_CAMERA = 0x27;      //10進字：39，請求配置文件相機
    public static final int REQUEST_CHOOSE_FILE = 0x28;         //10進字：40，請求選擇文件
    public static final int REQUEST_FORWARD_MESSAGE = 0x29;     //10進字：41，請求轉發消息
    public static final int REQUEST_CHOOSE_CONTACT = 0x30;      //10進字：48，請求_選擇_聯繫
    public static final int REQUEST_IMAGE_PICKER = 0x31;        //10進字：49，請求圖像選擇器
    public static final int REQUEST_SCAN_FORM_WEB = 0x32;       //10進字：50，請求掃描表格網頁
    public static final int REQUEST_MANAGE_STICKER = 0x34;      //10進字：52，請求管理貼紙
    public static final int REQUEST_PREVIEW_CARD = 0x35;        //10進字：53，請求預覽卡
    public static final int REQUEST_SCAN_DOMAIN = 0x36;         //10進字：54，請求掃描域
    public static final int REQUEST_CARD_CAMERA = 0x37;         //10進字：55，請求卡片相機
    public static final int REQUEST_CHROME_TAB = 0x38;          //10進字：56，請求_Chrome_標籤
    public static final int REQUEST_ZOOM_IMAGE = 0x39;          //10進字：57，請求縮放圖像
    public static final int REQUEST_LALE_APP = 0x40;            //10進字：64，請求LALE_APP
    public static final int REQUEST_MY_COLLECT = 0x41;          //10進字：65，請求我的收藏
    public static final int REQUEST_COLLECT_FUNCTION = 0x42;    //10進字：66，請求收集功能
    public static final int REQUEST_OPEN_NEW = 0x43;            //10進字：67，請求_打開_新
    public static final int REQUEST_FRIEND_CONTENT = 0x44;      //10進字：68，請求_好友內容
    public static final int REQUEST_BUSINESS_CARD = 0x45;       //10進字：69，請求_名片
    public static final int REQUEST_COMPANY_TAB = 0x46;         //10進字：70，請求_公司_TAB
    public static final int REQUEST_SPEECH_TO_TEXT = 0x47;      //10進字：71，請求_語音轉文本
//    public static final int REQUEST_CROP_PHOTO    = 0x33;

    public static final int MSG_STATE_TEXT_IN = 1;              //別人傳訊息
    public static final int MSG_STATE_TEXT_OUT = 2;             //自己傳訊息
    public static final int MSG_STATE_IMAGE_IN = 3;             //別人傳圖片
    public static final int MSG_STATE_IMAGE_OUT = 4;            //自己傳圖片
    public static final int MSG_STATE_VIDEO_IN = 5;             //別人傳視頻
    public static final int MSG_STATE_VIDEO_OUT = 6;            //自己傳視頻
    public static final int MSG_STATE_LOCATION_IN = 7;          //別人傳座標
    public static final int MSG_STATE_LOCATION_OUT = 8;         //自己傳座標
    public static final int MSG_STATE_VOICE_IN = 9;             //別人傳音訊
    public static final int MSG_STATE_VOICE_OUT = 10;           //自己傳音訊

    public static final int FILE_CHOOSER_RESULT_CODE = 1234;
    public static final int ACCESS_FINE_LOCATION_CODE = 1235;
    public static final int READ_CONTACTS_CODE = 1236;          //16進字：4D4
    public static final int RECORD_AUDIO_CODE = 1237;           //16進字：4D5
    public static final int CAMERA_CODE = 1238;                 //16進字：4D6
    public static final int PHOTOGRAPH_CODE = 1239;             //16進字：4D7
    public static final int ALBUM_CODE = 1240;                  //16進字：4D8
    public static final int TAKE_FILE_CODE = 1243;              //16進字：4DB

    public static final int DEFAULT_BUFFER_SIZE = 4096;
    public static final String FRIEND_TYPE_INVITE = "好友邀請";
    public static final String FRIEND_TYPE_FAVORITE = "我的最愛";
    public static final String FRIEND_TYPE_FRIEND = "好友";
    public static final String FRIEND_TYPE_LALETON = "服務通";
    public static final String FRIEND_NOT_FRIEND = "非好友";
    public static final String FRIEND_SEND_INVITE = "已送出邀請";
    public static final String FRIEND_TYPE_BAN = "封鎖好友";

    public static final String GROUP_TYPE_GROUP = "群組";
    public static final String GROUP_NOT_GROUP = "非群組";
    public static final String GROUP_TYPE_INVITE = "群組邀請";
    public static final String GROUP_TYPE_SERVICETON = "群組服務通";

    public static final String ROOM_KIND_FRIEND = "friend";
    public static final String ROOM_KIND_GROUP = "group";

    public static final int ROOM_TYPE_LIFE = 1;
    public static final int ROOM_TYPE_MEMIA = 2;
    public static final int ROOM_TYPE_SERVICE_TON = 3;
    public static final int ROOM_TYPE_WORK = 4;
    public static final int ROOM_TYPE_FREE_WORK = 5;

    public static final String MESSAGE_TYPE_IMAGE = "m.image";
    public static final String MESSAGE_TYPE_TEXT = "m.text";
    public static final String MESSAGE_TYPE_LOCATION = "m.location";
    public static final String MESSAGE_TYPE_VOICE = "m.audio";
    public static final String MESSAGE_TYPE_FILE = "m.file";
    public static final String MESSAGE_TYPE_VIDEO = "m.video";
    public static final String MESSAGE_TYPE_REPLY = "m.reply";
    public static final String MESSAGE_TYPE_NOTICE = "m.notice"; //通知
    public static final String MESSAGE_USERCARD_TAG = "<USER_CARD>"; //名片
    public static final String MESSAGE_MICROAPPCARD_TAG = "<MICROAPP_CARD>"; //微服務
    public static final String MESSAGE_LALETON_NEWS_TAG = "<NEWS_CARD>"; //快報
    public static final String MESSAGE_LALETON_NEWS_ART_TAG = "<NEWS_ART>"; //快報文章
    public static final String MESSAGE_LALETON_SERVICE_TAG = "<SERVICE_CARD>"; //公號
    public static final String MESSAGE_ANNOUNCE_TAG = "<GROUP_ANNOUNCE>"; //全部@Tag
    public static final String MESSAGE_AT_TAG = "<GROUP_AT>"; //@好友Tag
    public static final String MESSAGE_FORM_TAG = "<MYTASK_NOTIFICATION_CARD>"; //到關通知
    public static final String MESSAGE_ROBOTCARD_TAG = "<ROBOT_CARD>"; //六宮格機器人
    public static final String MESSAGE_ROBOTORDERCARD_TAG = "<ROBOT_ORDER_CARD>"; //指令卡片


    public static final String FOLDER_AUDIO = "audio";
    public static final String FOLDER_FILES = "files";
    public static final String FOLDER_VIDEO = "video";
    public static final String FOLDER_PICTURE = "picture";
    public static final String FOLDER_BACKGROUND = ".background";
    public static final String FOLDER_STICKER = ".sticker";

    public static final String ACTION_NEW_MESSAGE = "com.flowring.lale.NEW_MESSAGE";
    public static final String ACTION_OTHER_DEVICE_MESSAGE = "com.flowring.lale.OTHER_DEVICE_MESSAGE";
    public static final String ACTION_SERVICE_TON = "com.flowring.lale.SERVICE_TON";
    public static final String ACTION_ANSWER_CALL = "com.flowring.lale.ANSWER_CALL";
    public static final String ACTION_REJECT_CALL = "com.flowring.lale.REJECT_CALL";
    public static final String ACTION_BUSY_CALL = "com.flowring.lale.BUSY_CALL";
    public static final String ACTION_REDACT_MSG = "com.flowring.lale.REDACT_MSG";
    public static final String ACTION_CHANGE_NAME = "com.flowring.lale.CHANGE_NAME";
    public static final String ACTION_LOGIN_WX = "Login_WX";
    public static final String ACTION_FIREBASE_MESSAGE = "FIREBASE_MESSAGE";
    public static final String ACTION_FRIEND_INVITE = "FRIEND_INVITE";

    public static final String EXTRA_ROOM_ID = "room_id";
    public static final String EXTRA_SENDER_ID = "sender_id";
    public static final String EXTRA_MESSAGEINFO = "message_info";
    public static final String EXTRA_FIRM_ID = "firm_id";
    public static final String EXTRA_LALETON_NAME = "laleton_name";
    public static final String EXTRA_LALETON_URL = "laleton_url";
    public static final String EXTRA_REDACT_MESSAGE = "redact_message";
    public static final String EXTRA_REDACT_MESSAGE_ID = "redace_message_id";
    public static final String EXTRA_NEW_ROOM_NAME = "new_room_name";
    public static final String EXTRA_WX_UNIONID = "wx_unionid";

    public static final int LOGIN_TYPE_FB = 1;
    public static final int LOGIN_TYPE_GOOGLE = 2;
    public static final int LOGIN_TYPE_LINE = 3;
    public static final int LOGIN_TYPE_WECHAT = 4;
    public static final int LOGIN_TYPE_AGENT_FLOW = 6;

    public static final float MSG_WIDTH_WEIGHT = 0.58f;
    public static final int MAX_FILE_SIZE = 100000000;

    public static final int MIRCOTAB_TYPE_APP = 1;
    public static final int MIRCOTAB_TYPE_WEB = 2;

    public static final int LAYOUT_TYPE_LINEARLAYOUT = 1;
    public static final int LAYOUT_TYPE_RELATIVELAYOUT = 2;
    public static final int LAYOUT_TYPE_COORDINATORLAYOUT = 3;

    public static final String DOMAIN_LALEPASS = "https://lalepass.lale.im";
    public static final String DOMAIN_LALEJAR = "https://lalepass.lale.im";
    public static final String DOMAIN_MATRIX = "https://lalepass.lale.im";
    public static final String DOMAIN_JITSI = "https://meet.lale.im";
    public static final String DOMAIN_DEV6 = "https://laledev6.flowring.com";
    public static final String DOMAIN_QTS = "https://qalale01.flowring.com";
    public static final String DOMAIN_LALEPLUS = "https://devmtx2.lale.im";
    public static final String DOMAIN_WEBSOCKET = "ws://llwss.lale.im:8000/_lalesockets/client/v1_alpha/stream/";

    public static final String mmURL = "https://memia.lale.im/Memia/mall.do?app=lale2c&token=$token";
    public static final String kkURL = "https://konko.flowring.com/lale.do?token=$token";
    public static final String uuURL = "https://wuliao.flowring.com/lale.do?token=$token";
    public static final String workURL = "https://work.lale.im/WebAgenda/dau/index.html?token=$token";

    public static final String serviceCardURL = "https://news.lale.im/publicOfficial/index.html#/singlePublic/$pid/firm/$fid?token=$token";
    public static final String laleNewsURL = "https://news.lale.im/journalPublish.do?pid=106&token=$token";
    public static final long laleFirmID = 22;

    public static final String newsURL = "https://news.lale.im/_news/home?token=$token";
    public static final String newsMoreURL = "https://news.lale.im/_news/member?token=$token";

    public static final String userKURL = "https://konko.flowring.com/lale.do?fid=$fid&token=$token";
    public static final String userMURL = "https://memia.lale.im/Memia/mall.do?fid=$fid&token=$token";
    public static final String userWURL = "https://wuliao.flowring.com/lale.do?fid=$fid&token=$token";
    public static final String userEURL = "https://wuliao.flowring.com/lale.do?fid=$fid&token=$token";

    public static final String productURL = "https://www.lale.im/faq/index.html";
    public static final String privacyURL = "https://www.lale.im/laleapp_privacy_policy.html";
    public static final String lalePointURL = "https://points.lale.im/LalePoints/html/myPoints.html?token=$token";
    public static final String LaleTicketURL = "https://points.lale.im/coupons/html/myCoupons.html?token=$token";
    public static final String laleIntroduceURL = "https://news.lale.im/publicOfficial/index.html#/singlePublic/107/firm/22?token=$token";

    public static final String appCityURL = "http://laledev3.flowring.com:8082/smartcity/$token";//"http://laledev3.flowring.com:8082/";
    public static final String searchMaskURL = "https://app.lale.im:8443/MAP";//"https://flowringmap.ddns.net/MAP/";
    public static final String medCheckURL = "http://61.218.104.119/CHECK/index.html?token=$token";
    public static final String emedCheckURL = "http://61.218.104.119/CHECK/index2.html?token=$token";

    public static final String stickerStoreURL = "https://stickermarket.lale.im/stickermarket/homePage.jsp?token=$token";

    public static final String roomAlbumURL = "https://album.lale.im/#/{function}?roomId={roomId}&token=$token";

    public static final String ACCOUNT_PATTERN = "^[a-z0-9]+$";
    public static final String NUMBER_PATTERN = "^[0-9]+$";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{6,20}$";
    public static final String PASSWORD_CASE = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9]{0,}$";
    public static final String PASSWORD_LENGTH = ".{6,}$";
    public static final String CHATROOM = "^chatroom\\/(.*)";

    public static final String WEBAP_URL_SCHEMES = "lale://?weburl=";
    public static final String MICROAP_URL_SCHEMES = "lale://?appId=";
    public static final String USERID_URL_SCHEMES = "lale://?userId=";
    //    public static final String roleTreeURL = "WebAgenda/laleauth?app=RoleTree&token=$token";
    public static final String roleTreeURL = "/dau/index.html?lale_token=$laleToken&token=$afToken&page=addFriend";
    public static final String LOGIN_WECHAT_APPID = "wx696ee7f3d1e42f4f";
    public static final String LOGIN_WECHAT_SECRET = "7451d37de141561c297b8a261c6cea4a";
    public static final String chatbotRoomId = "@botlami:";
    public static final String cnRoomId = "@apptest:";
    public static final String flowringRoomId = "@fradmin:";
    public static final String activityBotId = "@laleactivity:";
    public static final String userCardURL = "/_laleton/openLaleApp.html?userId=";
    //for chatroom sticker reply
    public static final String stickerUrlTag = "<STICKER_URL>";
    public static final String customStickerTag = "<CUSTOM_STICKER>";
    public static final String stickerResTag = "<STICKER_INT>";
    private static final String ffURL = "/gawbueVue/index.html#/postWall/$token";
    private static final String userFURL = "/gawbueVue/index.html#/friendPost/$fid/$token";
    private static final String myFURL = "/gawbueVue/index.html#/myPost/$token";
    private static final String fBellURL = "/gawbueVue/index.html#/noticeMessage";
    private static final String albumURL = "/gawbueVue/index.html#/album/$token";
    //For Testing
    private static final String newsURLTest = "https://news.lale.im/_newstest/home?token=$token";
    private static final String DOMAIN_FRIEND = "https://gawbue.lale.im";
    private static final String DOMAIN_FRIEND_TEST = "http://192.168.13.4:8092";
    public static IWXAPI WXapi = null;

    //暫時存iframe網址，因為監聽url只會拿到domain，iframe網址會監聽不到
    public static String URL = "";
    public static String roomId = "";
    public static String displayName = "";

    public static String getNewsURL(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isTest = pref.getBoolean(CommonUtils.PREF_FRIEND_TEST, false);
        return isTest ? newsURLTest : newsURL;
    }

    public static String getFFURL(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isTest = pref.getBoolean(CommonUtils.PREF_FRIEND_TEST, false);
        String http = isTest ? DOMAIN_FRIEND_TEST : DOMAIN_FRIEND;
        return http + ffURL;
    }

    public static String getUserFURL(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isTest = pref.getBoolean(CommonUtils.PREF_FRIEND_TEST, false);
        String http = isTest ? DOMAIN_FRIEND_TEST : DOMAIN_FRIEND;
        return http + userFURL;
    }

    public static String getMyFURL(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isTest = pref.getBoolean(CommonUtils.PREF_FRIEND_TEST, false);
        String http = isTest ? DOMAIN_FRIEND_TEST : DOMAIN_FRIEND;
        return http + myFURL;
    }

    public static String getAlbumURL(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isTest = pref.getBoolean(CommonUtils.PREF_FRIEND_TEST, false);
        String http = isTest ? DOMAIN_FRIEND_TEST : DOMAIN_FRIEND;
        return http + albumURL;
    }

    public static String getBellFURL(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isTest = pref.getBoolean(CommonUtils.PREF_FRIEND_TEST, false);
        String http = isTest ? DOMAIN_FRIEND_TEST : DOMAIN_FRIEND;
        return http + fBellURL;
    }

    public static String getFriendDomain(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isTest = pref.getBoolean(CommonUtils.PREF_FRIEND_TEST, false);
        return isTest ? DOMAIN_FRIEND_TEST : DOMAIN_FRIEND;
    }

    public static String getCompanyName(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(CommonUtils.PREF_COMPANY_NAME, "");
    }

    public static String getCompanyURL(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(CommonUtils.PREF_COMPANY_URL, "");
    }

}
