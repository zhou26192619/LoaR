package com.loar.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个helper对应一个数据库的操作
 * Created by Justsy on 2017/3/9.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    protected final static String DATABASE_NAME = "CommonProvider.db";
    protected final static int DATABASE_VERSION = 1;

    private final static String SQL_OPTIONS = "CREATE TABLE " + CommonProvider.TABLE_OPTIONS + " ("
            + "_id INTEGER PRIMARY KEY ASC AUTOINCREMENT,"
            + CommonProvider.OptionsColumns.TAG + " TEXT,"
            + CommonProvider.OptionsColumns.KEY + " TEXT UNIQUE,"
            + CommonProvider.OptionsColumns.VALUE + " TEXT" + ");";

    private final static String SQL_IMAGES = "CREATE TABLE " + CommonProvider.TABLE_IMAGES + " ("
            + "_id INTEGER PRIMARY KEY ASC AUTOINCREMENT,"
            + CommonProvider.ImagesColumns.TAG + " TEXT,"
            + CommonProvider.ImagesColumns.URL + " TEXT UNIQUE,"
            + CommonProvider.ImagesColumns.IMAGE + " BLOB" + ");";

    protected List<String> sqls = new ArrayList<>();

    protected DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        addSql(SQL_IMAGES);
        addSql(SQL_OPTIONS);
    }

    /**
     * 增加sql语句,推荐构造方法调用
     *
     * @param sql
     */
    protected void addSql(String sql) {
        sqls.add(sql);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        for (String s : sqls) {
            db.execSQL(s);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == newVersion) {
            return;
        }
        switch (oldVersion) {

        }
    }
}