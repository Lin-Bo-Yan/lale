package com.flowring.laleents.tools.phone;

import static com.flowring.laleents.model.user.UserControlCenter.getUserMinInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import androidx.annotation.WorkerThread;

import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.friend.FriendInfo;
import com.flowring.laleents.model.msg.MessageInfo;
import com.flowring.laleents.model.msg.MessageItem;
import com.flowring.laleents.model.room.RoomInfoInPhone;
import com.flowring.laleents.model.room.RoomMinInfo;
import com.flowring.laleents.model.room.RoomMinInfoByListType;
import com.flowring.laleents.model.room.UserInRoom;
import com.flowring.laleents.model.stickerlibrary.CustomizeSticker;
import com.flowring.laleents.model.stickerlibrary.Sticker;
import com.flowring.laleents.model.stickerlibrary.Stickerlibrary;
import com.flowring.laleents.tools.StringUtils;
import com.flowring.laleents.tools.cloud.api.CloudUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AllData {

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    private static String MainServer = "https://laledev0.flowring.com/laleweb";
    //   private static String MainServer = "http://192.168.9.110:6780";
    private static String newsDomain = "https://news.lale.im";
    private static String memiaDomain = "https://memia.lale.im";

    private static String AnnounceServer = "https://laledev10.flowring.com/announce";

    public static void setMainServer(String mainServer) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString("MainServer", mainServer).apply();
        MainServer = mainServer;
    }

    public static String getMainServer() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString("MainServer", MainServer);

    }

    public static void setJitsiServer(String jitsiServer) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString("JitsiServer", jitsiServer).apply();
    }

    public static String getNewsDomain() {
        return newsDomain;
    }

    public static void setAnnouncementServer(String announceServer) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString("AnnounceServer",announceServer).apply();
        AnnounceServer = announceServer;
    }

    public static String getAnnouncementServer() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString("AnnounceServer",AnnounceServer);
    }

    public static String getMemiaDomain() {
        return memiaDomain;
    }

    static SQLiteDatabase m_database;
    public static DBHelper dbHelper;


    public static int getUnreadCount() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(getUserMinInfo().userId + "_UnreadCount", 0);
    }

    public static void setUnreadCount(int unreadCount) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(getUserMinInfo().userId + "_UnreadCount", unreadCount).apply();
    }

    public static int getUnreadWorkCount() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(getUserMinInfo().userId + "unreadWorkCount", 0);
    }

    public static void setUnreadWorkCount(int unreadWorkCount) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(getUserMinInfo().userId + "unreadWorkCount", unreadWorkCount).apply();
    }

    public static void init(Context AppContext) {
        context = AppContext;
    }

    public static void initSQL(String userId) {
        if (dbHelper != null && Objects.equals("v" + dbHelper.UserId, "v"+userId)) {
            StringUtils.HaoLog("不重複建立SQL");
            return;
        }
        final String dbName = "v"+userId.replace("@", "_").replace(":", "_").replace(".", "_");
        if (m_database != null){
            m_database.close();
        }
        dbHelper = new DBHelper(context, dbName, FinalData.DBVersion);
        m_database = dbHelper.getWritableDatabase();
        dbHelper.setDB(m_database);
    }


