package com.gagetalk.gagetalkcustomer.preference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by hyochan on 7/4/15.
 */
public class AppPref {

    private static final String TAG = "AppPref";

    private final String PREF_NAME = "com.gagetalk.gagetalkcustomer.pref";

    // public final static String LAST_READ_CHAT_NUM = "PREF_CHAT_NUM_VAL";

    private static AppPref appPref;
    private SharedPreferences sharedPreferences;
    private Context context;

    private AppPref(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
    }

    public static AppPref getInstance(Context context) {
        if (appPref == null) appPref = new AppPref(context);
        return appPref;
    }

    public void put(String key, String value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public void put(String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);
        editor.commit();
    }

    public void put(String key, int value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(key, value);
        editor.commit();
    }

    public String getValue(String key, String dftValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    public int getValue(String key, int dftValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getInt(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }

    }

    public boolean getValue(String key, boolean dftValue) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);

        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

}
