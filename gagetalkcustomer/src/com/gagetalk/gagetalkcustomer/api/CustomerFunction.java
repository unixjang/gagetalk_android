package com.gagetalk.gagetalkcustomer.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gagetalk.gagetalkcommon.constant.ConstPrefValue;
import com.gagetalk.gagetalkcommon.util.MyLog;

/**
 * Created by hyochan on 7/20/15.
 */
public class CustomerFunction {
    private static final String TAG = "CustomerNetwork";

    private static CustomerFunction customerFunction;
    private SharedPreferences sharedPreferences;
    private Context context;

    private String chatRoom;
    private String notiRoom;

    private CustomerFunction(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static CustomerFunction getInstance(Context context) {
        if (customerFunction == null) customerFunction = new CustomerFunction(context);
        return customerFunction;
    }

    public String getChatRoom(){
        return this.chatRoom;
    }

    public void setChatRoom(String chatRoom){
        this.chatRoom = chatRoom;
    }

    public void deleteChatRoom(){
        this.chatRoom = null;
    }

    public String getNotiRoom(){
        return this.notiRoom;
    }

    public void setNotiRoom(String notiRoom){
        this.notiRoom = notiRoom;
    }


    public void cusLogin(String id, String name, String pw) {
        SharedPreferences pref =
                context.getSharedPreferences(ConstPrefValue.CUS_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ConstPrefValue.ID, id);
        editor.putString(ConstPrefValue.NAME, name);
        editor.putString(ConstPrefValue.PASSWORD, pw);
        MyLog.d(TAG, "### cusLogin - LOGIN DATA ###");
        MyLog.d(TAG, "id : " + id);
        MyLog.d(TAG, "name : " + name);
        MyLog.d(TAG, "pw : " + pw);
        editor.commit();
        // CustomToast.getInstance(context).createToast(context.getResources().getString(R.string.login_success));
    }


    public boolean isCusLocallyLoggedIn(){
        boolean result = false;
        if (getCusID() != null) {
            result = true;
        }
        MyLog.d(TAG, "customer locally logged in : " + result);
        return result;
    }

    public String getCusID(){
        SharedPreferences pref = context.getSharedPreferences(ConstPrefValue.CUS_LOGIN, Context.MODE_PRIVATE);
        return pref.getString(ConstPrefValue.ID, null);
    }

    public String getCusPW(){
        SharedPreferences pref = context.getSharedPreferences(ConstPrefValue.CUS_LOGIN, Context.MODE_PRIVATE);
        return pref.getString(ConstPrefValue.PASSWORD, null);
    }

    public String getCusName(){
        SharedPreferences pref = context.getSharedPreferences(ConstPrefValue.CUS_LOGIN, Context.MODE_PRIVATE);
        return pref.getString(ConstPrefValue.NAME, null);
    }

    public void setCusName(String name){
        SharedPreferences pref =
                context.getSharedPreferences(ConstPrefValue.CUS_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ConstPrefValue.NAME, name);
        MyLog.i(TAG, "name : " + name);
        editor.commit();
    }

}
