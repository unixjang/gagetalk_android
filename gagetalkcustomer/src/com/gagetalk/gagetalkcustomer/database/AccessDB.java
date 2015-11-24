package com.gagetalk.gagetalkcustomer.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gagetalk.gagetalkcommon.api.Function;
import com.gagetalk.gagetalkcommon.constant.ConstValue;
import com.gagetalk.gagetalkcommon.util.MyLog;
import com.gagetalk.gagetalkcustomer.api.CustomerFunction;
import com.gagetalk.gagetalkcustomer.data.ChatRoomData;
import com.gagetalk.gagetalkcustomer.data.ChatData;

import java.util.ArrayList;

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

    // => START : CHAT_ROOM_TABLE EXECUTION
    public void insertChatRoom(ChatRoomData chatRoomData){
        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();

        cursor = db.rawQuery(
                "SELECT * FROM " + DBHelper.CHAT_ROOM_TABLE + " where mar_id = ?"
                , new String[]{chatRoomData.getMarId()});
        // mar_name should be unique
        if(cursor.getCount() == 0){
            Log.d(TAG, "insertChatRoom : " + chatRoomData.getMarId());
            MyLog.i(TAG, "getcount == 0 so lets insert : " + chatRoomData.getMarId());
            String sql = "INSERT INTO " + DBHelper.CHAT_ROOM_TABLE
                    + " values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Object[] args = {
                    chatRoomData.getMarId(),
                    chatRoomData.getMarName(),
                    chatRoomData.getCusId(),
                    chatRoomData.getCusName(),
                    chatRoomData.getMessage(),
                    chatRoomData.getType(),
                    chatRoomData.getPath(),
                    chatRoomData.getSendDate(),
                    chatRoomData.getReadMsg(),
                    chatRoomData.getSender()
            };
            db.execSQL(sql, args);
            mDBHelper.close();
        } else{
            Log.d(TAG, "updateChatRoom : " + chatRoomData.getMarId());
            mDBHelper.close();
            updateChatRoom(chatRoomData);
        }


    }

    public void deleteAllChatRoom(){
        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + DBHelper.CHAT_ROOM_TABLE);
        Log.d(TAG, "deleteAllChatRoom DONE !!!");
        mDBHelper.close();
    }

    public void deleteChatRoom(String marketName){
        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();
        db.execSQL(
                "DELETE FROM " + DBHelper.CHAT_ROOM_TABLE + " where mar_name = ?",
                new String[]{marketName}
        );
        Log.d(TAG, "deleteChatOn : " + marketName);
        mDBHelper.close();
    }

    public void updateChatRoomReadFlag(String mar_id){
        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();
        Object[] args = {mar_id};
        try{
            String sql = "UPDATE " + DBHelper.CHAT_ROOM_TABLE + " SET " +
                    "read = 1 where mar_id = ?";
        }finally {
            mDBHelper.close();
        }
    }

    public void updateChatRoom(ChatRoomData chatRoomData){
        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();
        Object[] args = {
                chatRoomData.getMarId(),
                chatRoomData.getMarName(),
                chatRoomData.getCusId(),
                chatRoomData.getCusName(),
                chatRoomData.getMessage(),
                chatRoomData.getType(),
                chatRoomData.getPath(),
                chatRoomData.getSendDate(),
                chatRoomData.getReadMsg(),
                chatRoomData.getSender(),
                chatRoomData.getMarId(),
                CustomerFunction.getInstance(context).getCusID()
        };
        try{
            String sql = "UPDATE " + DBHelper.CHAT_ROOM_TABLE + " SET " +
                    "mar_id =?, " +
                    "mar_name = ?, " +
                    "cus_id = ?, " +
                    "cus_name = ?, " +
                    "message = ?, " +
                    "type = ?, " +
                    "path = ?, " +
                    "send_date = ?, " +
                    "read = ?, " +
                    "sender = ? " +
                    "where mar_id = ? and cus_id =?";
            db.execSQL(sql, args);
        }finally {
            /*MyLog.d(TAG, "UPDATE ChatRoomData - "
                            + "\n mar_id : " + chatRoomData.getMarId()
                            + "\n mar_name : " + chatRoomData.getMarName()
                            + "\n cus_id : " + chatRoomData.getCusId()
                            + "\n cus_name : " + chatRoomData.getCusName()
                            + "\n message : " + chatRoomData.getMessage()
                            + "\n type : " + chatRoomData.getType()
                            + "\n path : " + chatRoomData.getPath()
                            + "\n send_date : " + chatRoomData.getSendDate()
                            + "\n read_msg : " + chatRoomData.getReadMsg()
                            + "\n sender : " + chatRoomData.getSender()
                            + "\n mar_id : " + chatRoomData.getMarId()
                            + "\n cus_id : " + Function.getInstance(context).getCusID()
            );*/
            mDBHelper.close();
        }
    }

    public ArrayList<ChatRoomData> selectChatRoom() {
        MyLog.i(TAG, "selectChatRoom : " + CustomerFunction.getInstance(context).getCusID());
        ArrayList<ChatRoomData> arrayChatRoom = new ArrayList<>();
        if(CustomerFunction.getInstance(context).getCusID() != null) {
            mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
            db = mDBHelper.getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + DBHelper.CHAT_ROOM_TABLE
                    + " WHERE cus_id = ? order by send_date DESC", new String[]{CustomerFunction.getInstance(context).getCusID()});

            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {

                ChatRoomData chatRoomData = new ChatRoomData(
                        cursor.getString(ConstValue.DB_CHAT_MAR_ID),
                        cursor.getString(ConstValue.DB_CHAT_MAR_NAME),
                        cursor.getString(ConstValue.DB_CHAT_CUS_ID),
                        cursor.getString(ConstValue.DB_CHAT_CUS_NAME),
                        cursor.getString(ConstValue.DB_CHAT_MESSAGE),
                        cursor.getInt(ConstValue.DB_CHAT_TYPE),
                        cursor.getString(ConstValue.DB_CHAT_PATH),
                        cursor.getString(ConstValue.DB_CHAT_SEND_DATE),
                        cursor.getInt(ConstValue.DB_CHAT_READ_MSG),
                        cursor.getString(ConstValue.DB_CHAT_SENDER));

                arrayChatRoom.add(chatRoomData);

                /*MyLog.d(TAG, "ChatRoomData - "
                                + "\n mar_id : " + chatRoomData.getMarId()
                                + "\n mar_name : " + chatRoomData.getMarName()
                                + "\n cus_id : " + chatRoomData.getCusId()
                                + "\n cus_name : " + chatRoomData.getCusName()
                                + "\n message : " + chatRoomData.getMessage()
                                + "\n type : " + chatRoomData.getType()
                                + "\n path : " + chatRoomData.getPath()
                                + "\n send_date : " + chatRoomData.getSendDate()
                                + "\n read_msg : " + chatRoomData.getReadMsg()
                                + "\n sender : " + chatRoomData.getSender()
                );*/
                cursor.moveToNext();
            }
            mDBHelper.close();
        }

        return arrayChatRoom;
    }

    // <= END : CHAT_ROOM_TABLE EXECUTION

    // => START : CHAT_TABLE EXECUTION
    public void insertChat(ChatData chatData){
        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();

        cursor = db.rawQuery(
                "SELECT room FROM " + DBHelper.CHAT_TABLE + " where mar_id = ?"
                , new String[]{chatData.getMarId()});
        // mar_name should be unique
        if(cursor.getCount() != 0){
            String sql = "INSERT INTO " + DBHelper.CHAT_TABLE
                    + " values(null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            Object[] args = {
                    chatData.getMarId(),
                    chatData.getMarName(),
                    chatData.getCusId(),
                    chatData.getCusName(),
                    chatData.getMessage(),
                    chatData.getType(),
                    chatData.getPath(),
                    chatData.getSendDate(),
                    chatData.getReadMsg(),
                    chatData.getSender()
            };
            db.execSQL(sql, args);
        }