//    public static void initSQL(String userId) {
//        StringUtils.HaoLog("dbHelper=" + dbHelper);
//
//        if (dbHelper != null){StringUtils.HaoLog("dbHelper UserId=" + dbHelper.UserId + " userId=" + userId);}
//
//        if (dbHelper != null && dbHelper.UserId != null && dbHelper.UserId.equals(userId.replace("@", "_").replace(":", "_").replace(".", "_"))) {
//            StringUtils.HaoLog("不重複建立SQL");
//        } else {
//            if (m_database != null){m_database.close();}
//            dbHelper = new DBHelper(context, userId.replace("@", "_").replace(":", "_").replace(".", "_"), FinalData.DBVersion);
//            m_database = dbHelper.getWritableDatabase();
//            dbHelper.setDB(m_database);
//        }
//    }

    public static void delectAll() {
        if (m_database != null) {
            dbHelper.DrppAll(m_database);
            dbHelper = null;
            m_database.close();
            m_database = null;
        }
    }


    public static boolean isFriend(String userId) {
        return dbHelper.friendDao.queryByKey(userId) != null;
    }

    public static String getRoomId(String userId) {
        if (dbHelper == null){return null;}
        if (dbHelper.friendDao == null){return null;}
        if (dbHelper.friendDao.queryByKey(userId) == null){return null;}
        StringUtils.HaoLog("dbHelper.friendDao.queryByKey(userId)=" + dbHelper.friendDao.queryByKey(userId));
        return dbHelper.friendDao.queryByKey(userId).roomId;
    }

    public static List<RoomMinInfo> getRoomMinInfos() {
        return dbHelper.roomDao.queryAll();
    }

    public static int getAllUnreadCount() {
        int AllUnreadCount = 0;
        List<RoomMinInfo> roomMinInfoList = dbHelper.roomDao.queryAll();
        for (RoomMinInfo roomMinInfo : roomMinInfoList) {
            if (roomMinInfo.unread_count > 0){
                AllUnreadCount += roomMinInfo.unread_count;
            }
        }
        StringUtils.HaoLog("AllUnreadCount=" + AllUnreadCount);
        return AllUnreadCount;
    }


    public static List<RoomMinInfo> getRoomMinInfoByGroupId(String GroupId) {
        Map<String, String> necessary = new HashMap<>();
        necessary.put("groupId", GroupId);
        return dbHelper.roomDao.searchNoSort(necessary);
    }

    public static List<RoomMinInfo> getRoomMinInfoByType(int type) {
        Map<String, Object> necessary = new HashMap<>();
        necessary.put("type", type);
        return dbHelper.roomDao.searchNoSort(necessary);
    }

    public static ArrayList<MessageInfo> getMsgsByRoomId(String roomId) {
        Map<String, String> s = new HashMap<>();
        s.put(MessageInfo.getRoomKey(), roomId);
        return dbHelper.msgDao.search(s, null);
    }

    public static ArrayList<MessageInfo> getMsgs(String roomId, int count) {
        Map<String, String> s = new HashMap<>();
        s.put(MessageInfo.getRoomKey(), roomId);
        ArrayList<MessageInfo> data = dbHelper.msgDao.search(s, null, 0, count);

        return data;
    }

    public static MessageInfo getMsg(String id) {
        return dbHelper.msgDao.queryByKey(id);
    }

    public static RoomMinInfo getRoomMinInfo(String key) {
        if (dbHelper == null){return null;}
        if (dbHelper.roomDao == null){return null;}
        return (RoomMinInfo) dbHelper.roomDao.queryByKey(key);
    }

    @WorkerThread
    public static RoomMinInfo getRoomMinInfoNoNull(String key) {
        if (key == null || key.isEmpty()){return null;}
        if (dbHelper == null){return null;}
        if (dbHelper.roomDao == null){return null;}
        RoomMinInfo roomMinInfe = (RoomMinInfo) dbHelper.roomDao.queryByKey(key);
        if (roomMinInfe != null){
            return roomMinInfe;
        } else {
            HttpReturn httpReturn = CloudUtils.iCloudUtils.getOneRoom(key);
            if (httpReturn.status == 200) {
                RoomMinInfo roomMinInfe2 = new Gson().fromJson(new Gson().toJson(httpReturn.data), RoomMinInfoByListType.class).getRoomMinInfo();
                if (roomMinInfe2 != null){AllData.updateRoom(roomMinInfe2);}
                return roomMinInfe2;
            } else {
                return null;
            }
        }
    }

    public static boolean updateRooms(List<RoomMinInfo> roomMinInfoList) {
        StringUtils.HaoLog("updateRooms=" + roomMinInfoList.size());
        dbHelper.roomDao.stopCallbacks = true;
        for (int i = 0; i < roomMinInfoList.size(); i++) {
            if (i == roomMinInfoList.size() - 1){dbHelper.roomDao.stopCallbacks = false;}
            boolean ok = dbHelper.roomDao.update(roomMinInfoList.get(i));
            StringUtils.HaoLog("updateRooms Info=" + roomMinInfoList.get(i).name + " " + roomMinInfoList.get(i).id + " ok=" + ok);
        }
        return true;
    }

    public static void CleanRooms() {
        dbHelper.roomDao.clearTable(m_database);
    }

    public static boolean updateRoom(RoomMinInfo value) {
//        if (value.status == 2)
//            return dbHelper.roomDao.deleteById(value.id);
//        else
        return dbHelper.roomDao.update(value);

    }

    public static boolean updateRoomInListFragment(RoomMinInfo value) {
        boolean isok = updateRoom(value);
        LocalBroadcastControlCenter.send(context, LocalBroadcastControlCenter.ACTION_MQTT_ROOM, value.id);
        return isok;
    }

    public static boolean updatestickerlibrary(Stickerlibrary value) {
        return dbHelper.stickerlibraryDao.update(value);
    }

    public static List<Stickerlibrary> getStickerlibraryAll() {
        return dbHelper.stickerlibraryDao.queryAll();
    }

    public static ArrayList<Sticker> getStickerByStickerLibraryId(String stickerLibraryId) {
        Map<String, String> necessary = new HashMap<>();
        necessary.put("stickerLibraryId", stickerLibraryId);
        return dbHelper.stickerDao.search(necessary, null);
    }

    public static List<Sticker> getStickerAll() {
        return dbHelper.stickerDao.queryAll();
    }

    public static boolean updateCustomizeSticker(List<CustomizeSticker> customizeStickers) {
        dbHelper.customizeStickerDao.deleteAll();
        for (int i = 0; i < customizeStickers.size(); i++) {
            dbHelper.customizeStickerDao.update(customizeStickers.get(i));
        }
        return true;
    }

    public static List<CustomizeSticker> getCustomizeStickerAll() {
        return dbHelper.customizeStickerDao.queryAll();
    }

    public static boolean updateSticker(ArrayList<Sticker> value) {
        for (Sticker sticker : value) {
            dbHelper.stickerDao.update(sticker);
        }
        return true;
    }

    public static boolean updateFriend(List<FriendInfo> value) {
        for (int i = 0; i < value.size(); i++) {
            dbHelper.friendDao.update(value.get(i));
        }
        return true;
    }

    public static boolean updateMsg(MessageItem msg) {
        RoomMinInfo roomInfo = AllData.getRoomMinInfo(msg.room_id);
        if (roomInfo != null) {
            if (roomInfo.last_msg_time < msg.timestamp) {
                roomInfo.last_msg_time = msg.timestamp;
                roomInfo.last_msg = msg.getText();
                if (msg.unreadCount > 0){roomInfo.unread_count = msg.unreadCount;}
            }
            AllData.updateRoom(roomInfo);
        }
        Gson gson = new Gson();
        MessageInfo NewMsg = gson.fromJson(gson.toJson(msg), MessageInfo.class);
        return dbHelper.msgDao.update(NewMsg);
    }

    public static boolean delMsgs(String roomId) {
        return dbHelper.msgDao.deleteByKey("room_id", roomId);
    }

    public static boolean delRoom(String roomId) {
        boolean isOk = dbHelper.roomDao.deleteByKey(RoomMinInfo.getKey(), roomId);
        if (isOk) {
            LocalBroadcastControlCenter.send(context, LocalBroadcastControlCenter.ACTION_MQTT_ROOM, roomId);
        }
        return isOk;
    }

    public static boolean updateRoomInPhone(RoomInfoInPhone roomInfoInPhone) {
        return dbHelper.roomInPhoneDao.update(roomInfoInPhone);
    }

    public static RoomInfoInPhone getRoomInPhone(String roomId) {
        RoomInfoInPhone roomInfoInPhone = (RoomInfoInPhone) dbHelper.roomInPhoneDao.queryByKey(roomId);
        if (roomInfoInPhone == null) {
            roomInfoInPhone = new RoomInfoInPhone();
            roomInfoInPhone.id = roomId;
            roomInfoInPhone.bg = null;
            roomInfoInPhone.msgTime = null;
        }
        updateRoomInPhone(roomInfoInPhone);
        return roomInfoInPhone;
    }

    public static boolean updateMsg(MessageInfo msg) {
        StringUtils.HaoLog("updateMsg=" + msg.getText());
        RoomMinInfo roomMinInfo = AllData.getRoomMinInfo(msg.room_id);
        if (roomMinInfo != null) {
            if (msg.timestamp > roomMinInfo.last_msg_time) {
                if (!msg.is_lale_message_read()) {
                    roomMinInfo.last_msg = msg.getText();
                    roomMinInfo.last_msg_time = msg.timestamp;
                }
                if (msg.unreadCount >= 0){roomMinInfo.unread_count = msg.unreadCount;}
                AllData.updateRoom(roomMinInfo);
            }
        }

        if (dbHelper == null){return false;}
        StringUtils.HaoLog("updateMsg=" + dbHelper);
        return dbHelper.msgDao.update(msg);
    }


    public static FriendInfo getFriend(String userId) {
        return dbHelper.friendDao.queryByKey(userId);
    }

    public static List<FriendInfo> getFriends() {
        return dbHelper.friendDao.queryAll();
    }

    public static boolean setFriends(ArrayList<FriendInfo> friend) {
        for (FriendInfo friendInfo : friend) {
            dbHelper.friendDao.update(friendInfo);
        }
        return true;
    }

    public static boolean setFriend(FriendInfo friend) {
        return dbHelper.friendDao.update(friend);
    }

    public static ArrayList<UserInRoom> getUsersInRoom(String roomId) {
        Map<String, String> map = new HashMap<>();
        map.put("roomId", roomId);
        return dbHelper.userInRoomDao.search(map, null);
    }

    public static UserInRoom getUserInRoom(String roomId, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("roomId", roomId);
        map.put("userId", userId);
        ArrayList<UserInRoom> userInRooms = dbHelper.userInRoomDao.search(map, null);
        if (userInRooms.size() > 0){
            return userInRooms.get(0);
        } else{
            return null;
        }
    }

    public static boolean setUserInRoom(UserInRoom userInRoom) {
        return dbHelper.userInRoomDao.update(userInRoom);
    }

    public static boolean setUserInRoom(String roomid, ArrayList<UserInRoom> userInRooms) {
        StringUtils.HaoLog("setUserInRoom");
        if (userInRooms == null){return false;}
        for (UserInRoom userInRoom : userInRooms) {
            StringUtils.HaoLog("setUserInRoom " + roomid + " " + userInRoom.userId);
            userInRoom.roomId = roomid;
            dbHelper.userInRoomDao.update(userInRoom);
        }
        return true;
    }
}

