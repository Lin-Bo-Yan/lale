package com.flowring.laleents.model.db;

import static com.flowring.laleents.tools.StringUtils.HaoLog;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.flowring.laleents.tools.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDao<T> extends TableAccess {


    public String tableName = "data";
    T t;
    Field primaryKey;

    public void setCreateIndexes(String[] createIndexes) {
        this.createIndexes = createIndexes;
    }

    String[] createIndexes = new String[0];


    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    private String sortOrder = null;

    String CREATE_TABLE() {
        String data = "CREATE TABLE IF NOT EXISTS " + tableName + "(";
        Field[] fields = t.getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            String type = " text";
            if (fields[i].getType().equals(String.class))
                type = " text";
            if (fields[i].getType().equals(boolean.class) || fields[i].getType().equals(Boolean.class))
                type = " flag";
            if (fields[i].getType().equals(int.class) || fields[i].getType().equals(Integer.class))
                type = " integer";
            if (fields[i].getType().equals(float.class))
                type = " float";
            if (fields[i].getType().equals(long.class) || fields[i].getType().equals(Long.class))
                type = " integer";

            data += fields[i].getName() + type + (fields[i].getName().equals(primaryKey.getName()) ? " primary key" : "") + ((i != fields.length - 1) ? ", " : "");
        }
        data += ");";
        return data;
    }

    private SQLiteDatabase m_database;
    String primaryKeyString;

    public BaseDao(T t, String primaryKey, String tableName, SQLiteDatabase database) {
        this.tableName = tableName;
        this.t = t;
        try {
            this.primaryKeyString = primaryKey;
            this.primaryKey = t.getClass().getDeclaredField(primaryKey);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        m_database = database;

    }

    public void setDB(SQLiteDatabase database) {
        m_database = database;
    }


    public void createTable(SQLiteDatabase db) {

        HaoLog("Create table command : " + CREATE_TABLE());
        db.execSQL(CREATE_TABLE());
    }

    @Override
    public void dropTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

    @Override
    public void clearTable(SQLiteDatabase db) {
        HaoLog("clearTable()");
        db.execSQL("DELETE FROM " + tableName);
    }

    @Override
    public void createIndex(SQLiteDatabase db) {

    }

    @Override
    public void updateDatabase(SQLiteDatabase db) {
        m_database = db;
    }

    public boolean insert(T data) {
        if (isExistdata(data))
            return update(data);
        ContentValues values = new ContentValues();
        Field[] fields = t.getClass().getFields();
        for (int i = 0; i < fields.length; i++) {

            try {
                if (fields[i].getType().equals(String.class))
                    values.put(fields[i].getName(), (String) fields[i].get(data));
                if (fields[i].getType().equals(long.class) || fields[i].getType().equals(Long.class))
                    values.put(fields[i].getName(), (Long) fields[i].get(data));
                if (fields[i].getType().equals(int.class) || fields[i].getType().equals(Integer.class))
                    values.put(fields[i].getName(), (Integer) fields[i].get(data));
                if (fields[i].getType().equals(boolean.class) || fields[i].getType().equals(Boolean.class))
                    values.put(fields[i].getName(), ((Boolean) fields[i].get(data)) ? 1 : 0);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        HaoLog(values.toString());
        long insert = m_database.insert(tableName, null, values);
        if (insert == -1) {
            HaoLog("insertdata() but failed to insert data!");
            return false;
        }
        return true;
    }

    public boolean insertNull(T data) {
        ContentValues values = new ContentValues();
        Field[] fields = t.getClass().getFields();
        Field[] t_fields = data.getClass().getFields();
        Map<String, Field> t_field_map = new HashMap();

        for (int i = 0; i < t_fields.length; i++) {
            t_field_map.put(t_fields[i].getName(), t_fields[i]);
        }
        for (int i = 0; i < fields.length; i++) {
            Field th = fields[i];
            Object value;
            try {

                if (t_field_map.keySet().contains(fields[i].getName())) {
                    th = t_field_map.get(fields[i].getName());
                    value = th.get(data);
                } else {
                    HaoLog("t=" + th.getName());
                    value = th.get(t);
                }


                if (th.getType().equals(String.class))
                    values.put(fields[i].getName(), (String) value);
                if (th.getType().equals(long.class) || th.getType().equals(Long.class))
                    values.put(fields[i].getName(), (Long) value);
                if (th.getType().equals(int.class) || th.getType().equals(Integer.class))
                    values.put(fields[i].getName(), (Integer) value);
                if (th.getType().equals(boolean.class) || th.getType().equals(Boolean.class))
                    values.put(fields[i].getName(), ((Boolean) value) ? 1 : 0);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        long insert = m_database.insert(tableName, null, values);
        if (insert == -1) {
            HaoLog("insertdata() but failed to insert data!");
            return false;
        }

        return true;
    }


    public boolean stopCallbacks = false;

    public boolean update(T data) {
        if (!isExistdata(data)) {
            return insertNull(data);
        }
        ContentValues values = new ContentValues();
        Field[] fields = data.getClass().getFields();
        for (int i = 0; i < fields.length; i++) {

            try {
                if (fields[i].getType().equals(String.class))
                    values.put(fields[i].getName(), (String) fields[i].get(data));
                if (fields[i].getType().equals(long.class) || fields[i].getType().equals(Long.class))
                    values.put(fields[i].getName(), (Long) fields[i].get(data));
                if (fields[i].getType().equals(int.class) || fields[i].getType().equals(Integer.class))
                    values.put(fields[i].getName(), (Integer) fields[i].get(data));
                if (fields[i].getType().equals(boolean.class) || fields[i].getType().equals(Boolean.class))
                    values.put(fields[i].getName(), ((Boolean) fields[i].get(data)) ? 1 : 0);
            } catch (IllegalAccessException e) {

                e.printStackTrace();
            }

        }
        long update = 0;
        try {
            Object key;
            key = data.getClass().getDeclaredField(primaryKeyString).get(data);

            update = m_database.update(tableName, values, primaryKey.getName() + "= '" + key + "'", null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            HaoLog(e.toString());
            e.printStackTrace();
        }
        if (update == 0) {
            HaoLog("updatedata() but with duplicated entries!");
            return false;
        }

        return true;
    }


    public T queryByKey(String data) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(tableName);
        queryBuilder.appendWhere(primaryKey.getName() + "='" + data + "'");
        Cursor cursor = queryBuilder.query(m_database, null, null, null, null, null, null);
        cursor.moveToFirst();
        if (cursor.isAfterLast()) {
            cursor.close();
            return null;
        }
        T t = cursorTodata(cursor);
        cursor.close();
        return t;
    }

    public List<T> queryAll() {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(tableName);
        ArrayList<T> arrayList = new ArrayList<>();
        Cursor cursor = queryBuilder.query(m_database, null, null, null, null, null, sortOrder);

        while (cursor.moveToNext()) {
            T room = cursorTodata(cursor);
            arrayList.add(room);

        }
        cursor.close();


        return arrayList;
    }

    public List<T> queryLimit(int limit) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(tableName);
        ArrayList<T> arrayList = new ArrayList<>();
        Cursor cursor = queryBuilder.query(m_database, null, null, null, null, null, sortOrder, "" + limit);
        while (cursor.moveToNext()) {
            T room = cursorTodata(cursor);
            arrayList.add(room);

        }
        cursor.close();


        return arrayList;
    }

    public int getSize(Map<String, String> necessary, Map<String, String> need) {

        String pragma = "PRAGMA case_sensitive_like = true";

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(tableName);
        m_database.execSQL(pragma);
        String selectionArgs = " WHERE ";
        boolean one = true;
        if (need != null)
            for (Map.Entry<String, String> data : need.entrySet()) {

                selectionArgs += ((one ? "" : " AND ") + "" + data.getKey() + " LIKE '%" + data.getValue() + "%'");
                one = false;
            }
        if (necessary != null)
            for (Map.Entry<String, String> data : necessary.entrySet()) {
                selectionArgs += ((one ? "" : " AND ") + "" + data.getKey() + " = '" + data.getValue() + "'");
                one = false;
            }

        HaoLog("SELECT COUNT(*) as co FROM " + tableName + selectionArgs);
        try {

            Cursor cursor = m_database.rawQuery("SELECT COUNT(*) as co FROM " + tableName + selectionArgs, new String[]{});
            cursor.moveToFirst();
            int COUNT = cursor.getInt(0);
            cursor.close();
            return COUNT;
        } catch (Exception e) {
            HaoLog(e.toString());
        }

        return -1;
    }

    public ArrayList<T> searchNoSort(Map<String, Object> necessary) {
        ArrayList<T> arrayList = new ArrayList<>();
        String pragma = "PRAGMA case_sensitive_like = true";

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(tableName);
        m_database.execSQL(pragma);
        String selectionArgs = " WHERE ";
        boolean one = true;

        if (necessary != null)
            for (Map.Entry<String, Object> data : necessary.entrySet()) {
                if (data.getValue() instanceof String)
                    selectionArgs += ((one ? "" : " AND ") + "" + data.getKey() + " = '" + data.getValue() + "'");
                else
                    selectionArgs += ((one ? "" : " AND ") + "" + data.getKey() + " = " + data.getValue());
                one = false;
            }
        if (one)
            selectionArgs = "";


        String sql = "SELECT * FROM " + tableName + selectionArgs;
        HaoLog(sql);
        try {
            Cursor cursor = m_database.rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
                T room = cursorTodata(cursor);
                arrayList.add(room);

            }
            cursor.close();
        } catch (Exception e) {
            HaoLog(e.toString());
        }

        return arrayList;
    }


    public ArrayList<T> search(Map<String, String> necessary, Map<String, String> need) {
        return search(necessary, need, null, 0);
    }

    public ArrayList<T> search(Map<String, String> necessary, Map<String, String> need, Integer limitStart, int limitAmount) {
        ArrayList<T> arrayList = new ArrayList<>();
        String pragma = "PRAGMA case_sensitive_like = true";

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(tableName);
        m_database.execSQL(pragma);
        String selectionArgs = " WHERE ";
        boolean one = true;
        if (need != null)
            for (Map.Entry<String, String> data : need.entrySet()) {

                selectionArgs += ((one ? "" : " AND ") + "" + data.getKey() + " LIKE '%" + data.getValue() + "%'");
                one = false;
            }
        if (necessary != null)
            for (Map.Entry<String, String> data : necessary.entrySet()) {
                selectionArgs += ((one ? "" : " AND ") + "" + data.getKey() + " = '" + data.getValue() + "'");
                one = false;
            }
        if (one)
            selectionArgs = "";

        String LIMIT = " LIMIT " + limitStart + "," + limitAmount;
        if (limitStart == null)
            LIMIT = "";

        String sql = "SELECT * FROM " + tableName + selectionArgs + ((sortOrder == null ? "" : " ORDER BY " + sortOrder) + LIMIT);

        try {
            Cursor cursor = m_database.rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
                T room = cursorTodata(cursor);
                arrayList.add(room);

            }
            cursor.close();
        } catch (Exception e) {
            HaoLog(e.toString());
        }

        return arrayList;
    }

    public boolean isExistdata(T data) {
        long count = 0;
        try {
            Object key;
            key = data.getClass().getDeclaredField(primaryKeyString).get(data);
            count = DatabaseUtils.queryNumEntries(m_database, tableName, primaryKey.getName() + "= '" + key + "'");
        } catch (Exception e) {
            HaoLog("isExistdata error = " + e.getMessage());
        }
        return count != 0;
    }

    private T cursorTodata(Cursor cursor) {
        T t2 = null;
        try {
            t2 = (T) t.getClass().newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        try {
            Field[] fields = t2.getClass().getFields();
            for (int i = 0; i < fields.length; i++) {
                Field f1 = t2.getClass().getDeclaredField(fields[i].getName());
                if (fields[i].getName() != null) {
                    int getColumnIndexOrThrow = cursor.getColumnIndexOrThrow(fields[i].getName());
                    if (f1.getType().equals(String.class))
                        f1.set(t2, cursor.getString(getColumnIndexOrThrow));
                    if (f1.getType().equals(long.class) || f1.getType().equals(Long.class))
                        f1.set(t2, cursor.getLong(getColumnIndexOrThrow));
                    if (f1.getType().equals(int.class) || f1.getType().equals(Integer.class))
                        f1.set(t2, cursor.getInt(getColumnIndexOrThrow));
                    if (f1.getType().equals(boolean.class) || f1.getType().equals(Boolean.class))
                        f1.set(t2, cursor.getInt(getColumnIndexOrThrow) == 1);
                }
            }

            return t2;

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return t2;
    }

    public boolean deleteById(String key) {
        if (queryByKey(key) == null)
            return true;
        boolean isDelete = (m_database.delete(tableName, primaryKey.getName() + "= '" + key + "'", null) >= 1);
        StringUtils.HaoLog("刪除" + isDelete);

        return isDelete;
    }

    public boolean deleteAll() {
        boolean isDelete = (m_database.delete(tableName, "", null) >= 1);
        return isDelete;
    }

    public boolean deleteByKey(String key, String value) {

        boolean isDelete = (m_database.delete(tableName, key + "= '" + value + "'", null) >= 1);
        return isDelete;

    }
}
