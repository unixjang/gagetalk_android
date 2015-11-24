package com.gagetalk.gagetalkowner.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by hyochan on 3/29/15.
 */
public class AccessDB {

    private static final String TAG = "AccessDB";
    private static AccessDB mInstance;
    private SharedPreferences mPref;
    private DBHelper mDBHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private Context context;


    private AccessDB(Context context){
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static AccessDB getInstance(Context context){
        if(mInstance == null) mInstance = new AccessDB(context);
        return mInstance;
    }

    public void insertMarket(){
        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.MY_CUSTOMER_TABLE, null);
/*
        if(cursor.getCount() == 0) {
            String sql = "INSERT INTO " + DBHelper.MY_CUSTOMER_TABLE + " VALUES(null, 0, 0)";
            db.execSQL(sql);
        }
*/
        mDBHelper.close();
    }

    public void deleteMarket(){
        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DBHelper.MY_CUSTOMER_TABLE);
        Log.i(TAG, "setting data successfully deleted");
        mDBHelper.close();
    }

    public void updateMarket(){
/*
        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();
        Object args[] = {number, name, phone, markets, image, email, id, password, personal_key, time};
        String sql = "UPDATE " + DBHelper.MANAGER_TABLE + " SET " +
                "number =?, " +
                "name=?, " +
                "phone=?, " +
                "markets=?, " +
                "image=?, " +
                "email=?, " +
                "id=?, " +
                "password=?, " +
                "personal_key=?, " +
                "time=? " +
                "where _id = 1";
        db.execSQL(sql, args);
        mDBHelper.close();
*/
    }

    public void selectMarket() {
/*        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();
        // cursor = db.rawQuery("SELECT * FROM " + DBHelper.CHAT_WITH_MARKET_TABLE, null);
        ArrayList<ChatPreference> arrayTmp = new ArrayList<ChatPreference>();
        String[] args = {room};
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.CHAT_WITH_MARKET_TABLE + " WHERE MARKET = ?", args);
        cursor.moveToFirst();

        Boolean mine = false;
        int id = 0;
        String message = "";
        int type = 0;
        String path = "";
        String date = "";

        // null, mine, room, id, message, type, date
        for(int i=0; i<cursor.getCount(); i++){
            if(cursor.getInt(1) == 0)
                mine = false;
            else mine = true;

            id = cursor.getInt(3);
            message = cursor.getString(4);
            type = cursor.getInt(5);
            path = cursor.getString(6);
            date = cursor.getString(7);

            arrayTmp.add(new ChatPreference(
                            mine, room, id, message, type, path, date)
            );

            Log.i(TAG, "id : " + id + ", message : " + message);

            cursor.moveToNext();
        }
        mDBHelper.close();
        return arrayTmp;*/
    }
}
