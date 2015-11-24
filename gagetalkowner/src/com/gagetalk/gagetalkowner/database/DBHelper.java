package com.gagetalk.gagetalkowner.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hyochan on 3/29/15.
 */
public class DBHelper extends SQLiteOpenHelper {
    //Use Disturb Mode
    public static final String DATABASE = "gagetalk.db";
    public static final String MY_CUSTOMER_TABLE = "my_customer_table";

    public static final int dbVersion = 1;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("CREATE TABLE " + MY_CUSTOMER_TABLE + "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "market_name TEXT");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + MY_CUSTOMER_TABLE + ";");
        onCreate(db);
    }
}
