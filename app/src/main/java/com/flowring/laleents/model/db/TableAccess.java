package com.flowring.laleents.model.db;

import android.database.sqlite.SQLiteDatabase;

abstract class TableAccess {

    public abstract void createTable(SQLiteDatabase db);

    public abstract void dropTable(SQLiteDatabase db);

    public abstract void clearTable(SQLiteDatabase db);

    public abstract void createIndex(SQLiteDatabase db);

    public abstract void updateDatabase(SQLiteDatabase db);
}
