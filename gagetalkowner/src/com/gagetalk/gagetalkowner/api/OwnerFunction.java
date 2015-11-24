package com.gagetalk.gagetalkowner.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gagetalk.gagetalkcommon.constant.ConstPrefValue;
import com.gagetalk.gagetalkcommon.util.MyLog;

/**
 * Created by hyochan on 7/21/15.
 */
public class OwnerFunction {

    private static final String TAG = "OwnerFunction";

    private static OwnerFunction ownerFunction;
    private SharedPreferences sharedPreferences;
    private Context context;

    private String chatRoom;
    private String notiRoom;

    private OwnerFunction(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static OwnerFunction getInstance(Context context) {
        if (ownerFunction == null) ownerFunction = new OwnerFunction(context);
        return ownerFunction;
    }

    public void marLogin(String id, String name, String pw) {
        SharedPreferences pref =
                context.getSharedPreferences(ConstPrefValue.MAR_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ConstPrefValue.ID, id);
        editor.putString(ConstPrefValue.NAME, name);
        editor.putString(ConstPrefValue.PASSWORD, pw);
        MyLog.d(TAG, "id : " + id);
        MyLog.d(TAG, "name : " + name);
        MyLog.d(TAG, "pw : " + pw);
        editor.commit();
        MyLog.d(TAG, "### marLogin - LOGIN DATA ###");
        // CustomToast.getInstance(context).createToast(context.getResources().getString(R.string.login_success));
    }

    public String getMarID(){
        SharedPreferences pref = context.getSharedPreferences(ConstPrefValue.MAR_LOGIN, Context.MODE_PRIVATE);
        return pref.getString(ConstPrefValue.ID, null);
    }

    public String getMarPW(){
        SharedPreferences pref = context.getSharedPreferences(ConstPrefValue.MAR_LOGIN, Context.MODE_PRIVATE);
        return pref.getString(ConstPrefValue.PASSWORD, null);
    }

    public String getMarName(){
        SharedPreferences pref = context.getSharedPreferences(ConstPrefValue.MAR_LOGIN, Context.MODE_PRIVATE);
        return pref.getString(ConstPrefValue.NAME, null);
    }

    public void setsMarName(String name){
        SharedPreferences pref =
                context.getSharedPreferences(ConstPrefValue.MAR_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ConstPrefValue.NAME, name);
        MyLog.i(TAG, "name : " + name);
        editor.commit();
    }
}