/*
        if(cursor.getCount() == 0) {
            String sql = "INSERT INTO " + DBHelper.MY_MARKET_TABLE + " VALUES(null, 0, 0)";
            db.execSQL(sql);
        }
*/
        Log.d(TAG, "insertCustomerChat : " + chatData.getMarName());
        mDBHelper.close();
    }

    public void deleteAllChat(String marId){
        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();
        db.execSQL(
                "DELETE FROM " + DBHelper.CHAT_TABLE + " where mar_id = ?",
                new String[]{marId}
        );
        Log.d(TAG, "deleteCustomerChats : " + marId);
        mDBHelper.close();
    }

    public void deleteOneChat(String marId, int selected){
        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();
        db.execSQL(
                "DELETE FROM " + DBHelper.CHAT_TABLE + " where mar_id = ? and _id =?",
                new Object[]{marId, selected}
        );
        Log.d(TAG, "deleteCustomerChats : " + marId + " , selected : " + selected);
        mDBHelper.close();
    }

    // update is needed to change the read status
    public void updateChat(String marId, int selected){
        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();
        Object args[] = {
                ConstValue.MSG_READ,
                marId,
                selected,
                ConstValue.MSG_NOT_READ
        };
        String sql = "UPDATE " + DBHelper.CHAT_TABLE + " SET " +
                "read=? " +
                "where mar_id = ? and _id < ? and read=?";
        db.execSQL(sql, args);
        mDBHelper.close();
    }

    public ArrayList<ChatData> selectChat(String marId) {
        mDBHelper = new DBHelper(context, DBHelper.DATABASE, null, DBHelper.dbVersion);
        db = mDBHelper.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.CHAT_TABLE + " where mar_id = ?", new String[]{marId});

        ArrayList<ChatData> arrayCustomerChat = new ArrayList<>();
        cursor.moveToFirst();
        for(int i=0; i<cursor.getCount(); i++){
            arrayCustomerChat.add(
                    new ChatData(
                            cursor.getString(ConstValue.DB_CHAT_MAR_ID), // mar_id
                            cursor.getString(ConstValue.DB_CHAT_MAR_NAME),
                            cursor.getString(ConstValue.DB_CHAT_CUS_ID), // cus_id
                            cursor.getString(ConstValue.DB_CHAT_CUS_NAME),
                            cursor.getString(ConstValue.DB_CHAT_MESSAGE),
                            cursor.getInt(ConstValue.DB_CHAT_TYPE),
                            cursor.getString(ConstValue.DB_CHAT_PATH),
                            cursor.getString(ConstValue.DB_CHAT_SEND_DATE),
                            cursor.getInt(ConstValue.DB_CHAT_READ_MSG),
                            cursor.getString(ConstValue.DB_CHAT_SENDER)
                    )
            );
            cursor.moveToNext();
        }

        mDBHelper.close();

        return arrayCustomerChat;
    }
    // <= END : CHAT_TABLE EXECUTION
}
