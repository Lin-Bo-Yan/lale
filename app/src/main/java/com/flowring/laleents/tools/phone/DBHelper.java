package com.flowring.laleents.tools.phone;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.flowring.laleents.model.db.BaseDao;
import com.flowring.laleents.model.db.MultipleKeyDao;
import com.flowring.laleents.model.friend.FriendInfo;
import com.flowring.laleents.model.msg.MessageInfo;
import com.flowring.laleents.model.room.RoomInfoInPhone;
import com.flowring.laleents.model.room.RoomMinInfo;
import com.flowring.laleents.model.room.UserInRoom;
import com.flowring.laleents.model.stickerlibrary.CustomizeSticker;
import com.flowring.laleents.model.stickerlibrary.Sticker;
import com.flowring.laleents.model.stickerlibrary.Stickerlibrary;
import com.flowring.laleents.tools.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class DBHelper extends SQLiteOpenHelper {
    private final static String DBName = "lale.db";
    String UserId = null;
    public BaseDao<MessageInfo> msgDao = null;
    public BaseDao roomDao = null;
    public BaseDao roomInPhoneDao = null;
    public BaseDao<Stickerlibrary> stickerlibraryDao = null;
    public BaseDao<Sticker> stickerDao = null;
    public BaseDao<FriendInfo> friendDao = null;
    public BaseDao<CustomizeSticker> customizeStickerDao = null;
    public MultipleKeyDao<UserInRoom> userInRoomDao = null;

    Context context;

    public DBHelper(Context context, String UserId, int DBVersion) {
        super(context, DBName, null, DBVersion);
        this.context = context;

        this.UserId = UserId;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    public void setDB(SQLiteDatabase sqLiteDatabase) {
        newTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {

        if (oldVer != newVer) {
            StringUtils.HaoLog("刪除db");
            DrppAll(sqLiteDatabase);
        }
    }

    public void DrppAll(SQLiteDatabase sqLiteDatabase) {
        Cursor c = sqLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> tables = new ArrayList<>();

// iterate over the result set, adding every table name to a list
        while (c.moveToNext()) {
            tables.add(c.getString(0));
        }

// call DROP TABLE on every table name
        for (String table : tables) {
            String dropQuery = "DROP TABLE IF EXISTS " + table;
            sqLiteDatabase.execSQL(dropQuery);
        }

    }

    void getTable(SQLiteDatabase sqLiteDatabase) {
        if (msgDao == null) {
            msgDao = new BaseDao<>(new MessageInfo(), MessageInfo.getKey(), "msgDao_" + UserId, sqLiteDatabase);
        }
        if (roomDao == null) {
            roomDao = new BaseDao<>(new RoomMinInfo(), RoomMinInfo.getKey(), "roomDao_" + UserId, sqLiteDatabase);
        }
        if (roomInPhoneDao == null) {
            roomInPhoneDao = new BaseDao<>(new RoomInfoInPhone(), RoomInfoInPhone.getKey(), "roomInPhoneDao_" + UserId, sqLiteDatabase);
        }

        if (friendDao == null) {
            friendDao = new BaseDao<>(new FriendInfo(), FriendInfo.getKey(), "friendDao_" + UserId, sqLiteDatabase);
        }

        if (userInRoomDao == null) {
            userInRoomDao = new MultipleKeyDao<>(new UserInRoom(), UserInRoom.getKeys(), "userInRoomDao_" + UserId, sqLiteDatabase);
        }

    }

    void newTable(SQLiteDatabase sqLiteDatabase) {
        getTable(sqLiteDatabase);
//     msgDao.dropTable(sqLiteDatabase);
        msgDao.createTable(sqLiteDatabase);
        msgDao.setCreateIndexes(new String[]{"CREATE INDEX INDEX_NAME  ON " + msgDao.tableName + " (" + MessageInfo.getRoomKey() + ");"});
        msgDao.setCreateIndexes(new String[]{"CREATE INDEX INDEX_NAME2  ON " + msgDao.tableName + " (" + MessageInfo.getKey() + ");"});
        msgDao.setCreateIndexes(new String[]{"CREATE INDEX INDEX_NAME3  ON " + msgDao.tableName + " (" + MessageInfo.getRoomKey(), MessageInfo.getTimestampKey() + ");"});
        msgDao.setSortOrder("timestamp DESC");
        msgDao.createIndex(sqLiteDatabase);


//     roomDao.dropTable(sqLiteDatabase);
        roomDao.createTable(sqLiteDatabase);
        roomDao.setSortOrder("topTime DESC, last_msg_time DESC ");
        roomDao.createIndex(sqLiteDatabase);

        roomInPhoneDao.createTable(sqLiteDatabase);
        roomInPhoneDao.createIndex(sqLiteDatabase);

//         friendDao.dropTable(sqLiteDatabase);
        friendDao.createTable(sqLiteDatabase);
        friendDao.createIndex(sqLiteDatabase);


        userInRoomDao.createTable(sqLiteDatabase);
        userInRoomDao.createIndex(sqLiteDatabase);

    }
}
