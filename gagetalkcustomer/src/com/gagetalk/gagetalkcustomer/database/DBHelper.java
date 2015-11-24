package com.gagetalk.gagetalkcustomer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hyochan on 3/29/15.
 */
public class DBHelper extends SQLiteOpenHelper{
    //Use Disturb Mode
    public static final String DATABASE = "gagetalk.db";
    // public static final String MY_MARKET_TABLE = "my_market_table";
    public static final String CHAT_ROOM_TABLE = "chat_room_table";
    public static final String CHAT_TABLE = "chat_table";

    public static final int dbVersion = 2;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
/*
        db.execSQL("CREATE TABLE " + MY_MARKET_TABLE + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "market_name TEXT, " +
                "tel TEXT, " +
                "phone TEXT, " +
                "img TEXT, " +
                "email TEXT, " +
                "address TEXT, " +
                "category TEXT, " +
                "homepage TEXT, " +
                "description TEXT)"
        );
*/

        db.execSQL("CREATE TABLE " + CHAT_ROOM_TABLE + "(" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "mar_id TEXT, " +
                        "mar_name TEXT, " +
                        "cus_id TEXT, " +
                        "cus_name TEXT, " +
                        "message TEXT, " +
                        "type integer, " +
                        "path TEXT, " +
                        "send_date TEXT, " +
                        "read integer, " +
                        "sender TEXT " +
                        ")"
        );

        db.execSQL("CREATE TABLE " + CHAT_TABLE + "(" +
                        "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "mar_id TEXT, " +
                        "mar_name TEXT, " +
                        "cus_id TEXT, " +
                        "cus_name TEXT, " +
                        "message TEXT, " +
                        "type integer, " +
                        "path TEXT, " +
                        "send_date TEXT, " +
                        "read integer, " +
                        "sender TEXT " +
                    ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // db.execSQL("DROP TABLE IF EXIST " + MY_MARKET_TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + CHAT_ROOM_TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + CHAT_TABLE + ";");
        onCreate(db);
    }
}
